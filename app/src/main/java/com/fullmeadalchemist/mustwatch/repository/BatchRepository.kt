/*
 * Copyright (c) 2017-2018 Full Mead Alchemist, LLC.
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
import com.fullmeadalchemist.mustwatch.db.AppDatabase
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.User
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*

interface BatchRepository {
    fun addBatch(batch: Batch): LiveData<Long>
    fun addBatches(batches: List<Batch>): LiveData<List<Long>>
    fun getBatch(id: Long?): LiveData<Batch>
    fun getBatches(user: User): LiveData<List<Batch>>
    fun getBatchesForUserId(userId: UUID): LiveData<List<Batch>>
    fun updateBatch(batch: Batch): LiveData<Int>
    fun getBatchIngredients(batchId: Long): LiveData<List<BatchIngredient>>
    fun upsertBatchIngredients(batch: Batch)
    fun addIngredient(batchIngredient: BatchIngredient): LiveData<Long>
}

class BatchRepositoryImpl(private val database: AppDatabase) : BatchRepository {

    // FIXME: get only Batches from current user
    //return batchDao.loadBatchesForUser();
    val batches: LiveData<List<Batch>>
        get() = database.batchDao().all

    override fun addBatch(batch: Batch): LiveData<Long> {
        val batchIdLiveData = MutableLiveData<Long>()
        Timber.d("Adding batch to db:\n%s", batch.toString())
        doAsync {
            try {
                val bid: Long = database.batchDao().insert(batch)
                batchIdLiveData.postValue(bid)
                batch.id = bid
                upsertBatchIngredients(batch)
            } catch (e: Exception) {
                Timber.e("Failed to add batches:\n%s", e.toString())
            }
        }
        return batchIdLiveData
    }

    override fun addBatches(batches: List<Batch>): LiveData<List<Long>> {
        val batchIdsLiveData = MutableLiveData<List<Long>>()
        Timber.d("Adding %d batches to db:\n%s", batches.size, TextUtils.join("\n", batches))
        doAsync {
            try {
                batchIdsLiveData.postValue(database.batchDao().insertAll(*batches.toTypedArray()))
            } catch (e: Exception) {
                Timber.e("Failed to add batches:\n%s", e.toString())
            }
        }
        return batchIdsLiveData
    }

    override fun getBatch(id: Long?): LiveData<Batch> {
        return database.batchDao()[id]
    }

    override fun getBatches(user: User): LiveData<List<Batch>> {
        return database.batchDao().getAll(user.uid)
    }

    override fun getBatchesForUserId(userId: UUID): LiveData<List<Batch>> {
        return database.batchDao().getAll(userId)
    }

    /**
     * @param batch the Batch object to be updated
     * @return Observable number of rows updated
     */
    override fun updateBatch(batch: Batch): LiveData<Int> {
        val updatedLiveData = MutableLiveData<Int>()
        doAsync {
            try {
                updatedLiveData.postValue(database.batchDao().updateBatch(batch))
            } catch (e: Exception) {
                Timber.e("Failed to update batch:\n%s", e.toString())
            }
        }

        upsertBatchIngredients(batch)
        return updatedLiveData
    }

    override fun getBatchIngredients(batchId: Long): LiveData<List<BatchIngredient>> {
        return database.batchDao().getIngredientsForBatch(batchId)
    }

    override fun addIngredient(batchIngredient: BatchIngredient): LiveData<Long> {
        val updatedLiveData = MutableLiveData<Long>()
        doAsync {
            try {
                updatedLiveData.postValue( database.batchIngredientDao().insert(batchIngredient))
            } catch (e: Exception) {
                Timber.e("Failed to add batchIngredient:\n%s", e.toString())
            }
        }
        return updatedLiveData
    }

    override fun upsertBatchIngredients(batch: Batch) {
        if (batch.ingredients == null) {
            Timber.w("Batch Ingredients is null")
            return
        }

        val updatedBatchIngredientIds = MutableLiveData<List<Long>>()
        batch.ingredients?.let {
            for (batchIngredient in it) {
                batchIngredient.batchId = batch.id
            }
            doAsync {
                try {
                    val batchIds = database.batchDao().upsertBatchIngredients(it)
                    updatedBatchIngredientIds.postValue(batchIds)
                    Timber.d("Inserted %d BatchIngredients into the database", batchIds.size)
                } catch (e: Exception) {
                    Timber.e("Failed to update batch:\n%s", e.toString())
                }
            }
        }
    }
}
