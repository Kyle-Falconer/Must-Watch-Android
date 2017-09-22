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
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index(value = "id", unique = true)})
public class Ingredient {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @NonNull
    public String id;

    @Expose
    @SerializedName("type")
    @ColumnInfo(name = "type")
    public String type;

    @Expose
    @SerializedName("combined_sugars_total_pct")
    @ColumnInfo(name = "combined_sugars_total_pct")
    public Float totalPct;

    @Expose
    @SerializedName("density__kg_per_m3")
    @ColumnInfo(name = "density__kg_per_m3")
    public Float density;

    @Expose
    @SerializedName("acidity")
    @ColumnInfo(name = "acidity")
    public Float acidity;

}
