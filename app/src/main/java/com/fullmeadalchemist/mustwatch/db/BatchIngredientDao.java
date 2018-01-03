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
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.util.List;

@Dao
public interface BatchIngredientDao {

    @Query("SELECT * FROM batch_ingredient WHERE id = :batchIngredientId LIMIT 1")
    LiveData<BatchIngredient> load(String batchIngredientId);

    @Query("SELECT * FROM batch_ingredient "
            + "WHERE batch_id = :batch_id")
    LiveData<List<BatchIngredient>> getIngredientsForBatch(long batch_id);


    @Query("SELECT * FROM batch_ingredient "
            + "WHERE recipe_id = :recipe_id")
    LiveData<List<BatchIngredient>> getIngredientsForRecipe(long recipe_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(BatchIngredient batchIngredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(BatchIngredient... batchIngredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<BatchIngredient> ingredients);

    @Delete
    void delete(BatchIngredient batchIngredient);
}
