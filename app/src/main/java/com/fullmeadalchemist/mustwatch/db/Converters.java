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
import android.text.TextUtils;
import android.util.Log;

import com.fullmeadalchemist.mustwatch.vo.Batch;

import org.jscience.physics.amount.Amount;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.measure.quantity.Volume;
import javax.measure.unit.Unit;

import static javax.measure.unit.NonSI.GALLON_LIQUID_US;
import static javax.measure.unit.NonSI.LITER;
import static javax.measure.unit.NonSI.OUNCE_LIQUID_US;


public class Converters {

    private static final String TAG = Converters.class.getSimpleName();
    private static final String SEPARATOR = " ";

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
        if (calendar == null) {
            return null;
        }
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime().getTime();
    }

    @TypeConverter
    public static Calendar timestampToCalendar(Long value) {
        if (value == null) {
            return null;
        }
        Date d = new Date(value);
        Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        gregorianCalendar.setTime(d);
        return gregorianCalendar;
    }


    @TypeConverter
    public static Amount<Volume> fromVolumeText(String volText) {
        if (TextUtils.isEmpty(volText)) {
            return null;
        }

        String[] valueTexts = TextUtils.split(volText.trim(), SEPARATOR);
        if (valueTexts.length != 2) {
            Log.e(TAG, String.format("Could not parse Amount<Volume> from \"%s\"", volText));
            return null;
        }

        Log.v(TAG, String.format("Parsing Amount<Volume> from string %s", volText));
        Double value = Double.parseDouble(valueTexts[0]);
        String unitText = valueTexts[1];
        Unit<Volume> unit;
        switch (unitText) {
            case "L":
                unit = LITER;
                break;
            case "gal":
                unit = GALLON_LIQUID_US;
                break;
            case "oz":
                unit = OUNCE_LIQUID_US;
                break;
            default:
                unit = LITER;
                break;
        }
        return Amount.valueOf(value, unit);
    }

    @TypeConverter
    public static String toVolumeText(Amount<Volume> volume) {
        if (volume == null) {
            return null;
        }
        return String.format("%s%s%s",
                volume.getEstimatedValue(),
                SEPARATOR,
                volume.getUnit().toString());
    }


    @TypeConverter
    public static Batch.BatchStatusEnum fromBatchStatusString(String status) {
        if (status == null) {
            return null;
        }
        return Batch.BatchStatusEnum.fromString(status);
    }

    @TypeConverter
    public static String toBatchStatusString(Batch.BatchStatusEnum statusEnum) {
        return (statusEnum == null) ? null : statusEnum.toString();
    }


}
