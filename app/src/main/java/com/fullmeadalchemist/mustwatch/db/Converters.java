/*
 * Copyright (c) 2017 Full Mead Alchemist, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Long calendarToTimestamp(Calendar calendar) {
        if (calendar == null){
            return null;
        }
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime().getTime();
    }

    @TypeConverter
    public static Calendar timestampToCalendar(Long value) {
        if (value == null){
            return null;
        }
        Date d = new Date(value);
        Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        gregorianCalendar.setTime(d);
        return gregorianCalendar;
    }
}
