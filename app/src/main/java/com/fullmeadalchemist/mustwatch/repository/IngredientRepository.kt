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

import com.fullmeadalchemist.mustwatch.db.IngredientDao
import com.fullmeadalchemist.mustwatch.vo.Ingredient

import javax.inject.Inject
import javax.inject.Singleton

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@Singleton
class IngredientRepository @Inject
constructor(val ingredientDao: IngredientDao) {

    val sugarEntries: LiveData<List<Ingredient>>
        get() = ingredientDao.allSugars

    val nutrientEntries: LiveData<List<Ingredient>>
        get() = ingredientDao.allNutrients

    val yeastEntries: LiveData<List<Ingredient>>
        get() = ingredientDao.allYeasts

    val stabilizerEntries: LiveData<List<Ingredient>>
        get() = ingredientDao.allStabilizers

    val all: LiveData<List<Ingredient>>
        get() = ingredientDao.all

    fun addIngredients(ingredients: List<Ingredient>) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.size)
        Observable.fromCallable<Any> { ingredientDao.insertAll(*ingredients.toTypedArray()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun addIngredients(ingredients: Array<Ingredient>) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.size)
        Observable.fromCallable<Any> { ingredientDao.insertAll(*ingredients) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun getIngredientById(id: String): LiveData<Ingredient> {
        return ingredientDao.getById(id)
    }
}
