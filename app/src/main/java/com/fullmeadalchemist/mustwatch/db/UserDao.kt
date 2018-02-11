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

import com.fullmeadalchemist.mustwatch.vo.User

@Dao
interface UserDao {

    @get:Query("SELECT * FROM user")
    val all: List<User>

    @Query("SELECT * FROM user WHERE id = :userId LIMIT 1")
    fun load(userId: Long): LiveData<User>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    fun findByEmail(email: String): LiveData<User>

    @Insert
    fun insert(user: User): Long?

    @Insert
    fun insertAll(vararg users: User)

    @Insert
    fun insertAll(users: List<User>)

    @Delete
    fun delete(user: User)
}
