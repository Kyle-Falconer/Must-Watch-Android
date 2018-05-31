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
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.fullmeadalchemist.mustwatch.R
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
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.*
import com.fullmeadalchemist.mustwatch.ui.common.DatePickerFragment.YEAR
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.HOUR
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.MINUTE
import com.fullmeadalchemist.mustwatch.ui.common.TimePickerFragment.TIME_SET_EVENT
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum
import com.fullmeadalchemist.mustwatch.vo.Batch.BatchStatusEnum.PLANNING
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Ingredient.IngredientType.*
import com.fullmeadalchemist.mustwatch.vo.Recipe
import com.fullmeadalchemist.mustwatch.vo.Recipe.Companion.RECIPE_ID
import dagger.android.support.AndroidSupportInjection
import systems.uom.common.Imperial.GALLON_UK
import systems.uom.common.Imperial.OUNCE_LIQUID
import systems.uom.common.USCustomary.FAHRENHEIT
import systems.uom.common.USCustomary.FLUID_OUNCE
import systems.uom.common.USCustomary.GALLON_DRY
import systems.uom.common.USCustomary.GALLON_LIQUID
import systems.uom.common.USCustomary.LITER
import systems.uom.common.USCustomary.OUNCE
import systems.uom.common.USCustomary.POUND
import systems.uom.common.USCustomary.TEASPOON
import tec.units.ri.unit.Units.CELSIUS
import tec.units.ri.unit.Units.GRAM
import tec.units.ri.unit.Units.KILOGRAM
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*


class BatchFormFragment : Fragment() {

    lateinit var dataBinding: BatchFormFragmentBinding
    internal var abbreviationMap: MutableMap<String, String> = HashMap()
    private var FORM_MODE: MODES? = null
    lateinit var viewModel: BatchFormViewModel

    private val timePickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Received TIME_SET_EVENT")
            val hourOfDay = intent.getIntExtra(HOUR, 0)
            val minute = intent.getIntExtra(TimePickerFragment.MINUTE, 0)
            viewModel.batch.value?.createDate?.set(Calendar.HOUR, hourOfDay)
            viewModel.batch.value?.createDate?.set(Calendar.MINUTE, minute)
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
            viewModel.batch.value?.createDate?.set(Calendar.YEAR, year)
            viewModel.batch.value?.createDate?.set(Calendar.MONTH, month)
            viewModel.batch.value?.createDate?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            Timber.d("Date was set by user with DatePickerFragment to %s/%s/%s", dayOfMonth, month, year)
            updateUiDateTime()
        }
    }

    private val ingredientPickerMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (activity == null) {
                // FIXME: the app will crash if this is true.
                Timber.e("Application Context is null")
            }
            Timber.d("Received INGREDIENT_SET_EVENT")
            val ingredientString = intent.getStringExtra(INGREDIENT)
            val amount = intent.getFloatExtra(AMOUNT, 0.0f)
            val unitString = intent.getStringExtra(UNIT)
            Timber.v("Got values from AddIngredientDialog: %s %s %s", ingredientString, amount, unitString)
            val batchIngredient = BatchIngredient()
            batchIngredient.ingredientId = ingredientString

            batchIngredient.quantityVol = toVolume(amount, abbreviationMap[unitString])
            viewModel.addIngredient(batchIngredient)
            updateUiIngredientsTable()
        }
    }

    private fun updateUiIngredientsTable() {
        view?.let { v ->
            viewModel.batch.value?.ingredients?.let { ingredients ->
                // FIXME: this is not performant and looks ghetto.
                Timber.d("Found %s BatchIngredients for this Batch; adding them to the ingredientsList", ingredients.size)
                val ingredientsList = v.findViewById<LinearLayout>(R.id.ingredients_list)
                ingredientsList.removeAllViews()
                for (ingredient in ingredients) {
                    val ingredientView = BatchIngredientView(activity)
                    ingredientView.setBatchIngredient(ingredient)
                    ingredientsList.addView(ingredientView)
                }
            }
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
        abbreviationMap[resources.getString(R.string.OUNCE_LIQUID_UK)] = unitToTextAbbr(OUNCE_LIQUID)
        abbreviationMap[resources.getString(R.string.TEASPOON)] = unitToTextAbbr(TEASPOON)
        abbreviationMap[resources.getString(R.string.GRAM)] = unitToTextAbbr(GRAM)
        abbreviationMap[resources.getString(R.string.KILOGRAM)] = unitToTextAbbr(KILOGRAM)
        abbreviationMap[resources.getString(R.string.OUNCE)] = unitToTextAbbr(OUNCE)
        abbreviationMap[resources.getString(R.string.POUND)] = unitToTextAbbr(POUND)

        viewModel = ViewModelProviders.of(this).get(BatchFormViewModel::class.java)

        val bundle = this.arguments
        if (bundle != null) {
            val batchId = bundle.getLong(BATCH_ID, java.lang.Long.MIN_VALUE)
            val recipeId = bundle.getLong(RECIPE_ID, java.lang.Long.MIN_VALUE)
            if (batchId != java.lang.Long.MIN_VALUE) {
                Timber.i("Got Batch ID %d from the NavigationController. Acting as a Batch Editor.", batchId)
                viewModel.getBatch(batchId).observe(this, Observer<Batch> { batch ->
                    if (batch != null) {
                        viewModel.batch.value = batch
                        //dataBinding. .batch = batch
                        if (batch.status != null) {
                            dataBinding.status.setText(batch.status.toString())
                        } else {
                            dataBinding.status.setText(PLANNING.toString())
                        }
                        updateUiDateTime()
                        viewModel.getBatchIngredients(batchId).observe(this, Observer<List<BatchIngredient>> { batchIngredients ->
                            if (batchIngredients != null) {
                                Timber.v("Loaded %s Batch ingredients", batchIngredients.size)
                                viewModel.batch.value?.ingredients = batchIngredients
                                updateUiIngredientsTable()
                            } else {
                                Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId)
                            }
                            Timber.i("Loaded Batch with ID %d:\n%s", batch.id, batch)
                        })
                        updateSpinners(viewModel.batch.value)
                    } else {
                        Timber.e("Got a null Batch!")
                    }
                })
            } else if (recipeId != java.lang.Long.MIN_VALUE) {
                viewModel.getRecipe(recipeId).observe(this, Observer<Recipe> { recipe ->
                    if (recipe != null) {
                        viewModel.batch.value = Batch()
                        viewModel.batch.value?.name = recipe.name
                        viewModel.batch.value?.outputVolume = recipe.outputVol
                        viewModel.batch.value?.targetSgStarting = recipe.startingSG
                        viewModel.batch.value?.targetSgFinal = recipe.finalSG
                        viewModel.batch.value?.status = PLANNING
                        viewModel.batch.value?.createDate = Calendar.getInstance()
                        viewModel.currentUserId.observe(this, Observer<Long> { userId ->
                            if (userId != null) {
                                Timber.d("Setting batch user ID to %s", userId)
                                viewModel.batch.value?.userId = userId
                            } else {
                                Timber.e("Could not set the Batch User ID, since it's null?!")
                            }
                        })

                        dataBinding.status.setText(viewModel.batch.value?.status.toString())
                        updateUiDateTime()
                        viewModel.getRecipeIngredients(recipeId).observe(this, Observer<List<BatchIngredient>> { recipeIngredients ->
                            if (recipeIngredients != null) {
                                Timber.v("Loaded %s Recipe ingredients", recipeIngredients.size)
                                viewModel.batch.value?.ingredients = recipeIngredients
                                updateUiIngredientsTable()
                            } else {
                                Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Batch %s", batchId)
                            }
                            Timber.i("Loaded Recipe with ID %d:\n%s", recipe.id, recipe)
                        })
                        updateSpinners(viewModel.batch.value)
                    } else {
                        Timber.e("Got a null Recipe!")
                    }
                })
            }
        } else {
            Timber.i("No Batch ID was received. Acting as a Batch Creation form.")
            viewModel.batch.value = Batch()
            viewModel.batch.value?.createDate = Calendar.getInstance()
            viewModel.currentUserId.observe(this, Observer<Long> { userId ->
                if (userId != null) {
                    Timber.d("Setting batch user ID to %s", userId)
                    viewModel.batch.value?.userId = userId
                } else {
                    Timber.e("Could not set the Batch User ID, since it's null?!")
                }
            })
//            dataBinding.batch = viewModel.batch
        }
        return dataBinding.root
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.batch.value != null) {
            // We're in "edit" mode, so set the title accordingly
            FORM_MODE = MODES.EDIT
            val formTitle = activity!!.findViewById<TextView>(R.id.batchFormTitleTV)
            if (formTitle != null) {
                var title = resources.getString(R.string.edit_batch_title)
                title = String.format(title, viewModel.batch.value?.id)
                formTitle.text = title
            }
        } else {
            FORM_MODE = MODES.CREATE
        }
        updateUiDateTime()

        val dateField = activity!!.findViewById<TextView>(R.id.createDateDate)
        dateField?.setOnClickListener { _ ->
            Timber.i("Date was clicked!")
            val newFragment = DatePickerFragment()
            val args = Bundle()
            viewModel.batch.value?.createDate?.let {
                args.putInt(YEAR, it.get(Calendar.YEAR))
                args.putInt(MONTH, it.get(Calendar.MONTH))
                args.putInt(DAY_OF_MONTH, it.get(Calendar.DAY_OF_MONTH))
            }
            newFragment.arguments = args
            newFragment.setTargetFragment(this, DATE_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        val timeField = activity!!.findViewById<TextView>(R.id.createDateTime)
        timeField?.setOnClickListener { _ ->
            Timber.i("Time was clicked!")
            val newFragment = TimePickerFragment()
            val args = Bundle()
            viewModel.batch.value?.createDate?.let {
                args.putInt(HOUR, it.get(Calendar.HOUR))
                args.putInt(MINUTE, it.get(Calendar.MINUTE))
            }
            newFragment.arguments = args
            newFragment.setTargetFragment(this, TIME_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "timePicker")
        }

        val addSugarButton = activity!!.findViewById<Button>(R.id.add_sugar_button)
        addSugarButton?.setOnClickListener { _ ->
            Timber.i("addSugarButton was clicked!")
            val newFragment = AddIngredientDialog()
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, SUGAR.toString())
            newFragment.arguments = args
            newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "sugarPicker")
        }

        val addNutrientButton = activity!!.findViewById<Button>(R.id.add_nutrient_button)
        addNutrientButton?.setOnClickListener { _ ->
            Timber.i("addNutrientButton was clicked!")
            val newFragment = AddIngredientDialog()
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, NUTRIENT.toString())
            newFragment.arguments = args
            newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "nutrientPicker")
        }

        val addYeastButton = activity!!.findViewById<Button>(R.id.add_yeast_button)
        addYeastButton?.setOnClickListener { _ ->
            Timber.i("addYeastButton was clicked!")
            val newFragment = AddIngredientDialog()
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, YEAST.toString())
            newFragment.arguments = args
            newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "yeastPicker")
        }

        val addStabilizerButton = activity!!.findViewById<Button>(R.id.add_stabilizer_button)
        addStabilizerButton?.setOnClickListener { _ ->
            Timber.i("addStabilizerButton was clicked!")
            val newFragment = AddIngredientDialog()
            val args = Bundle()
            args.putString(INGREDIENT_TYPE, STABILIZER.toString())
            newFragment.arguments = args
            newFragment.setTargetFragment(this, INGREDIENT_REQUEST_CODE)
            newFragment.show(activity!!.supportFragmentManager, "stabilizerPicker")
        }

        LocalBroadcastManager.getInstance(activity!!).registerReceiver(datePickerMessageReceiver,
                IntentFilter(DATE_SET_EVENT))
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(timePickerMessageReceiver,
                IntentFilter(TIME_SET_EVENT))
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(ingredientPickerMessageReceiver,
                IntentFilter(INGREDIENT_SET_EVENT))

        val submitButton = activity!!.findViewById<Button>(R.id.button_submit)
        if (submitButton != null) {
            submitButton.setOnClickListener { _ ->
                Timber.i("Submit button clicked!")
                FORM_MODE = if (viewModel.batch.value?.id == null) MODES.CREATE else MODES.EDIT

                viewModel.batch.value?.let { batch ->
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
                        Timber.d("Current batch state:\n%s", viewModel.batch.value?.toString())
                        viewModel.saveNewBatch(batch).observe(this, Observer<Long> { savedBatchId ->
                            if (savedBatchId != null) {
                                Timber.i("Successfully saved Batch, which now has ID=%s", savedBatchId)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(datePickerMessageReceiver)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(timePickerMessageReceiver)
                                LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(ingredientPickerMessageReceiver)
                                Snackbar.make(activity!!.findViewById(R.id.container), "Saved batch!", Snackbar.LENGTH_LONG).show()
                                Answers.getInstance().logCustom(CustomEvent("Batch create success"))
                                Timber.e("Navigation to batch detail of batch #${savedBatchId} not yet supported")
                                //navigationController.navigateFromAddBatch(savedBatchId)
                            } else {
                                Answers.getInstance().logCustom(CustomEvent("Batch create failed"))
                                Snackbar.make(activity!!.findViewById(R.id.container), "Failed to save batch!", Snackbar.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Timber.d("We are in EDIT mode for batch #%s", batch.id)
                        Timber.d("Current batch state:\n%s", batch.toString())
                        viewModel.updateBatch()
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(datePickerMessageReceiver)
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(timePickerMessageReceiver)
                        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(ingredientPickerMessageReceiver)
                        Snackbar.make(activity!!.findViewById(R.id.container), "Updated batch!", Snackbar.LENGTH_LONG).show()
                        Answers.getInstance().logCustom(CustomEvent("Batch edit success"))

                        Timber.e("Navigation to batch detail of batch #${batch.id} not yet supported")
//                    navigationController.navigateFromEditBatch(viewModel.batch.value?.id)
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
        if (viewModel.batch.value == null) {
            return
        }
        val timeField = activity!!.findViewById<TextView>(R.id.createDateTime)
        if (timeField != null) {
            timeField.text = calendarToLocaleTime(viewModel.batch.value?.createDate)
        } else {
            Timber.e("Could not find createDateTime in View")
        }

        val dateField = activity!!.findViewById<TextView>(R.id.createDateDate)
        if (dateField != null) {
            dateField.text = calendarToLocaleDate(viewModel.batch.value?.createDate)
        } else {
            Timber.e("Could not find createDateDate in View")
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