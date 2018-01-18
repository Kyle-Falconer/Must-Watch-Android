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

package com.fullmeadalchemist.mustwatch.ui.batch.detail;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchDetailFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.log.LogRecyclerViewAdapter;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.text.DecimalFormat;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.BrewFormulae.estimateBatchSG;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToStringResource;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleTime;
import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchDetailFragment extends LifecycleFragment implements Injectable {

    protected RecyclerView logsRecyclerView;
    protected LogRecyclerViewAdapter logsAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    BatchDetailFragmentBinding dataBinding;
    private BatchDetailViewModel viewModel;
    private Locale defaultLocale = Locale.getDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.batch_detail_fragment,
                container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchDetailViewModel.class);

        logsAdapter = new LogRecyclerViewAdapter(null, logEntry -> {
            Timber.i(String.format("Log entry clicked:\n%s", logEntry.toString()));
        });

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initClickListeners();

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            Timber.i("Got Batch ID %d from the NavigationController. Acting as a Batch Editor.", batchId);

            if (batchId != Long.MIN_VALUE) {
                if (viewModel.batch != null) {
                    Timber.v("Reusing viewmodel data");
                    dataBinding.setBatch(viewModel.batch);
                    updateBatchUiInfo();
                    updateIngredientUiInfo();
                } else {

                    viewModel.getBatch(batchId).observe(this, batch -> {
                        if (batch != null) {
                            dataBinding.setBatch(batch);
                            viewModel.batch = batch;
                            updateBatchUiInfo();

                            viewModel.getBatchIngredients(batchId).observe(this, batchIngredients -> {
                                if (batchIngredients != null) {
                                    Timber.v("Loaded %s Batch ingredients", batchIngredients.size());
                                    viewModel.batch.ingredients = batchIngredients;
                                    updateIngredientUiInfo();
                                    updateBatchUiInfo();
                                } else {
                                    Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId);
                                }
                                Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch);
                            });

                        } else {
                            Timber.w("Received a null Batch from the RecipeDetailViewModel.");
                        }
                    });
                    viewModel.getLogsForBatch(batchId).observe(this, batches -> {
                        // update UI
                        logsAdapter.dataSet = batches;
                        logsAdapter.notifyDataSetChanged();
                    });
                }
            }
        } else {
            Timber.i("No Batch ID was received. Redirecting to the Batch Creation form.");
            navigationController.navigateToAddBatch();
        }

        logsRecyclerView = getActivity().findViewById(R.id.logs_list);
        logsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        logsRecyclerView.setLayoutManager(llm);
        logsRecyclerView.setAdapter(logsAdapter);
    }

    private void updateBatchUiInfo() {
        dataBinding.createDateDate.setText(calendarToLocaleDate(viewModel.batch.createDate));
        dataBinding.createDateTime.setText(calendarToLocaleTime(viewModel.batch.createDate));

        if (viewModel.batch.outputVolume != null) {
            double volumeAmount = (double) viewModel.batch.outputVolume.getValue();
            DecimalFormat f = new DecimalFormat("#.##");
            dataBinding.outputVolumeAmount.setText(f.format(volumeAmount));
            String unitString = getResources().getString(unitToStringResource(viewModel.batch.outputVolume.getUnit()));
            dataBinding.outputVolumeAmountUnit.setText(unitString);
        }

        if (viewModel.batch.targetABV != null) {
            Double sgStartingValue = estimateBatchSG(viewModel.batch);
            float abv_pct = viewModel.batch.targetABV * 100;
            DecimalFormat f = new DecimalFormat("0.##");
            dataBinding.targetABV.setText(String.format(defaultLocale, "%s%%", f.format(abv_pct)));
        }

        if (viewModel.batch.status != null) {
            dataBinding.status.setText(viewModel.batch.status.toString());
        }

        if (viewModel.batch.targetSgStarting != null) {
            Double sgStartingValue = estimateBatchSG(viewModel.batch);
            if (sgStartingValue != null) {
                DecimalFormat f = new DecimalFormat("#.###");
                dataBinding.targetSgStarting.setText(f.format(sgStartingValue));
            }
        }

        if (viewModel.batch.targetSgFinal != null) {
            Double sgFinalValue = viewModel.batch.targetSgFinal;
            DecimalFormat f = new DecimalFormat("#.###");
            dataBinding.targetSgFinal.setText(f.format(sgFinalValue));
        }
    }

    private void updateIngredientUiInfo() {
        if (viewModel.batch.ingredients != null) {
            // FIXME: this is not performant and looks ghetto.
            Timber.d("Found %s BatchIngredients for this Batch; adding them to the ingredientsList", viewModel.batch.ingredients.size());
            LinearLayout ingredientsList = getActivity().findViewById(R.id.ingredients_list);
            ingredientsList.removeAllViews();
            for (BatchIngredient ingredient : viewModel.batch.ingredients) {
                BatchIngredientView ingredientText = new BatchIngredientView(getActivity());
                ingredientText.setBatchIngredient(ingredient);
                ingredientsList.addView(ingredientText);
            }
        } else {
            Timber.d("No Ingredients found for this Recipe.");
        }
    }

    private void initClickListeners() {
        Button submitButton = getActivity().findViewById(R.id.button_edit_batch);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Timber.i("Edit Batch button clicked");
                navigationController.navigateToEditBatch(viewModel.batch.id);
            });
        }
        Button addLogButton = getActivity().findViewById(R.id.button_add_log_entry);
        if (addLogButton != null) {
            addLogButton.setOnClickListener(v -> {
                Timber.i("Add Log Entry button clicked");
                navigationController.navigateToAddLog(viewModel.batch.id);
            });
        }
    }
}
