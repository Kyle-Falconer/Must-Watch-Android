package com.fullmeadalchemist.mustwatch.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class FormatUtils {
    public static String dateToLocaleDateLong(Date d) {
        if (d == null) {
            return "null";
        }
        DateFormat timeInstance = DateFormat.getDateInstance(DateFormat.LONG);
        return timeInstance.format(d);
    }

    public static String calendarToLocaleDateLong(Calendar c) {
        if (c == null) {
            return "null";
        }
        Date time_to_set = c.getTime();
        DateFormat timeInstance = DateFormat.getDateInstance(DateFormat.LONG);
        return timeInstance.format(time_to_set);
    }
}
