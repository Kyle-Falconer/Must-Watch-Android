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
import com.fullmeadalchemist.mustwatch.db.AppDatabase
import com.fullmeadalchemist.mustwatch.db.BatchIngredientDao
import com.fullmeadalchemist.mustwatch.db.RecipeDao
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Recipe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface RecipeRepository {
    fun getPublicRecipes(): LiveData<List<Recipe>>

    fun getRecipes(userId: UUID): LiveData<List<Recipe>>

    fun addRecipe(recipe: Recipe): LiveData<Long>

    fun addRecipes(recipes: Array<Recipe>)

    fun getRecipe(id: Long): LiveData<Recipe>

    fun updateRecipe(recipe: Recipe): LiveData<Int>

    fun getRecipeIngredients(recipeId: Long?): LiveData<List<BatchIngredient>>
}

class RecipeRepositoryImpl(private val database: AppDatabase) : RecipeRepository {

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

    override fun getPublicRecipes(): LiveData<List<Recipe>> {
        return database.recipeDao().publicRecipes
    }

    override fun getRecipes(userId: UUID): LiveData<List<Recipe>> {
        // FIXME: get only Recipes accessible to current user
        return database.recipeDao().getRecipesForUser(userId)
    }

    override fun addRecipe(recipe: Recipe): LiveData<Long> {
        val recipeIdLiveData = MutableLiveData<Long>()
        Timber.d("Adding recipe to db: %s", recipe.toString())
        doAsync {
            try {
                val recipeId = database.recipeDao().insert(recipe)
                Timber.v("Got Recipe ID %s after inserting it into the database", recipeId)
                recipeIdLiveData.postValue(recipeId)
                recipe.ingredients?.let { ingredients ->
                    if (ingredients.isNotEmpty()) {
                        for (batchIngredients in ingredients) {
                            batchIngredients.recipeId = recipeId
                        }
                        doAsync {
                            database.batchIngredientDao().insertAll(ingredients)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e("Failed to update batch:\n%s", e.toString())
            }
        }
        return recipeIdLiveData
    }

    override fun addRecipes(recipes: Array<Recipe>) {
        Timber.d("Adding %d recipes to db", recipes.size)
        // FIXME: there's got to be a more efficient way of doing this
        for (r in recipes) {
            addRecipe(r)
        }
    }

    override fun getRecipe(uid: Long): LiveData<Recipe> {
        return database.recipeDao()[uid]
    }

    /**
     * @param recipe the Batch object to be updated
     * @return Observable number of rows updated
     */
    override fun updateRecipe(recipe: Recipe): LiveData<Int> {
        val updatedLiveData = MutableLiveData<Int>()
        doAsync {
            try {
                val recipeId = database.recipeDao().updateRecipe(recipe)
                updatedLiveData.postValue(recipeId)
            } catch (e: Exception) {
                Timber.e("Failed to update batch:\n%s", e.toString())
            }
        }
        return updatedLiveData
    }

    override fun getRecipeIngredients(recipeId: Long?): LiveData<List<BatchIngredient>> {
        return database.recipeDao().getIngredientsForRecipe(recipeId!!)
    }
}
