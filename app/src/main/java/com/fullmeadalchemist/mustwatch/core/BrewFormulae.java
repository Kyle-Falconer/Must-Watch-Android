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

package com.fullmeadalchemist.mustwatch.core;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

import java.text.DecimalFormat;

import javax.measure.Quantity;
import javax.measure.quantity.Volume;

import tec.units.ri.AbstractQuantity;
import timber.log.Timber;

import static systems.uom.common.CGS.GRAM;
import static systems.uom.common.USCustomary.LITER;


public class BrewFormulae {

    public static double SGToBrix(double SG) {
        return 135.997 * Math.pow(SG, 3) - 630.272 * Math.pow(SG, 2) + 1111.14 * SG - 616.868;
    }

    public static double SGToSugarConc(double SG) {
        return 4 + 10 * SG * SGToBrix(SG);
    }

    public static Double estimateBatchSG(Batch b) {
        Timber.d("Calculating the SG for batch %s", b.name);
        if (b.ingredients == null) {
            Timber.d("No sugars in this batch.");
            return null;
        }
        double sugars = 0d;
        for (BatchIngredient ingredient : b.ingredients) {
            if (ingredient.quantityMass != null) {
                sugars += (double) ingredient.quantityMass.to(GRAM).getValue();
            }
        }

        Timber.d("Counted sugars to be %s grams", String.valueOf(sugars));
        Double sg = null;
        if (Math.abs(sugars - 0d) > 0.0001) {
            sg = sugarConcToSG(sugars, b.outputVolume);
        }
        Timber.d("Calculated SG to be %s", String.valueOf(sg));
        return sg;
    }

    /**
     * Guess the SG based on the g/L weight of sugars.
     * Accuracy is only to 3 decimal places
     *
     * @param totalSugars  the amount of sugars in grams.
     * @param outputVolume the output volume of the batch.
     * @return calculated specific gravity based on provided sugars and output volume.
     */
    public static double sugarConcToSG(double totalSugars, Quantity<Volume> outputVolume) {
        double sugars_in_grams_per_L = totalSugars / (double) outputVolume.to(LITER).getValue();
        double sg_guess = 0.992;    // lowest point

        double loopLimit = 100000;
        double gpl;
        double diff;
        for (int i = 0; i < loopLimit; i++) {
            gpl = SGToSugarConc(sg_guess);
            if (Math.round(gpl) == Math.round(sugars_in_grams_per_L)) {
                return sg_guess;
            }
            // scale by number of integer digits + 2, ie gpl of 312.45 means scale of 5 decimal points => 0.00001
            diff = Math.pow(10, -1 * (2 + (Math.round(gpl) + "").length()));
            sg_guess = sg_guess * (1 + diff);
        }
        return 0d;
    }


    public static double calcAbvPct(Double OG, Double FG) {
        return (76.08 * (OG - FG) / (1.775 - OG)) * (FG / 0.794);
    }

    public static double getFinalABV_pct(Double targetStartingSG, Double targetFinalSG, Double totalSugars, AbstractQuantity<Volume> outputVolume) {
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
        double OG = (targetStartingSG != null) ? targetStartingSG : sugarConcToSG(totalSugars, outputVolume);
        double FG = targetFinalSG;
        Timber.d("Read the target final specific gravity to be %4.3f", FG);
        //double brix = ( 135.997 * Math.pow(SG,3))  - ( 630.272*Math.pow(SG,2) ) + (111.14*SG) - 616.868;
        double brix = (182.9622 * Math.pow(FG, 3)) - (777.3009 * Math.pow(FG, 2)) + (1264.517 * FG) - 670.1832;
        double baume = 145 / (145 - brix); // 145 * (1 - (1 / SG));

        /*
        ABV =(76.08 * (og-fg) / (1.775-og)) * (fg / 0.794)

        Brix -> Gravity
        GRAVITY = BRIX ÷ (258.6 - (BRIX ÷ 258.2 × 227.1))) + 1

        ABW
        ABW = ABV × 0.79336

        Gravity -> Brix
        BRIX = (((182.4601 × GRAVITY - 775.6821) × GRAVITY + 1262.7794) × GRAVITY - 669.5622)
        */

        double abv_pct = calcAbvPct(OG, FG);
        double abw_pct = 0.8 * baume;
        Timber.d("Estimated percent of Alcohol by Weight to be %4.3f", abw_pct);
        Timber.d("Estimated percent of Alcohol by Volume to be %4.3f", abv_pct);
        return abv_pct;
    }
}
