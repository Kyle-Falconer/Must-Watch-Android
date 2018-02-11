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

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(indices = arrayOf(Index(value = "email", unique = true)))
class User(@field:ColumnInfo(name = "name")
           var name: String?, @field:ColumnInfo(name = "email")
           var email: String?) {

    @Expose
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = Long.MIN_VALUE

    val isAnon: Boolean
        @Ignore
        get() = id != Long.MIN_VALUE && name == null && email == null

    override fun toString(): String {
        return if (isAnon) {
            String.format("id: %s, Anonymous", id)
        } else {
            String.format("id: %s \nname: %s \nemail: %s \n", id, name, email)
        }
    }
}
