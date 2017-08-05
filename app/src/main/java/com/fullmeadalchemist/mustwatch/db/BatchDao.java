package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.List;

/**
 * Created by Kyle on 7/22/2017.
 */

@Dao
public interface BatchDao {
    @Query("SELECT * FROM batch")
    LiveData<List<Batch>> getAll();

    @Query("SELECT * FROM batch WHERE id = :batch_id LIMIT 1")
    LiveData<Batch> get(Long batch_id);

    @Query("SELECT * FROM batch WHERE user_id = :user_id")
    LiveData<List<Batch>> loadBatchesForUser(long user_id);

    @Query("SELECT * FROM log_entry "
            + "INNER JOIN batch on batch.id = log_entry.batch_id "
            + "WHERE batch.id = :batch_id")
    LiveData<List<LogEntry>> loadLogEntriesForBatch(long batch_id);

    @Insert
    Long insert(Batch batch);

    @Insert
    List<Long> insertAll(Batch... batches);

    @Insert
    void insert(LogEntry entry);

    @Insert
    void insertAll(LogEntry... entries);

    @Delete
    void delete(Batch batch);
}