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

package com.fullmeadalchemist.mustwatch.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class FormatUtils {


    public static String calendarToLocaleDateTimeLong(Calendar c) {
        if (c == null) {
            return "null";
        }
        Date d = c.getTime();
        return dateToLocaleDateTimeLong(d);
    }

    public static String dateToLocaleDateTimeLong(Date d) {
        if (d == null) {
            return "null";
        }
        DateFormat timeInstance = DateFormat.getDateTimeInstance();
        return timeInstance.format(d);
    }
//
//    public static String calendarToLocaleDateTimeLong(Calendar c) {
//        if (c == null) {
//            return "null";
//        }
//        Date d = c.getTime();
//        return dateToLocaleDateTimeLong(d);
//    }

    public static String calendarToLocaleDate(Calendar c) {
        if (c == null) {
            return "null";
        }
        Date d = c.getTime();
        return dateToLocaleDate(d);
    }

    public static String dateToLocaleDate(Date d) {
        if (d == null) {
            return "null";
        }
        DateFormat timeInstance = DateFormat.getDateInstance(DateFormat.LONG);
        return timeInstance.format(d);
    }

    public static String calendarToLocaleTime(Calendar c) {
        if (c == null) {
            return "null";
        }
        Date d = c.getTime();
        return dateToLocaleTime(d);
    }

    public static String dateToLocaleTime(Date d) {
        if (d == null) {
            return "null";
        }
        DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT);
        return timeInstance.format(d);
    }
}
