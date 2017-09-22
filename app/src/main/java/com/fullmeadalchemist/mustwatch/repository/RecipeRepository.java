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

package com.fullmeadalchemist.mustwatch.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.fullmeadalchemist.mustwatch.db.BatchIngredientDao;
import com.fullmeadalchemist.mustwatch.db.RecipeDao;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;
import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class RecipeRepository {

    private final Application application;
    private final RecipeDao recipeDao;
    private final BatchIngredientDao batchIngredientDao;

    @Inject
    public RecipeRepository(Application application, RecipeDao recipeDao, BatchIngredientDao batchIngredientDao) {
        this.application = application;
        this.recipeDao = recipeDao;
        this.batchIngredientDao = batchIngredientDao;
    }

    public LiveData<List<Recipe>> getRecipes(Long userId) {
        // FIXME: get only Recipes accessible to current user
        return recipeDao.getRecipesForUser(userId);
    }

    public LiveData<List<Recipe>> getPublicRecipes() {
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
//                        Recipe[] jsonObj = reader.constructUsingGson(Recipe[].class);
//                        for (Recipe r : jsonObj) {
//                            r.publicReadable = true;
//                        }
//                        addRecipes(jsonObj);
//                    } else {
//                        Timber.d("Recipes already found in the database.");
//                    }
//                });
        return recipeDao.getPublicRecipes();
    }

    public LiveData<Long> addRecipe(Recipe recipe) {
        MutableLiveData<Long> recipeIdLiveData = new MutableLiveData<>();
        Timber.d("Adding recipe to db: %s", recipe.toString());
        Observable.fromCallable(() -> recipeDao.insert(recipe))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r_id -> {
                    Timber.v("Got Recipe ID %s after inserting it into the database", r_id);
                    recipeIdLiveData.setValue(r_id);
                    if (recipe.ingredients.size() > 0) {
                        for (BatchIngredient bi : recipe.ingredients) {
                            bi.recipeId = r_id;
                        }
                        Observable.fromCallable(() -> batchIngredientDao.insertAll(recipe.ingredients))
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                }, e -> {
                    Timber.e("Failed to update batch:\n%s", e.toString());
                });
        return recipeIdLiveData;
    }

    public void addRecipes(Recipe[] recipes) {
        Timber.d("Adding %d recipes to db", recipes.length);
        // FIXME: there's got to be a more efficient way of doing this
        for (Recipe r : recipes) {
            addRecipe(r);
        }
    }

    public LiveData<Recipe> getRecipe(long id) {
        return recipeDao.get(id);
    }

    /**
     * @param recipe the Batch object to be updated
     * @return Observable number of rows updated
     */
    public LiveData<Integer> updateRecipe(Recipe recipe) {
        MutableLiveData<Integer> updatedLiveData = new MutableLiveData<>();
        Observable.fromCallable(() -> recipeDao.updateRecipe(recipe))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedLiveData::setValue, e -> {
                    Timber.e("Failed to update batch:\n%s", e.toString());
                });
        return updatedLiveData;
    }

    public LiveData<List<BatchIngredient>> getRecipeIngredients(Long recipeId) {
        return recipeDao.getIngredientsForRecipe(recipeId);
    }
}
