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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Kyle on 7/23/2017.
 */

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = LogRecyclerViewAdapter.class.getSimpleName();
    private final LogEntryClickCallback logEntryClickCallback;
    List<LogEntry> dataSet;
    @Inject
    NavigationController navigationController;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public LogRecyclerViewAdapter(List<LogEntry> dataSet, LogEntryClickCallback logEntryClickCallback) {
        if (dataSet == null)
            dataSet = new ArrayList<>();
        this.dataSet = dataSet;
        this.logEntryClickCallback = logEntryClickCallback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        //View v = LayoutInflater.from(viewGroup.getContext())
        //        .inflate(R.layout.batch_item_list, viewGroup, false);


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.log_entry_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        final LogEntry logEntry = dataSet.get(position);

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getBatchNumberTextView().setText("LogEntry #" + logEntry.id + "\n" + logEntry.toString());
//
//        Calendar createDate = mDataSet.get(position).getCreateDate();
//        String formattedCreateDate = calendarToLocaleDateTimeLong(b.getCreateDate());
//        if (createDate != null) {
//            viewHolder.getBatchCreateDateTextView().setText(formattedCreateDate);
//        }
//
//        viewHolder.getBatchStatusTextView().setText(b.getStatusString());

        viewHolder.itemView.setOnClickListener(v -> {
            if (logEntryClickCallback != null) {
                Log.d(TAG, String.format("Element for LogEntry #%s was clicked.", logEntry.id));
                logEntryClickCallback.onClick(logEntry);
            } else {
                Log.wtf(TAG, "No click listener set!?");
            }
        });
//
//
//        viewHolder.getBatchTargetAbvTextView().setText(String.format("%s%%", b.getTargetABV()));
//
//        String outVol = b.getFormattedOutputVolume();
//        if (outVol != null) {
//            viewHolder.getOutputVolumeTextView().setText(outVol);
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public interface LogEntryClickCallback {
        void onClick(LogEntry entry);
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView batchNumberTextView;
        private final TextView batchCreateDateTextView;
        private final TextView batchStatusTextView;
        private final TextView batchTargetAbvTextView;
        private final TextView outputVolumeTextView;

        public ViewHolder(View v) {
            super(v);
            batchNumberTextView = (TextView) v.findViewById(R.id.name);
            batchStatusTextView = (TextView) v.findViewById(R.id.batchCardStatusTextView);
            batchTargetAbvTextView = (TextView) v.findViewById(R.id.batchCardTargetAbvTextView);
            outputVolumeTextView = (TextView) v.findViewById(R.id.batchCardOutputVolTextView);
            batchCreateDateTextView = (TextView) v.findViewById(R.id.batchCardCreateDateTextView);
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
