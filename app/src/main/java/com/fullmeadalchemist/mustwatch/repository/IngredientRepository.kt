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
import com.fullmeadalchemist.mustwatch.db.AppDatabase
import com.fullmeadalchemist.mustwatch.db.IngredientDao
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

interface IngredientRepository {
    fun addIngredients(ingredients: List<Ingredient>)

    fun addIngredients(ingredients: Array<Ingredient>)

    fun getIngredientById(id: String): LiveData<Ingredient>

    fun getSugars(): LiveData<List<Ingredient>>

    fun getNutrients(): LiveData<List<Ingredient>>

    fun getYeasts(): LiveData<List<Ingredient>>

    fun getStabilizers(): LiveData<List<Ingredient>>

    fun getAllIngredients(): LiveData<List<Ingredient>>
}

class IngredientRepositoryImpl(private val database: AppDatabase) : IngredientRepository {

    override fun getSugars(): LiveData<List<Ingredient>> {
        return database.ingredientDao().allSugars
    }

    override fun getNutrients(): LiveData<List<Ingredient>> {
        return database.ingredientDao().allNutrients
    }

    override fun getYeasts(): LiveData<List<Ingredient>> {
        return database.ingredientDao().allYeasts
    }

    override fun getStabilizers(): LiveData<List<Ingredient>> {
        return database.ingredientDao().allStabilizers
    }

    override fun getAllIngredients(): LiveData<List<Ingredient>> {
        return database.ingredientDao().all
    }

    override fun addIngredients(ingredients: List<Ingredient>) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.size)
        Observable.fromCallable<Any> { database.ingredientDao().insertAll(*ingredients.toTypedArray()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun addIngredients(ingredients: Array<Ingredient>) {
        Timber.d("Adding %s Ingredient objects to the db", ingredients.size)
        Observable.fromCallable<Any> { database.ingredientDao().insertAll(*ingredients) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun getIngredientById(id: String): LiveData<Ingredient> {
        return database.ingredientDao().getById(id)
    }
}
