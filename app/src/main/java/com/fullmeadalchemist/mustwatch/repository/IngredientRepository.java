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

import android.arch.lifecycle.LiveData;

import com.fullmeadalchemist.mustwatch.db.IngredientDao;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class IngredientRepository {
    private final IngredientDao ingredientDao;

    @Inject
    public IngredientRepository(IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public void addIngredients(List<Ingredient> ingredients) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.size());
        Observable.fromCallable(() -> ingredientDao.insertAll(ingredients.toArray(new Ingredient[ingredients.size()])))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void addIngredients(Ingredient[] ingredients) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.length);
        Observable.fromCallable(() -> ingredientDao.insertAll(ingredients))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public LiveData<List<Ingredient>> getSugarEntries() {
        return ingredientDao.getAllSugars();
    }

    public LiveData<List<Ingredient>> getNutrientEntries() {
        return ingredientDao.getAllNutrients();
    }

    public LiveData<List<Ingredient>> getYeastEntries() {
        return ingredientDao.getAllYeasts();
    }

    public LiveData<List<Ingredient>> getStabilizerEntries() {
        return ingredientDao.getAllStabilizers();
    }

    public LiveData<List<Ingredient>> getAll() {
        return ingredientDao.getAll();
    }

    public LiveData<Ingredient> getIngredientById(String id) {
        return ingredientDao.getById(id);
    }
}
