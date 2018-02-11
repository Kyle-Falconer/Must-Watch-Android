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
import android.text.TextUtils
import com.fullmeadalchemist.mustwatch.db.BatchDao
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatchRepository @Inject
constructor(val batchDao: BatchDao) {

    // FIXME: get only Batches from current user
    //return batchDao.loadBatchesForUser();
    val batches: LiveData<List<Batch>>
        get() = batchDao.all

    fun addBatch(batch: Batch): LiveData<Long> {
        val batchIdLiveData = MutableLiveData<Long>()
        Timber.d("Adding batch to db:\n%s", batch.toString())
        Observable.fromCallable<Any> { batchDao.insert(batch) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { batchId ->
                    val bid = batchId as Long
                    batchIdLiveData.value = bid
                    batch.id = bid
                    upsertBatchIngredients(batch)
                }
        return batchIdLiveData
    }

    fun addBatches(batches: List<Batch>): LiveData<List<Long>> {
        val batchIdsLiveData = MutableLiveData<List<Long>>()
        Timber.d("Adding %d batches to db:\n%s", batches.size, TextUtils.join("\n", batches))
        Observable.fromCallable<Any> { batchDao.insertAll(*batches.toTypedArray()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ l ->
                    batchIdsLiveData.setValue(l as List<Long>)
                })

        return batchIdsLiveData
    }

    fun getBatch(id: Long?): LiveData<Batch> {
        return batchDao.get(id)
    }

    fun getBatches(user: User): LiveData<List<Batch>> {
        return batchDao.getAll(user.id as Long)
    }

    /**
     * @param batch the Batch object to be updated
     * @return Observable number of rows updated
     */
    fun updateBatch(batch: Batch): LiveData<Int> {
        val updatedLiveData = MutableLiveData<Int>()
        Observable.fromCallable<Any> { batchDao.updateBatch(batch) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
                    updatedLiveData.setValue(v as Int)
                }, { e ->
                    Timber.e("Failed to update batch:\n%s", e.toString())
                })
        upsertBatchIngredients(batch)
        return updatedLiveData
    }

    fun getBatchIngredients(batchId: Long): LiveData<List<BatchIngredient>> {
        return batchDao.getIngredientsForBatch(batchId)
    }

    private fun upsertBatchIngredients(batch: Batch?) {
        if (batch?.ingredients == null) {
            Timber.w("Batch Ingredients is null")
            return
        }
        if (batch.id == null) {
            Timber.e("No batch ID for these batch ingredients!")
            return
        }

        batch.ingredients?.let {
            for (batchIngredient in it) {
                batchIngredient.batchId = batch.id
            }
            Observable.fromCallable<Any> { batchDao.upsertBatchIngredients(it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ ret ->
                        ret?.let {
                            Timber.d("Inserted %d BatchIngredients into the database", -1)
                        }
                    }, { e ->
                        Timber.e("Failed to insert batch ingredients:\n%s", e.toString())
                    })
        }
    }
}
