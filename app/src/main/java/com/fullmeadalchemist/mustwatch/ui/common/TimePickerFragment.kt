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

package com.fullmeadalchemist.mustwatch.ui.common

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.text.format.DateFormat
import android.widget.TimePicker
import org.koin.android.viewmodel.ext.android.sharedViewModel

import java.util.Calendar

import timber.log.Timber


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val is24HourFormat = DateFormat.is24HourFormat(activity)
        return TimePickerDialog(activity, this, hour, minute, is24HourFormat)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Timber.d("Broadcasting %s with time: %s:%s", TIME_SET_EVENT, hourOfDay, minute)

        val intent = Intent(TIME_SET_EVENT)
        intent.putExtra(HOUR, hourOfDay)
        intent.putExtra(MINUTE, minute)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    companion object {
        val TIME_SET_EVENT = "TIME_SET_EVENT"
        val HOUR = "HOUR"
        val MINUTE = "MINUTE"
    }
}