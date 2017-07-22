package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Group {

    @PrimaryKey
    public long id;
    public String name;
    public List<User> admins;
    public List<User> members;

    public Group() {
        this.name = null;
        this.admins = new ArrayList<>();
        this.members = new ArrayList<>();
    }

}
