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

@Entity(tableName = "batch",
        indices = {@Index(value = "user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id"))
@TypeConverters({Converters.class})
public class Batch {

    @Ignore
    public static final String BATCH_ID = "BATCH_ID";

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "user_id")
    public Long userId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "target_sg_starting")
    public Float targetSgStarting;

    @ColumnInfo(name = "target_sg_final")
    public Float targetSgFinal;

    @ColumnInfo(name = "target_abv")
    public Float targetABV;

    @ColumnInfo(name = "starting_ph")
    public Float startingPh;

    @ColumnInfo(name = "starting_temp_c")
    public Float startingTemp;

    @ColumnInfo(name = "output_volume")
    public Float outputVolume;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "create_date")
    public Calendar createDate;

    @ColumnInfo(name = "notes")
    public String notes;

    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("Batch #%s\n" +
                        "User id: %d\n" +
                        "Name: %s\n" +
                        "Create date: %s\n" +
                        "Status: %s\n" +
                        "Output volume: %f\n"+
                        "ABV: %f\n",
                id,
                userId,
                name,
                calendarToLocaleDateTimeLong(createDate),
                status,
                outputVolume,
                targetABV);
    }
}