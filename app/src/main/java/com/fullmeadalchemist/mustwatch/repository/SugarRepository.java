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
import android.util.Log;

import com.fullmeadalchemist.mustwatch.db.SugarDao;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;
import com.fullmeadalchemist.mustwatch.vo.Sugar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class SugarRepository {
    private static final String TAG = SugarRepository.class.getSimpleName();

    private final SugarDao sugarDao;

    @Inject
    public SugarRepository(SugarDao sugarDao) {
        this.sugarDao = sugarDao;
    }

    public void addSugars(List<Sugar> sugars) {
        Log.d(TAG, String.format("Adding %s Sugar objects to the db", sugars.size()));
        Observable.fromCallable(() -> sugarDao.insertAll(sugars.toArray(new Sugar[sugars.size()])))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void addSugars(Sugar[] sugars) {
        Log.d(TAG, String.format("Adding %s Sugar objects to the db", sugars.length));
        Observable.fromCallable(() -> sugarDao.insertAll(sugars))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public LiveData<List<LogEntry>> getSugarEntries() {
        return sugarDao.getAll();
    }
}