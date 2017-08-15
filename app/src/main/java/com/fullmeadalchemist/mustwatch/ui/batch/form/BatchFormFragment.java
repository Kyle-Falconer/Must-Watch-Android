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

package com.fullmeadalchemist.mustwatch.ui.batch.form;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.Calendar;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.DATE_SET_EVENT;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.DAY_OF_MONTH;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.MONTH;
import static com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.YEAR;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.HOUR;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.MINUTE;
import static com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.TIME_SET_EVENT;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleTime;
import static com.fullmeadalchemist.mustwatch.util.ValueParsers.toFloat;
import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchFormFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;
    BatchFormFragmentBinding dataBinding;
    private MODES FORM_MODE;
    private BatchFormViewModel viewModel;
    private BroadcastReceiver timePickerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received TIME_SET_EVENT");
            Integer hourOfDay = intent.getIntExtra(HOUR, 0);
            Integer minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0);
            viewModel.batch.createDate.set(Calendar.HOUR, hourOfDay);
            viewModel.batch.createDate.set(Calendar.MINUTE, minute);
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
            viewModel.batch.createDate.set(Calendar.YEAR, year);
            viewModel.batch.createDate.set(Calendar.MONTH, month);
            viewModel.batch.createDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d(TAG, String.format("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year));
            updateUiDateTime();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.batch_form_fragment,
                container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchFormViewModel.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Log.i(TAG, "Got Batch ID %d from the NavigationController. Acting as a Batch Editor.");

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            if (batchId != Long.MIN_VALUE) {
                viewModel.getBatch(batchId).observe(this, batch -> {
                    if (batch != null) {
                        Log.i(TAG, String.format("Loaded Batch with ID %d:\n%s", batch.id, batch));
                        viewModel.batch = batch;
                        dataBinding.setBatch(batch);
                        updateUiDateTime();
                    } else {
                        Log.e(TAG, "Got a null Batch!");
                    }
                });
            }
        } else {
            Log.i(TAG, "No Batch ID was received. Acting as a Batch Creation form.");
            viewModel.batch = new Batch();
            viewModel.batch.createDate = Calendar.getInstance();
            viewModel.getCurrentUserId().observe(this, userId -> {
                if (userId == null) {
                    Log.e(TAG, "Could not set the Batch User ID, since it's null?!");
                    return;
                }
                Log.d(TAG, String.format("Setting batch user ID to %s", userId));
                viewModel.batch.userId = userId;
            });
            dataBinding.setBatch(viewModel.batch);
        }
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (viewModel.batch != null && viewModel.batch.id != null) {
            // We're in "edit" mode, so set the title accordingly
            FORM_MODE = MODES.EDIT;
            TextView formTitle = getActivity().findViewById(R.id.batchFormTitleTV);
            if (formTitle != null) {
                String title = getResources().getString(R.string.edit_batch_title);
                title = String.format(title, viewModel.batch.id);
                formTitle.setText(title);
            }
        } else {
            FORM_MODE = MODES.CREATE;
        }
        updateUiDateTime();

        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setOnClickListener(v -> {
                Log.i(TAG, "Date was clicked!");
                DialogFragment newFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt(YEAR, viewModel.batch.createDate.get(Calendar.YEAR));
                args.putInt(MONTH, viewModel.batch.createDate.get(Calendar.MONTH));
                args.putInt(DAY_OF_MONTH, viewModel.batch.createDate.get(Calendar.DAY_OF_MONTH));
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
                args.putInt(HOUR, viewModel.batch.createDate.get(Calendar.HOUR));
                args.putInt(MINUTE, viewModel.batch.createDate.get(Calendar.MINUTE));
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, TIME_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            });
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(datePickerMessageReceiver,
                new IntentFilter(DATE_SET_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(timePickerMessageReceiver,
                new IntentFilter(TIME_SET_EVENT));


        Button submitButton = getActivity().findViewById(R.id.button_submit);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Log.i(TAG, "Submit button clicked!");
                FORM_MODE = (viewModel.batch.id == null) ? MODES.CREATE : MODES.EDIT;
                viewModel.batch.name = dataBinding.name.getText().toString().trim();
                viewModel.batch.targetSgStarting = toFloat(dataBinding.targetSgStarting.getText().toString().trim());
                viewModel.batch.targetSgFinal = toFloat(dataBinding.targetSgFinal.getText().toString().trim());
                viewModel.batch.targetABV = toFloat(dataBinding.targetABV.getText().toString().trim());
                viewModel.batch.startingPh = toFloat(dataBinding.startingPh.getText().toString().trim());
                viewModel.batch.startingTemp = toFloat(dataBinding.startingTemp.getText().toString().trim());
                viewModel.batch.outputVolume = toFloat(dataBinding.outputVolume.getText().toString().trim());
                viewModel.batch.status = dataBinding.status.getText().toString().trim();
                viewModel.batch.notes = dataBinding.notes.getText().toString().trim();
                if (FORM_MODE == MODES.CREATE) {
                    Log.d(TAG, "We are in CREATE mode.");
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.saveNewBatch();
                } else {
                    Log.d(TAG, String.format("We are in EDIT mode for batch #%s", viewModel.batch.id));
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.updateBatch();
                }
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(datePickerMessageReceiver);
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timePickerMessageReceiver);
                navigationController.navigateToBatches();
            });
        } else {
            Log.e(TAG, "Cannot find submit button in view");
        }
    }

    private void updateUiDateTime() {
        if (viewModel.batch == null) {
            return;
        }
        TextView timeField = getActivity().findViewById(R.id.createDateTime);
        if (timeField != null) {
            timeField.setText(calendarToLocaleTime(viewModel.batch.createDate));
        } else {
            Log.e(TAG, "Could not find createDateTime in View");
        }

        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setText(calendarToLocaleDate(viewModel.batch.createDate));
        } else {
            Log.e(TAG, "Could not find createDateDate in View");
        }
    }

    private enum MODES {CREATE, EDIT}
}