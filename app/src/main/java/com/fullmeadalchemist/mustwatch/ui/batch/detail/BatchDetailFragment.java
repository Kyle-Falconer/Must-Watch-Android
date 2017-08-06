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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchDetailFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    private BatchDetailViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchDetailViewModel.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Log.i(TAG, "Got Batch ID %d from the NavigationController. Acting as a Batch Editor.");

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            if (batchId != Long.MIN_VALUE) {
                viewModel.getBatch(batchId).observe(this, batch -> {
                    Log.i(TAG, String.format("Loaded Batch with ID %d:\n%s", batch.id, batch));
                });
            }
        } else {
            Log.i(TAG, "No Batch ID was received. Acting as a Batch Creation form.");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_detail_fragment, container, false);

        return rootView;
    }
}
