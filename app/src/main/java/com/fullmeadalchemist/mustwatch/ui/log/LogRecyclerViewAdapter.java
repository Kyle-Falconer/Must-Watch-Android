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
import java.util.Locale;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = LogRecyclerViewAdapter.class.getSimpleName();
    private final LogEntryClickCallback logEntryClickCallback;
    public List<LogEntry> dataSet;
    private Locale defaultLocale = Locale.getDefault();
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


        viewHolder.getEntryDateTextView().setText(calendarToLocaleDate(logEntry.entryDate));
        viewHolder.getNoteTextView().setText(logEntry.note);

        if (logEntry.sg == null ||logEntry.sg < 0.01) {
            viewHolder.getSgTextView().setText("-");
        } else {
            viewHolder.getSgTextView().setText(String.format(defaultLocale, "%.3f", logEntry.sg));
        }


        if (logEntry.acidity == null ||logEntry.acidity < 0.01) {
            viewHolder.getAcidityTextView().setText("-");
        } else {
            viewHolder.getAcidityTextView().setText(String.format(defaultLocale, "%.2f", logEntry.acidity));
        }


        viewHolder.itemView.setOnClickListener(v -> {
            if (logEntryClickCallback != null) {
                Log.d(TAG, String.format("Element for LogEntry #%s was clicked.", logEntry.id));
                logEntryClickCallback.onClick(logEntry);
            } else {
                Log.wtf(TAG, "No click listener set!?");
            }
        });



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
        private final TextView entryDateTextView;
        private final TextView noteTextView;
        private final TextView sgTextView;
        private final TextView acidityTextView;

        public ViewHolder(View v) {
            super(v);
            entryDateTextView = v.findViewById(R.id.entryDate);
            sgTextView = v.findViewById(R.id.sg);
            acidityTextView = v.findViewById(R.id.acidity);
            noteTextView = v.findViewById(R.id.note);
        }

        public TextView getEntryDateTextView() {
            return entryDateTextView;
        }

        public TextView getSgTextView() {
            return sgTextView;
        }

        public TextView getAcidityTextView() {
            return acidityTextView;
        }

        public TextView getNoteTextView() {
            return noteTextView;
        }

    }
}
