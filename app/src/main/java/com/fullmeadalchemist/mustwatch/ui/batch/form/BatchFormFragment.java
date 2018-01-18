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

import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView;
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.UnitMapper.toVolume;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToStringResource;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr;
import static com.fullmeadalchemist.mustwatch.core.ValueParsers.toDouble;
import static com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat;
import static com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.AMOUNT;
import static com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.INGREDIENT;
import static com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.INGREDIENT_SET_EVENT;
import static com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.INGREDIENT_TYPE;
import static com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.UNIT;
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
import static com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum.PLANNING;
import static com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.NUTRIENT;
import static com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.STABILIZER;
import static com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.SUGAR;
import static com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.YEAST;
import static com.fullmeadalchemist.mustwatch.vo.Recipe.RECIPE_ID;
import static systems.uom.common.Imperial.GALLON_UK;
import static systems.uom.common.Imperial.OUNCE_LIQUID;
import static systems.uom.common.USCustomary.FAHRENHEIT;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.GALLON_DRY;
import static systems.uom.common.USCustomary.GALLON_LIQUID;
import static systems.uom.common.USCustomary.LITER;
import static systems.uom.common.USCustomary.OUNCE;
import static systems.uom.common.USCustomary.POUND;
import static systems.uom.common.USCustomary.TEASPOON;
import static tec.units.ri.unit.Units.CELSIUS;
import static tec.units.ri.unit.Units.GRAM;
import static tec.units.ri.unit.Units.KILOGRAM;


public class BatchFormFragment extends Fragment implements Injectable {

    private static final int DATE_REQUEST_CODE = 1;
    private static final int TIME_REQUEST_CODE = 2;
    private static final int INGREDIENT_REQUEST_CODE = 3;

    private Activity activity;

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
            Timber.d("Received TIME_SET_EVENT");
            Integer hourOfDay = intent.getIntExtra(HOUR, 0);
            Integer minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0);
            viewModel.batch.createDate.set(Calendar.HOUR, hourOfDay);
            viewModel.batch.createDate.set(Calendar.MINUTE, minute);
            Timber.d("Time was set by user with TimePickerFragment to %s:%s", hourOfDay, minute);
            updateUiDateTime();
        }
    };
    private BroadcastReceiver datePickerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Received DATE_SET_EVENT");
            Integer year = intent.getIntExtra(DatePickerFragment.YEAR, 0);
            Integer month = intent.getIntExtra(DatePickerFragment.MONTH, 0);
            Integer dayOfMonth = intent.getIntExtra(DatePickerFragment.DAY_OF_MONTH, 0);
            viewModel.batch.createDate.set(Calendar.YEAR, year);
            viewModel.batch.createDate.set(Calendar.MONTH, month);
            viewModel.batch.createDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Timber.d("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year);
            updateUiDateTime();
        }
    };

    private BroadcastReceiver ingredientPickerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activity == null){
                // FIXME: the app will crash if this is true.
                Timber.e("Application Context is null");
            }
            Timber.d("Received INGREDIENT_SET_EVENT");
            String ingredientString = intent.getStringExtra(INGREDIENT);
            float amount = intent.getFloatExtra(AMOUNT, 0.0f);
            String unitString = intent.getStringExtra(UNIT);
            Timber.v("Got values from AddIngredientDialog: %s %s %s", ingredientString, amount, unitString);
            BatchIngredient batchIngredient = new BatchIngredient();
            batchIngredient.ingredientId = ingredientString;

            batchIngredient.quantityVol = toVolume(amount, abbreviationMap.get(unitString));
            viewModel.addIngredient(batchIngredient);
            updateUiIngredientsTable();
        }
    };

    private void updateUiIngredientsTable() {
        if (viewModel.batch.ingredients != null) {
            // FIXME: this is not performant and looks ghetto.
            Timber.d("Found %s BatchIngredients for this Batch; adding them to the ingredientsList", viewModel.batch.ingredients.size());
            LinearLayout ingredientsList = activity.findViewById(R.id.ingredients_list);
            ingredientsList.removeAllViews();
            for (BatchIngredient ingredient : viewModel.batch.ingredients) {
                BatchIngredientView ingredientView = new BatchIngredientView(activity);
                ingredientView.setBatchIngredient(ingredient);
                ingredientsList.addView(ingredientView);
            }
        } else {
            Timber.d("No Ingredients found for this Recipe.");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

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
        abbreviationMap.put(getResources().getString(R.string.TEASPOON), unitToTextAbbr(TEASPOON));
        abbreviationMap.put(getResources().getString(R.string.GRAM), unitToTextAbbr(GRAM));
        abbreviationMap.put(getResources().getString(R.string.KILOGRAM), unitToTextAbbr(KILOGRAM));
        abbreviationMap.put(getResources().getString(R.string.OUNCE), unitToTextAbbr(OUNCE));
        abbreviationMap.put(getResources().getString(R.string.POUND), unitToTextAbbr(POUND));


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchFormViewModel.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Timber.i("Got Batch ID %d from the NavigationController. Acting as a Batch Editor.");

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            long recipeId = bundle.getLong(RECIPE_ID, Long.MIN_VALUE);
            if (batchId != Long.MIN_VALUE) {
                viewModel.getBatch(batchId).observe(this, batch -> {
                    if (batch != null) {
                        viewModel.batch = batch;
                        dataBinding.setBatch(batch);
                        if (batch.status != null) {
                            dataBinding.status.setText(batch.status.toString());
                        } else {
                            dataBinding.status.setText(PLANNING.toString());
                        }
                        updateUiDateTime();
                        viewModel.getBatchIngredients(batchId).observe(this, batchIngredients -> {
                            if (batchIngredients != null) {
                                Timber.v("Loaded %s Batch ingredients", batchIngredients.size());
                                viewModel.batch.ingredients = batchIngredients;
                                updateUiIngredientsTable();
                            } else {
                                Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId);
                            }
                            Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch);
                        });
                        updateSpinners(viewModel.batch);
                    } else {
                        Timber.e("Got a null Batch!");
                    }
                });
            } else if (recipeId != Long.MIN_VALUE) {
                viewModel.getRecipe(recipeId).observe(this, recipe -> {
                    if (recipe != null) {
                        viewModel.batch = new Batch();
                        viewModel.batch.name = recipe.name;
                        viewModel.batch.outputVolume = recipe.outputVol;
                        viewModel.batch.targetSgStarting = recipe.startingSG;
                        viewModel.batch.targetSgFinal = recipe.finalSG;
                        viewModel.batch.status = PLANNING;
                        viewModel.batch.createDate = Calendar.getInstance();
                        viewModel.getCurrentUserId().observe(this, userId -> {
                            if (userId == null) {
                                Timber.e("Could not set the Batch User ID, since it's null?!");
                                return;
                            }
                            Timber.d("Setting batch user ID to %s", userId);
                            viewModel.batch.userId = userId;
                        });
                        dataBinding.setBatch(viewModel.batch);

                        dataBinding.status.setText(viewModel.batch.status.toString());
                        updateUiDateTime();
                        viewModel.getRecipeIngredients(recipeId).observe(this, recipeIngredients -> {
                            if (recipeIngredients != null) {
                                Timber.v("Loaded %s Recipe ingredients", recipeIngredients.size());
                                viewModel.batch.ingredients = recipeIngredients;
                                updateUiIngredientsTable();
                            } else {
                                Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId);
                            }
                            Timber.i("Loaded Recipe with ID %d:\n%s", recipe.id, recipe);
                        });
                        updateSpinners(viewModel.batch);
                    } else {
                        Timber.e("Got a null Recipe!");
                    }
                });
            }
        } else {
            Timber.i("No Batch ID was received. Acting as a Batch Creation form.");
            viewModel.batch = new Batch();
            viewModel.batch.createDate = Calendar.getInstance();
            viewModel.getCurrentUserId().observe(this, userId -> {
                if (userId == null) {
                    Timber.e("Could not set the Batch User ID, since it's null?!");
                    return;
                }
                Timber.d("Setting batch user ID to %s", userId);
                viewModel.batch.userId = userId;
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
        updateUiDateTime();

        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setOnClickListener(v -> {
                Timber.i("Date was clicked!");
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
                Timber.i("Time was clicked!");
                DialogFragment newFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt(HOUR, viewModel.batch.createDate.get(Calendar.HOUR));
                args.putInt(MINUTE, viewModel.batch.createDate.get(Calendar.MINUTE));
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, TIME_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            });
        }

        Button addSugarButton = getActivity().findViewById(R.id.add_sugar_button);
        if (addSugarButton != null) {
            addSugarButton.setOnClickListener(v -> {
                Timber.i("addSugarButton was clicked!");
                DialogFragment newFragment = new AddIngredientDialog();
                Bundle args = new Bundle();
                args.putString(INGREDIENT_TYPE, SUGAR.toString());
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "sugarPicker");
            });
        }

        Button addNutrientButton = getActivity().findViewById(R.id.add_nutrient_button);
        if (addNutrientButton != null) {
            addNutrientButton.setOnClickListener(v -> {
                Timber.i("addNutrientButton was clicked!");
                DialogFragment newFragment = new AddIngredientDialog();
                Bundle args = new Bundle();
                args.putString(INGREDIENT_TYPE, NUTRIENT.toString());
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "nutrientPicker");
            });
        }

        Button addYeastButton = getActivity().findViewById(R.id.add_yeast_button);
        if (addYeastButton != null) {
            addYeastButton.setOnClickListener(v -> {
                Timber.i("addYeastButton was clicked!");
                DialogFragment newFragment = new AddIngredientDialog();
                Bundle args = new Bundle();
                args.putString(INGREDIENT_TYPE, YEAST.toString());
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "yeastPicker");
            });
        }

        Button addStabilizerButton = getActivity().findViewById(R.id.add_stabilizer_button);
        if (addStabilizerButton != null) {
            addStabilizerButton.setOnClickListener(v -> {
                Timber.i("addStabilizerButton was clicked!");
                DialogFragment newFragment = new AddIngredientDialog();
                Bundle args = new Bundle();
                args.putString(INGREDIENT_TYPE, STABILIZER.toString());
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE);
                newFragment.show(getActivity().getSupportFragmentManager(), "stabilizerPicker");
            });
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(datePickerMessageReceiver,
                new IntentFilter(DATE_SET_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(timePickerMessageReceiver,
                new IntentFilter(TIME_SET_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(ingredientPickerMessageReceiver,
                new IntentFilter(INGREDIENT_SET_EVENT));

        Button submitButton = getActivity().findViewById(R.id.button_submit);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Timber.i("Submit button clicked!");
                FORM_MODE = (viewModel.batch.id == null) ? MODES.CREATE : MODES.EDIT;
                viewModel.batch.name = dataBinding.name.getText().toString().trim();
                viewModel.batch.targetSgStarting = toDouble(dataBinding.targetSgStarting.getText().toString().trim());
                viewModel.batch.targetSgFinal = toDouble(dataBinding.targetSgFinal.getText().toString().trim());
                viewModel.batch.targetABV = toFloat(dataBinding.targetABV.getText().toString().trim());
                viewModel.batch.startingPh = toFloat(dataBinding.startingPh.getText().toString().trim());
                viewModel.batch.startingTemp = toFloat(dataBinding.startingTemp.getText().toString().trim());

                viewModel.batch.outputVolume = toVolume(
                        dataBinding.outputVolumeAmount.getText().toString(),
                        abbreviationMap.get(dataBinding.outputVolumeAmountUnit.getSelectedItem().toString()));

                viewModel.batch.status = BatchStatusEnum.fromString(dataBinding.status.getText().toString().trim());
                viewModel.batch.notes = dataBinding.notes.getText().toString().trim();

                if (FORM_MODE == MODES.CREATE) {
                    Timber.d("We are in CREATE mode.");
                    Timber.d("Current batch state:\n%s", viewModel.batch.toString());
                    viewModel.saveNewBatch().observe(this, savedBatchId -> {
                        if (savedBatchId != null) {
                            Timber.i("Successfully saved Batch, which now has ID=%s", savedBatchId);
                            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(datePickerMessageReceiver);
                            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timePickerMessageReceiver);
                            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(ingredientPickerMessageReceiver);
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
                    Timber.d("We are in EDIT mode for batch #%s", viewModel.batch.id);
                    Timber.d("Current batch state:\n%s", viewModel.batch.toString());
                    viewModel.updateBatch();
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(datePickerMessageReceiver);
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timePickerMessageReceiver);
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(ingredientPickerMessageReceiver);
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Updated batch!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Answers.getInstance().logCustom(new CustomEvent("Batch edit success"));
                    navigationController.navigateFromEditBatch(viewModel.batch.id);
                }

            });
        } else {
            Timber.e("Cannot find submit button in view");
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
            Timber.e("Could not update spinners because batch is null");
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
//                    Timber.v("Volume spinner item selected: pos=%s, id=%s, string=%s", pos, id, volSpin.getItemAtPosition(pos).toString()));
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
            Timber.e("Could not find createDateTime in View");
        }

        TextView dateField = getActivity().findViewById(R.id.createDateDate);
        if (dateField != null) {
            dateField.setText(calendarToLocaleDate(viewModel.batch.createDate));
        } else {
            Timber.e("Could not find createDateDate in View");
        }
    }

    private enum MODES {CREATE, EDIT}
}