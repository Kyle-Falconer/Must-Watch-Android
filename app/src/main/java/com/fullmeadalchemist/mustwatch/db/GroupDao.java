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

/**
 * Created by Kyle on 7/22/2017.
 */
@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    List<Group> getAll();

    @Query("SELECT * FROM groups WHERE name LIKE :name LIMIT 1")
    LiveData<Group> findByName(String name);

    @Query("SELECT * FROM user "
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