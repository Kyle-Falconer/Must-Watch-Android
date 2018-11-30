/*
 * Copyright (c) 2018 Full Mead Alchemist, LLC.
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

package com.fullmeadalchemist.mustwatch

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.fullmeadalchemist.mustwatch.BuildConfig.DEBUG
import com.fullmeadalchemist.mustwatch.core.JSONResourceReader
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators
import com.fullmeadalchemist.mustwatch.repository.BatchRepository
import com.fullmeadalchemist.mustwatch.repository.IngredientRepository
import com.fullmeadalchemist.mustwatch.repository.RecipeRepository
import com.fullmeadalchemist.mustwatch.repository.UserRepository
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.doAsync
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber


class StartActivity : AppCompatActivity() {

    private val batchRepository: BatchRepository by inject()
    private val userRepository: UserRepository by inject()
    private val ingredientRepository: IngredientRepository by inject()
    private val recipeRepository: RecipeRepository by inject()

    val viewModel: MainViewModel by viewModel()

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.setHomeAsUpIndicator(0)
        toggle.syncState()

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(toolbar, navController, drawer_layout)
        NavigationUI.setupWithNavController(nav_view, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("Navigating up")
        return NavigationUI.navigateUp(nav_host_fragment.findNavController(), drawer_layout) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()

        if (DEBUG) {
            viewModel.isFirstLaunch.observe(this, Observer<Boolean> { isFirstLaunch ->
                if (isFirstLaunch != null && isFirstLaunch) {
                    loadDummyData()
                } else {
                    Timber.d("skipping dummy data load")
                }
            })
        }
    }


    private fun loadDummyData() {

        doAsync {
            userRepository.getCurrentUser().value?.let { user ->
                Timber.d("Loading dummy database")
                ingredientRepository.getAllIngredients().observe(this@StartActivity, Observer<List<Ingredient>> { ingredients ->
                    Timber.w("was notified that the ingredients have changed")
                    // Ingredients loader
                    // FIXME: move to ingredientRepository?
                    // FIXME: check for differences as well and update existing
                    if (ingredients != null && ingredients.isEmpty()) {
                        val reader = JSONResourceReader(resources, R.raw.ingredients)
                        val jsonObj = reader.constructFromJson(Array<Ingredient>::class.java)
                        if (jsonObj.isNotEmpty()) {
                            Timber.i("Populating the database with Ingredient data")
                            ingredientRepository.addIngredients(jsonObj)
                        } else {
                            Timber.e("Loaded no ingredients from the JSON resources!")
                        }

                    } else if (ingredients != null && ingredients.isNotEmpty()) {
                        recipeRepository.getPublicRecipes().observe(this@StartActivity, Observer<List<Recipe>> { recipes ->
                            Timber.w("was notified that the recipes have changed")

                            // Recipes loader
                            // FIXME: move to recipeRepository?
                            // FIXME: check for differences as well and update existing
                            if (recipes != null && recipes.isEmpty()) {
                                val reader = JSONResourceReader(resources, R.raw.recipes)
                                val recipeJsonObj = reader.constructFromJson(Array<Recipe>::class.java)
                                if (recipeJsonObj.isNotEmpty()) {
                                    for (r in recipeJsonObj) {
                                        r.publicReadable = true
                                    }

                                    Timber.d("Populating the database with Recipe data")
                                    recipeRepository?.addRecipes(recipeJsonObj)
                                } else {
                                    Timber.e("Loaded no recipes from the JSON resources!")
                                }

                            } else {
                                Timber.d("Recipes already found in the database.")
                            }
                        })
                    } else {
                        Timber.d("Ingredients already found in the database.")
                    }
                })

                batchRepository.getBatches(user).observe(this@StartActivity, Observer<List<Batch>> { batches ->
                    if (batches != null && batches.isEmpty()) {
                        Timber.d("Got user with no batches; generating batches...")
                        val dummyBatches = DemoGenerators.generateDummyBatchesWithData(user.uid, 10)
                        batchRepository.addBatches(dummyBatches)
                    } else if (batches != null) {
                        Timber.d("Got user with %s batches.",
                                batches.size.toString())
                    } else {
                        Timber.d("Got no batches for user %s",
                                user.uid)
                    }
                })
            }

        }
    }
}