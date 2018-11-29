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

package com.fullmeadalchemist.mustwatch.ui.batch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.vo.Batch
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

class BatchListViewAdapter
/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val batchClickCallback: BatchClickCallback?, dataSet: List<Batch> = arrayListOf()) : RecyclerView.Adapter<BatchListViewAdapter.ViewHolder>() {
    internal var dataSet: List<Batch>? = null
    private val defaultLocale = Locale.getDefault()


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.batch_card_view, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Timber.d("Element $position set.")

        val b = dataSet!![position]

        viewHolder.batchLabelTextView.text = String.format(defaultLocale, "%s", b.name)
        viewHolder.batchNumberTextView.text = String.format(defaultLocale, "Batch %d", b.id)

        b.outputVolume?.value?.let {
            val outVol = it.toFloat()
            if (outVol < 0.01) {
                viewHolder.outputVolumeTextView.text = "-"
            } else {
                val volumeAmount = b.outputVolume!!.value as Double
                val f = DecimalFormat("#.##")
                viewHolder.outputVolumeTextView.text = String.format(defaultLocale, "%s %s", f.format(volumeAmount), b.outputVolume!!.unit.toString())
            }
        }

        b.targetABV?.let {
            if (it < 0.01) {
                viewHolder.batchTargetAbvTextView.text = "-"
            } else {
                val abv_pct = b.targetABV!! * 100
                val f = DecimalFormat("#.##")
                viewHolder.batchTargetAbvTextView.text = String.format(defaultLocale, "%s%%", f.format(abv_pct.toDouble()))
            }
        }

        if (b.status != null) {
            viewHolder.batchStatusTextView.text = b.status!!.toString()
        } else {
            viewHolder.batchStatusTextView.text = Batch.BatchStatusEnum.PLANNING.toString()
        }

        val formattedCreateDate = calendarToLocaleDate(b.createDate)
        viewHolder.batchCreateDateTextView.text = formattedCreateDate


        viewHolder.itemView.setOnClickListener { v ->
            if (batchClickCallback != null) {
                Timber.d("Element for batch #%s was clicked.", b.id)
                batchClickCallback.onClick(b)
            } else {
                Timber.e("No click listener set or Batch is null!?")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataSet == null) 0 else dataSet!!.size
    }

    interface BatchClickCallback {
        fun onClick(repo: Batch)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val batchLabelTextView: TextView = v.findViewById(R.id.name)
        val batchNumberTextView: TextView = v.findViewById(R.id.batch_id)
        val batchCreateDateTextView: TextView = v.findViewById(R.id.batchCardCreateDateTextView)
        val batchStatusTextView: TextView = v.findViewById(R.id.batchCardStatusTextView)
        val batchTargetAbvTextView: TextView = v.findViewById(R.id.batchCardTargetAbvTextView)
        val outputVolumeTextView: TextView = v.findViewById(R.id.batchCardOutputVolTextView)
    }
}
