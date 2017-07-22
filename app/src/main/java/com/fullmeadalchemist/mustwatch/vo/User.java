package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(indices = {@Index(value = "email", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("id: %s \nname: %s \nemail: %s \n#", id, name, email);
    }
}
