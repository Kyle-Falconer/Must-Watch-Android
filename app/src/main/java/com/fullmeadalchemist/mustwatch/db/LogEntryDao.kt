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
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import com.fullmeadalchemist.mustwatch.vo.LogEntry

@Dao
interface LogEntryDao {
    @get:Query("SELECT * FROM log_entry")
    val all: List<LogEntry>

    @Query("SELECT * FROM log_entry WHERE batch_id = :batch_id")
    fun loadAllByBatchIds(batch_id: Long?): LiveData<List<LogEntry>>

    @Insert
    fun insert(entry: LogEntry): Long?

    @Insert
    fun insertAll(vararg entries: LogEntry)

    @Delete
    fun delete(entry: LogEntry)
}
