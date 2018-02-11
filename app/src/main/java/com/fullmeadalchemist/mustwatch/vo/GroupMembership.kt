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

package com.fullmeadalchemist.mustwatch.vo

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = "group_membership", primaryKeys = arrayOf("group_id", "user_id"), indices = arrayOf(Index(value = "user_id")), foreignKeys = arrayOf(ForeignKey(entity = Group::class, parentColumns = arrayOf("id"), childColumns = arrayOf("group_id")), ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"))))
class GroupMembership {

    @Expose
    @NonNull
    @SerializedName("group_id")
    @ColumnInfo(name = "group_id")
    var id: Long = Long.MIN_VALUE

    @Expose
    @NonNull
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    var userId: Long = Long.MIN_VALUE

    @ColumnInfo(name = "access_read")
    var accessRead: Boolean = false

    @ColumnInfo(name = "access_admin")
    var accessAdmin: Boolean = false
}
