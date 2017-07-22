package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Kyle on 7/22/2017.
 */

@Entity(tableName = "batch",
        indices = {@Index(value = "user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id"))
public class Batch {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "target_sg_starting")
    public Double targetSgStarting;

    @ColumnInfo(name = "target_sg_final")
    public Double targetSgFinal;

    @ColumnInfo(name = "target_abv")
    public Double targetABV;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "create_date")
    public Date createDate;

}