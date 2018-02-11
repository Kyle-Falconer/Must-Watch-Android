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

package com.fullmeadalchemist.mustwatch.ui.log

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController
import com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LogRecyclerViewAdapter
/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val logEntryClickCallback: LogEntryClickCallback?, dataSet: List<LogEntry> = arrayListOf()) : RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder>() {
    var dataSet: List<LogEntry>? = null
    @Inject
    lateinit var navigationController: NavigationController
    private val defaultLocale = Locale.getDefault()


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        //View v = LayoutInflater.from(viewGroup.getContext())
        //        .inflate(R.layout.batch_item_list, viewGroup, false);


        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.log_entry_card_view, viewGroup, false)
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Timber.d("Element $position set.")

        val logEntry = dataSet!![position]


        viewHolder.entryDateTextView.text = calendarToLocaleDate(logEntry.entryDate)
        viewHolder.noteTextView.text = logEntry.note

        logEntry.sg?.let {
            if (it < 0.01) {
                viewHolder.sgTextView.text = "-"
            } else {
                viewHolder.sgTextView.text = String.format(defaultLocale, "%.3f", logEntry.sg)
            }
        }

        logEntry.acidity?.let {
            if (it < 0.01) {
                viewHolder.acidityTextView.text = "-"
            } else {
                viewHolder.acidityTextView.text = String.format(defaultLocale, "%.2f", logEntry.acidity)
            }
        }

        viewHolder.itemView.setOnClickListener { v ->
            if (logEntryClickCallback != null) {
                Timber.d("Element for LogEntry #%s was clicked.", logEntry.id)
                logEntryClickCallback.onClick(logEntry)
            } else {
                Timber.wtf("No click listener set!?")
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (dataSet == null) 0 else dataSet!!.size
    }

    interface LogEntryClickCallback {
        fun onClick(entry: LogEntry)
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val entryDateTextView: TextView
        val noteTextView: TextView
        val sgTextView: TextView
        val acidityTextView: TextView

        init {
            entryDateTextView = v.findViewById(R.id.entryDate)
            sgTextView = v.findViewById(R.id.sg)
            acidityTextView = v.findViewById(R.id.acidity)
            noteTextView = v.findViewById(R.id.note)
        }

    }
}
