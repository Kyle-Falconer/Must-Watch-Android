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

package com.fullmeadalchemist.mustwatch.ui.recipe.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct
import com.fullmeadalchemist.mustwatch.databinding.RecipeDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import com.fullmeadalchemist.mustwatch.vo.Recipe.Companion.RECIPE_ID
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*


class RecipeDetailFragment : Fragment() {

    lateinit var dataBinding: RecipeDetailFragmentBinding
    lateinit var viewModel: RecipeDetailViewModel
    private val defaultLocale = Locale.getDefault()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail_fragment,
                container, false)

        viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel::class.java)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initClickListeners()

        val bundle = this.arguments
        if (bundle != null) {
            val recipeId = bundle.getLong(RECIPE_ID, java.lang.Long.MIN_VALUE)
            Timber.v("Got Recipe ID %d from the NavigationController.", recipeId)

            if (recipeId != java.lang.Long.MIN_VALUE) {
                if (viewModel.recipe != null) {
                    Timber.v("Reusing viewmodel data")
                    dataBinding.recipe = viewModel.recipe
                    updateRecipeUiInfo()
                    updateRecipeIngredientUiInfo()
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
                                    viewModel.recipe?.ingredients = recipeIngredients
                                    updateRecipeIngredientUiInfo()
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
                val abv_pct = calcAbvPct(it.startingSG as Double, it.finalSG as Double)
                val f = DecimalFormat("0.##")
                dataBinding.targetABV.text = String.format(defaultLocale, "%s%%", f.format(abv_pct))
            }
        }
    }

    private fun updateRecipeIngredientUiInfo() {
        viewModel.recipe?.ingredients?.let {
            // FIXME: this is not performant and looks ghetto.
            Timber.d("Found %s BatchIngredients for this Recipe; adding them to the ingredientsList", it.size)
            val ingredientsList = activity!!.findViewById<LinearLayout>(R.id.ingredients_list)
            ingredientsList.removeAllViews()
            for (ingredient in it) {
                val ingredientText = BatchIngredientView(activity)
                ingredientText.setBatchIngredient(ingredient)
                ingredientsList.addView(ingredientText)
            }
        }
    }

    private fun initClickListeners() {
        val submitButton = activity!!.findViewById<Button>(R.id.button_create_batch_from_recipe)
        submitButton?.setOnClickListener { v ->
            Timber.i("Create From Recipe button clicked")
            // navigationController.navigateToCreateFromBatch(viewModel.recipe?.id)
        }
    }
}
