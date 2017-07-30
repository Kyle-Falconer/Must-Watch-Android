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


import android.arch.lifecycle.LiveData;

import com.fullmeadalchemist.mustwatch.db.UserDao;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {
    //private final Webservice webservice;
    private final UserDao userDao;

    @Inject
    public UserRepository(UserDao userDao) {
        //this.webservice = webservice;
        this.userDao = userDao;
    }

    public LiveData<User> getUser(String userId) {
        //refreshUser(userId);
        // return a LiveData directly from the database.
        return userDao.load(userId);
    }
}
