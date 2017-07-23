package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "groups")
public class Group {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;

}
