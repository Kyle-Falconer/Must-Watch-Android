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
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.BrewFormulae.estimateBatchSG
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleTime
import com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToStringResource
import com.fullmeadalchemist.mustwatch.databinding.BatchDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView
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

    protected lateinit var logsRecyclerView: RecyclerView
    protected lateinit var logsAdapter: LogRecyclerViewAdapter


    val viewModel: BatchDetailViewModel by sharedViewModel()

    private lateinit var binding: BatchDetailFragmentBinding
    private val defaultLocale = Locale.getDefault()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = BatchDetailFragmentBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
//        binding = DataBindingUtil.inflate(inflater, R.layout.batch_detail_fragment, container, false)
//                as BatchDetailFragmentBinding

        logsAdapter = LogRecyclerViewAdapter(object : LogRecyclerViewAdapter.LogEntryClickCallback {
            override fun onClick(entry: LogEntry) {
                Timber.i(String.format("Log entry clicked:\n%s", entry.toString()))
            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initClickListeners()

        val bundle = this.arguments
        if (bundle != null) {

            val batchId = bundle.getLong(BATCH_ID, java.lang.Long.MIN_VALUE)
            Timber.i("Got Batch ID %d from the NavigationController. Acting as a Batch Editor.", batchId)

            if (batchId != java.lang.Long.MIN_VALUE) {
                if (viewModel.batch != null) {
                    Timber.v("Reusing viewmodel data")
                    this.binding
                    binding.batch = viewModel.batch
                    updateBatchUiInfo()
                    updateIngredientUiInfo()
                } else {


                    viewModel.getBatch(batchId).observe(this, Observer<Batch> { batch ->
                        if (batch != null) {
                            binding.batch = batch
                            viewModel.batch = batch
                            updateBatchUiInfo()

                            viewModel.getBatchIngredients(batchId).observe(this, Observer<List<BatchIngredient>> { batchIngredients ->
                                if (batchIngredients != null) {
                                    Timber.v("Loaded %s Batch ingredients", batchIngredients.size)
                                    viewModel.batch?.ingredients = batchIngredients
                                    updateIngredientUiInfo()
                                    updateBatchUiInfo()
                                } else {
                                    Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId)
                                }
                                Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch)
                            })

                        } else {
                            Timber.w("Received a null Batch from the RecipeDetailViewModel.")
                        }
                    })
                    viewModel.getLogsForBatch(batchId).observe(this, Observer<List<LogEntry>> { batches ->
                        // update UI
                        logsAdapter.dataSet = batches
                        logsAdapter.notifyDataSetChanged()
                    })
                }
            }
        } else {
            Timber.i("No Batch ID was received. Redirecting to the Batch Creation form.")
            //navigationController.navigateToAddBatch()
            findNavController().navigate(R.id.batchFormFragment)
        }

        logsRecyclerView = activity!!.findViewById(R.id.logs_list)
        logsRecyclerView.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL

        logsRecyclerView.layoutManager = llm
        logsRecyclerView.adapter = logsAdapter
    }

    private fun updateBatchUiInfo() {
        viewModel.batch?.let {
            binding.createDateDate.text = calendarToLocaleDate(it.createDate)
            binding.createDateTime.text = calendarToLocaleTime(it.createDate)

            if (it.outputVolume != null) {
                val volumeAmount = it.outputVolume!!.value as Double
                val f = DecimalFormat("#.##")
                binding.outputVolumeAmount.text = f.format(volumeAmount)
                val unitString = resources.getString(unitToStringResource(it.outputVolume!!.unit))
                binding.outputVolumeAmountUnit.text = unitString
            }

            it.targetABV?.let {
                val abv_pct = it * 100
                val f = DecimalFormat("0.##")
                binding.targetABV.setText(String.format(defaultLocale, "%s%%", f.format(abv_pct.toDouble())))
            }

            it.status?.let {
                binding.status.text = it.toString()
            }

            if (it.targetSgStarting != null) {
                val sgStartingValue = estimateBatchSG(it)
                if (sgStartingValue != null) {
                    val f = DecimalFormat("#.###")
                    binding.targetSgStarting.text = f.format(sgStartingValue)
                }
            }

            if (it.targetSgFinal != null) {
                val sgFinalValue = it.targetSgFinal
                val f = DecimalFormat("#.###")
                binding.targetSgFinal.text = f.format(sgFinalValue)
            }
        }
    }

    private fun updateIngredientUiInfo() {
        viewModel.batch?.ingredients?.let {
            // FIXME: this is not performant and looks ghetto.
            Timber.d("Found %s BatchIngredients for this Batch; adding them to the ingredientsList", it.size)
            val ingredientsList = activity!!.findViewById<LinearLayout>(R.id.ingredients_list)
            ingredientsList.removeAllViews()
            for (ingredient in it) {
                val ingredientText = BatchIngredientView(activity)
                ingredientText.setBatchIngredient(ingredient)
                ingredientsList.addView(ingredientText)
            }
        }
    }

    private fun initClickListeners() {
        val submitButton = activity!!.findViewById<Button>(R.id.button_edit_batch)
        submitButton?.setOnClickListener { _ ->
            var batchIdToEdit = 0L
            viewModel.batch?.let {
                batchIdToEdit = it.id
            }
            Timber.i("Edit Batch button clicked for id %d", batchIdToEdit)

            val bundle = Bundle()
            bundle.putLong("batch_id", batchIdToEdit)
            findNavController().navigate(R.id.batchFormFragment, bundle)
//            navigationController.navigateToEditBatch(batchIdToEdit)
        }
        val addLogButton = activity!!.findViewById<Button>(R.id.button_add_log_entry)
        addLogButton?.setOnClickListener { _ ->
            var batchIdToEdit = 0L
            viewModel.batch?.let {
                batchIdToEdit = it.id
            }
            Timber.i("Add Log Entry button clicked for id %d", batchIdToEdit)

            //val nagArgs = BatchDetailFragmentDirections.action_batchDetailFragment_to_batchFormFragment()
            val bundle = Bundle()
            bundle.putLong("batch_id", batchIdToEdit)
            findNavController().navigate(R.id.logFormFragment, bundle)
//            navigationController.navigateToAddLog(batchIdToEdit)
        }
    }
}
