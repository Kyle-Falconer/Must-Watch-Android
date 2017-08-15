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

package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;


@Entity(tableName = "group_membership",
        primaryKeys = {"group_id", "user_id"},
        indices = {@Index(value = "user_id")},
        foreignKeys = {
                @ForeignKey(entity = Group.class,
                        parentColumns = "id",
                        childColumns = "group_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id")})
public class GroupMembership {

    @ColumnInfo(name = "group_id")
    public long groupId;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "access_read")
    public boolean accessRead;

    @ColumnInfo(name = "access_admin")
    public boolean accessAdmin;
}
