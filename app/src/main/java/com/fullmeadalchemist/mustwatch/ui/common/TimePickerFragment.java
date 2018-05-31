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

package com.fullmeadalchemist.mustwatch.ui.common;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import timber.log.Timber;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TIME_SET_EVENT = "TIME_SET_EVENT";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());
        return new TimePickerDialog(getActivity(), this, hour, minute, is24HourFormat);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Timber.d("Broadcasting %s with time: %s:%s", TIME_SET_EVENT, hourOfDay, minute);

        Intent intent = new Intent(TIME_SET_EVENT);
        intent.putExtra(HOUR, hourOfDay);
        intent.putExtra(MINUTE, minute);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}