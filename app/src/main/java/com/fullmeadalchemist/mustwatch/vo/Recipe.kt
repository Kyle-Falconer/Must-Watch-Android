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

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import javax.measure.Quantity
import javax.measure.quantity.Volume

@Entity(tableName = "recipe", indices = arrayOf(Index(value = "creator_user_id"), Index(value = "owner_user_id"), Index(value = "owner_group_id")), foreignKeys = arrayOf(ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("creator_user_id")), ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("owner_user_id")), ForeignKey(entity = Group::class, parentColumns = arrayOf("id"), childColumns = arrayOf("owner_group_id"))))
class Recipe {

    @Expose
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = Long.MIN_VALUE

    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String? = null

    @Expose
    @SerializedName("creator_user_id")
    @ColumnInfo(name = "creator_user_id")
    var creatorUserId: Long? = null

    @Expose
    @SerializedName("owner_user_id")
    @ColumnInfo(name = "owner_user_id")
    var ownerUserId: Long? = null

    @Expose
    @SerializedName("owner_group_id")
    @ColumnInfo(name = "owner_group_id")
    var ownerGroupId: Long? = null

    @Expose
    @SerializedName("public_readable")
    @ColumnInfo(name = "public_readable")
    var publicReadable: Boolean = false

    @Expose
    @SerializedName("output_volume")
    @ColumnInfo(name = "output_volume")
    var outputVol: Quantity<Volume>? = null

    @Expose
    @SerializedName("carbonation")
    @ColumnInfo(name = "carbonation")
    var carbonation: Float? = null

    @Expose
    @SerializedName("min_days_to_ferment")
    @ColumnInfo(name = "min_days_to_ferment")
    var minDaysToFerment: Int? = null

    @Expose
    @SerializedName("max_days_to_ferment")
    @ColumnInfo(name = "max_days_to_ferment")
    var maxDaysToFerment: Int? = null

    @Expose
    @SerializedName("min_days_to_age")
    @ColumnInfo(name = "min_days_to_age")
    var minDaysToAge: Int? = null

    @Expose
    @SerializedName("starting_sg")
    @ColumnInfo(name = "starting_sg")
    var startingSG: Double? = null

    @Expose
    @SerializedName("final_sg")
    @ColumnInfo(name = "final_sg")
    var finalSG: Double? = null

    @Ignore
    @SerializedName("ingredients")
    @Expose
    var ingredients: List<BatchIngredient>? = null

    @Expose
    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    var notes: String? = null

    @Expose
    @SerializedName("url_found")
    @ColumnInfo(name = "url_found")
    var urlFound: String? = null

    override fun toString(): String {
        return String.format(
                "creator_user_id : %s,\n" +
                        "owner_user_id : %s,\n" +
                        "owner_group_id : %s,\n" +
                        "public_readable : %s,\n" +
                        "output_volume : %s,\n" +
                        "carbonation : %s,\n" +
                        "min_days_to_ferment : %s,\n" +
                        "max_days_to_ferment : %s,\n" +
                        "starting_sg : %s,\n" +
                        "final_sg : %s,\n" +
                        "notes : %s,\n" +
                        "url_found : %s,\n" +
                        "ingredients : %s",
                creatorUserId,
                ownerUserId,
                ownerGroupId,
                publicReadable,
                if (outputVol == null) "null" else outputVol!!.toString(),
                carbonation,
                minDaysToFerment,
                maxDaysToFerment,
                startingSG,
                finalSG,
                notes,
                urlFound,
                ingredients
        )
    }

    companion object {

        @Ignore
        val RECIPE_ID = "RECIPE_ID"
    }
}
