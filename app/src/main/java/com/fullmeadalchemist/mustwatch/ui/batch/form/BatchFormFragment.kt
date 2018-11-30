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

package com.fullmeadalchemist.mustwatch.ui.batch.form

import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.findNavController
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleDate
import com.fullmeadalchemist.mustwatch.core.FormatUtils.calendarToLocaleTime
import com.fullmeadalchemist.mustwatch.core.UnitMapper.*
import com.fullmeadalchemist.mustwatch.core.ValueParsers.toDouble
import com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.Companion.AMOUNT
import com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.Companion.INGREDIENT
import com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.Companion.INGREDIENT_SET_EVENT
import com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.Companion.INGREDIENT_TYPE
import com.fullmeadalchemist.mustwatch.ui.batch.form.AddIngredientDialog.Companion.UNIT
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.Companion.DATE_SET_EVENT
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.Companion.DAY_OF_MONTH
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.Companion.MONTH
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.Companion.YEAR
import com.fullmeadalchemist.mustwatch.ui.common.IngredientListViewAdapter
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.Companion.HOUR
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.Companion.MINUTE
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.Companion.TIME_SET_EVENT
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum.PLANNING
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.*
import com.fullmeadalchemist.mustwatch.vo.Recipe
import com.fullmeadalchemist.mustwatch.vo.Recipe.Companion.RECIPE_ID
import com.fullmeadalchemist.mustwatch.vo.User
import org.koin.android.viewmodel.ext.android.sharedViewModel
import systems.uom.common.Imperial.GALLON_UK
import systems.uom.common.Imperial.OUNCE_LIQUID
import systems.uom.common.USCustomary.*
import tec.units.ri.unit.Units.*
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*


class BatchFormFragment : Fragment() {

    lateinit var dataBinding: BatchFormFragmentBinding
    internal var abbreviationMap: MutableMap<String, String> = HashMap()
    private lateinit var ingredientListViewAdapter: IngredientListViewAdapter
    private lateinit var ingredientsRecyclerView: RecyclerView
    private var FORM_MODE: MODES? = null

    val viewModel: MainViewModel by sharedViewModel()

    private val timePickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received TIME_SET_EVENT")
            val hourOfDay = intent.getIntExtra(HOUR, 0)
            val minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0)
            viewModel.batch?.createDate?.set(Calendar.HOUR, hourOfDay)
            viewModel.batch?.createDate?.set(Calendar.MINUTE, minute)
            Timber.d("Time was set by user with TimePickerFragment to %s:%s", hourOfDay, minute)
            updateUiDateTime()
        }
    }
    private val datePickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received DATE_SET_EVENT")
            val year = intent.getIntExtra(DatePickerFragment.YEAR, 0)
            val month = intent.getIntExtra(DatePickerFragment.MONTH, 0)
            val dayOfMonth = intent.getIntExtra(DatePickerFragment.DAY_OF_MONTH, 0)
            viewModel.batch?.createDate?.set(Calendar.YEAR, year)
            viewModel.batch?.createDate?.set(Calendar.MONTH, month)
            viewModel.batch?.createDate?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            Timber.d("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year)
            updateUiDateTime()
        }
    }

    private val ingredientPickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received INGREDIENT_SET_EVENT")
            val ingredientString = intent.getStringExtra(INGREDIENT)
            val amount = intent.getFloatExtra(AMOUNT, 0.0f)
            val unitString = intent.getStringExtra(UNIT)
            Timber.v("Got values from AddIngredientDialog: %s %s %s", ingredientString, amount, unitString)
            val batchIngredient = BatchIngredient()
            batchIngredient.ingredientId = ingredientString
            batchIngredient.batchId = viewModel.batch?.id

            val parsedVolumeUnit = toVolume(amount, abbreviationMap[unitString])
            if (parsedVolumeUnit == null) {
                Timber.d("attempting to get the unit as a mass.")
                batchIngredient.quantityMass = toMass(amount, abbreviationMap[unitString])
                Timber.d("decoded ingredient amount as a mass: $amount ${batchIngredient.quantityMass}")
            } else {
                batchIngredient.quantityVol = parsedVolumeUnit
                Timber.d("decoded ingredient amount as a volume: $amount ${batchIngredient.quantityVol}")
            }
            viewModel.addIngredientToBatch(batchIngredient)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.batch_form_fragment,
                container, false) as BatchFormFragmentBinding
        dataBinding.setLifecycleOwner(this)

        // FIXME: Mess. This should be moved somewhere else so other classes can use it
        abbreviationMap[resources.getString(R.string.DEGREES_C)] = unitToTextAbbr(CELSIUS)
        abbreviationMap[resources.getString(R.string.DEGREES_F)] = unitToTextAbbr(FAHRENHEIT)
        abbreviationMap[resources.getString(R.string.LITER)] = unitToTextAbbr(LITER)
        abbreviationMap[resources.getString(R.string.GALLON_LIQUID_US)] = unitToTextAbbr(GALLON_LIQUID)
        abbreviationMap[resources.getString(R.string.GALLON_DRY_US)] = unitToTextAbbr(GALLON_DRY)
        abbreviationMap[resources.getString(R.string.GALLON_LIQUID_UK)] = unitToTextAbbr(GALLON_UK)
        abbreviationMap[resources.getString(R.string.OUNCE_LIQUID_US)] = unitToTextAbbr(FLUID_OUNCE)
        abbreviationMap[resources.getString(R.string.TEASPOON)] = unitToTextAbbr(TEASPOON)
        abbreviationMap[resources.getString(R.string.GRAM)] = unitToTextAbbr(GRAM)
        abbreviationMap[resources.getString(R.string.KILOGRAM)] = unitToTextAbbr(KILOGRAM)
        abbreviationMap[resources.getString(R.string.OUNCE)] = unitToTextAbbr(OUNCE)
        abbreviationMap[resources.getString(R.string.POUND)] = unitToTextAbbr(POUND)


        val bundle = this.arguments

        val batchId = bundle?.getLong(BATCH_ID, java.lang.Long.MIN_VALUE)
        val recipeId = bundle?.getLong(RECIPE_ID, java.lang.Long.MIN_VALUE)
        if (batchId != java.lang.Long.MIN_VALUE) {
            Timber.i("Got Batch ID %d from the NavigationController. Acting as a Batch Editor.", batchId)
            viewModel.getBatch(batchId).observe(this, Observer<Batch> { batch ->
                if (batch != null) {
                    viewModel.batch = batch
                    dataBinding.batch = batch
                    if (batch.status != null) {
                        dataBinding.status.setText(batch.status.toString())
                    } else {
                        dataBinding.status.setText(PLANNING.toString())
                    }
                    updateUiDateTime()

                    viewModel.getBatchIngredients(batchId!!).observe(this, Observer<List<BatchIngredient>> { batchIngredients ->
                        if (batchIngredients != null) {
                            Timber.v("Loaded %s Batch ingredients", batchIngredients.size)
                            ingredientListViewAdapter.dataSet = batchIngredients
                            ingredientListViewAdapter.notifyDataSetChanged()
                        } else {
                            Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId)
                        }
                        Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch)
                    })
                    updateSpinners(viewModel.batch)
                } else {
                    Timber.e("Got a null Batch!")
                }
            })
        } else {
            Timber.i("No Batch ID was received. Acting as a Batch Creation form.")
            viewModel.batch = Batch()
            viewModel.batch?.createDate = Calendar.getInstance()
            viewModel.getCurrentUser().observe(this, Observer<User> { user ->
                user?.let {
                    Timber.d("Setting batch user ID to %s", it.uid)
                    viewModel.batch?.userId = it.uid
                }
            })
            dataBinding.batch = viewModel.batch
        }

        ingredientListViewAdapter = IngredientListViewAdapter(object : IngredientListViewAdapter.IngredientClickCallback {
            override fun onClick(repo: BatchIngredient) {
                Timber.w("Clicking on a BatchIngredient in this list is not yet supported")
            }
        })
        ingredientsRecyclerView = dataBinding.root.findViewById(R.id.ingredients_list)
        ingredientsRecyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        ingredientsRecyclerView.layoutManager = llm
        ingredientsRecyclerView.adapter = ingredientListViewAdapter

        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.batch != null) {
            // We're in "edit" mode, so set the title accordingly
            FORM_MODE = MODES.EDIT
            val formTitle = dataBinding.root.findViewById<TextView>(R.id.batchFormTitleTV)
            if (formTitle != null) {
                var title = resources.getString(R.string.edit_batch_title)
                title = String.format(title, viewModel.batch?.id)
                formTitle.text = title
            }
        } else {
            FORM_MODE = MODES.CREATE
        }
        updateUiDateTime()

        val dateField = dataBinding.root.findViewById<TextView>(R.id.createDateDate)
        dateField?.setOnClickListener { _ ->
            Timber.i("Date was clicked!")
            val newFragment = DatePickerFragment()
            val args = Bundle()
            viewModel.batch?.createDate?.let {
                args.putInt(YEAR, it.get(Calendar.YEAR))
                args.putInt(MONTH, it.get(Calendar.MONTH))
                args.putInt(DAY_OF_MONTH, it.get(Calendar.DAY_OF_MONTH))
            }
            newFragment.arguments = args
            newFragment.setTargetFragment(this, DATE_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        val timeField = dataBinding.root.findViewById<TextView>(R.id.createDateTime)
        timeField?.setOnClickListener { _ ->
            Timber.i("Time was clicked!")
            val newFragment = TimePickerFragment()
            val args = Bundle()
            viewModel.batch?.createDate?.let {
                args.putInt(HOUR, it.get(Calendar.HOUR))
                args.putInt(MINUTE, it.get(Calendar.MINUTE))
            }
            newFragment.arguments = args
            newFragment.setTargetFragment(this, TIME_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "timePicker")
        }

        val addSugarButton = dataBinding.root.findViewById<Button>(R.id.add_sugar_button)
        addSugarButton?.setOnClickListener { v ->
            Timber.i("addSugarButton was clicked!")
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, SUGAR.toString())
            v.findNavController().navigate(R.id.addIngredientDialog, args)
        }

        val addNutrientButton = dataBinding.root.findViewById<Button>(R.id.add_nutrient_button)
        addNutrientButton?.setOnClickListener { v ->
            Timber.i("addNutrientButton was clicked!")
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, NUTRIENT.toString())
            v.findNavController().navigate(R.id.addIngredientDialog, args)
        }

        val addYeastButton = dataBinding.root.findViewById<Button>(R.id.add_yeast_button)
        addYeastButton?.setOnClickListener { v ->
            Timber.i("addYeastButton was clicked!")
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, YEAST.toString())
            v.findNavController().navigate(R.id.addIngredientDialog, args)
        }

        val addStabilizerButton = dataBinding.root.findViewById<Button>(R.id.add_stabilizer_button)
        addStabilizerButton?.setOnClickListener { v ->
            Timber.i("addStabilizerButton was clicked!")
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, STABILIZER.toString())
            v.findNavController().navigate(R.id.addIngredientDialog, args)
        }

        LocalBroadcastManager.getInstance(activity!!).registerReceiver(datePickerMessageReceiver,
                IntentFilter(DATE_SET_EVENT))
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(timePickerMessageReceiver,
                IntentFilter(TIME_SET_EVENT))
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(ingredientPickerMessageReceiver,
                IntentFilter(INGREDIENT_SET_EVENT))

        val submitButton = activity!!.findViewById<Button>(R.id.button_submit)
        if (submitButton != null) {
            submitButton.setOnClickListener { view ->
                Timber.i("Submit button clicked!")
                FORM_MODE = if (viewModel.batch?.id == null || viewModel.batch?.id == 0L) MODES.CREATE else MODES.EDIT

                viewModel.batch?.let { batch ->
                    batch.name = dataBinding.name.text.toString().trim { it <= ' ' }
                    batch.targetSgStarting = toDouble(dataBinding.targetSgStarting.text.toString().trim { it <= ' ' })
                    batch.targetSgFinal = toDouble(dataBinding.targetSgFinal.text.toString().trim { it <= ' ' })
                    batch.targetABV = toFloat(dataBinding.targetABV.text.toString().trim { it <= ' ' })
                    batch.startingPh = toFloat(dataBinding.startingPh.text.toString().trim { it <= ' ' })
                    batch.startingTemp = toFloat(dataBinding.startingTemp.text.toString().trim { it <= ' ' })

                    batch.outputVolume = toVolume(
                            dataBinding.outputVolumeAmount.text.toString(),
                            abbreviationMap[dataBinding.outputVolumeAmountUnit.selectedItem.toString()])

                    batch.status = BatchStatusEnum.fromString(dataBinding.status.text.toString().trim { it <= ' ' })
                    batch.notes = dataBinding.notes.text.toString().trim { it <= ' ' }

                    if (FORM_MODE == MODES.CREATE) {
                        Timber.d("We are in CREATE mode.")
                        Timber.d("Current batch state:\n%s", viewModel.batch?.toString())
                        viewModel.saveNewBatch(batch).observe(this, Observer<Long> { savedBatchId ->
                            if (savedBatchId != null) {
                                viewModel.batch?.id = savedBatchId
                                Timber.i("Successfully saved Batch, which now has ID=%s", savedBatchId)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(datePickerMessageReceiver)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(timePickerMessageReceiver)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(ingredientPickerMessageReceiver)
                                Snackbar.make(view, "Saved batch!", Snackbar.LENGTH_LONG).show()
                                Answers.getInstance().logCustom(CustomEvent("Batch create success"))
                                Timber.e("Navigation to batch detail of batch #$savedBatchId not yet supported")

                                val bundle = Bundle()
                                bundle.putLong(BATCH_ID, savedBatchId)
                                view.findNavController().navigate(R.id.batchDetailFragment, bundle)
                            } else {
                                Answers.getInstance().logCustom(CustomEvent("Batch create failed"))
                                Snackbar.make(view, "Failed to save batch!", Snackbar.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Timber.d("We are in EDIT mode for batch #%s", batch.id)
                        Timber.d("Current batch state:\n%s", batch.toString())
                        viewModel.updateBatch(batch)
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(datePickerMessageReceiver)
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(timePickerMessageReceiver)
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(ingredientPickerMessageReceiver)

                        Snackbar.make(view, "Updated batch!", Snackbar.LENGTH_LONG).show()
                        Answers.getInstance().logCustom(CustomEvent("Batch edit success"))

                        val bundle = Bundle()
                        bundle.putLong(BATCH_ID, batch.id)
                        view.findNavController().navigate(R.id.batchDetailFragment, bundle)
                    }
                }

            }
        } else {
            Timber.e("Cannot find submit button in view")
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                index = i
                break
            }
        }
        return index
    }

    private fun updateSpinners(batch: Batch?) {
        if (batch == null) {
            Timber.e("Could not update spinners because batch is null")
            return
        }
        batch.outputVolume?.let {
            val volumeAmount = it.value as Double
            val f = DecimalFormat("#.##")
            dataBinding.outputVolumeAmount.setText(f.format(volumeAmount))
            val volSpin = dataBinding.outputVolumeAmountUnit
            val unitString = resources.getString(unitToStringResource(it.unit))
            volSpin.setSelection(getIndex(volSpin, unitString))

            //            volSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            //                    Timber.v("Volume spinner item selected: pos=%s, id=%s, string=%s", pos, id, volSpin.getItemAtPosition(pos).toString()));
            //
            //                    Object item = parent.getItemAtPosition(pos);
            //                }
            //
            //                public void onNothingSelected(AdapterView<?> parent) {
            //                }
            //            });
        }
    }

    private fun updateUiDateTime() {
        viewModel.batch?.let { batch ->
            activity?.let { a ->
                val timeField = a.findViewById<TextView>(R.id.createDateTime)
                if (timeField != null) {
                    timeField.text = calendarToLocaleTime(batch.createDate)
                } else {
                    Timber.e("Could not find createDateTime in View")
                }

                val dateField = a.findViewById<TextView>(R.id.createDateDate)
                if (dateField != null) {
                    dateField.text = calendarToLocaleDate(batch.createDate)
                } else {
                    Timber.e("Could not find createDateDate in View")
                }
            }
        }

    }

    private enum class MODES {
        CREATE, EDIT
    }

    companion object {
        private val DATE_REQUEST_CODE = 1
        private val TIME_REQUEST_CODE = 2
        private val INGREDIENT_REQUEST_CODE = 3
    }
}