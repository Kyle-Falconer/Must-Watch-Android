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

package com.fullmeadalchemist.mustwatch.ui.recipe.detail;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.RecipeDetailFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.BatchIngredientView;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.text.DecimalFormat;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct;
import static com.fullmeadalchemist.mustwatch.vo.Recipe.RECIPE_ID;


public class RecipeDetailFragment extends LifecycleFragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    RecipeDetailFragmentBinding dataBinding;
    private RecipeDetailViewModel viewModel;
    private Locale defaultLocale = Locale.getDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail_fragment,
                container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeDetailViewModel.class);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initClickListeners();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long recipeId = bundle.getLong(RECIPE_ID, Long.MIN_VALUE);
            Timber.v("Got Recipe ID %d from the NavigationController.", recipeId);

            if (recipeId != Long.MIN_VALUE) {
                if (viewModel.recipe != null) {
                    Timber.v("Reusing viewmodel data");
                    dataBinding.setRecipe(viewModel.recipe);
                    updateRecipeUiInfo();
                    updateRecipeIngredientUiInfo();
                } else {
                    Timber.v("Going to the RecipeRepository to get the Recipe with id %s", recipeId);
                    viewModel.getRecipe(recipeId).observe(this, recipe -> {
                        if (recipe != null) {
                            Timber.v("Loaded Recipe with ID %d:\n%s", recipe.id, recipe);

                            viewModel.recipe = recipe;
                            dataBinding.setRecipe(viewModel.recipe);
                            updateRecipeUiInfo();

                            viewModel.getRecipeIngredients(recipeId).observe(this, recipeIngredients -> {
                                if (recipeIngredients != null) {
                                    Timber.v("Loaded %s Recipe ingredients", recipeIngredients.size());
                                    viewModel.recipe.ingredients = recipeIngredients;
                                    updateRecipeIngredientUiInfo();
                                } else {
                                    Timber.w("Received nothing from the RecipeRepository when trying to get ingredients for Recipe %s", recipeId);
                                }
                            });

                        } else {
                            Timber.w("Received a null Batch from the RecipeDetailViewModel.");
                        }
                    });
                }
            }
        } else {
            Timber.i("No Recipe ID was received. Redirecting to the Batch Creation form.");
            navigationController.navigateToAddBatch();
        }
    }

    private void updateRecipeUiInfo() {
        if (viewModel.recipe.startingSG != null && viewModel.recipe.finalSG != null) {
            double abv_pct = calcAbvPct((double) viewModel.recipe.startingSG, (double) viewModel.recipe.finalSG);
            DecimalFormat f = new DecimalFormat("0.##");
            dataBinding.targetABV.setText(String.format(defaultLocale, "%s%%", f.format(abv_pct)));
        }
    }

    private void updateRecipeIngredientUiInfo() {
        if (viewModel.recipe.ingredients != null) {
            // FIXME: this is not performant and looks ghetto.
            Timber.d("Found %s BatchIngredients for this Recipe; adding them to the ingredientsList", viewModel.recipe.ingredients.size());
            LinearLayout ingredientsList = getActivity().findViewById(R.id.ingredients_list);
            ingredientsList.removeAllViews();
            for (BatchIngredient ingredient : viewModel.recipe.ingredients) {
                BatchIngredientView ingredientText = new BatchIngredientView(getActivity());
                ingredientText.setBatchIngredient(ingredient);
                ingredientsList.addView(ingredientText);
            }
        } else {
            Timber.d("No Ingredients found for this Recipe.");
        }
    }

    private void initClickListeners() {
        Button submitButton = getActivity().findViewById(R.id.button_create_batch_from_recipe);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Timber.i("Create From Recipe button clicked");
                navigationController.navigateToCreateFromBatch(viewModel.recipe.id);
            });
        }
    }
}
