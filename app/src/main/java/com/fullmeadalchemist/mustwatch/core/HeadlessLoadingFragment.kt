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

package com.fullmeadalchemist.mustwatch.core

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.generateDummyBatchesWithData
import com.fullmeadalchemist.mustwatch.repository.BatchRepository
import com.fullmeadalchemist.mustwatch.repository.IngredientRepository
import com.fullmeadalchemist.mustwatch.repository.RecipeRepository
import com.fullmeadalchemist.mustwatch.repository.UserRepository
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HeadlessLoadingFragment : Fragment() {

    private var updated = false

    var batchRepository: BatchRepository? = null
    var userRepository: UserRepository? = null
    var ingredientRepository: IngredientRepository? = null
    var recipeRepository: RecipeRepository? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
    }

    override fun onStart() {
        super.onStart()
        populateDb()
    }

    override fun onStop() {
        super.onStop()
        Timber.i("Finished! Shutting down headless fragment.")
    }

    private fun populateDb() {
        if (batchRepository == null ||
                userRepository == null ||
                ingredientRepository == null ||
                recipeRepository == null) {
            Timber.e("null repos")
            return
        }

        if (updated) {
            return
        }
        updated = true

        Timber.i("Populating database...")

        Observable.create<Any> { _ ->
            ingredientRepository?.all?.observe(this, Observer<List<Ingredient>> { ingredients ->
                Timber.w("was notified that the ingredients have changed")

                // Ingredients loader
                // FIXME: move to ingredientRepository?
                // FIXME: check for differences as well and update existing
                if (ingredients != null && ingredients.isEmpty()) {
                    val reader = JSONResourceReader(resources, R.raw.ingredients)
                    val jsonObj = reader.constructFromJson(Array<Ingredient>::class.java)
                    if (jsonObj.isNotEmpty()) {
                        Timber.i("Populating the database with Ingredient data")
                        ingredientRepository?.addIngredients(jsonObj)
                    } else {
                        Timber.e("Loaded no ingredients from the JSON resources!")
                    }

                } else if (ingredients != null && ingredients.isNotEmpty()) {
                    recipeRepository?.publicRecipes?.observe(this@HeadlessLoadingFragment, Observer<List<Recipe>> { recipes ->
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

            // Dummy data loader
            userRepository?.currentUserId?.observe(this@HeadlessLoadingFragment, Observer<Long> { userId ->
                Timber.w("was notified that the current user has changed")
                if (userId != null) {
                    batchRepository!!.batches.observe(this@HeadlessLoadingFragment, Observer<List<Batch>> { batches ->
                        if (batches != null && batches.isEmpty()) {
                            Timber.d("Got user with no batches; generating batches...")
                            val dummyBatches = generateDummyBatchesWithData(userId, 20)
                            batchRepository!!.addBatches(dummyBatches)
                        } else if (batches != null) {
                            Timber.d("Got user with %s batches.",
                                    batches.size.toString())
                        } else {
                            Timber.d("Got no batches for user %s",
                                    userId)
                        }
                    })
                }
            })
        }.subscribeOn(Schedulers.io()).subscribe()
    }
}
