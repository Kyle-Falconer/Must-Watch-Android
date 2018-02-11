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

package com.fullmeadalchemist.mustwatch.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Recipe

@Dao
interface RecipeDao {

    @get:Query("SELECT * FROM recipe WHERE public_readable=1")
    val publicRecipes: LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id = :recipe_id LIMIT 1")
    operator fun get(recipe_id: Long?): LiveData<Recipe>

    @Query("SELECT * FROM recipe WHERE creator_user_id = :user_id")
    fun getRecipesForCreator(user_id: Long): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE owner_user_id = :user_id")
    fun getRecipesForOwner(user_id: Long): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE owner_group_id = :group_id")
    fun getRecipesForGroup(group_id: Long): LiveData<List<Recipe>>

    @Query("SELECT * FROM batch_ingredient WHERE recipe_id=:recipe_id")
    fun getIngredientsForRecipe(recipe_id: Long): LiveData<List<BatchIngredient>>

    // FIXME: pull from groups this user has read+ access to as well
    @Query("SELECT * FROM recipe WHERE creator_user_id = :user_id OR owner_user_id = :user_id OR public_readable=1")
    fun getRecipesForUser(user_id: Long): LiveData<List<Recipe>>

    @Insert
    fun insert(recipe: Recipe): Long

    @Insert
    fun insertAll(vararg recipes: Recipe): List<Long>

    @Delete
    fun delete(recipe: Recipe)

    @Update
    fun updateRecipe(recipe: Recipe): Int

}
