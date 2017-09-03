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

package com.fullmeadalchemist.mustwatch.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import javax.measure.Quantity;
import javax.measure.quantity.Volume;

@Entity(tableName = "batch_ingredient",
        indices = {@Index(value = "ingredient_id"), @Index(value = "batch_id"), @Index(value = "recipe_id") },
        foreignKeys = {
                @ForeignKey(entity = Batch.class,
                        parentColumns = "id",
                        childColumns = "batch_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipe_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "ingredient_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class BatchIngredient {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "ingredient_id")
    public String ingredientId;

    @ColumnInfo(name = "batch_id")
    public Long batchId;

    @ColumnInfo(name = "recipe_id")
    public Long recipeId;

    @ColumnInfo(name = "quantity")
    public Quantity<Volume> quantity;
}
