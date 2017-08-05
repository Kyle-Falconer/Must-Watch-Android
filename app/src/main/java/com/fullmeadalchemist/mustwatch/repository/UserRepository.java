/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.repository;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fullmeadalchemist.mustwatch.db.UserDao;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.fullmeadalchemist.mustwatch.MustWatchApp.MUST_WATCH_SHARED_PREFS;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {
    private static final String TAG = UserRepository.class.getSimpleName();
    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    private final UserDao userDao;
    private final Application app;
    private User user;


    @Inject
    public UserRepository(Application app, UserDao userDao) {
        //this.webservice = webservice;
        this.app = app;
        this.userDao = userDao;
    }

    public Observable<Long> addUser(User user) {
        Log.d(TAG, "Adding user to db: " + user.toString());
        return Observable.fromCallable(() -> userDao.insert(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public LiveData<User> getUser(long userId) {
        Log.d(TAG, String.format("Getting user from User ID %d", userId));
        return userDao.load(userId);
    }

    public LiveData<User> getCurrentUser() {
        // FIXME: pull this from persistent storage.
        SharedPreferences pSharedPref = app.getSharedPreferences(MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            Long stored_id = pSharedPref.getLong(CURRENT_USER_ID, Long.MIN_VALUE);
            if (stored_id != Long.MIN_VALUE) {
                Log.d(TAG, String.format("Got User ID %d from shared preferences as the current User ID.", stored_id));
                return getUser(stored_id);
            } else {
                Log.d(TAG, "Found no User ID in shared preferences.");
            }
        }
        if (this.user == null) {
            Log.i(TAG, "No User found locally. Preparing an Anonymous User.");
            User anon = new User(null, null);
            MutableLiveData<User> userLiveData = new MutableLiveData<>();
            addUser(anon).subscribe(id -> {
                Log.i(TAG, String.format("Got ID %d for Anonymous user.", id));
                anon.id = id;
                userLiveData.setValue(anon);
                setCurrentUser(userLiveData.getValue());
            }, e -> {
                Log.e(TAG, "failed to add Anonymous User to the  database");
            });
            return userLiveData;
        }
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        userLiveData.setValue(this.user);
        return userLiveData;
    }

    private void setCurrentUser(User user) {
        Log.d(TAG, "Setting current User to: " + user.toString());
        if (user.id != null) {
            SharedPreferences pSharedPref = app.getSharedPreferences(MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE);
            if (pSharedPref != null) {
                SharedPreferences.Editor editor = pSharedPref.edit();
                //editor.remove(CURRENT_USER_ID).commit();
                editor.putLong(CURRENT_USER_ID, user.id).apply();
                Log.d(TAG, String.format("Stored User ID %d in shared preferences as the current User ID.", user.id));
            }
        }
        this.user = user;
    }
}
