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

package com.fullmeadalchemist.mustwatch.ui.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

import com.fullmeadalchemist.mustwatch.repository.UserRepository
import com.fullmeadalchemist.mustwatch.vo.User

import javax.inject.Inject

class UserProfileViewModel @Inject
constructor(private val userRepo: UserRepository) : ViewModel() {
    val currentUserId: LiveData<Long>


    init {
        this.currentUserId = userRepo.currentUserId
    }

    fun getUser(id: Long?): LiveData<User> {
        return userRepo.getUser(id!!)
    }

    companion object {
        fun create(activity: FragmentActivity): UserProfileViewModel {
            return ViewModelProviders.of(activity).get(UserProfileViewModel::class.java)
        }
    }
}
