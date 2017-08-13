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

package com.fullmeadalchemist.mustwatch.demo;

import android.annotation.SuppressLint;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DemoGenerators {

    private static final String[] RECIPE_LABELS = {
            "null",
            "Beginner Traditional",
            "Beginner Metheglin",
            "Beginner Cyser",
            "Capsicumel",
            "Sparkling Hydromel",
            "Beginner Melomel",
            "Rhodomel",
            "Chai",
            "Bochet",
            "Braggot",
            "Smoked Oaked Capsicumel",
            "Berry Supreme Melomel",
            "Cinnamon Metheglin",
            "Heartbound Hibiscus",
            "Pomegranate Melomel",
            "Persimmon Melomel",
            "Sweet Habanero Capsicumel",
            "Smoked Capsicumel Mk. II",
            "Heartbound Hibiscus: pH Control and Cold Variation",
            "Stupid Quick Session Mead",
            "Modified JAOM",
            "Juniper Berry Metheglin",
            "Peach Cinnamon Melomel",
            "Green Tea Lemon Metheglomel"
    };

    private static final String[] STATUS_TYPES = {"brewing", "fermenting", "aging", "bottled"};

    private static Set<Integer> usedRecipes = new HashSet<>();

    @SuppressLint("DefaultLocale")
    public static List<Batch> generateDummyBatchesWithData(Long userId, int numBatches) {
        List<Batch> batches = new ArrayList<>();
        for (int i = 0; i < numBatches; i++) {
            Batch b = new Batch();

            b.name = getRecipe();
            // FIXME: change to random, date in the past

            b.createDate = GregorianCalendar.getInstance();
            b.status = getStatus();
            b.targetSgStarting = roundThreeDecimalPlaces(randFloat(1.10f, 1.30f));
            b.targetSgFinal = roundThreeDecimalPlaces(randFloat(0.95f, 1.05f));
            b.startingPh = roundTwoDecimalPlaces(randFloat(3.0f, 5.5f));
            b.startingTemp = roundOneDecimalPlace(randFloat(65f, 75f));
            b.outputVolume = round(randFloat(1f, 15f));
            b.targetABV = roundTwoDecimalPlaces(randFloat(0.08f, 0.17f));
            b.userId = userId;
            b.notes = String.format("Dummy Batch entry #%d", i);
            batches.add(b);
        }
        return batches;
    }

    private static String getStatus() {
        return STATUS_TYPES[randInt(0, STATUS_TYPES.length - 1)];
    }

    private static String getRecipe() {
        Integer rNo = 0;
        if (usedRecipes.size() == 0) {
            usedRecipes.add(0);
        }
        while (usedRecipes.contains(rNo)) {
            rNo = randInt(0, RECIPE_LABELS.length - 1);
        }
        usedRecipes.add(rNo);
        return RECIPE_LABELS[rNo];
    }

    private static float round(float n){
        return (float) Math.floor(n);
    }

    private static float roundOneDecimalPlace(float n) {
        return (float) (Math.round(n * 10.0) / 10.0);
    }

    private static float roundTwoDecimalPlaces(float n) {
        return (float) (Math.round(n * 100.0) / 100.0);
    }

    private static float roundThreeDecimalPlaces(float n) {
        return (float) (Math.round(n * 1000.0) / 1000.0);
    }

//    private static BatchItem.BatchStatusEnum getRandomBatchStatus() {
//        int randPlan = randInt(0, 4);
//        BatchItem.BatchStatusEnum plan = null;
//        switch (randPlan) {
//            case 0:
//                plan = PLANNING;
//                break;
//            case 1:
//                plan = FERMENTING;
//                break;
//            case 2:
//                plan = AGING;
//                break;
//            case 3:
//                plan = BOTTLED;
//                break;
//        }
//        return plan;
//    }

    @SuppressLint("DefaultLocale")
    private static void addLogsToBatchItem(Long batchId, int numLogsToGenerate, Calendar batchCreateDate) {
        for (int i = 0; i < numLogsToGenerate; i++) {
            LogEntry l = new LogEntry();
            l.note = String.format("Dummy Log entry #%d", i);
            l.acidity = randFloat(3f, 6f);

            // FIXME: change to random, ascending dates, starting after batch creation date, going up to today
            l.entryDate = GregorianCalendar.getInstance();
            l.batchId = batchId;
        }
    }


    private static int randInt(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    private static float randFloat(float low, float high) {
        Random r = new Random();
        return r.nextFloat() * (high - low) + low;
    }
}
