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

package com.fullmeadalchemist.mustwatch.ui.log

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleTime
import com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.*
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.*
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*


class LogFormFragment : Fragment() {

    val viewModel: MainViewModel by sharedViewModel()

    private val timePickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received TIME_SET_EVENT")
            val hourOfDay = intent.getIntExtra(HOUR, 0)
            val minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0)
            if (viewModel.logEntry == null) {
                viewModel.logEntry = LogEntry()
            }
            viewModel.updateLogEntryTime(hourOfDay, minute)
            Timber.d("Time was set by user with TimePickerFragment to %s:%s", hourOfDay, minute)
            updateUiDateTime()
        }
    }
    private val datePickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received DATE_SET_EVENT")
            val year = intent.getIntExtra(DatePickerFragment.YEAR, 0)
            val month = intent.getIntExtra(DatePickerFragment.MONTH, 0)
            val dayOfMonth = intent.getIntExtra(DatePickerFragment.DAY_OF_MONTH, 0)

            viewModel.updateLogEntryDate(year, month, dayOfMonth)
            Timber.d("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year)
            updateUiDateTime()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = this.arguments
        val batchId = bundle!!.getLong(BATCH_ID, java.lang.Long.MIN_VALUE)
        if (batchId != java.lang.Long.MIN_VALUE) {
            if (viewModel.logEntry == null) {
                viewModel.logEntry = LogEntry()
            }
            viewModel.logEntry?.batchId = batchId
            viewModel.selectedBatchId = batchId
            Timber.i("Created LogEntry with Batch ID %d", batchId)

        } else {
            Timber.e("No Batch ID was received. Redirecting back to Batch list.")
//            navigationController.navigateToBatches()
        }

        updateUiDateTime()
        initClickListeners()
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(datePickerMessageReceiver,
                IntentFilter(DATE_SET_EVENT))
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(timePickerMessageReceiver,
                IntentFilter(TIME_SET_EVENT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.log_form_fragment, container, false)
    }

    private fun initClickListeners() {
        val dateField = activity!!.findViewById<TextView>(R.id.createDateDate)
        dateField?.setOnClickListener { _ ->
            Timber.i("Date was clicked!")
            val newFragment = DatePickerFragment()
            val args = Bundle()
            args.putInt(YEAR, viewModel.logEntry?.entryDate!!.get(Calendar.YEAR))
            args.putInt(MONTH, viewModel.logEntry?.entryDate!!.get(Calendar.MONTH))
            args.putInt(DAY_OF_MONTH, viewModel.logEntry?.entryDate!!.get(Calendar.DAY_OF_MONTH))
            newFragment.arguments = args
            newFragment.setTargetFragment(this, DATE_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        val timeField = activity!!.findViewById<TextView>(R.id.createDateTime)
        timeField?.setOnClickListener { _ ->
            Timber.i("Time was clicked!")
            val newFragment = TimePickerFragment()
            val args = Bundle()
            args.putInt(HOUR, viewModel.logEntry?.entryDate!!.get(Calendar.HOUR))
            args.putInt(MINUTE, viewModel.logEntry?.entryDate!!.get(Calendar.MINUTE))
            newFragment.arguments = args
            newFragment.setTargetFragment(this, TIME_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "timePicker")
        }

        val submitButton = activity!!.findViewById<Button>(R.id.button_submit)
        submitButton?.setOnClickListener { view ->
            Timber.i("Submit button clicked!")
            viewModel.logEntry?.let {
                val phTv = view.findViewById<TextView>(R.id.ph)
                if (phTv != null) {
                    it.acidity = toFloat(phTv.text.toString().trim { it <= ' ' })
                }

                val sgTv = view.findViewById<TextView>(R.id.sg)
                if (sgTv != null) {
                    it.sg = toFloat(sgTv.text.toString().trim { it <= ' ' })
                }

                val noteTv = activity!!.findViewById<TextView>(R.id.notes)
                if (noteTv != null) {
                    it.note = noteTv.text.toString().trim { it <= ' ' }
                }

                viewModel.addLogEntry(it)
            }
            if (viewModel.selectedBatchId == null) {
                // TODO: add metric event
                // TODO: add option for User to send error logs
                Timber.e("could not get the batch ID that this log entry is for")
                alert("Something went wrong when trying to save this log entry.") {
                    yesButton { Timber.w("yes button clicked") }
                    noButton { Timber.w("no button clicked") }
                }.show()

            } else {
                val bundle = Bundle()
                bundle.putLong(BATCH_ID, viewModel.selectedBatchId!!)
                view.findNavController().navigate(R.id.batchDetailFragment, bundle)
            }
        }
    }

    private fun updateUiDateTime() {
        viewModel.logEntry?.let {
            val timeField = activity!!.findViewById<TextView>(R.id.createDateTime)
            if (timeField != null) {
                timeField.text = calendarToLocaleTime(it.entryDate)
            } else {
                Timber.e("Could not find createDateTime in View")
            }

            val dateField = activity!!.findViewById<TextView>(R.id.createDateDate)
            if (dateField != null) {
                dateField.text = calendarToLocaleDate(it.entryDate)
            } else {
                Timber.e("Could not find createDateDate in View")
            }
        }
    }

    companion object {
        private val DATE_REQUEST_CODE = 1
        private val TIME_REQUEST_CODE = 2
    }
}
