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

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.widget.DatePicker

import java.util.Calendar

import timber.log.Timber

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of TimePickerDialog and return it
        return DatePickerDialog(activity!!, this, year, month, day)
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        Timber.d("Broadcasting %s with date: %s/%s/%s", DATE_SET_EVENT, dayOfMonth, month, year)

        val intent = Intent(DATE_SET_EVENT)
        intent.putExtra(YEAR, year)
        intent.putExtra(MONTH, month)
        intent.putExtra(DAY_OF_MONTH, dayOfMonth)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    companion object {
        const val DATE_SET_EVENT = "DATE_SET_EVENT"
        const val YEAR = "YEAR"
        const val MONTH = "MONTH"
        const val DAY_OF_MONTH = "DAY_OF_MONTH"
    }
}