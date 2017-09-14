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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;

public class BatchListViewAdapter extends RecyclerView.Adapter<BatchListViewAdapter.ViewHolder> {

    private static final String TAG = BatchListViewAdapter.class.getSimpleName();
    private final BatchClickCallback batchClickCallback;
    List<Batch> dataSet;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.batch_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Timber.d("Element " + position + " set.");

        final Batch b = dataSet.get(position);

        viewHolder.getBatchLabelTextView().setText(String.format(defaultLocale, "%s", b.name));
        viewHolder.getBatchNumberTextView().setText(String.format(defaultLocale, "Batch %d", b.id));

        if (b.outputVolume == null || (double) b.outputVolume.getValue() < 0.01) {
            viewHolder.getOutputVolumeTextView().setText("-");
        } else {
            double volumeAmount = (double) b.outputVolume.getValue();
            DecimalFormat f = new DecimalFormat("#.##");
            viewHolder.getOutputVolumeTextView().setText(String.format(defaultLocale, "%s %s", f.format(volumeAmount), b.outputVolume.getUnit().toString()));
        }
        if (b.targetABV == null || b.targetABV < 0.01) {
            viewHolder.getBatchTargetAbvTextView().setText("-");
        } else {
            float abv_pct = b.targetABV * 100;
            DecimalFormat f = new DecimalFormat("#.##");
            viewHolder.getBatchTargetAbvTextView().setText(String.format(defaultLocale, "%s%%", f.format(abv_pct)));
        }

        if (b.status != null) {
            viewHolder.getBatchStatusTextView().setText(b.status.toString());
        } else {
            viewHolder.getBatchStatusTextView().setText(Batch.BatchStatusEnum.PLANNING.toString());
        }

        String formattedCreateDate = calendarToLocaleDate(b.createDate);
        viewHolder.getBatchCreateDateTextView().setText(formattedCreateDate);


        viewHolder.itemView.setOnClickListener(v -> {
            if (batchClickCallback != null) {
                Timber.d("Element for batch #%s was clicked.", b.id);
                batchClickCallback.onClick(b);
            } else {
                Timber.e("No click listener set or Batch is null!?");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public interface BatchClickCallback {
        void onClick(Batch repo);
    }

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
