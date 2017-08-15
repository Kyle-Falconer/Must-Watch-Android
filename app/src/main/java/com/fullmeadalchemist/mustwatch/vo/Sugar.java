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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index(value = "name", unique = true)})
public class Sugar {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    public String name;

    @Expose
    @SerializedName("density__kg_per_m3")
    @ColumnInfo(name = "density__kg_per_m3")
    public float density;

    @Expose
    @SerializedName("combined_sugars_total_pct")
    @ColumnInfo(name = "combined_sugars_total_pct")
    public float totalPct;

    @Expose
    @SerializedName("sucrose_pct")
    @ColumnInfo(name = "sucrose_pct")
    public float sucrosePct;

    @Expose
    @SerializedName("fructose_pct")
    @ColumnInfo(name = "fructose_pct")
    public float fructosePct;

    @Expose
    @SerializedName("glucose_pct")
    @ColumnInfo(name = "glucose_pct")
    public float glucosePct;

    @Expose
    @SerializedName("maltose_pct")
    @ColumnInfo(name = "maltose_pct")
    public float maltosePct;

    @Expose
    @SerializedName("polysaccharides_pct")
    @ColumnInfo(name = "polysaccharides_pct")
    public float polysaccharidesPct;

}
