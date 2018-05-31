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
import android.arch.lifecycle.MutableLiveData
import com.fullmeadalchemist.mustwatch.core.MustWatchPreferences
import com.fullmeadalchemist.mustwatch.db.UserDao
import com.fullmeadalchemist.mustwatch.vo.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class UserRepository {

    @Inject
    lateinit var prefs: MustWatchPreferences
    @Inject
    lateinit var userDao: UserDao

    private var user: User? = null

    val currentUserId: LiveData<Long>
        get() {
            val userIdLiveData = MutableLiveData<Long>()
            val stored_id = prefs.getCurrentUserId()
            if (stored_id != null) {
                Timber.d("Got User ID %d from shared preferences as the current User ID.", stored_id)
                userIdLiveData.postValue(stored_id)
                return userIdLiveData
            }

            if (this.user == null) {
                Timber.i("No User found locally. Preparing an Anonymous User.")
                val anon = User(null, null)
                return addUser(anon)
            }
            this.user?.let {
                prefs.setCurrentUserId(it.id)
                userIdLiveData.postValue(it.id)
            }

            return userIdLiveData

        }

    //    public Observable<Long> addUser2(User user) {
    //        Log.d(TAG, "Adding user to db: " + user.toString());
    //        MutableLiveData<Long> userLiveData = new MutableLiveData<>();
    //        return Observable.fromCallable(() -> userDao.insert(user))
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread());
    //    }


    fun addUser(user: User): LiveData<Long> {
        Timber.d("Adding user to db:\n%s", user.toString())
        val userLiveData = MutableLiveData<Long>()
        Observable.fromCallable<Any> { userDao.insert(user) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uid ->
                    val uid_value = uid as Long
                    prefs.setCurrentUserId(uid_value)
                    userLiveData.postValue(uid_value)
                }, { e ->
                    Timber.e(e, "Failed to add user")
                })
        return userLiveData
    }

    fun getUser(userId: Long): LiveData<User> {
        Timber.d("Getting user from User ID %d", userId)
        return userDao.load(userId)
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

    private fun setCurrentUser(user: User) {
        Timber.d("Setting current User to: %s", user.toString())
        user.id?.let {
            prefs.setCurrentUserId(it)
        }
        this.user = user
    }
}
