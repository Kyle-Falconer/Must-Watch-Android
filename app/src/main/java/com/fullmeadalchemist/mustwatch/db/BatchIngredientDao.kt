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

@Dao
interface BatchIngredientDao {

    @Query("SELECT * FROM batch_ingredient WHERE id = :batchIngredientId LIMIT 1")
    fun load(batchIngredientId: String): LiveData<BatchIngredient>

    @Query("SELECT * FROM batch_ingredient " + "WHERE batch_id = :batch_id")
    fun getIngredientsForBatch(batch_id: Long): LiveData<List<BatchIngredient>>


    @Query("SELECT * FROM batch_ingredient " + "WHERE recipe_id = :recipe_id")
    fun getIngredientsForRecipe(recipe_id: Long): LiveData<List<BatchIngredient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(batchIngredient: BatchIngredient): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg batchIngredient: BatchIngredient): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ingredients: List<BatchIngredient>): List<Long>

    @Delete
    fun delete(batchIngredient: BatchIngredient)
}
