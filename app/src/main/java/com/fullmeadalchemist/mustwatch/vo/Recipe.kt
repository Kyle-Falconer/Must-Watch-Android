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
import java.util.*
import javax.measure.Quantity
import javax.measure.quantity.Volume

@Entity(tableName = "recipe",
        indices = [
            Index(value = ["creator_user_id"]),
            Index(value = ["owner_user_id"]),
            Index(value = ["owner_group_id"])],
        foreignKeys = [
            ForeignKey(entity = User::class,
                    parentColumns = ["uid"],
                    childColumns = ["creator_user_id"]),
            ForeignKey(entity = User::class,
                    parentColumns = ["uid"],
                    childColumns = ["owner_user_id"]),
            ForeignKey(entity = Group::class,
                    parentColumns = ["id"],
                    childColumns = ["owner_group_id"])
        ])
data class Recipe(@Expose @ColumnInfo(name = "name") var name: String? = null,
                  @Expose @SerializedName("creator_user_id") @ColumnInfo(name = "creator_user_id") var creatorUserId: UUID? = null,
                  @Expose @SerializedName("owner_user_id") @ColumnInfo(name = "owner_user_id") var ownerUserId: UUID? = null,
                  @Expose @SerializedName("owner_group_id") @ColumnInfo(name = "owner_group_id") var ownerGroupId: Long? = null,
                  @Expose @SerializedName("public_readable") @ColumnInfo(name = "public_readable") var publicReadable: Boolean = false,
                  @Expose @SerializedName("output_volume") @ColumnInfo(name = "output_volume") var outputVol: Quantity<Volume>? = null,
                  @Expose @SerializedName("carbonation") @ColumnInfo(name = "carbonation") var carbonation: Float? = null,
                  @Expose @SerializedName("min_days_to_ferment") @ColumnInfo(name = "min_days_to_ferment") var minDaysToFerment: Int? = null,
                  @Expose @SerializedName("max_days_to_ferment") @ColumnInfo(name = "max_days_to_ferment") var maxDaysToFerment: Int? = null,
                  @Expose @SerializedName("min_days_to_age") @ColumnInfo(name = "min_days_to_age") var minDaysToAge: Int? = null,
                  @Expose @SerializedName("starting_sg") @ColumnInfo(name = "starting_sg") var startingSG: Double? = null,
                  @Expose @SerializedName("final_sg") @ColumnInfo(name = "final_sg") var finalSG: Double? = null,
                  @Ignore @SerializedName("ingredients") @Expose var ingredients: List<BatchIngredient>? = null,
                  @Expose @SerializedName("notes") @ColumnInfo(name = "notes") var notes: String? = null,
                  @Expose @SerializedName("url_found") @ColumnInfo(name = "url_found") var urlFound: String? = null) {

    @Expose
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = 0

    companion object {
        @Ignore
        val RECIPE_ID = "RECIPE_ID"
    }
}
