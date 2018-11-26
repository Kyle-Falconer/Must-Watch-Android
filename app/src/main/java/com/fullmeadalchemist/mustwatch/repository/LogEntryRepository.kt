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
import com.fullmeadalchemist.mustwatch.db.LogEntryDao
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface LogEntryRepository {
    fun addLogEntry(logEntry: LogEntry)

    fun getLogEntries(batchId: Long?): LiveData<List<LogEntry>>
}

class LogEntryRepositoryImpl(private val database: AppDatabase) : LogEntryRepository {

    override fun addLogEntry(logEntry: LogEntry) {
        Timber.d("Adding LogEntry to db: %s", logEntry.toString())
        Observable.fromCallable<Any> { database.logEntryDao().insert(logEntry) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun getLogEntries(batchId: Long?): LiveData<List<LogEntry>> {
        return database.logEntryDao().loadAllByBatchIds(batchId)
    }
}
