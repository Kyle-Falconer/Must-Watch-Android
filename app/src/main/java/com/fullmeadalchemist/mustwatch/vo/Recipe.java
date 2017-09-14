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
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Volume;

@Entity(tableName = "recipe",
        indices = {
                @Index(value = "creator_user_id"),
                @Index(value = "owner_user_id"),
                @Index(value = "owner_group_id")
        },
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "creator_user_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "owner_user_id"),
                @ForeignKey(entity = Group.class,
                        parentColumns = "id",
                        childColumns = "owner_group_id")
        })
public class Recipe {

    @Ignore
    public static final String RECIPE_ID = "RECIPE_ID";

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    public String name;

    @Expose
    @SerializedName("creator_user_id")
    @ColumnInfo(name = "creator_user_id")
    public Long creatorUserId;

    @Expose
    @SerializedName("owner_user_id")
    @ColumnInfo(name = "owner_user_id")
    public Long ownerUserId;

    @Expose
    @SerializedName("owner_group_id")
    @ColumnInfo(name = "owner_group_id")
    public Long ownerGroupId;

    @Expose
    @SerializedName("public_readable")
    @ColumnInfo(name = "public_readable")
    public boolean publicReadable;

    @Expose
    @SerializedName("output_volume")
    @ColumnInfo(name = "output_volume")
    public Quantity<Volume> outputVol;

    @Expose
    @SerializedName("carbonation")
    @ColumnInfo(name = "carbonation")
    public Float carbonation;

    @Expose
    @SerializedName("min_days_to_ferment")
    @ColumnInfo(name = "min_days_to_ferment")
    public Integer minDaysToFerment;

    @Expose
    @SerializedName("max_days_to_ferment")
    @ColumnInfo(name = "max_days_to_ferment")
    public Integer maxDaysToFerment;

    @Expose
    @SerializedName("min_days_to_age")
    @ColumnInfo(name = "min_days_to_age")
    public Integer minDaysToAge;

    @Expose
    @SerializedName("starting_sg")
    @ColumnInfo(name = "starting_sg")
    public Float startingSG;

    @Expose
    @SerializedName("final_sg")
    @ColumnInfo(name = "final_sg")
    public Float finalSG;

    @Ignore
    @SerializedName("ingredients")
    @Expose
    public List<BatchIngredient> ingredients = null;

    @Expose
    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    public String notes;

    @Expose
    @SerializedName("url_found")
    @ColumnInfo(name = "url_found")
    public String urlFound;

    public String toString() {
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
                (outputVol == null) ? "null" : outputVol.toString(),
                carbonation,
                minDaysToFerment,
                maxDaysToFerment,
                startingSG,
                finalSG,
                notes,
                urlFound,
                ingredients
        );
    }
}
