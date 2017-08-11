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

package com.fullmeadalchemist.mustwatch.binding;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDateTimeLong;


public class BindingUtils {
    private static final String TAG = BindingUtils.class.getSimpleName();

    @BindingAdapter("android:text")
    public static void setFloat(TextView view, float value) {
        if (Float.isNaN(value)) view.setText("");
        else view.setText(String.valueOf(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static float getFloat(TextView view) {
        String num = view.getText().toString();
        if (num.isEmpty()) return 0.0F;
        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException e) {
            return 0.0F;
        }
    }

    @BindingAdapter("android:text")
    public static void setCalendar(TextView view, Calendar value) {
        if (value == null) view.setText("");
        else view.setText(calendarToLocaleDateTimeLong(value));
    }

    @Nullable
    @InverseBindingAdapter(attribute = "android:text")
    public static Calendar getCalendar(TextView view) {
        String val = view.getText().toString();
        if (val.isEmpty()) return null;
        try {
            Calendar gregorianCalendar = GregorianCalendar.getInstance();
            Log.e(TAG, String.format("Not yet implemented. Need to parse value %s to Calendar", val));
            // FIXME
            return gregorianCalendar;
        } catch (Exception e) {
            Log.e(TAG, String.format("Error while trying to parse Calendar with value %s", val));
            return null;
        }
    }
}