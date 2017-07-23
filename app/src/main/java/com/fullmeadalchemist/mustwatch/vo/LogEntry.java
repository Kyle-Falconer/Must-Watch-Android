package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kyle on 7/22/2017.
 */

@Entity(tableName = "log_entry",
        indices = {@Index(value = "batch_id")},
        foreignKeys = @ForeignKey(entity = Batch.class,
                parentColumns = "id",
                childColumns = "batch_id"))
public class LogEntry {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "batch_id")
    public long batchId;

    @ColumnInfo(name = "acidity")
    public Double acidity;

    @ColumnInfo(name = "note")
    public String note;
}