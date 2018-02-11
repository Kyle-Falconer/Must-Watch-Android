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

import com.fullmeadalchemist.mustwatch.db.Converters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Calendar

import com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDateTimeLong


@Entity(tableName = "log_entry",
        indices = arrayOf(Index(value = "batch_id")),
        foreignKeys = arrayOf(ForeignKey(entity = Batch::class, parentColumns = arrayOf("id"), childColumns = arrayOf("batch_id"))))
@TypeConverters(Converters::class)
class LogEntry {

    @Expose
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = Long.MIN_VALUE

    @ColumnInfo(name = "batch_id")
    var batchId: Long = 0

    @ColumnInfo(name = "entry_date")
    var entryDate: Calendar? = null

    @ColumnInfo(name = "sg")
    var sg: Float? = null

    @ColumnInfo(name = "acidity")
    var acidity: Float? = null

    @ColumnInfo(name = "note")
    var note: String? = null

    @SuppressLint("DefaultLocale")
    @Ignore
    override fun toString(): String {
        return String.format("Batch ID %s\n" +
                "Entry Date: %s\n" +
                "Acidity: %f\n" +
                "SG: %f\n" +
                "Note: %s",
                batchId,
                calendarToLocaleDateTimeLong(entryDate),
                acidity,
                sg,
                note)
    }
}