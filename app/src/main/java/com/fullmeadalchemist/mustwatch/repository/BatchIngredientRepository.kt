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

package com.fullmeadalchemist.mustwatch.repository

import com.fullmeadalchemist.mustwatch.db.BatchIngredientDao
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class BatchIngredientRepository @Inject
constructor(val batchIngredientDao: BatchIngredientDao) {

    fun addBatchIngredients(ingredients: List<BatchIngredient>) {
        Timber.d("Adding %s BatchIngredient objects to the db", ingredients.size)
        Observable.fromCallable<Any> { batchIngredientDao.insertAll(ingredients) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}
