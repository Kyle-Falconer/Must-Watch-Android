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

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.generateDummyBatchesWithData
import com.fullmeadalchemist.mustwatch.di.Injectable
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
import javax.inject.Inject

class HeadlessLoadingFragment : Fragment(), Injectable {

    private var updated = false

    @Inject
    lateinit var batchRepository: BatchRepository
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var ingredientRepository: IngredientRepository
    @Inject
    lateinit var recipeRepository: RecipeRepository

    lateinit var lifecycleContext: LifecycleOwner

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
        lifecycleContext = this
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
        if (updated) {
            return
        }
        updated = true

        Timber.i("Populating database...")


        Observable.create<Any> { emitter ->

            ingredientRepository!!.all.observe(lifecycleContext!!, Observer<List<Ingredient>> { ingredients ->
                Timber.w("was notified that the ingredients have changed")

                // Ingredients loader
                // FIXME: move to IngredientRepository
                // FIXME: check for differences as well and update existing
                if (ingredients != null && ingredients.size == 0) {
                    val reader = JSONResourceReader(resources, R.raw.ingredients)
                    val jsonObj = reader.constructUsingGson(Array<Ingredient>::class.java)
                    if (jsonObj.size > 0) {
                        Timber.i("Populating the database with Ingredient data")
                        ingredientRepository!!.addIngredients(jsonObj)
                    } else {
                        Timber.e("Loaded no ingredients from the JSON resources!")
                    }

                } else if (ingredients != null && ingredients.size > 0) {
                    recipeRepository!!.publicRecipes.observe(lifecycleContext!!, Observer<List<Recipe>> { recipes ->
                        Timber.w("was notified that the recipes have changed")

                        // Recipes loader
                        // FIXME: move to RecipeRepository
                        // FIXME: check for differences as well and update existing
                        if (recipes != null && recipes.size == 0) {
                            val reader = JSONResourceReader(resources, R.raw.recipes)
                            val recipeJsonObj = reader.constructUsingGson(Array<Recipe>::class.java)
                            if (recipeJsonObj.size > 0) {
                                for (r in recipeJsonObj) {
                                    r.publicReadable = true
                                }

                                Timber.d("Populating the database with Recipe data")
                                recipeRepository!!.addRecipes(recipeJsonObj)
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
            userRepository!!.currentUserId.observe(lifecycleContext!!, Observer<Long> { userId ->
                Timber.w("was notified that the current user has changed")
                if (userId != null) {
                    batchRepository!!.batches.observe(lifecycleContext!!, Observer<List<Batch>> { batches ->
                        if (batches != null && batches.size == 0) {
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
