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

package com.fullmeadalchemist.mustwatch.core

import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient

import java.text.DecimalFormat

import javax.measure.Quantity
import javax.measure.quantity.Volume

import tec.units.ri.AbstractQuantity
import timber.log.Timber

import systems.uom.common.CGS.GRAM
import systems.uom.common.USCustomary.LITER


object BrewFormulae {

    fun SGToBrix(SG: Double): Double {
        return 135.997 * Math.pow(SG, 3.0) - 630.272 * Math.pow(SG, 2.0) + 1111.14 * SG - 616.868
    }

    fun SGToSugarConc(SG: Double): Double {
        return 4 + 10.0 * SG * SGToBrix(SG)
    }

    fun estimateBatchSG(b: Batch): Double? {
        Timber.d("Calculating the SG for batch %s", b.name)
        if (b.ingredients == null) {
            Timber.d("No sugars in this batch.")
            return null
        }
        var sugars = 0.0
        b.ingredients?.let {
            for (ingredient in it) {
                if (ingredient.quantityMass != null) {
                    val gramsMass = ingredient.quantityMass to GRAM
                    sugars +=        gramsMass.first?.value as Double
                }
            }
        }

        Timber.d("Counted sugars to be %s grams", sugars.toString())
        var sg: Double? = null
        if (Math.abs(sugars - 0.0) > 0.0001) {
            sg = sugarConcToSG(sugars, b.outputVolume!!)
        }
        Timber.d("Calculated SG to be %s", sg.toString())
        return sg
    }

    /**
     * Guess the SG based on the g/L weight of sugars.
     * Accuracy is only to 3 decimal places
     *
     * @param totalSugars  the amount of sugars in grams.
     * @param outputVolume the output volume of the batch.
     * @return calculated specific gravity based on provided sugars and output volume.
     */
    fun sugarConcToSG(totalSugars: Double, outputVolume: Quantity<Volume>): Double {
        val sugars_in_grams_per_L = totalSugars / outputVolume.to(LITER).value as Double
        var sg_guess = 0.992    // lowest point

        val loopLimit = 100000.0
        var gpl: Double
        var diff: Double
        var i = 0
        while (i < loopLimit) {
            gpl = SGToSugarConc(sg_guess)
            if (Math.round(gpl) == Math.round(sugars_in_grams_per_L)) {
                return sg_guess
            }
            // scale by number of integer digits + 2, ie gpl of 312.45 means scale of 5 decimal points => 0.00001
            diff = Math.pow(10.0, (-1 * (2 + (Math.round(gpl).toString() + "").length)).toDouble())
            sg_guess = sg_guess * (1 + diff)
            i++
        }
        return 0.0
    }


    fun calcAbvPct(OG: Double?, FG: Double?): Double {
        return 76.08 * (OG!! - FG!!) / (1.775 - OG) * (FG / 0.794)
    }

    fun getFinalABV_pct(targetStartingSG: Double?, targetFinalSG: Double?, totalSugars: Double?, outputVolume: AbstractQuantity<Volume>): Double {
        // http://www.boulder.nist.gov/div838/publications.html
        // https://www.brewersfriend.com/2011/06/16/alcohol-by-volume-calculator-updated/
        /*
        - Baume is PA (approx)
        - BRIX = 135.997(SG^3) - 630.272(SG^2) + 111.14SG - 616.868
        - SG = 1.00001+(BRIX/(258.6-0.89*BRIX))
        - Baume (Bates et. al. 1942 @ 20 degC) = 145*(1-(1/SG))  => SG=145/(145-Baume)   ( also  Brix = 0.033431522+0.5532*Plato  to match 145-145/SG formula )
        - %ABW (Alc by weight) = 0.8 * % ABV (Alc by Volume)
        - %ABV = Baume
         */
        val OG = targetStartingSG ?: sugarConcToSG(totalSugars!!, outputVolume)
        val FG = targetFinalSG!!
        Timber.d("Read the target final specific gravity to be %4.3f", FG)
        //double brix = ( 135.997 * Math.pow(SG,3))  - ( 630.272*Math.pow(SG,2) ) + (111.14*SG) - 616.868;
        val brix = 182.9622 * Math.pow(FG, 3.0) - 777.3009 * Math.pow(FG, 2.0) + 1264.517 * FG - 670.1832
        val baume = 145 / (145 - brix) // 145 * (1 - (1 / SG));

        /*
        ABV =(76.08 * (og-fg) / (1.775-og)) * (fg / 0.794)

        Brix -> Gravity
        GRAVITY = BRIX ÷ (258.6 - (BRIX ÷ 258.2 × 227.1))) + 1

        ABW
        ABW = ABV × 0.79336

        Gravity -> Brix
        BRIX = (((182.4601 × GRAVITY - 775.6821) × GRAVITY + 1262.7794) × GRAVITY - 669.5622)
        */

        val abv_pct = calcAbvPct(OG, FG)
        val abw_pct = 0.8 * baume
        Timber.d("Estimated percent of Alcohol by Weight to be %4.3f", abw_pct)
        Timber.d("Estimated percent of Alcohol by Volume to be %4.3f", abv_pct)
        return abv_pct
    }
}
