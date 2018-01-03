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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchIngredientRepository;
import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.IngredientRepository;
import com.fullmeadalchemist.mustwatch.repository.RecipeRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;
import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class BatchFormViewModel extends ViewModel {

    protected Batch batch;
    private BatchRepository batchRepository;
    private BatchIngredientRepository batchIngredientRepository;
    private RecipeRepository recipeRepository;
    private IngredientRepository ingredientRepository;
    private UserRepository userRepository;

    @Inject
    public BatchFormViewModel(BatchRepository batchRepository, BatchIngredientRepository batchIngredientRepository, RecipeRepository recipeRepository, IngredientRepository ingredientRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.batchIngredientRepository = batchIngredientRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchRepository.getBatch(id);
    }

    public void addBatch(Batch batch) {
        if (batch.ingredients == null) {
            batch.ingredients = new ArrayList<>();
        }
        batchRepository.addBatch(batch);
    }

    void addIngredient(BatchIngredient batchIngredient) {
        if (batch.ingredients == null){
            batch.ingredients = new ArrayList<>();
        }
        batch.ingredients.add(batchIngredient);
    }

    public LiveData<Long> getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }

    public void updateBatch() {
        if (batch.ingredients == null){
            batch.ingredients = new ArrayList<>();
        }
        batchRepository.updateBatch(batch);
    }

    public LiveData<Long> saveNewBatch() {
        return batchRepository.addBatch(batch);
    }

    public LiveData<Recipe> getRecipe(long recipeId) {
        return recipeRepository.getRecipe(recipeId);
    }

    public LiveData<Ingredient> getIngredient(String id) {
        return ingredientRepository.getIngredientById(id);
    }

    public LiveData<List<Ingredient>> getSugars() {
        return ingredientRepository.getSugarEntries();
    }

    public LiveData<List<Ingredient>> getNutrients() {
        return ingredientRepository.getNutrientEntries();
    }

    public LiveData<List<Ingredient>> getYeasts() {
        return ingredientRepository.getYeastEntries();
    }

    public LiveData<List<Ingredient>> getStabilizers() {
        return ingredientRepository.getStabilizerEntries();
    }

    public LiveData<List<BatchIngredient>> getBatchIngredients(long batchId) {
        return batchRepository.getBatchIngredients(batchId);
    }

    public LiveData<List<Recipe>> getRecipes(Long userId) {
        if (userId == null) {
            return recipeRepository.getPublicRecipes();
        } else {
            return recipeRepository.getRecipes(userId);
        }
    }

    public LiveData<List<BatchIngredient>> getRecipeIngredients(long recipeId) {
        return recipeRepository.getRecipeIngredients(recipeId);
    }
}
