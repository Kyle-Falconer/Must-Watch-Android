package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "groups",
        indices = {@Index(value = {"name"}, unique = true)})
public class Group {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;

    public Group(String name) {
        this.name = name;
    }
}
