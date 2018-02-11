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

package com.fullmeadalchemist.mustwatch.ui.recipe

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.fullmeadalchemist.mustwatch.repository.RecipeRepository
import com.fullmeadalchemist.mustwatch.repository.UserRepository
import com.fullmeadalchemist.mustwatch.vo.Recipe

import javax.inject.Inject

class RecipeViewModel @Inject
constructor(private val recipeRepository: RecipeRepository, private val userRepository: UserRepository) : ViewModel() {
    private val recipes: LiveData<List<Recipe>>? = null

    val currentUserId: LiveData<Long>
        get() = userRepository.currentUserId

    fun getRecipes(userId: Long?): LiveData<List<Recipe>> {
        return if (userId == null) {
            recipeRepository.publicRecipes
        } else {
            recipeRepository.getRecipes(userId)
        }
    }

    fun addRecipe(recipe: Recipe) {
        recipeRepository.addRecipe(recipe)
    }

}
