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
import java.util.List;
import java.util.Random;

public class DemoGenerators {

    @SuppressLint("DefaultLocale")
    public static List<Batch> generateDummyBatchesWithData(Long userId, int numBatches) {
        List<Batch> batches = new ArrayList<>();
        for (int i = 0; i < numBatches; i++) {
            Batch b = new Batch();

            // FIXME: change to random, date in the past
            b.createDate = GregorianCalendar.getInstance();
            b.outputVolume = randFloat(1f, 12f);
            b.targetABV = randFloat(8f, 17f);
            b.userId = userId;
            b.notes = String.format("Dummy Batch entry #%d", i);
            batches.add(b);
        }
        return batches;
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
