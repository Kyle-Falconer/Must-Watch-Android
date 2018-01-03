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

package com.fullmeadalchemist.mustwatch.ui.batch.form;


import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.measure.Quantity;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr;
import static com.fullmeadalchemist.mustwatch.core.ValueParsers.safeLongToInt;
import static com.fullmeadalchemist.mustwatch.core.ValueParsers.toFloat;

public class AddIngredientDialog extends DialogFragment implements Injectable {

    public static final String INGREDIENT_TYPE = "INGREDIENT_TYPE";
    public static final String INGREDIENT_SET_EVENT = "INGREDIENT_SET_EVENT";

    // Broadcast keys
    public static final String INGREDIENT = "INGREDIENT";
    public static final String AMOUNT = "AMOUNT";
    public static final String UNIT = "UNIT";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    List<Ingredient> spinnerItems;

    private BatchFormViewModel viewModel;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ingredient_dialog, null);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchFormViewModel.class);

        String typeString = getArguments().getString(INGREDIENT_TYPE);
        Ingredient.IngredientType type;
        type = Ingredient.IngredientType.fromString(typeString);
        if (type == null) {
            type = Ingredient.IngredientType.SUGAR;
        }
        Timber.d("Got INGREDIENT_TYPE=%s", type);
        spinnerItems = new ArrayList<>();
        LiveData<List<Ingredient>> ingredientsSpinnerObjects;
        int unitsSpinnerResource = 0;
        switch (type) {
            case YEAST:
                unitsSpinnerResource = R.array.mass_units_list;
                ingredientsSpinnerObjects = viewModel.getYeasts();
                break;
            case NUTRIENT:
                unitsSpinnerResource = R.array.mass_units_list;
                ingredientsSpinnerObjects = viewModel.getNutrients();
                break;
            case STABILIZER:
                unitsSpinnerResource = R.array.mass_units_list;
                ingredientsSpinnerObjects = viewModel.getStabilizers();
                break;
            case SUGAR:
            default:
                unitsSpinnerResource = R.array.sugar_units_list;
                ingredientsSpinnerObjects = viewModel.getSugars();
                break;
        }

        ingredientsSpinnerObjects.observe(this, ingredients -> {
            if (ingredients != null){
                Timber.d("updating ingredients spinner with %d objects", ingredients.size());
                spinnerItems = ingredients;
                List<String> stringifiedIngredients = new ArrayList<>(ingredients.size());
                for (Ingredient ingredient : ingredients) {
                    if (ingredient != null) {
                        int resID = getResources().getIdentifier(ingredient.id,
                                "string", this.getActivity().getPackageName());
                        stringifiedIngredients.add(getString(resID));
                    } else {
                        Timber.e("Got a null ingredient from the database!?");
                    }
                }
                Spinner ingredientsSpinner = view.findViewById(R.id.ingredients_spinner);
                ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stringifiedIngredients);
                ingredientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ingredientsSpinner.setAdapter(ingredientsAdapter);
            }
        });

        Spinner unitsSpinner = view.findViewById(R.id.quantity_unit_spinner);
        ArrayAdapter<CharSequence> unitsAdapter = ArrayAdapter.createFromResource(getActivity(),
                unitsSpinnerResource, android.R.layout.simple_spinner_item);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsAdapter);

        return new AlertDialog.Builder(getActivity()).setView(view).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Spinner spinner = view.findViewById(R.id.ingredients_spinner);
            int selectedItemId = safeLongToInt(spinner.getSelectedItemId());
            Ingredient selectedIngredient = spinnerItems.get(selectedItemId);
            Timber.d("Registered spinner number %d selected, corresponding to ingredient %s", selectedItemId, selectedIngredient.id);

            TextView qtyAmount = view.findViewById(R.id.quantity_amount);
            String qtyAmountValueText = qtyAmount.getText().toString();

            float qtyValue = toFloat(qtyAmountValueText, 0.0f);

            //Spinner unitsSpinner = view.findViewById(R.id.quantity_unit_spinner);
            String selectedUnit = unitsSpinner.getSelectedItem().toString();

            Timber.d("Ingredient selected: %s, %s %s", selectedIngredient.id, qtyValue, selectedUnit);

            Intent intent = new Intent(INGREDIENT_SET_EVENT);
            intent.putExtra(INGREDIENT, selectedIngredient.id);
            intent.putExtra(AMOUNT, qtyValue);
            intent.putExtra(UNIT, selectedUnit);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }).setNegativeButton(android.R.string.cancel, null).create();
    }


}