package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    LiveData<User> findByEmail(String email);

    @Insert
    void insert(User user);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}