package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

/**
 * Created by Kyle on 7/22/2017.
 */
@Entity(tableName = "group_membership",
        primaryKeys = {"group_id", "user_id"},
        indices = {@Index(value = "user_id")},
        foreignKeys = {
                @ForeignKey(entity = Group.class,
                        parentColumns = "id",
                        childColumns = "group_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id")})
public class GroupMembership {

    @ColumnInfo(name = "group_id")
    public long groupId;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "access_read")
    public boolean accessRead;

    @ColumnInfo(name = "access_admin")
    public boolean accessAdmin;
}
