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

package com.fullmeadalchemist.mustwatch.core;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import timber.log.Timber;

public class ValueParsers {
    private static final String TAG = ValueParsers.class.getSimpleName();

    @NonNull
    public static Float toFloat(String value, float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NullPointerException | NumberFormatException e) {
            Timber.w("Failed to parse value \"%s\" to a Float", value);
            return defaultValue;
        }
    }

    @Nullable
    public static Float toFloat(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NullPointerException | NumberFormatException e) {
            Timber.w("Failed to parse value \"%s\" to a Float", value);
            return null;
        }
    }

    @NonNull
    public static Double toDouble(String value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            Timber.w("Failed to parse value \"%s\" to a Double", value);
            return defaultValue;
        }
    }

    @Nullable
    public static Double toDouble(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            Timber.w("Failed to parse value \"%s\" to a Double", value);
            return null;
        }
    }

    /**
     * https://stackoverflow.com/a/1590842/940217
     * @param l
     * @return
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

}
