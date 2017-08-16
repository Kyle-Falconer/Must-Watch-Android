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
import android.util.Log;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;

import org.jscience.physics.amount.Amount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.measure.quantity.Volume;
import javax.measure.unit.NonSI;

public class DemoGenerators {

    private static final String TAG = DemoGenerators.class.getSimpleName();

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

    private static final String[] STATUS_TYPES = {"planning", "brewing", "fermenting", "aging", "bottled"};


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
            b.outputVolume = getRandVolume();
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
        Integer recipe_index = randInt(0, RECIPE_LABELS.length - 1);
        Integer rNo = randInt(0, 100);
        return String.format("%s #%s", RECIPE_LABELS[recipe_index], rNo);
    }

    private static Amount<Volume> getRandVolume() {
        int n = randInt(1, 3);
        Log.v(TAG, String.format("Got n=%d", n));
        Amount<Volume> vol;
        switch (n) {
            case 1:
                vol = Amount.valueOf(randInt(32, 128), NonSI.OUNCE_LIQUID_US);
                break;
            case 2:
                vol = Amount.valueOf(randInt(3, 25), NonSI.LITER);
                break;
            default:
                vol = Amount.valueOf(randInt(1, 15), NonSI.GALLON_LIQUID_US);
                break;
        }
        return vol;
    }

    private static float round(float n) {
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

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     * https://stackoverflow.com/a/363692/940217
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     */
    private static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static float randFloat(float low, float high) {
        Random r = new Random();
        return r.nextFloat() * (high - low) + low;
    }

}
