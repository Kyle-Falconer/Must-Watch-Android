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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.Calendar;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchFormFragment extends LifecycleFragment implements Injectable,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private static final String TAG = BatchListFragment.class.getSimpleName();
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ;
    @Inject
    NavigationController navigationController;
    BatchFormFragmentBinding dataBinding;
    private MODES FORM_MODE;
    private BatchFormViewModel viewModel;

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
                    Log.i(TAG, String.format("Loaded Batch with ID %d:\n%s", batch.id, batch));
                    viewModel.batch = batch;
                    dataBinding.setBatch(batch);
                });
            }
        } else {
            Log.i(TAG, "No Batch ID was received. Acting as a Batch Creation form.");
            viewModel.batch = new Batch();
            viewModel.getCurrentUser().observe(this, user -> {
                if (user == null) {
                    Log.e(TAG, "Could not set the Batch User ID, since it's null?!");
                    return;
                }
                Log.d(TAG, String.format("Setting batch user ID to %s", user.id));
                viewModel.batch.userId = user.id;
            });
            dataBinding.setBatch(viewModel.batch);
        }


        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (viewModel.batch != null) {
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

        Button submitButton = getActivity().findViewById(R.id.button_submit);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Log.w(TAG, "Submit button clicked!");
                FORM_MODE = (viewModel.batch.id == null) ? MODES.CREATE : MODES.EDIT;
                viewModel.batch.targetSgStarting = dataBinding.getBatch().targetSgStarting;
                viewModel.batch.targetSgFinal = dataBinding.getBatch().targetSgFinal;
                viewModel.batch.targetABV = dataBinding.getBatch().targetABV;
                viewModel.batch.startingPh = dataBinding.getBatch().startingPh;
                viewModel.batch.startingTemp = dataBinding.getBatch().startingTemp;
                viewModel.batch.outputVolume = dataBinding.getBatch().outputVolume;
                viewModel.batch.status = dataBinding.getBatch().status;
                viewModel.batch.createDate = dataBinding.getBatch().createDate;
                viewModel.batch.notes = dataBinding.getBatch().notes;
                if (FORM_MODE == MODES.CREATE) {
                    Log.d(TAG, "We are in CREATE mode.");
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.saveNewBatch();
                } else {
                    Log.d(TAG, String.format("We are in EDIT mode for batch #%s", viewModel.batch.id));
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.updateBatch();
                }
                navigationController.navigateToBatches();
            });
        } else {
            Log.e(TAG, "Cannot find submit button in view");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        viewModel.batch.createDate.set(Calendar.HOUR, hourOfDay);
        viewModel.batch.createDate.set(Calendar.MINUTE, minute);
        Log.d(TAG, String.format("Time was set by user with TimePickerFragment to %s:%s", hourOfDay, minute));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        viewModel.batch.createDate.set(Calendar.YEAR, year);
        viewModel.batch.createDate.set(Calendar.MONTH, month);
        viewModel.batch.createDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Log.d(TAG, String.format("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year));
    }

    private enum MODES {CREATE, EDIT}
}