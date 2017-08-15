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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchDetailFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.log.LogRecyclerViewAdapter;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;
import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleTime;
import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchDetailFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    protected RecyclerView logsRecyclerView;
    protected LogRecyclerViewAdapter logsAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    BatchDetailFragmentBinding dataBinding;
    private BatchDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.batch_detail_fragment,
                container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchDetailViewModel.class);

        logsAdapter = new LogRecyclerViewAdapter(null, logEntry -> {
            Log.i(TAG, String.format("Log entry clicked:\n%s", logEntry.toString()));
        });


        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initClickListeners();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Log.i(TAG, "Got Batch ID %d from the NavigationController. Acting as a Batch Editor.");

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            if (batchId != Long.MIN_VALUE) {
                viewModel.getBatch(batchId).observe(this, batch -> {
                    if (batch != null) {
                        Log.i(TAG, String.format("Loaded Batch with ID %d:\n%s", batch.id, batch));
                        dataBinding.setBatch(batch);
                        viewModel.batch = batch;
                        dataBinding.createDateDate.setText(calendarToLocaleDate(batch.createDate));
                        dataBinding.createDateTime.setText(calendarToLocaleTime(batch.createDate));
                    } else {
                        Log.w(TAG, "Received a null Batch from the BatchDetailViewModel.");
                    }
                });
                viewModel.getLogsForBatch(batchId).observe(this, batches -> {
                    // update UI
                    logsAdapter.dataSet = batches;
                    logsAdapter.notifyDataSetChanged();
                });

            }
        } else {
            Log.i(TAG, "No Batch ID was received. Redirecting to the Batch Creation form.");
            navigationController.navigateToAddBatch();
        }

        logsRecyclerView = getActivity().findViewById(R.id.logs_list);
        logsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        logsRecyclerView.setLayoutManager(llm);
        logsRecyclerView.setAdapter(logsAdapter);
    }

    private void initClickListeners() {
        Button submitButton = getActivity().findViewById(R.id.button_edit_batch);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Log.i(TAG, "Edit Batch button clicked");
                navigationController.navigateToEditBatch(viewModel.batch.id);
            });
        }
        Button addLogButton = getActivity().findViewById(R.id.button_add_log_entry);
        if (addLogButton != null) {
            addLogButton.setOnClickListener(v -> {
                Log.i(TAG, "Add Log Entry button clicked");
                navigationController.navigateToAddLog(viewModel.batch.id);
            });
        }
    }
}
