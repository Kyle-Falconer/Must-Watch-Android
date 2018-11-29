/*
 * Copyright (c) 2017-2018 Full Mead Alchemist, LLC.
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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.ValueParsers.safeLongToInt
import com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import org.jetbrains.anko.sdk15.coroutines.onClick
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class AddIngredientDialog : Fragment() {

    private var ingredientsSpinner: Spinner? = null
    private var unitsSpinner: Spinner? = null
    private var qtyTextView: TextView? = null
    private var okButton: Button? = null
    private var cancelButton: Button? = null


    val viewModel: MainViewModel by sharedViewModel()

    private var spinnerItems: List<Ingredient>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ingredient_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { a ->
            ingredientsSpinner = a.findViewById(R.id.ingredients_spinner)
            unitsSpinner = a.findViewById(R.id.quantity_unit_spinner)
            qtyTextView = a.findViewById(R.id.quantity_amount)

            okButton = a.findViewById(R.id.saveButton)
            cancelButton = a.findViewById(R.id.cancelButton)

            val typeString = arguments!!.getString(INGREDIENT_TYPE)
            var type: Ingredient.IngredientType?
            type = Ingredient.IngredientType.fromString(typeString!!)
            if (type == null) {
                type = Ingredient.IngredientType.SUGAR
            }
            Timber.d("Got INGREDIENT_TYPE=%s", type)
            spinnerItems = arrayListOf()
            val ingredientsSpinnerObjects: LiveData<List<Ingredient>>
            var unitsSpinnerResource = 0
            when (type) {
                Ingredient.IngredientType.YEAST -> {
                    unitsSpinnerResource = R.array.mass_units_list
                    ingredientsSpinnerObjects = viewModel.getYeasts()
                }
                Ingredient.IngredientType.NUTRIENT -> {
                    unitsSpinnerResource = R.array.mass_units_list
                    ingredientsSpinnerObjects = viewModel.getNutrients()
                }
                Ingredient.IngredientType.STABILIZER -> {
                    unitsSpinnerResource = R.array.mass_units_list
                    ingredientsSpinnerObjects = viewModel.getStabilizers()
                }
                Ingredient.IngredientType.SUGAR -> {
                    unitsSpinnerResource = R.array.sugar_units_list
                    ingredientsSpinnerObjects = viewModel.getSugars()
                }
            }

            ingredientsSpinnerObjects.observe(this, Observer<List<Ingredient>> { ingredients ->
                if (ingredients != null) {
                    Timber.d("updating ingredients spinner with %d objects", ingredients.size)
                    spinnerItems = ingredients
                    val stringifiedIngredients = ArrayList<String>(ingredients.size)
                    for (ingredient in ingredients) {
                        val resID = resources.getIdentifier(ingredient.id,
                                "string", this.activity!!.packageName)
                        stringifiedIngredients.add(getString(resID))
                    }
                    val ingredientsAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, stringifiedIngredients)
                    ingredientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    ingredientsSpinner?.adapter = ingredientsAdapter
                }
            })

            val unitsAdapter = ArrayAdapter.createFromResource(activity!!,
                    unitsSpinnerResource, android.R.layout.simple_spinner_item)
            unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitsSpinner?.adapter = unitsAdapter


        }
        initClickListeners()
    }

    private fun initClickListeners() {

        okButton?.onClick { view ->
            Timber.d("ok button was pressed")
            ingredientsSpinner?.let { spinner ->

                val selectedItemId = safeLongToInt(spinner.selectedItemId)
                val selectedIngredient = spinnerItems?.get(selectedItemId)
                Timber.d("Registered spinner number %d selected, corresponding to ingredient %s", selectedItemId, selectedIngredient?.id)

                val qtyAmountValueText = qtyTextView?.text.toString()
                val qtyValue = toFloat(qtyAmountValueText, 0.0f)
                val selectedUnit = unitsSpinner?.selectedItem.toString()
                Timber.d("Ingredient selected: %s, %s %s", selectedIngredient?.id, qtyValue, selectedUnit)


                val intent = Intent(INGREDIENT_SET_EVENT)
                intent.putExtra(INGREDIENT, selectedIngredient?.id)
                intent.putExtra(AMOUNT, qtyValue)
                intent.putExtra(UNIT, selectedUnit)
                LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
                fragmentManager?.popBackStack()
            }
        }

        cancelButton?.onClick {
            Timber.d("cancel button was pressed")
            fragmentManager?.popBackStack()
        }
    }

    companion object {
        const val INGREDIENT_TYPE = "INGREDIENT_TYPE"
        const val INGREDIENT_SET_EVENT = "INGREDIENT_SET_EVENT"

        // Broadcast keys
        const val INGREDIENT = "INGREDIENT"
        const val AMOUNT = "AMOUNT"
        const val UNIT = "UNIT"
    }
}