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

package com.fullmeadalchemist.mustwatch.demo

import android.annotation.SuppressLint
import android.os.Build
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import systems.uom.common.USCustomary.*
import tec.units.ri.quantity.Quantities
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import javax.measure.Quantity
import javax.measure.quantity.Volume

object DemoGenerators {

    private val TAG = DemoGenerators::class.java.simpleName

    private val RECIPE_LABELS = arrayOf(
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
            "Green Tea Lemon Metheglomel")

    val status: Batch.BatchStatusEnum?
        get() {
            val n = randInt(1, 4)
            val statusEnum: Batch.BatchStatusEnum?
            when (n) {
                1 -> statusEnum = Batch.BatchStatusEnum.PLANNING
                2 -> statusEnum = Batch.BatchStatusEnum.FERMENTING
                3 -> statusEnum = Batch.BatchStatusEnum.AGING
                4 -> statusEnum = Batch.BatchStatusEnum.BOTTLED
                else -> statusEnum = null
            }
            return statusEnum
        }

    private val recipe: String
        get() {
            val recipe_index = randInt(0, RECIPE_LABELS.size - 1)
            val rNo = randInt(0, 100)
            return String.format("%s #%s", RECIPE_LABELS[recipe_index], rNo)
        }

    val randVolume: Quantity<Volume>
        get() {
            val n = randInt(1, 3)
            val vol: Quantity<Volume>
            when (n) {
                1 -> vol = Quantities.getQuantity(randInt(32, 128), FLUID_OUNCE)
                2 -> vol = Quantities.getQuantity(randInt(3, 25), LITER)
                else -> vol = Quantities.getQuantity(randInt(1, 15), GALLON_LIQUID)
            }
            return vol
        }

    @SuppressLint("DefaultLocale")
    fun generateDummyBatchesWithData(userId: Long?, numBatches: Int): List<Batch> {
        val batches = ArrayList<Batch>()
        for (i in 0 until numBatches) {
            val b = Batch()

            b.name = recipe
            // FIXME: change to random, date in the past

            b.createDate = GregorianCalendar.getInstance()
            b.status = status
            b.targetSgStarting = roundThreeDecimalPlaces(randDouble(1.10, 1.30))
            b.targetSgFinal = roundThreeDecimalPlaces(randDouble(0.95, 1.05))
            b.startingPh = roundTwoDecimalPlaces(randFloat(3.0f, 5.5f))
            b.startingTemp = roundOneDecimalPlace(randFloat(65f, 75f))
            b.outputVolume = randVolume
            b.targetABV = roundTwoDecimalPlaces(randFloat(0.08f, 0.17f))
            b.userId = userId
            b.notes = String.format("Dummy Batch entry #%d", i)
            batches.add(b)
        }
        return batches
    }

    fun round(n: Float): Float {
        return Math.floor(n.toDouble()).toFloat()
    }

    fun roundOneDecimalPlace(n: Float): Float {
        return (Math.round(n * 10.0) / 10.0).toFloat()
    }

    fun roundTwoDecimalPlaces(n: Float): Float {
        return (Math.round(n * 100.0) / 100.0).toFloat()
    }

    fun roundThreeDecimalPlaces(n: Float): Float {
        return (Math.round(n * 1000.0) / 1000.0).toFloat()
    }

    fun roundThreeDecimalPlaces(n: Double): Double {
        return roundThreeDecimalPlaces(n.toFloat()).toDouble()
    }


    @SuppressLint("DefaultLocale")
    fun addLogsToBatchItem(batchId: Long, numLogsToGenerate: Int, batchCreateDate: Calendar) {
        for (i in 0 until numLogsToGenerate) {
            val l = LogEntry()
            l.note = String.format("Dummy Log entry #%d", i)
            l.acidity = randFloat(3f, 6f)

            // FIXME: change to random, ascending dates, starting after batch creation date, going up to today
            l.entryDate = GregorianCalendar.getInstance()
            l.batchId = batchId
        }
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * `Integer.MAX_VALUE - 1`.
     * https://stackoverflow.com/a/363692/940217
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     */
    fun randInt(min: Int, max: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ThreadLocalRandom.current().nextInt(min, max + 1)
        } else {
            val rand = Random()
            return rand.nextInt(max - min + 1) + min
        }
    }

    fun randFloat(low: Float, high: Float): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ThreadLocalRandom.current().nextFloat() * (high - low) + low
        } else {
            val r = Random()
            return r.nextFloat() * (high - low) + low
        }
    }

    fun randDouble(low: Double, high: Double): Double {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ThreadLocalRandom.current().nextDouble() * (high - low) + low
        } else {
            val r = Random()
            return r.nextDouble() * (high - low) + low
        }
    }
}
