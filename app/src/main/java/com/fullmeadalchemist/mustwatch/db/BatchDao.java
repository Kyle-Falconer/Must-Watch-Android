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
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

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

    @Query("SELECT * FROM log_entry "
            + "INNER JOIN batch on batch.id = log_entry.batch_id "
            + "WHERE batch.id = :batch_id")
    LiveData<List<LogEntry>> loadLogEntriesForBatch(long batch_id);

    @Insert
    Long insert(Batch batch);

    @Insert
    List<Long> insertAll(Batch... batches);

    @Insert
    void insert(LogEntry entry);

    @Insert
    void insertAll(LogEntry... entries);

    @Delete
    void delete(Batch batch);

    @Update
    int updateBatch(Batch batch);
}