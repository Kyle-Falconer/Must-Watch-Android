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

import com.fullmeadalchemist.mustwatch.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import tec.units.ri.quantity.Quantities;

import static com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDateTimeLong;


public class BindingUtils {
    private static final String TAG = BindingUtils.class.getSimpleName();

    @BindingAdapter("android:text")
    public static void setFloat(TextView view, float value) {
        if (Float.isNaN(value)) {
            view.setText("");
        } else {
            DecimalFormat f = new DecimalFormat("#.##");
            view.setText(f.format(value));
        }
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
    public static void setDouble(TextView view, double value) {
        if (Double.isNaN(value)) {
            view.setText("");
        } else {
            DecimalFormat f = new DecimalFormat("#.##");
            view.setText(f.format(value));
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static double getDouble(TextView view) {
        String num = view.getText().toString();
        if (num.isEmpty()) return 0.0d;
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }

    @BindingAdapter("android:text")
    public static void setVolume(TextView view, Quantity<Volume> volumeQuantity) {
        if (volumeQuantity == null) {
            view.setText(R.string.none);
        } else {
            double volumeValue = volumeQuantity.getValue().doubleValue();

            if (volumeValue < 0.01) {
                view.setText("-");
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                view.setText(String.format("%s %s",
                        decimalFormat.format(volumeValue),
                        volumeQuantity.getUnit()));
            }
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Quantity<Volume> getVolume(TextView view) {
        // FIXME: make safer?
        String num = view.getText().toString();
        Quantity<Volume> vol = (Quantity<Volume>) Quantities.getQuantity(num);
        return vol;
    }

    @BindingAdapter("android:text")
    public static void setMass(TextView view, Quantity<Mass> massQuantity) {
        if (massQuantity == null) {
            view.setText(R.string.none);
        } else {
            double massValue = massQuantity.getValue().doubleValue();

            if (massValue < 0.01) {
                view.setText("-");
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                view.setText(String.format("%s %s",
                        decimalFormat.format(massValue),
                        massQuantity.getUnit()));
            }
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Quantity<Mass> getMass(TextView view) {
        // FIXME: make safer?
        String num = view.getText().toString();
        Quantity<Mass> mass = (Quantity<Mass>) Quantities.getQuantity(num);
        return mass;
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