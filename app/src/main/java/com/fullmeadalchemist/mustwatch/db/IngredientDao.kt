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
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import com.fullmeadalchemist.mustwatch.vo.Ingredient

@Dao
interface IngredientDao {

    @get:Query("SELECT * FROM Ingredient")
    val all: LiveData<List<Ingredient>>

    @get:Query("SELECT * FROM Ingredient WHERE type=\"SUGAR\"")
    val allSugars: LiveData<List<Ingredient>>

    @get:Query("SELECT * FROM Ingredient WHERE type=\"NUTRIENT\"")
    val allNutrients: LiveData<List<Ingredient>>

    @get:Query("SELECT * FROM Ingredient WHERE type=\"YEAST\"")
    val allYeasts: LiveData<List<Ingredient>>

    @get:Query("SELECT * FROM Ingredient WHERE type=\"STABILIZER\"")
    val allStabilizers: LiveData<List<Ingredient>>

    @Query("SELECT * FROM Ingredient WHERE id = :ingredientId LIMIT 1")
    fun getById(ingredientId: String): LiveData<Ingredient>

    @Insert
    fun insert(ingredient: Ingredient): Long?

    @Insert
    fun insertAll(vararg ingredients: Ingredient): List<Long>

    @Delete
    fun delete(ingredient: Ingredient)

}
