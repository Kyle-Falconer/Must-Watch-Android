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

package com.fullmeadalchemist.mustwatch.ui.log;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;

import javax.inject.Inject;

/**
 * Created by Kyle on 7/22/2017.
 */

public class LogListFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = LogListFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected LogRecyclerViewAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;

    private LogViewModel viewModel;
    private FloatingActionButton fab;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.log_list, container, false);
        rootView.setTag(TAG);

//        mAdapter = new LogRecyclerViewAdapter(null, batch -> {
//            navigationController.navigateToEditBatch(batch.id);
//        });

        Long batchId = null;
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LogViewModel.class);
        viewModel.getLogEntries(batchId).observe(this, logEntries -> {
            // update UI
            if (logEntries != null) {
                Log.d(TAG, String.format("Got %d log entries for batch #%d", logEntries.size(), batchId));
            }
            mAdapter.dataSet = logEntries;
            mAdapter.notifyDataSetChanged();
        });

        fab = rootView.findViewById(R.id.logs_fab);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        FloatingActionButton fab = rootView.findViewById(R.id.logs_fab);

        // http://stackoverflow.com/a/35981886/940217
        // https://code.google.com/p/android/issues/detail?id=230298
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //FloatingActionButton floater = recyclerView.findViewById(R.id.batches_fab);
                if (fab == null) {
                    Log.e(TAG, "The FloatingActionButton is null!");
                    return;
                }
                if (dy > 0 && fab.isShown()) {
                    Log.d(TAG, "hiding FAB");
                    fab.hide();
                } else if (dy < 20 && !fab.isShown()) {
                    Log.d(TAG, "showing FAB");
                    fab.show();
                }
            }
        });

        if (fab != null) {
            fab.setOnClickListener(v -> {
                Log.d(TAG, "Floating Action Button was clicked!");
                navigationController.navigateToAddBatch();
            });
        } else {
            Log.e(TAG, "FloatingActionButton at R.id.logs_fab is null!");
        }


        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}