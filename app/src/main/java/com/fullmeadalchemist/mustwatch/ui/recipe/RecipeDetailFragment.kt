/*
 * Copyright (c) 2017 Full Mead Alchemist, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.ui.recipe

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct
import com.fullmeadalchemist.mustwatch.databinding.RecipeDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.IngredientListViewAdapter
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import com.fullmeadalchemist.mustwatch.vo.Recipe.Companion.RECIPE_ID
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*


class RecipeDetailFragment : Fragment() {

    lateinit var dataBinding: RecipeDetailFragmentBinding
    private val defaultLocale = Locale.getDefault()

    lateinit var ingredientListViewAdapter: IngredientListViewAdapter
    lateinit var ingredientsRecyclerView: RecyclerView

    val viewModel: MainViewModel by sharedViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail_fragment,
                container, false)

        ingredientListViewAdapter = IngredientListViewAdapter(object : IngredientListViewAdapter.IngredientClickCallback {
            override fun onClick(repo: BatchIngredient) {
                Timber.w("Clicking on a BatchIngredient in this list is not yet supported")
            }
        })

        ingredientsRecyclerView = dataBinding.root.findViewById(R.id.ingredients_list)
        ingredientsRecyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        ingredientsRecyclerView.layoutManager = llm
        ingredientsRecyclerView.adapter = ingredientListViewAdapter

        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initClickListeners()

        val bundle = this.arguments
        if (bundle != null) {
            val recipeId = bundle.getLong(RECIPE_ID, java.lang.Long.MIN_VALUE)
            viewModel.selectedRecipeId = recipeId
            Timber.v("Got Recipe ID %d from the NavigationController.", recipeId)

            if (recipeId != java.lang.Long.MIN_VALUE) {
                // TODO: verify the user has permissions to read this recipe
                if (viewModel.recipe != null) {
                    Timber.v("Reusing viewmodel data")
                    dataBinding.recipe = viewModel.recipe
                    updateRecipeUiInfo()
                    viewModel.getRecipeIngredients(recipeId).observe(this, Observer<List<BatchIngredient>> { recipeIngredients ->
                        if (recipeIngredients != null) {
                            Timber.v("Loaded %s Recipe ingredients", recipeIngredients.size)
                            ingredientListViewAdapter.dataSet = recipeIngredients
                            ingredientListViewAdapter.notifyDataSetChanged()
                        } else {
                            Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Recipe %s", recipeId)
                        }
                    })
                } else {
                    Timber.v("Going to the RecipeRepository to get the Recipe with id %s", recipeId)
                    viewModel.getRecipe(recipeId).observe(this, Observer<Recipe> { recipe ->
                        if (recipe != null) {
                            Timber.v("Loaded Recipe with ID %d:\n%s", recipe.id, recipe)

                            viewModel.recipe = recipe
                            dataBinding.recipe = viewModel.recipe
                            updateRecipeUiInfo()

                            viewModel.getRecipeIngredients(recipeId).observe(this, Observer<List<BatchIngredient>> { recipeIngredients ->
                                if (recipeIngredients != null) {
                                    Timber.v("Loaded %s Recipe ingredients", recipeIngredients.size)
                                    ingredientListViewAdapter.dataSet = recipeIngredients
                                    ingredientListViewAdapter.notifyDataSetChanged()
                                } else {
                                    Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Recipe %s", recipeId)
                                }
                            })

                        } else {
                            Timber.w("Received a null Batch from the RecipeDetailViewModel.")
                        }
                    })
                }
            }
        } else {
            Timber.i("No Recipe ID was received. Redirecting to the Batch Creation form.")
            // navigationController.navigateToAddBatch()
        }
    }

    private fun updateRecipeUiInfo() {
        viewModel.recipe?.let {
            if (it.startingSG != null && it.finalSG != null) {
                val abvPct = calcAbvPct(it.startingSG as Double, it.finalSG as Double)
                val f = DecimalFormat("0.##")
                dataBinding.targetABV.text = String.format(defaultLocale, "%s%%", f.format(abvPct))
            }
        }
    }

    private fun initClickListeners() {
        val submitButton = dataBinding.root.findViewById<Button>(R.id.button_create_batch_from_recipe)
        submitButton?.setOnClickListener { v ->
            Timber.i("Create From Recipe button clicked")

            // TODO: show loading progress while we convert the recipe to a batch
            viewModel.getCurrentUser().observe(this, Observer { user ->
                val batch = Batch(
                        user?.uid,
                        viewModel.recipe?.name,
                        viewModel.recipe?.startingSG,
                        viewModel.recipe?.finalSG,
                        null,
                        null,
                        null,
                        viewModel.recipe?.outputVol,
                        Batch.BatchStatusEnum.PLANNING,
                        Calendar.getInstance(),
                        viewModel.recipe?.notes
                )

                viewModel.saveNewBatch(batch).observe(this, Observer<Long> { newBatchId ->
                    viewModel.getRecipeIngredients(viewModel.selectedRecipeId).observe(this, Observer { ingredients ->
                        if (ingredients == null) {
                            Timber.w("No ingredients found for this recipe. Nothing to insert.")
                            navigateToBatchForm(newBatchId)
                        } else {
                            for (ingredient in ingredients){
                                ingredient.batchId = newBatchId
                            }
                            Timber.d("Inserting ${ingredients.size} ingredients to batch with id $newBatchId")
                            viewModel.addIngredientsToBatch(ingredients).observe(this, Observer { insertedRecipeIds ->
                                insertedRecipeIds?.let { recipeIds ->
                                    for (id in recipeIds){
                                        Timber.d("Inserted recipe with ID: $id")
                                    }
                                    navigateToBatchForm(newBatchId)
                                }
                            })
                        }
                    })
                })
            })
        }
    }

    private fun navigateToBatchForm(batchId : Long?) {
        if (batchId == null){
            Timber.e("Failed to create a batch from this recipe!")
        } else {
            val bundle = Bundle()
            bundle.putLong(BATCH_ID, batchId)
            // TODO: somehow indicate that this is a new batch so that if the user backs out without saving, the batch is deleted.
            dataBinding.root.findNavController().navigate(R.id.batchFormFragment, bundle)
        }
    }
}
