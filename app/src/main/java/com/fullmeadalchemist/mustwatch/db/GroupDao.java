package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.Group;

import java.util.List;

/**
 * Created by Kyle on 7/22/2017.
 */
@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    List<Group> getAll();

    @Query("SELECT * FROM groups WHERE id IN (:userIds)")
    List<Group> loadAllByIds(int[] userIds);

    @Insert
    void insert(Group group);

    @Insert
    void insertAll(Group... groups);

    @Delete
    void delete(Group group);
}