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

package com.fullmeadalchemist.mustwatch.ui.log.form;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment;

import java.util.Calendar;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.DATE_SET_EVENT;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.DAY_OF_MONTH;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.MONTH;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.YEAR;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.HOUR;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.MINUTE;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.TIME_SET_EVENT;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleTime;
import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class LogFormFragment extends Fragment implements Injectable {

    private static final String TAG = LogFormFragment.class.getSimpleName();
    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    private Long batchId;
    private LogFormViewModel viewModel;
    private BroadcastReceiver timePickerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received TIME_SET_EVENT");
            Integer hourOfDay = intent.getIntExtra(HOUR, 0);
            Integer minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0);
            viewModel.logEntry.entryDate.set(Calendar.HOUR, hourOfDay);
            viewModel.logEntry.entryDate.set(Calendar.MINUTE, minute);
            Log.d(TAG, String.format("Time was set by user with TimePickerFragment to %s:%s", hourOfDay, minute));
            updateUiDateTime();
        }
    };
    private BroadcastReceiver datePickerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received DATE_SET_EVENT");
            Integer year = intent.getIntExtra(DatePickerFragment.YEAR, 0);
            Integer month = intent.getIntExtra(DatePickerFragment.MONTH, 0);
            Integer dayOfMonth = intent.getIntExtra(DatePickerFragment.DAY_OF_MONTH, 0);
            viewModel.logEntry.entryDate.set(Calendar.YEAR, year);
            viewModel.logEntry.entryDate.set(Calendar.MONTH, month);
            viewModel.logEntry.entryDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d(TAG, String.format("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year));
            updateUiDateTime();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LogFormViewModel.class);

        Bundle bundle = this.getArguments();
        long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
        if (batchId != Long.MIN_VALUE) {
            viewModel.logEntry.batchId = batchId;
            this.batchId = batchId;
            Log.i(TAG, String.format("Created LogEntry with Batch ID %d", batchId));

        } else {
            Log.e(TAG, "No Batch ID was received. Redirecting back to Batch list.");
            navigationController.navigateToBatches();
        }

        updateUiDateTime();
        initClickListeners();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(datePickerMessageReceiver,
                new IntentFilter(DATE_SET_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(timePickerMessageReceiver,
                new IntentFilter(TIME_SET_EVENT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_form_fragment, container, false);
    }

    private void initClickListeners() {
        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setOnClickListener(v -> {
                Log.i(TAG, "Date was clicked!");
                DialogFragment newFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt(YEAR, viewModel.logEntry.entryDate.get(Calendar.YEAR));
                args.putInt(MONTH, viewModel.logEntry.entryDate.get(Calendar.MONTH));
                args.putInt(DAY_OF_MONTH, viewModel.logEntry.entryDate.get(Calendar.DAY_OF_MONTH));
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, DATE_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            });
        }

        TextView timeField = getActivity().findViewById(R.id.createDateTime);
        if (timeField != null) {
            timeField.setOnClickListener(v -> {
                Log.i(TAG, "Time was clicked!");
                DialogFragment newFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt(HOUR, viewModel.logEntry.entryDate.get(Calendar.HOUR));
                args.putInt(MINUTE, viewModel.logEntry.entryDate.get(Calendar.MINUTE));
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, TIME_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            });
        }

        Button submitButton = getActivity().findViewById(R.id.button_submit);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Log.i(TAG, "Submit button clicked!");

                TextView phTv = getActivity().findViewById(R.id.ph);
                if (phTv != null) {
                    viewModel.logEntry.acidity = toFloat(phTv.getText().toString().trim());
                }

                TextView sgTv = getActivity().findViewById(R.id.sg);
                if (sgTv != null) {
                    viewModel.logEntry.sg = toFloat(sgTv.getText().toString().trim());
                }

                TextView noteTv = getActivity().findViewById(R.id.notes);
                if (noteTv != null) {
                    viewModel.logEntry.note = noteTv.getText().toString().trim();
                }

                viewModel.saveNewLogEntry();
                navigationController.navigateToBatchDetail(this.batchId);
            });
        }
    }

    private void updateUiDateTime() {
        if (viewModel.logEntry == null) {
            return;
        }
        TextView timeField = getActivity().findViewById(R.id.createDateTime);
        if (timeField != null) {
            timeField.setText(calendarToLocaleTime(viewModel.logEntry.entryDate));
        } else {
            Log.e(TAG, "Could not find createDateTime in View");
        }

        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setText(calendarToLocaleDate(viewModel.logEntry.entryDate));
        } else {
            Log.e(TAG, "Could not find createDateDate in View");
        }
    }
}
