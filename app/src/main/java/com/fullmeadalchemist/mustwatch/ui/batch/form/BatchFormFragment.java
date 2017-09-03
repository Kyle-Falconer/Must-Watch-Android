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
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.core.UnitMapper.toVolume;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToStringResource;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr;
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
import static systems.uom.common.Imperial.GALLON_UK;
import static systems.uom.common.Imperial.OUNCE_LIQUID;
import static systems.uom.common.USCustomary.FAHRENHEIT;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.GALLON_DRY;
import static systems.uom.common.USCustomary.GALLON_LIQUID;
import static systems.uom.common.USCustomary.LITER;
import static systems.uom.common.USCustomary.OUNCE;
import static systems.uom.common.USCustomary.POUND;
import static tec.units.ri.unit.Units.CELSIUS;
import static tec.units.ri.unit.Units.GRAM;
import static tec.units.ri.unit.Units.KILOGRAM;


public class BatchFormFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;
    BatchFormFragmentBinding dataBinding;
    Map<String, String> abbreviationMap = new HashMap<>();
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

        // FIXME: Mess. This should be moved somewhere else so other classes can use it
        abbreviationMap.put(getResources().getString(R.string.DEGREES_C), unitToTextAbbr(CELSIUS));
        abbreviationMap.put(getResources().getString(R.string.DEGREES_F), unitToTextAbbr(FAHRENHEIT));
        abbreviationMap.put(getResources().getString(R.string.LITER), unitToTextAbbr(LITER));
        abbreviationMap.put(getResources().getString(R.string.GALLON_LIQUID_US), unitToTextAbbr(GALLON_LIQUID));
        abbreviationMap.put(getResources().getString(R.string.GALLON_DRY_US), unitToTextAbbr(GALLON_DRY));
        abbreviationMap.put(getResources().getString(R.string.GALLON_LIQUID_UK), unitToTextAbbr(GALLON_UK));
        abbreviationMap.put(getResources().getString(R.string.OUNCE_LIQUID_US), unitToTextAbbr(FLUID_OUNCE));
        abbreviationMap.put(getResources().getString(R.string.OUNCE_LIQUID_UK), unitToTextAbbr(OUNCE_LIQUID));
        abbreviationMap.put(getResources().getString(R.string.GRAM), unitToTextAbbr(GRAM));
        abbreviationMap.put(getResources().getString(R.string.KILOGRAM), unitToTextAbbr(KILOGRAM));
        abbreviationMap.put(getResources().getString(R.string.OUNCE), unitToTextAbbr(OUNCE));
        abbreviationMap.put(getResources().getString(R.string.POUND), unitToTextAbbr(POUND));


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
                        if (batch.status != null) {
                            dataBinding.status.setText(batch.status.toString());
                        } else {
                            dataBinding.status.setText(BatchStatusEnum.PLANNING.toString());
                        }
                        updateUiDateTime();
                        updateSpinners(viewModel.batch);
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

                viewModel.batch.outputVolume = toVolume(
                        dataBinding.outputVolumeAmount.getText().toString(),
                        abbreviationMap.get(dataBinding.outputVolumeAmountUnit.getSelectedItem().toString()));

                viewModel.batch.status = BatchStatusEnum.fromString(dataBinding.status.getText().toString().trim());
                viewModel.batch.notes = dataBinding.notes.getText().toString().trim();

                if (FORM_MODE == MODES.CREATE) {
                    Log.d(TAG, "We are in CREATE mode.");
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.saveNewBatch().observe(this, savedBatchId -> {
                        if (savedBatchId != null) {
                            Log.i(TAG, String.format("Successfully saved Batch, which now has ID=%s", savedBatchId));
                            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(datePickerMessageReceiver);
                            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timePickerMessageReceiver);
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Saved batch!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Answers.getInstance().logCustom(new CustomEvent("Batch create success"));
                            navigationController.navigateFromAddBatch(savedBatchId);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent("Batch create failed"));
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Failed to save batch!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
                } else {
                    Log.d(TAG, String.format("We are in EDIT mode for batch #%s", viewModel.batch.id));
                    Log.d(TAG, String.format("Current batch state:\n%s", viewModel.batch.toString()));
                    viewModel.updateBatch();
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(datePickerMessageReceiver);
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timePickerMessageReceiver);
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Updated batch!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Answers.getInstance().logCustom(new CustomEvent("Batch edit success"));
                    navigationController.navigateFromEditBatch(viewModel.batch.id);
                }

            });
        } else {
            Log.e(TAG, "Cannot find submit button in view");
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void updateSpinners(Batch batch) {
        if (batch == null) {
            Log.e(TAG, "Could not update spinners because batch is null");
            return;
        }
        if (batch.outputVolume != null) {
            double volumeAmount = (double) batch.outputVolume.getValue();
            DecimalFormat f = new DecimalFormat("#.##");
            dataBinding.outputVolumeAmount.setText(f.format(volumeAmount));
            Spinner volSpin = dataBinding.outputVolumeAmountUnit;
            String unitString = getResources().getString(unitToStringResource(batch.outputVolume.getUnit()));
            volSpin.setSelection(getIndex(volSpin, unitString));

//            volSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                    Log.v(TAG, String.format("Volume spinner item selected: pos=%s, id=%s, string=%s", pos, id, volSpin.getItemAtPosition(pos).toString()));
//
//                    Object item = parent.getItemAtPosition(pos);
//                }
//
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });
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