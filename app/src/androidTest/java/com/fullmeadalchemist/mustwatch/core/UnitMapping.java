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
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.textAbbrToMassUnit;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.textAbbrToTempUnit;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.textAbbrToVolUnit;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.unitToTextAbbr;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static systems.uom.common.Imperial.GALLON_UK;
import static systems.uom.common.Imperial.OUNCE_LIQUID;
import static systems.uom.common.USCustomary.FAHRENHEIT;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.GALLON_DRY;
import static systems.uom.common.USCustomary.GALLON_LIQUID;
import static systems.uom.common.USCustomary.LITER;
import static systems.uom.common.USCustomary.OUNCE;
import static systems.uom.common.USCustomary.POUND;
import static systems.uom.common.USCustomary.TEASPOON;
import static tec.units.ri.unit.Units.CELSIUS;
import static tec.units.ri.unit.Units.GRAM;
import static tec.units.ri.unit.Units.KILOGRAM;

@RunWith(AndroidJUnit4.class)
public class UnitMapping {

    private Resources res;
    private Quantity<Temperature> cTemp;
    private Quantity<Temperature> fTemp;
    private Quantity<Volume> literVol;
    private Quantity<Volume> ukgalVol;
    private Quantity<Volume> usgalVol;
    private Quantity<Volume> usgalDryVol;
    private Quantity<Volume> flozVol;
    private Quantity<Volume> flozukVol;
    private Quantity<Volume> teaspoonMass;
    private Quantity<Mass> gramMass;
    private Quantity<Mass> kgMass;
    private Quantity<Mass> ounceMass;
    private Quantity<Mass> poundMass;


    @Before
    public void initValidString() {
        res = InstrumentationRegistry.getTargetContext().getResources();

        cTemp = Quantities.getQuantity(2, CELSIUS);
        fTemp = Quantities.getQuantity(2, FAHRENHEIT);

        literVol = Quantities.getQuantity(2, LITER);
        usgalVol = Quantities.getQuantity(32, GALLON_LIQUID);
        usgalDryVol = Quantities.getQuantity(32, GALLON_DRY);
        flozVol = Quantities.getQuantity(32, FLUID_OUNCE);
        ukgalVol = Quantities.getQuantity(32, GALLON_UK);
        flozukVol = Quantities.getQuantity(32, OUNCE_LIQUID);

        gramMass = Quantities.getQuantity(2, GRAM);
        kgMass = Quantities.getQuantity(2, KILOGRAM);
        ounceMass = Quantities.getQuantity(2, OUNCE);
        poundMass = Quantities.getQuantity(2, POUND);
        teaspoonMass = Quantities.getQuantity(2, TEASPOON);
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

        String usgalDryAbbr = qtyToTextAbbr(usgalDryVol);
        assertThat(usgalDryAbbr, is(not(nullValue())));
        assertThat(usgalDryAbbr, is("gal_dry_us"));

        String flozAbbr = qtyToTextAbbr(flozVol);
        assertThat(flozAbbr, is(not(nullValue())));
        assertThat(flozAbbr, is("oz_fl_us"));

        String flozukAbbr = qtyToTextAbbr(flozukVol);
        assertThat(flozukAbbr, is(not(nullValue())));
        assertThat(flozukAbbr, is("oz_fl_uk"));

        String ukgalAbbr = qtyToTextAbbr(ukgalVol);
        assertThat(ukgalAbbr, is(not(nullValue())));
        assertThat(ukgalAbbr, is("gal_uk"));

        String teaspoonAbbr = qtyToTextAbbr(teaspoonMass);
        assertThat(teaspoonAbbr, is(not(nullValue())));
        assertThat(teaspoonAbbr, is("tsp"));

        String gramAbbr = qtyToTextAbbr(gramMass);
        assertThat(gramAbbr, is(not(nullValue())));
        assertThat(gramAbbr, is("g"));

        String kgAbbr = qtyToTextAbbr(kgMass);
        assertThat(kgAbbr, is(not(nullValue())));
        assertThat(kgAbbr, is("kg"));

        String ounceAbbr = qtyToTextAbbr(ounceMass);
        assertThat(ounceAbbr, is(not(nullValue())));
        assertThat(ounceAbbr, is("oz"));

        String poundAbbr = qtyToTextAbbr(poundMass);
        assertThat(poundAbbr, is(not(nullValue())));
        assertThat(poundAbbr, is("lb"));
    }

    @Test
    public void stringsToUnits() {
        Unit<?> tempCUnit = textAbbrToTempUnit("C");
        assertThat(tempCUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(tempCUnit), is("C"));

        Unit<?> tempFUnit = textAbbrToTempUnit("F");
        assertThat(tempFUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(tempFUnit), is("F"));

        Unit<?> literUnit = textAbbrToVolUnit("L");
        assertThat(literUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(literUnit), is("L"));

        Unit<?> usgalUnit = textAbbrToVolUnit("gal_us");
        assertThat(usgalUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(usgalUnit), is("gal_us"));

        Unit<?> usgalDryAbbr = textAbbrToVolUnit("gal_dry_us");
        assertThat(usgalDryAbbr, is(not(nullValue())));
        assertThat(unitToTextAbbr(usgalDryAbbr), is("gal_dry_us"));

        Unit<?> usflozUnit = textAbbrToVolUnit("oz_fl_us");
        assertThat(usflozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(usflozUnit), is("oz_fl_us"));

        Unit<?> ukgalUnit = textAbbrToVolUnit("gal_uk");
        assertThat(ukgalUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ukgalUnit), is("gal_uk"));

        Unit<?> ukflozUnit = textAbbrToVolUnit("oz_fl_uk");
        assertThat(ukflozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ukflozUnit), is("oz_fl_uk"));

        Unit<?> tspUnit = textAbbrToVolUnit("tsp");
        assertThat(tspUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(tspUnit), is("tsp"));

        Unit<?> gramUnit = textAbbrToMassUnit("g");
        assertThat(gramUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(gramUnit), is("g"));

        Unit<?> kgUnit = textAbbrToMassUnit("kg");
        assertThat(kgUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(kgUnit), is("kg"));

        Unit<?> ozUnit = textAbbrToMassUnit("oz");
        assertThat(ozUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(ozUnit), is("oz"));

        Unit<?> lbUnit = textAbbrToMassUnit("lb");
        assertThat(lbUnit, is(not(nullValue())));
        assertThat(unitToTextAbbr(lbUnit), is("lb"));
    }
}
