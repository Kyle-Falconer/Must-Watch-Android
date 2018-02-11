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

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.support.annotation.NonNull
import android.text.TextUtils

import com.fullmeadalchemist.mustwatch.db.Converters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.text.DecimalFormat
import java.util.Calendar

import javax.measure.Quantity
import javax.measure.quantity.Volume

import timber.log.Timber

import com.fullmeadalchemist.mustwatch.core.BrewFormulae.sugarConcToSG
import com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr
import com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDateTimeLong
import systems.uom.common.CGS.GRAM

@Entity(tableName = "batch",
        indices = arrayOf(
                Index(value = "user_id")),
        foreignKeys = arrayOf(ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = ForeignKey.CASCADE)))
@TypeConverters(Converters::class)
class Batch {

    @Expose
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = Long.MIN_VALUE

    @ColumnInfo(name = "user_id")
    var userId: Long? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "target_sg_starting")
    var targetSgStarting: Double? = null

    @ColumnInfo(name = "target_sg_final")
    var targetSgFinal: Double? = null

    @ColumnInfo(name = "target_abv")
    var targetABV: Float? = null

    @ColumnInfo(name = "starting_ph")
    var startingPh: Float? = null

    @ColumnInfo(name = "starting_temp_c")
    var startingTemp: Float? = null

    @ColumnInfo(name = "output_volume")
    var outputVolume: Quantity<Volume>? = null

    @ColumnInfo(name = "status")
    var status: BatchStatusEnum? = null

    @ColumnInfo(name = "create_date")
    var createDate: Calendar? = null

    @ColumnInfo(name = "notes")
    var notes: String? = null

    @Ignore
    @SerializedName("ingredients")
    @Expose
    var ingredients: List<BatchIngredient>? = null

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        val f = DecimalFormat("0.##")
        return String.format("Batch #%s\n" +
                "User id: %d\n" +
                "Name: %s\n" +
                "Create date: %s\n" +
                "Status: %s\n" +
                "Output volume: %s %s\n" +
                "ABV: %s\n" +
                "ingredients : %s",
                id,
                userId,
                name,
                calendarToLocaleDateTimeLong(createDate),
                if (status == null) "null" else status!!.toString(),
                if (outputVolume == null) "null" else f.format(outputVolume!!.value),
                if (outputVolume == null) "null" else unitToTextAbbr(outputVolume!!.unit),
                if (targetABV == null) "null" else f.format(targetABV),
                if (ingredients == null) "null" else TextUtils.join(", ", ingredients))
    }

    enum class BatchStatusEnum private constructor(private val status: String) {

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