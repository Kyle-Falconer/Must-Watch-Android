package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Kyle on 4/8/2017.
 */

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
