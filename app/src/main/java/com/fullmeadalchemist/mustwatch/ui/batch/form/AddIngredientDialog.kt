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


import android.app.Dialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.core.ValueParsers.safeLongToInt
import com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AddIngredientDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    internal var spinnerItems: List<Ingredient>? = null

    private var viewModel: BatchFormViewModel? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.ingredient_dialog, null)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchFormViewModel::class.java)

        val typeString = arguments!!.getString(INGREDIENT_TYPE)
        var type: Ingredient.IngredientType?
        type = Ingredient.IngredientType.fromString(typeString!!)
        if (type == null) {
            type = Ingredient.IngredientType.SUGAR
        }
        Timber.d("Got INGREDIENT_TYPE=%s", type)
        spinnerItems = ArrayList()
        val ingredientsSpinnerObjects: LiveData<List<Ingredient>>
        var unitsSpinnerResource = 0
        when (type) {
            Ingredient.IngredientType.YEAST -> {
                unitsSpinnerResource = R.array.mass_units_list
                ingredientsSpinnerObjects = viewModel!!.yeasts
            }
            Ingredient.IngredientType.NUTRIENT -> {
                unitsSpinnerResource = R.array.mass_units_list
                ingredientsSpinnerObjects = viewModel!!.nutrients
            }
            Ingredient.IngredientType.STABILIZER -> {
                unitsSpinnerResource = R.array.mass_units_list
                ingredientsSpinnerObjects = viewModel!!.stabilizers
            }
            Ingredient.IngredientType.SUGAR -> {
                unitsSpinnerResource = R.array.sugar_units_list
                ingredientsSpinnerObjects = viewModel!!.sugars
            }
        }

        ingredientsSpinnerObjects.observe(this, Observer<List<Ingredient>> { ingredients ->
            if (ingredients != null) {
                Timber.d("updating ingredients spinner with %d objects", ingredients.size)
                spinnerItems = ingredients
                val stringifiedIngredients = ArrayList<String>(ingredients.size)
                for (ingredient in ingredients) {
                    if (ingredient != null) {
                        val resID = resources.getIdentifier(ingredient.id,
                                "string", this.activity!!.packageName)
                        stringifiedIngredients.add(getString(resID))
                    } else {
                        Timber.e("Got a null ingredient from the database!?")
                    }
                }
                val ingredientsSpinner = view.findViewById<Spinner>(R.id.ingredients_spinner)
                val ingredientsAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, stringifiedIngredients)
                ingredientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ingredientsSpinner.adapter = ingredientsAdapter
            }
        })

        val unitsSpinner = view.findViewById<Spinner>(R.id.quantity_unit_spinner)
        val unitsAdapter = ArrayAdapter.createFromResource(activity!!,
                unitsSpinnerResource, android.R.layout.simple_spinner_item)
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitsSpinner.adapter = unitsAdapter

        return AlertDialog.Builder(activity!!).setView(view).setPositiveButton(android.R.string.ok) { dialog, which ->
            val spinner = view.findViewById<Spinner>(R.id.ingredients_spinner)
            val selectedItemId = safeLongToInt(spinner.selectedItemId)
            val selectedIngredient = spinnerItems!![selectedItemId]
            Timber.d("Registered spinner number %d selected, corresponding to ingredient %s", selectedItemId, selectedIngredient.id)

            val qtyAmount = view.findViewById<TextView>(R.id.quantity_amount)
            val qtyAmountValueText = qtyAmount.text.toString()

            val qtyValue = toFloat(qtyAmountValueText, 0.0f)

            //Spinner unitsSpinner = view.findViewById(R.id.quantity_unit_spinner);
            val selectedUnit = unitsSpinner.selectedItem.toString()

            Timber.d("Ingredient selected: %s, %s %s", selectedIngredient.id, qtyValue, selectedUnit)

            val intent = Intent(INGREDIENT_SET_EVENT)
            intent.putExtra(INGREDIENT, selectedIngredient.id)
            intent.putExtra(AMOUNT, qtyValue)
            intent.putExtra(UNIT, selectedUnit)
            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
        }.setNegativeButton(android.R.string.cancel, null).create()
    }

    companion object {

        val INGREDIENT_TYPE = "INGREDIENT_TYPE"
        val INGREDIENT_SET_EVENT = "INGREDIENT_SET_EVENT"

        // Broadcast keys
        val INGREDIENT = "INGREDIENT"
        val AMOUNT = "AMOUNT"
        val UNIT = "UNIT"
    }


}