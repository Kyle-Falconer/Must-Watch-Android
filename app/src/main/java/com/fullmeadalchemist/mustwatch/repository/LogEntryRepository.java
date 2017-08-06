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

package com.fullmeadalchemist.mustwatch.repository;


import android.arch.lifecycle.LiveData;

import com.fullmeadalchemist.mustwatch.db.LogEntryDao;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LogEntryRepository {
    private static final String TAG = LogEntryRepository.class.getSimpleName();

    private final LogEntryDao logEntryDao;

    @Inject
    public LogEntryRepository(LogEntryDao logEntryDao) {
        this.logEntryDao = logEntryDao;
    }

    public LiveData<List<LogEntry>> getLogEntries(Long batchId){
        return logEntryDao.loadAllByBatchIds(batchId);
    }
}
