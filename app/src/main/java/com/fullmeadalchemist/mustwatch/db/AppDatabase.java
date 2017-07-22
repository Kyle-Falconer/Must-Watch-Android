package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;


/**
 * Created by Kyle on 7/22/2017.
 */

@Database(entities = {User.class, Batch.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract BatchDao batchDao();
}