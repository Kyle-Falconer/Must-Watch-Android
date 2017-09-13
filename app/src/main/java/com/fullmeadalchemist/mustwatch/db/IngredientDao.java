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

package com.fullmeadalchemist.mustwatch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM Ingredient WHERE id = :ingredientId LIMIT 1")
    LiveData<Ingredient> getById(String ingredientId);

    @Query("SELECT * FROM Ingredient")
    LiveData<List<Ingredient>> getAll();

    @Query("SELECT * FROM Ingredient WHERE type=\"SUGAR\"")
    LiveData<List<Ingredient>> getAllSugars();

    @Query("SELECT * FROM Ingredient WHERE type=\"NUTRIENT\"")
    LiveData<List<Ingredient>> getAllNutrients();

    @Query("SELECT * FROM Ingredient WHERE type=\"YEAST\"")
    LiveData<List<Ingredient>> getAllYeasts();

    @Query("SELECT * FROM Ingredient WHERE type=\"STABILIZER\"")
    LiveData<List<Ingredient>> getAllStabilizers();

    @Insert
    Long insert(Ingredient ingredient);

    @Insert
    List<Long> insertAll(Ingredient... ingredients);

    @Delete
    void delete(Ingredient ingredient);

}