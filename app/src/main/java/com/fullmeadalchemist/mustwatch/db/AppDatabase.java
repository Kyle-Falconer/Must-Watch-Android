package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Group;
import com.fullmeadalchemist.mustwatch.vo.GroupMembership;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;
import com.fullmeadalchemist.mustwatch.vo.User;


/**
 * Created by Kyle on 7/22/2017.
 */

@Database(entities = {User.class, Batch.class, LogEntry.class, Group.class, GroupMembership.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "mustwatch-db";
    /**
     * The only instance
     */
    private static AppDatabase dbInstance;

    /**
     * Gets the singleton instance of SampleDatabase.
     *
     * @param context The context.
     * @return The singleton instance of SampleDatabase.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ex")
                    .build();
        }
        return dbInstance;
    }

    public abstract UserDao userDao();

    public abstract BatchDao batchDao();

    public abstract LogEntryDao logEntryDao();

    public abstract GroupDao groupDao();
}