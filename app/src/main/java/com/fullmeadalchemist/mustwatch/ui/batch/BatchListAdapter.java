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

package com.fullmeadalchemist.mustwatch.ui.batch;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchCardViewBinding;
import com.fullmeadalchemist.mustwatch.ui.common.DataBoundListAdapter;
import com.fullmeadalchemist.mustwatch.util.Objects;
import com.fullmeadalchemist.mustwatch.vo.Batch;

/**
 * A RecyclerView adapter for {@link Batch} class.
 */
public class BatchListAdapter extends DataBoundListAdapter<Batch, BatchCardViewBinding> {
    private static final String TAG = BatchListAdapter.class.getSimpleName();
    private final DataBindingComponent dataBindingComponent;
    private final BatchClickCallback batchClickCallback;

    public BatchListAdapter(DataBindingComponent dataBindingComponent,
                            BatchClickCallback batchClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.batchClickCallback = batchClickCallback;
    }

    @Override
    protected BatchCardViewBinding createBinding(ViewGroup parent) {
        BatchCardViewBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.batch_card_view,
                        parent, false, dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Batch batch = binding.getBatch();
            Log.d(TAG, String.format("Element for batch #%s was clicked.", batch.id));
            if (batch != null && batchClickCallback != null) {
                batchClickCallback.onClick(batch);
            }
        });
        return binding;
    }

    @Override
    protected void bind(BatchCardViewBinding binding, Batch item) {
        binding.setBatch(item);
    }

    @Override
    protected boolean areItemsTheSame(Batch oldItem, Batch newItem) {
        return Objects.equals(oldItem.id, newItem.id) &&
                Objects.equals(oldItem.createDate, newItem.createDate);
    }

    @Override
    protected boolean areContentsTheSame(Batch oldItem, Batch newItem) {
        return Objects.equals(oldItem.name, newItem.name) &&
                Objects.equals(oldItem.notes ,newItem.notes);
    }

    public interface BatchClickCallback {
        void onClick(Batch repo);
    }
}