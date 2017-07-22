package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.List;

/**
 * Created by Kyle on 7/22/2017.
 */

@Dao
public interface BatchDao {
    @Query("SELECT * FROM batch")
    List<Batch> getAll();

    @Query("SELECT * FROM batch WHERE id IN (:batchIds)")
    List<Batch> loadAllByIds(int[] batchIds);

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    public LiveData<List<Batch>> loadBatchesForUser(long user_id);

    @Insert
    void insert(Batch batch);

    @Insert
    void insertAll(Batch... batches);

    @Delete
    void delete(Batch batch);
}