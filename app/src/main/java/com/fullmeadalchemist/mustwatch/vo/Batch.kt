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
import android.databinding.BaseObservable
import android.support.annotation.NonNull
import com.fullmeadalchemist.mustwatch.db.Converters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*
import javax.measure.Quantity
import javax.measure.quantity.Volume

@Entity(tableName = "batch",
        indices = [Index(value = ["user_id"])],
        foreignKeys = [
            ForeignKey(entity = User::class,
                    parentColumns = arrayOf("uid"),
                    childColumns = arrayOf("user_id"),
                    onDelete = ForeignKey.CASCADE)
        ])
@TypeConverters(Converters::class)
data class Batch(@ColumnInfo(name = "user_id") var userId: UUID? = null,
                 @ColumnInfo(name = "name") var name: String? = null,
                 @ColumnInfo(name = "target_sg_starting") var targetSgStarting: Double? = null,
                 @ColumnInfo(name = "target_sg_final") var targetSgFinal: Double? = null,
                 @ColumnInfo(name = "target_abv") var targetABV: Float? = null,
                 @ColumnInfo(name = "starting_ph") var startingPh: Float? = null,
                 @ColumnInfo(name = "starting_temp_c") var startingTemp: Float? = null,
                 @ColumnInfo(name = "output_volume") var outputVolume: Quantity<Volume>? = null,
                 @ColumnInfo(name = "status") var status: BatchStatusEnum? = null,
                 @ColumnInfo(name = "create_date") var createDate: Calendar? = null,
                 @ColumnInfo(name = "notes") var notes: String? = null,
                 @Ignore @SerializedName("ingredients") @Expose var ingredients: MutableList<BatchIngredient> = mutableListOf())
    : BaseObservable() {

    @Expose
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = 0

    enum class BatchStatusEnum constructor(private val status: String) {

        PLANNING("planning"),
        FERMENTING("fermenting"),
        AGING("aging"),
        BOTTLED("bottled");

        override fun toString(): String {
            return name
        }

        companion object {

            fun fromString(text: String): BatchStatusEnum? {
                for (b in BatchStatusEnum.values()) {
                    if (b.name.equals(text, ignoreCase = true)) {
                        return b
                    }
                }
                return null
            }
        }
    }

    companion object {

        @Ignore
        val BATCH_ID = "BATCH_ID"
    }
}