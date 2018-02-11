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

package com.fullmeadalchemist.mustwatch.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient


@Dao
interface BatchDao {
    @get:Query("SELECT * FROM batch")
    val all: LiveData<List<Batch>>

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    fun getAll(user_id: Long): LiveData<List<Batch>>

    @Query("SELECT * FROM batch WHERE id = :batch_id LIMIT 1")
    operator fun get(batch_id: Long?): LiveData<Batch>

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    fun loadBatchesForUser(user_id: Long): LiveData<List<Batch>>

    @Query("SELECT * FROM batch_ingredient WHERE batch_id=:batch_id")
    fun getIngredientsForBatch(batch_id: Long): LiveData<List<BatchIngredient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertBatchIngredients(batchIngredients: List<BatchIngredient>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(batch: Batch): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg batches: Batch): List<Long>

    @Delete
    fun delete(batch: Batch)

    @Update
    fun updateBatch(batch: Batch): Int
}