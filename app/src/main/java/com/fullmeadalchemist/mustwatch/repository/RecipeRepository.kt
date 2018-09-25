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

package com.fullmeadalchemist.mustwatch.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.fullmeadalchemist.mustwatch.db.BatchIngredientDao
import com.fullmeadalchemist.mustwatch.db.RecipeDao
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository {
    @Inject
    lateinit var recipeDao: RecipeDao
    @Inject
    lateinit var batchIngredientDao: BatchIngredientDao
    // TODO: load this from the JSON file and/or server, sync with db, return LiveData
    // application.getApplicationContext()
    //        MutableLiveData<Long> recipeIdLiveData = new MutableLiveData<>();
    //        Observable.fromCallable(() -> recipeDao.getPublicRecipes())
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(recipes -> {
    //                    recipes.getValue();
    //                    if (recipes == null || recipes.size() == 0) {
    //                        Timber.i("Populating the database with Recipe data");
    //
    //                        JSONResourceReader reader = new JSONResourceReader(context.getResources(), R.raw.recipes);
    //                        Recipe[] jsonObj = reader.constructFromJson(Recipe[].class);
    //                        for (Recipe r : jsonObj) {
    //                            r.publicReadable = true;
    //                        }
    //                        addRecipes(jsonObj);
    //                    } else {
    //                        Timber.d("Recipes already found in the database.");
    //                    }
    //                });
    val publicRecipes: LiveData<List<Recipe>>
        get() = recipeDao.publicRecipes

    fun getRecipes(userId: Long?): LiveData<List<Recipe>> {
        // FIXME: get only Recipes accessible to current user
        return recipeDao.getRecipesForUser(userId!!)
    }

    fun addRecipe(recipe: Recipe): LiveData<Long> {
        val recipeIdLiveData = MutableLiveData<Long>()
        Timber.d("Adding recipe to db: %s", recipe.toString())
        Observable.fromCallable<Any> { recipeDao.insert(recipe) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r_id ->
                    val rid = r_id as Long
                    Timber.v("Got Recipe ID %s after inserting it into the database", rid)
                    recipeIdLiveData.setValue(rid)
                    recipe.ingredients?.let {
                        if (it.isNotEmpty()) {
                            for (bi in it) {
                                bi.recipeId = rid
                            }
                            Observable.fromCallable<Any> { batchIngredientDao.insertAll(it) }
                                    .subscribeOn(Schedulers.io())
                                    .subscribe()
                        }
                    }
                }) { e -> Timber.e("Failed to update batch:\n%s", e.toString()) }
        return recipeIdLiveData
    }

    fun addRecipes(recipes: Array<Recipe>) {
        Timber.d("Adding %d recipes to db", recipes.size)
        // FIXME: there's got to be a more efficient way of doing this
        for (r in recipes) {
            addRecipe(r)
        }
    }

    fun getRecipe(id: Long): LiveData<Recipe> {
        return recipeDao[id]
    }

    /**
     * @param recipe the Batch object to be updated
     * @return Observable number of rows updated
     */
    fun updateRecipe(recipe: Recipe): LiveData<Int> {
        val updatedLiveData = MutableLiveData<Int>()
        Observable.fromCallable<Any> { recipeDao.updateRecipe(recipe) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
                    updatedLiveData.setValue(v as Int)
                }, { e ->
                    Timber.e("Failed to update batch:\n%s", e.toString())
                })
        return updatedLiveData
    }

    fun getRecipeIngredients(recipeId: Long?): LiveData<List<BatchIngredient>> {
        return recipeDao.getIngredientsForRecipe(recipeId!!)
    }
}
