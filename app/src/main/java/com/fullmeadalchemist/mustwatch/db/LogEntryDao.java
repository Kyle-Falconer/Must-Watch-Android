package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.List;

/**
 * Created by Kyle on 7/22/2017.
 */
@Dao
interface LogEntryDao {
    @Query("SELECT * FROM log_entry")
    List<LogEntry> getAll();

    @Query("SELECT * FROM log_entry WHERE batch_id = :batch_id")
    List<LogEntry> loadAllByBatchIds(Long batch_id);

    @Insert
    void insert(LogEntry entry);

    @Insert
    void insertAll(LogEntry... entries);

    @Delete
    void delete(LogEntry entry);
}
