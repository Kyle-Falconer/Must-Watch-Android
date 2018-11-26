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

package com.fullmeadalchemist.mustwatch.vo

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.measure.Quantity
import javax.measure.quantity.Mass
import javax.measure.quantity.Volume

@Entity(tableName = "batch_ingredient",
        indices = [
            Index(value = ["ingredient_id"]),
            Index(value = ["batch_id"]),
            Index(value = ["recipe_id"])],
        foreignKeys = [
            ForeignKey(entity = Batch::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("batch_id"),
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Recipe::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("recipe_id"),
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Ingredient::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("ingredient_id"),
                    onDelete = ForeignKey.CASCADE)])
data class BatchIngredient(@Expose @SerializedName("ingredient_id") @ColumnInfo(name = "ingredient_id") var ingredientId: String? = null,
                           @Expose @SerializedName("batch_id") @ColumnInfo(name = "batch_id") var batchId: Long? = null,
                           @Expose @SerializedName("recipe_id") @ColumnInfo(name = "recipe_id") var recipeId: Long? = null,
                           @Expose @SerializedName("quantity_vol") @ColumnInfo(name = "quantity_vol") var quantityVol: Quantity<Volume>? = null,
                           @Expose @SerializedName("quantity_mass") @ColumnInfo(name = "quantity_mass") var quantityMass: Quantity<Mass>? = null) {

    @Expose
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = 0
}
