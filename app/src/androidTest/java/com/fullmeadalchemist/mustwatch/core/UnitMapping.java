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

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;

import tec.units.ri.quantity.Quantities;

import static com.fullmeadalchemist.mustwatch.core.UnitMapper.qtyToTextAbbr;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.textAbbrToUnit;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static systems.uom.common.Imperial.GALLON_UK;
import static systems.uom.common.USCustomary.FAHRENHEIT;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.GALLON_LIQUID;
import static systems.uom.common.USCustomary.LITER;
import static systems.uom.common.USCustomary.OUNCE;
import static tec.units.ri.unit.Units.CELSIUS;
import static tec.units.ri.unit.Units.GRAM;

@RunWith(AndroidJUnit4.class)
public class UnitMapping {

    private Resources res;
    private Quantity<Temperature> cTemp;
    private Quantity<Temperature> fTemp;
    private Quantity<Volume> literVol;
    private Quantity<Volume> ukgalVol;
    private Quantity<Volume> usgalVol;
    private Quantity<Volume> flozVol;
    private Quantity<Volume> flozukVol;
    private Quantity<Mass> gramMass;
    private Quantity<Mass> ounceMass;


    @Before
    public void initValidString() {
        res = InstrumentationRegistry.getTargetContext().getResources();

        cTemp = Quantities.getQuantity(2, CELSIUS);
        fTemp = Quantities.getQuantity(2, FAHRENHEIT);

        literVol = Quantities.getQuantity(2, LITER);
        usgalVol = Quantities.getQuantity(32, GALLON_LIQUID);
        flozVol = Quantities.getQuantity(32, FLUID_OUNCE);
        ukgalVol = Quantities.getQuantity(32, GALLON_UK);
        flozukVol = Quantities.getQuantity(32, FLUID_OUNCE);  // FIXME: access to OUNCE_LIQUID_UK is private!?

        gramMass = Quantities.getQuantity(2, GRAM);
        ounceMass = Quantities.getQuantity(2, OUNCE);
    }


    @Test
    public void unitsToStrings() {

        String cAbbr = qtyToTextAbbr(cTemp);
        assertThat(cAbbr, is(not(nullValue())));
        assertThat(cAbbr, is("C"));

        String fAbbr = qtyToTextAbbr(fTemp);
        assertThat(fAbbr, is(not(nullValue())));
        assertThat(fAbbr, is("F"));

        String literAbbr = qtyToTextAbbr(literVol);
        assertThat(literAbbr, is(not(nullValue())));
        assertThat(literAbbr, is("L"));

        String usgalAbbr = qtyToTextAbbr(usgalVol);
        assertThat(usgalAbbr, is(not(nullValue())));
        assertThat(usgalAbbr, is("gal_us"));

        String ukgalAbbr = qtyToTextAbbr(ukgalVol);
        assertThat(ukgalAbbr, is(not(nullValue())));
        assertThat(ukgalAbbr, is("gal_uk"));

        String flozAbbr = qtyToTextAbbr(flozVol);
        assertThat(flozAbbr, is(not(nullValue())));
        assertThat(flozAbbr, is("oz_fl"));

        // FIXME: ambiguous to US fluid ounce
        String flozukAbbr = qtyToTextAbbr(flozukVol);
        assertThat(flozukAbbr, is(not(nullValue())));
        assertThat(flozukAbbr, is("oz_fl"));

        // FIXME
        // This one is buggy; ounceMass' unit has no symbol or name
        // relying on ounceMass.getUnit().toString
        String ounceAbbr = qtyToTextAbbr(ounceMass);
        assertThat(ounceAbbr, is(not(nullValue())));
        assertThat(ounceAbbr, is("oz"));

        String gramAbbr = qtyToTextAbbr(gramMass);
        assertThat(gramAbbr, is(not(nullValue())));
        assertThat(gramAbbr, is("g"));
    }

    @Test
    public void stringsToUnits() {

        Unit<?> literUnit = textAbbrToUnit("L");
        assertThat(literUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(literUnit), is("L"));

        Unit<?> usgalUnit = textAbbrToUnit("gal_us");
        assertThat(usgalUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(usgalUnit), is("gal_us"));

        Unit<?> usflozUnit = textAbbrToUnit("oz_fl");
        assertThat(usflozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(usflozUnit), is("oz_fl"));

        // FIXME: ambiguous to US fluid ounce
        Unit<?> ukflozUnit = textAbbrToUnit("oz_fl_uk");
        assertThat(ukflozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ukflozUnit), is("oz_fl"));

        Unit<?> ukgalUnit = textAbbrToUnit("gal_uk");
        assertThat(ukgalUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ukgalUnit), is("gal_uk"));

        Unit<?> ozUnit = textAbbrToUnit("oz");
        assertThat(ozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ozUnit), is("oz"));

        Unit<?> gramUnit = textAbbrToUnit("g");
        assertThat(gramUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(gramUnit), is("g"));
    }
}
