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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.RecipeRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;
import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.List;

import javax.inject.Inject;


public class RecipeDetailViewModel extends ViewModel {

    public Recipe recipe;
    private RecipeRepository recipeRepository;
    private UserRepository userRepository;

    @Inject
    public RecipeDetailViewModel(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Recipe> getRecipe(Long id) {
        return recipeRepository.getRecipe(id);
    }

    public LiveData<List<BatchIngredient>> getRecipeIngredients(Long recipeId) {
        return recipeRepository.getRecipeIngredients(recipeId);
    }

    public LiveData<Long> getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }

}
