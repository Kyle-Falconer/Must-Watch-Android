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

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.fullmeadalchemist.mustwatch.db.Converters;

import java.util.Calendar;

import static com.fullmeadalchemist.mustwatch.util.FormatUtils.calendarToLocaleDateTimeLong;


@Entity(tableName = "log_entry",
        indices = {@Index(value = "batch_id")},
        foreignKeys = @ForeignKey(entity = Batch.class,
                parentColumns = "id",
                childColumns = "batch_id"))
@TypeConverters({Converters.class})
public class LogEntry {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "batch_id")
    public long batchId;

    @ColumnInfo(name = "entry_date")
    public Calendar entryDate;

    @ColumnInfo(name = "sg")
    public Float sg;

    @ColumnInfo(name = "acidity")
    public Float acidity;

    @ColumnInfo(name = "note")
    public String note;

    @SuppressLint("DefaultLocale")
    @Ignore
    @Override
    public String toString() {
        return String.format("Batch ID %s\n" +
                        "Entry Date: %s\n" +
                        "Acidity: %f\n" +
                        "SG: %f\n" +
                        "Note: %s",
                batchId,
                calendarToLocaleDateTimeLong(entryDate),
                acidity,
                sg,
                note);
    }
}