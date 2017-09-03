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

package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.Group;
import com.fullmeadalchemist.mustwatch.vo.GroupMembership;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.List;


@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    List<Group> getAll();

    @Query("SELECT * FROM groups WHERE name LIKE :name LIMIT 1")
    LiveData<Group> findByName(String name);

    @Query("SELECT id, name, email FROM user "
            + "INNER JOIN group_membership on group_membership.user_id = user.id "
            + "WHERE group_membership.group_id = :groupId")
    LiveData<List<User>> loadAllMembersInGroup(Long groupId);

    @Insert
    void insert(Group group);

    @Insert
    void insert(GroupMembership membership);

    @Insert
    void insertAll(Group... groups);

    @Delete
    void delete(Group group);

}