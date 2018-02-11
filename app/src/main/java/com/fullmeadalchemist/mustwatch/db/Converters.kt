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

import android.arch.persistence.room.TypeConverter
import android.text.TextUtils
import com.fullmeadalchemist.mustwatch.core.UnitMapper.qtyToTextAbbr
import com.fullmeadalchemist.mustwatch.core.UnitMapper.textAbbrToUnit
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import tec.units.ri.quantity.Quantities
import timber.log.Timber
import java.util.*
import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.Mass
import javax.measure.quantity.Volume

class Converters {

    companion object {
        val SEPARATOR = " "
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        if (calendar == null) {
            return null
        }
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar.time.time
    }

    @TypeConverter
    fun timestampToCalendar(value: Long?): Calendar? {
        if (value == null) {
            return null
        }
        val d = Date(value)
        val gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
        gregorianCalendar.time = d
        return gregorianCalendar
    }


    @TypeConverter
    fun fromVolumeText(volText: String): Quantity<Volume>? {
        if (TextUtils.isEmpty(volText)) {
            return null
        }

        val valueTexts = TextUtils.split(volText.trim { it <= ' ' }, SEPARATOR)
        if (valueTexts.size != 2) {
            Timber.e("Could not parse Amount<Volume> from \"%s\"", volText)
            return null
        }

        Timber.v("Parsing Amount<Volume> from string %s", volText)
        val value = java.lang.Double.parseDouble(valueTexts[0])
        val unitText = valueTexts[1]

        val unit: Unit<Volume>
        try {
            unit = textAbbrToUnit(unitText) as Unit<Volume>
        } catch (e: ClassCastException) {
            Timber.e("Failed to cast unit text %s to Unit<Volume>", unitText)
            return null
        }

        return Quantities.getQuantity(value, unit)
    }

    @TypeConverter
    fun toVolumeText(volume: Quantity<Volume>?): String? {
        return if (volume == null) {
            null
        } else String.format("%s%s%s",
                volume.value,
                SEPARATOR,
                qtyToTextAbbr(volume))
    }


    @TypeConverter
    fun fromMassText(massText: String): Quantity<Mass>? {
        if (TextUtils.isEmpty(massText)) {
            return null
        }

        val valueTexts = TextUtils.split(massText.trim { it <= ' ' }, SEPARATOR)
        if (valueTexts.size != 2) {
            Timber.e("Could not parse Amount<Mass> from \"%s\"", massText)
            return null
        }

        Timber.v("Parsing Amount<Mass> from string %s", massText)
        val value = java.lang.Double.parseDouble(valueTexts[0])
        val unitText = valueTexts[1]

        val unit: Unit<Mass>
        try {
            unit = textAbbrToUnit(unitText) as Unit<Mass>
        } catch (e: ClassCastException) {
            Timber.e("Failed to cast unit text %s to Unit<Mass>", unitText)
            return null
        }

        return Quantities.getQuantity(value, unit)
    }

    @TypeConverter
    fun toMassText(mass: Quantity<Mass>?): String? {
        return if (mass == null) {
            null
        } else String.format("%s%s%s",
                mass.value,
                SEPARATOR,
                qtyToTextAbbr(mass))
    }


    @TypeConverter
    fun fromBatchStatusString(status: String?): Batch.BatchStatusEnum? {
        return if (status == null) {
            null
        } else Batch.BatchStatusEnum.fromString(status)
    }

    @TypeConverter
    fun toBatchStatusString(statusEnum: Batch.BatchStatusEnum?): String? {
        return statusEnum?.toString()
    }

    @TypeConverter
    fun fromIngredientTypeString(type: String?): Ingredient.IngredientType? {
        return if (type == null) {
            null
        } else Ingredient.IngredientType.fromString(type)
    }

    @TypeConverter
    fun toIngredientTypeString(typeEnum: Ingredient.IngredientType?): String? {
        return typeEnum?.toString()
    }
}
