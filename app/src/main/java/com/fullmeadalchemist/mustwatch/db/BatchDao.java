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

package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.util.List;


@Dao
public interface BatchDao {
    @Query("SELECT * FROM batch")
    LiveData<List<Batch>> getAll();

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    LiveData<List<Batch>> getAll(long user_id);

    @Query("SELECT * FROM batch WHERE id = :batch_id LIMIT 1")
    LiveData<Batch> get(Long batch_id);

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    LiveData<List<Batch>> loadBatchesForUser(long user_id);

    @Query("SELECT * FROM batch_ingredient WHERE batch_id=:batch_id")
    LiveData<List<BatchIngredient>> getIngredientsForBatch(long batch_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] upsertBatchIngredients(List<BatchIngredient> batchIngredients);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Batch batch);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(Batch... batches);

    @Delete
    void delete(Batch batch);

    @Update
    int updateBatch(Batch batch);
}