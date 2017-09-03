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
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recipe",
        indices = {
                @Index(value = "creator_user_id"),
                @Index(value = "owner_user_id"),
                @Index(value = "owner_group_id")
        },
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "creator_user_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "owner_user_id"),
                @ForeignKey(entity = Group.class,
                        parentColumns = "id",
                        childColumns = "owner_group_id")
        })
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "creator_user_id")
    public long creatorUserId;

    @ColumnInfo(name = "owner_user_id")
    public long ownerUserId;

    @ColumnInfo(name = "owner_group_id")
    public long ownerGroupId;

    @ColumnInfo(name = "public_readable")
    public boolean publicReadable;

    @ColumnInfo(name = "carbonation")
    public Float carbonation;
}
