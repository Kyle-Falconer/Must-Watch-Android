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

package com.fullmeadalchemist.mustwatch.core;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.IngredientRepository;
import com.fullmeadalchemist.mustwatch.repository.RecipeRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;
import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.generateDummyBatchesWithData;

public class HeadlessLoadingFragment extends Fragment implements Injectable {

    private boolean updated = false;

    @Inject
    BatchRepository batchRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    IngredientRepository ingredientRepository;
    @Inject
    RecipeRepository recipeRepository;

    private LifecycleOwner lifecycleContext;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        lifecycleContext = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        populateDb();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("Finished! Shutting down headless fragment.");
    }

    private void populateDb() {
        if (updated) {
            return;
        }
        updated = true;

        Timber.i("Populating database...");


        Observable.create(emitter -> {

            ingredientRepository.getAll().observe(lifecycleContext, ingredients -> {
                Timber.w("was notified that the ingredients have changed");

                // Ingredients loader
                // FIXME: move to IngredientRepository
                // FIXME: check for differences as well and update existing
                if (ingredients != null && ingredients.size() == 0) {
                    JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.ingredients);
                    Ingredient[] jsonObj = reader.constructUsingGson(Ingredient[].class);
                    if (jsonObj.length == 0) {
                        Timber.e("Loaded no ingredients from the JSON resources!");
                        return;
                    }

                    Timber.i("Populating the database with Ingredient data");
                    ingredientRepository.addIngredients(jsonObj);

                } else if (ingredients != null && ingredients.size() > 0) {
                    recipeRepository.getPublicRecipes().observe(lifecycleContext, recipes -> {
                        Timber.w("was notified that the recipes have changed");

                        // Recipes loader
                        // FIXME: move to RecipeRepository
                        // FIXME: check for differences as well and update existing
                        if (recipes != null && recipes.size() == 0) {
                            JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.recipes);
                            Recipe[] recipeJsonObj = reader.constructUsingGson(Recipe[].class);
                            if (recipeJsonObj.length == 0) {
                                Timber.e("Loaded no recipes from the JSON resources!");
                                return;
                            }
                            for (Recipe r : recipeJsonObj) {
                                r.publicReadable = true;
                            }

                            Timber.d("Populating the database with Recipe data");
                            recipeRepository.addRecipes(recipeJsonObj);
                        } else {
                            Timber.d("Recipes already found in the database.");
                        }
                    });
                } else {
                    Timber.d("Ingredients already found in the database.");
                }
            });

            // Dummy data loader
            userRepository.getCurrentUserId().observe(lifecycleContext, userId -> {
                Timber.w("was notified that the current user has changed");
                if (userId != null) {
                    batchRepository.getBatches().observe(lifecycleContext, batches -> {
                        if (batches != null && batches.size() == 0) {
                            Timber.d("Got user with no batches; generating batches...");
                            List<Batch> dummyBatches = generateDummyBatchesWithData(userId, 20);
                            batchRepository.addBatches(dummyBatches);
                        } else {
                            Timber.d("Got user with %d batches.",
                                    (batches == null) ? "null" : batches.size());
                        }
                    });
                }
            });
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
