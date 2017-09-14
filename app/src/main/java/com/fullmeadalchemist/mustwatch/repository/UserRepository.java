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
import android.arch.lifecycle.MutableLiveData;

import com.fullmeadalchemist.mustwatch.MustWatchPreferences;
import com.fullmeadalchemist.mustwatch.db.UserDao;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {
    private static final String TAG = UserRepository.class.getSimpleName();
    private final UserDao userDao;
    MustWatchPreferences prefs;
    private User user;

    @Inject
    public UserRepository(MustWatchPreferences prefs, UserDao userDao) {
        this.prefs = prefs;
        this.userDao = userDao;
    }

//    public Observable<Long> addUser2(User user) {
//        Log.d(TAG, "Adding user to db: " + user.toString());
//        MutableLiveData<Long> userLiveData = new MutableLiveData<>();
//        return Observable.fromCallable(() -> userDao.insert(user))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }


    public LiveData<Long> addUser(User user) {
        Timber.d("Adding user to db:\n%s", user.toString());
        MutableLiveData<Long> userLiveData = new MutableLiveData<>();
        Observable.fromCallable(() -> userDao.insert(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uid -> {
                    prefs.setCurrentUserId(uid);
                    userLiveData.postValue(uid);
                }, e -> {
                    Timber.e("Failed to add user", e);
                });
        return userLiveData;
    }

    public LiveData<User> getUser(long userId) {
        Timber.d("Getting user from User ID %d", userId);
        return userDao.load(userId);
    }

    public LiveData<Long> getCurrentUserId() {
        MutableLiveData<Long> userIdLiveData = new MutableLiveData<>();
        Long stored_id = prefs.getCurrentUserID();
        if (stored_id != null) {
            Timber.d("Got User ID %d from shared preferences as the current User ID.", stored_id);
            userIdLiveData.postValue(stored_id);
            return userIdLiveData;
        }

        if (this.user == null) {
            Timber.i("No User found locally. Preparing an Anonymous User.");
            User anon = new User(null, null);
            return addUser(anon);
        }
        prefs.setCurrentUserId(this.user.id);
        userIdLiveData.postValue(this.user.id);
        return userIdLiveData;
    }

//    public LiveData<User> getCurrentUserId2() {
//        Long stored_id = prefs.getCurrentUserID();
//        if (stored_id != null) {
//            Log.d(TAG, String.format("Got User ID %d from shared preferences as the current User ID.", stored_id));
//            return getUser(stored_id);
//        }
//
//        if (this.user == null) {
//            Log.i(TAG, "No User found locally. Preparing an Anonymous User.");
//            User anon = new User(null, null);
//            MutableLiveData<User> userLiveData = new MutableLiveData<>();
//            addUser2(anon).subscribe(id -> {
//                Log.i(TAG, String.format("Got ID %d for Anonymous user.", id));
//                anon.id = id;
//                userLiveData.setValue(anon);
//                setCurrentUser(userLiveData.getValue());
//            }, e -> {
//                Log.e(TAG, "failed to add Anonymous User to the  database");
//            });
//            return userLiveData;
//        }
//        MutableLiveData<User> userLiveData = new MutableLiveData<>();
//        userLiveData.setValue(this.user);
//        return userLiveData;
//    }

    private void setCurrentUser(User user) {
        Timber.d("Setting current User to: %s", user.toString());
        if (user.id != null) {
            prefs.setCurrentUserId(user.id);
        }
        this.user = user;
    }
}
