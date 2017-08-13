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

import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.List;

/**
 * Created by Kyle on 7/22/2017.
 */
@Dao
public interface LogEntryDao {
    @Query("SELECT * FROM log_entry")
    List<LogEntry> getAll();

    @Query("SELECT * FROM log_entry WHERE batch_id = :batch_id")
    LiveData<List<LogEntry>> loadAllByBatchIds(Long batch_id);

    @Insert
    Long insert(LogEntry entry);

    @Insert
    void insertAll(LogEntry... entries);

    @Delete
    void delete(LogEntry entry);
}
