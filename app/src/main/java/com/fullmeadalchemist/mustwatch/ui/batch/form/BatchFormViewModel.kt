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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.fullmeadalchemist.mustwatch.repository.*
import com.fullmeadalchemist.mustwatch.vo.*
import java.util.*

class BatchFormViewModel(
        private val batchRepository: BatchRepository,
        private val batchIngredientRepository: BatchIngredientRepository,
        private val recipeRepository: RecipeRepository,
        private val ingredientRepository: IngredientRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    var batch = MutableLiveData<Batch>()

    val currentUserId: LiveData<User>
        get() = userRepository.getCurrentUser()

    val sugars: LiveData<List<Ingredient>>
        get() = ingredientRepository.getSugars()

    val nutrients: LiveData<List<Ingredient>>
        get() = ingredientRepository.getNutrients()

    val yeasts: LiveData<List<Ingredient>>
        get() = ingredientRepository.getYeasts()

    val stabilizers: LiveData<List<Ingredient>>
        get() = ingredientRepository.getStabilizers()

    fun getBatch(id: Long?): LiveData<Batch> {
        return batchRepository.getBatch(id)
    }

    fun addBatch(batch: Batch) {
        if (batch.ingredients == null) {
            batch.ingredients = arrayListOf()
        }
        batchRepository.addBatch(batch)
    }

    internal fun addIngredient(batchIngredient: BatchIngredient) {
        batch.value?.let { b ->
            if (b.ingredients == null) {
                b.ingredients = ArrayList()
            }
            (b.ingredients as ArrayList).let { bi ->
                bi.add(batchIngredient)
                batchIngredientRepository.addBatchIngredients(bi)
            }
        }
    }

    fun updateBatch(batch: Batch) {
        if (batch.ingredients == null) {
            batch.ingredients = ArrayList()
        }
        batchRepository.updateBatch(batch)
    }

    fun saveNewBatch(batch: Batch): LiveData<Long> {
        return batchRepository.addBatch(batch)
    }

    fun getRecipe(recipeId: Long): LiveData<Recipe> {
        return recipeRepository.getRecipe(recipeId)
    }

    fun getIngredient(id: String): LiveData<Ingredient> {
        return ingredientRepository.getIngredientById(id)
    }

    fun getBatchIngredients(batchId: Long): LiveData<List<BatchIngredient>> {
        return batchRepository.getBatchIngredients(batchId)
    }

    fun getRecipes(userId: UUID?): LiveData<List<Recipe>> {
        return if (userId == null) {
            recipeRepository.getPublicRecipes()
        } else {
            recipeRepository.getRecipes(userId)
        }
    }

    fun getRecipeIngredients(recipeId: Long): LiveData<List<BatchIngredient>> {
        return recipeRepository.getRecipeIngredients(recipeId)
    }
}
