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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;

public class BatchListViewAdapter extends RecyclerView.Adapter<BatchListViewAdapter.ViewHolder> {

    private static final String TAG = BatchListViewAdapter.class.getSimpleName();
    private final BatchClickCallback batchClickCallback;
    List<Batch> dataSet;
    @Inject
    NavigationController navigationController;
    private Locale defaultLocale = Locale.getDefault();

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public BatchListViewAdapter(List<Batch> dataSet, BatchClickCallback batchClickCallback) {
        if (dataSet == null)
            dataSet = new ArrayList<>();
        this.dataSet = dataSet;
        this.batchClickCallback = batchClickCallback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        //View v = LayoutInflater.from(viewGroup.getContext())
        //        .inflate(R.layout.batch_item_list, viewGroup, false);


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.batch_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        final Batch b = dataSet.get(position);

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.getBatchLabelTextView().setText(String.format(defaultLocale, "%s", b.name));
        viewHolder.getBatchNumberTextView().setText(String.format(defaultLocale, "Batch %d", b.id));

        if (b.outputVolume == null || b.outputVolume.getEstimatedValue() < 0.01) {
            viewHolder.getOutputVolumeTextView().setText("-");
        } else {
            double volumeAmount = b.outputVolume.getEstimatedValue();
            DecimalFormat f = new DecimalFormat("#.##");
            viewHolder.getOutputVolumeTextView().setText(String.format(defaultLocale, "%s %s", f.format(volumeAmount), b.outputVolume.getUnit().toString()));
        }
        if (b.targetABV == null || b.targetABV < 0.01) {
            viewHolder.getBatchTargetAbvTextView().setText("-");
        } else {
            float abv_pct = b.targetABV*100;
            DecimalFormat f = new DecimalFormat("#.##");
            viewHolder.getBatchTargetAbvTextView().setText(String.format(defaultLocale, "%s%%",  f.format(abv_pct)));
        }


        viewHolder.getBatchStatusTextView().setText(b.status);
        String formattedCreateDate = calendarToLocaleDate(b.createDate);
        viewHolder.getBatchCreateDateTextView().setText(formattedCreateDate);


        viewHolder.itemView.setOnClickListener(v -> {
            if (batchClickCallback != null) {
                Log.d(TAG, String.format("Element for batch #%s was clicked.", b.id));
                batchClickCallback.onClick(b);
            } else {
                Log.wtf(TAG, "No click listener set or Batch is null!?");
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public interface BatchClickCallback {
        void onClick(Batch repo);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView batchLabelTextView;
        private final TextView batchNumberTextView;
        private final TextView batchCreateDateTextView;
        private final TextView batchStatusTextView;
        private final TextView batchTargetAbvTextView;
        private final TextView outputVolumeTextView;

        public ViewHolder(View v) {
            super(v);
            batchLabelTextView = v.findViewById(R.id.name);
            batchNumberTextView = v.findViewById(R.id.batch_id);
            batchStatusTextView = v.findViewById(R.id.batchCardStatusTextView);
            batchTargetAbvTextView = v.findViewById(R.id.batchCardTargetAbvTextView);
            outputVolumeTextView = v.findViewById(R.id.batchCardOutputVolTextView);
            batchCreateDateTextView = v.findViewById(R.id.batchCardCreateDateTextView);
        }

        public TextView getBatchLabelTextView() {
            return batchLabelTextView;
        }

        public TextView getBatchNumberTextView() {
            return batchNumberTextView;
        }

        public TextView getBatchStatusTextView() {
            return batchStatusTextView;
        }

        public TextView getBatchTargetAbvTextView() {
            return batchTargetAbvTextView;
        }

        public TextView getBatchCreateDateTextView() {
            return batchCreateDateTextView;
        }

        public TextView getOutputVolumeTextView() {
            return outputVolumeTextView;
        }
    }
}
