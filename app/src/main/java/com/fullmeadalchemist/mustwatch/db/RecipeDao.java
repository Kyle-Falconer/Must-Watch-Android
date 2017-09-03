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
import android.arch.persistence.room.Update;

import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe WHERE id = :recipe_id LIMIT 1")
    LiveData<Recipe> get(Long recipe_id);

    @Query("SELECT * FROM recipe WHERE creator_user_id = :user_id")
    LiveData<List<Recipe>> getRecipesForCreator(long user_id);

    @Query("SELECT * FROM recipe WHERE owner_user_id = :user_id")
    LiveData<List<Recipe>> getRecipesForOwner(long user_id);

    @Query("SELECT * FROM recipe WHERE owner_group_id = :group_id")
    LiveData<List<Recipe>> getRecipesForGroup(long group_id);

    @Query("SELECT * FROM recipe WHERE public_readable=1")
    LiveData<List<Recipe>> getPublicRecipes();

    @Insert
    long insert(Recipe recipe);

    @Insert
    List<Long> insertAll(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);

    @Update
    int updateRecipe(Recipe recipe);
}
