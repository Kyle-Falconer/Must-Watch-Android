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

package com.fullmeadalchemist.mustwatch.ui.batch.detail

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.BrewFormulae.estimateBatchSG
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleTime
import com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToStringResource
import com.fullmeadalchemist.mustwatch.databinding.BatchDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.IngredientListViewAdapter
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.ui.log.LogRecyclerViewAdapter
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*


class BatchDetailFragment : Fragment() {

    private lateinit var logsRecyclerView: RecyclerView
    private lateinit var logsAdapter: LogRecyclerViewAdapter

    private lateinit var ingredientListViewAdapter: IngredientListViewAdapter
    private lateinit var ingredientsRecyclerView:RecyclerView

    val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: BatchDetailFragmentBinding
    private val defaultLocale = Locale.getDefault()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = BatchDetailFragmentBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)


        ingredientListViewAdapter = IngredientListViewAdapter(object : IngredientListViewAdapter.IngredientClickCallback {
            override fun onClick(repo: BatchIngredient) {
                Timber.w("Clicking on a BatchIngredient in this list is not yet supported")
            }
        })
        ingredientsRecyclerView = binding.root.findViewById(R.id.ingredients_list)
        ingredientsRecyclerView.setHasFixedSize(true)
        val ingredientsLlm = LinearLayoutManager(context)
        ingredientsLlm.orientation = LinearLayoutManager.VERTICAL
        ingredientsRecyclerView.layoutManager = ingredientsLlm
        ingredientsRecyclerView.adapter = ingredientListViewAdapter


        logsAdapter = LogRecyclerViewAdapter(object : LogRecyclerViewAdapter.LogEntryClickCallback {
            override fun onClick(entry: LogEntry) {
                Timber.i(String.format("Log entry clicked:\n%s", entry.toString()))
            }
        })
        logsRecyclerView = binding.root.findViewById(R.id.logs_list)
        logsRecyclerView.setHasFixedSize(true)
        val logsLlm = LinearLayoutManager(context)
        logsLlm.orientation = LinearLayoutManager.VERTICAL
        logsRecyclerView.layoutManager = logsLlm
        logsRecyclerView.adapter = logsAdapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initClickListeners()

        val bundle = this.arguments
        if (bundle != null) {

            val batchId = bundle.getLong(BATCH_ID, java.lang.Long.MIN_VALUE)
            Timber.i("Got Batch ID %d from the NavigationController.", batchId)

            if (batchId != java.lang.Long.MIN_VALUE) {
                if (viewModel.batch != null) {
                    Timber.v("Reusing viewmodel data")
                    binding.batch = viewModel.batch
                    updateBatchUiInfo()
                    viewModel.getBatchIngredients(batchId).observe(this, Observer<List<BatchIngredient>> { batchIngredients ->
                        if (batchIngredients != null) {
                            Timber.v("Loaded %s Batch ingredients", batchIngredients.size)
                            ingredientListViewAdapter.dataSet = batchIngredients
                            ingredientListViewAdapter.notifyDataSetChanged()
                        } else {
                            Timber.w("Received nothing from the BatchRepository when trying to get ingredients for Batch %s", batchId)
                        }
                    })
                } else {
                    viewModel.getBatch(batchId).observe(this, Observer<Batch> { batch ->
                        if (batch != null) {
                            binding.batch = batch
                            viewModel.batch = batch
                            updateBatchUiInfo()

                            viewModel.getBatchIngredients(batchId).observe(this, Observer<List<BatchIngredient>> { batchIngredients ->
                                if (batchIngredients != null) {
                                    Timber.v("Loaded %s Batch ingredients", batchIngredients.size)
                                    ingredientListViewAdapter.dataSet = batchIngredients
                                    ingredientListViewAdapter.notifyDataSetChanged()
                                    updateBatchUiInfo()
                                } else {
                                    Timber.w("Received nothing from the BatchRepository when trying to get ingredients for Batch %s", batchId)
                                }
                                Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch)
                            })

                        } else {
                            Timber.w("Received a null Batch from the RecipeDetailViewModel.")
                        }
                    })
                }
                viewModel.getLogEntries(batchId).observe(this, Observer<List<LogEntry>> { batches ->
                    // update UI
                    logsAdapter.dataSet = batches
                    logsAdapter.notifyDataSetChanged()
                })
            }
        } else {
            Timber.i("No Batch ID was received. Redirecting to the Batch Creation form.")
            findNavController().navigate(R.id.batchFormFragment)
        }
    }

    private fun updateBatchUiInfo() {
        viewModel.batch?.let {batch->
            binding.createDateDate.text = calendarToLocaleDate(batch.createDate)
            binding.createDateTime.text = calendarToLocaleTime(batch.createDate)

            if (batch.outputVolume != null) {
                val volumeAmount = batch.outputVolume?.value?.toDouble()
                val f = DecimalFormat("#.##")
                binding.outputVolumeAmount.text = f.format(volumeAmount)
                val unitString = resources.getString(unitToStringResource(batch.outputVolume!!.unit))
                binding.outputVolumeAmountUnit.text = unitString
            }

            batch.targetABV?.let {
                val abvPercent = it * 100
                val f = DecimalFormat("0.##")
                binding.targetABV.text = String.format(defaultLocale, "%s%%", f.format(abvPercent.toDouble()))
            }

            batch.status?.let { status ->
                binding.status.text = status.toString()
            }

            if (batch.targetSgStarting != null) {
                val sgStartingValue = estimateBatchSG(batch)
                if (sgStartingValue != null) {
                    val f = DecimalFormat("#.###")
                    binding.targetSgStarting.text = f.format(sgStartingValue)
                }
            }

            if (batch.targetSgFinal != null) {
                val sgFinalValue = batch.targetSgFinal
                val f = DecimalFormat("#.###")
                binding.targetSgFinal.text = f.format(sgFinalValue)
            }
        }
    }

    private fun initClickListeners() {
        val editButton = binding.root.findViewById<Button>(R.id.button_edit_batch)
        editButton?.setOnClickListener {view ->
            var batchIdToEdit = 0L
            viewModel.batch?.let {
                batchIdToEdit = it.id
            }
            Timber.i("Edit Batch button clicked for id %d", batchIdToEdit)

            val bundle = Bundle()
            bundle.putLong(BATCH_ID, batchIdToEdit)
            view.findNavController().navigate(R.id.batchFormFragment, bundle)
        }
        val addLogButton = binding.root.findViewById<Button>(R.id.button_add_log_entry)
        addLogButton?.setOnClickListener {view ->
            var batchIdToEdit = 0L
            viewModel.batch?.let {
                batchIdToEdit = it.id
            }
            Timber.i("Add Log Entry button clicked for id %d", batchIdToEdit)

            //val nagArgs = BatchDetailFragmentDirections.action_batchDetailFragment_to_batchFormFragment()
            val bundle = Bundle()
            bundle.putLong(BATCH_ID, batchIdToEdit)
            view.findNavController().navigate(R.id.logFormFragment, bundle)
        }
    }
}
