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

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.RecipeDetailFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;
import com.fullmeadalchemist.mustwatch.ui.log.LogRecyclerViewAdapter;

import java.text.DecimalFormat;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct;
import static com.fullmeadalchemist.mustwatch.vo.Recipe.RECIPE_ID;


public class RecipeDetailFragment extends LifecycleFragment implements Injectable {

    protected LogRecyclerViewAdapter logsAdapter;
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

        logsAdapter = new LogRecyclerViewAdapter(null, logEntry -> {
            Timber.i("Log entry clicked:\n%s", logEntry.toString());
        });

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initClickListeners();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long recipeId = bundle.getLong(RECIPE_ID, Long.MIN_VALUE);
            Timber.i("Got Recipe ID %d from the NavigationController. Acting as a Batch Editor.", recipeId);

            if (recipeId != Long.MIN_VALUE) {
                viewModel.getRecipe(recipeId).observe(this, recipe -> {
                    if (recipe != null) {
                        Timber.i("Loaded Recipe with ID %d:\n%s", recipe.id, recipe);
                        dataBinding.setRecipe(recipe);
                        viewModel.recipe = recipe;


                        if (viewModel.recipe.startingSG != null && viewModel.recipe.finalSG != null) {
                            double abv_pct = calcAbvPct((double) viewModel.recipe.startingSG, (double) viewModel.recipe.finalSG);
                            DecimalFormat f = new DecimalFormat("0.##");
                            dataBinding.targetABV.setText(String.format(defaultLocale, "%s%%", f.format(abv_pct)));
                        }

                    } else {
                        Timber.w("Received a null Batch from the RecipeDetailViewModel.");
                    }
                });
            }
        } else {
            Timber.i("No Recipe ID was received. Redirecting to the Recipe Creation form.");
            navigationController.navigateToAddBatch();
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
