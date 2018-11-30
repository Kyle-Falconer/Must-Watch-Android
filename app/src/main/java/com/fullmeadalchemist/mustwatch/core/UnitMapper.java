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

import android.support.annotation.Nullable;

import com.fullmeadalchemist.mustwatch.R;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;

import tec.units.ri.quantity.Quantities;
import timber.log.Timber;

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


public class UnitMapper {

    // FIXME: These methods are repetitive

    /**
     * Convert standard unit names into jscience Units.
     * <p>
     * Used for serialization and deserialization.
     *
     * @param abbr the standard or abbreviated name of the unit
     * @return the Unit that maps to the abbreviation, such as 'L' -> LITER
     */
    public static Unit<Volume> textAbbrToVolUnit(String abbr) {
        Unit<Volume> unit;
        switch (abbr) {
            case "L":
                unit = LITER;
                break;
            case "gal_us":
                unit = GALLON_LIQUID;
                break;
            case "gal_dry_us":
                unit = GALLON_DRY;
                break;
            case "gal_uk":
                unit = GALLON_UK;
                break;
            case "oz_fl_us":
            case "fl oz":
            case "fl_oz":
            case "oz_fl_uk":
            case "oz_fl":
                unit = FLUID_OUNCE;
                break;
            case "tsp":
                unit = TEASPOON;
                break;
            default:
                unit = null;
                break;
        }
        return unit;
    }

    public static Unit<Mass> textAbbrToMassUnit(String abbr) {
        Unit<Mass> unit;
        switch (abbr) {
            case "g":
                unit = GRAM;
                break;
            case "kg":
                unit = KILOGRAM;
                break;
            case "oz":
                unit = OUNCE;
                break;
            case "lb":
                unit = POUND;
                break;
            default:
                unit = null;
                break;
        }
        return unit;
    }

    public static Unit<Temperature> textAbbrToTempUnit(String abbr) {
        Unit<Temperature> unit;
        switch (abbr) {
            case "C":
                unit = CELSIUS;
                break;
            case "F":
                unit = FAHRENHEIT;
                break;
            default:
                unit = null;
                break;
        }
        return unit;
    }

    /**
     * Convert Quantity's Units into standard unit names.
     * <p>
     * Used for serialization and deserialization.
     *
     * @param qty The quantity to use to produce the string representation.
     * @return the standard or abbreviated name of the unit
     */
    public static String qtyToTextAbbr(Quantity<?> qty) {
        if (qty == null) {
            return null;
        }
        return unitToTextAbbr(qty.getUnit());
    }

    /**
     * Convert Units into standard unit names.
     * <p>
     * Used for serialization and deserialization.
     *
     * @param unit The quantity to use to produce the string representation.
     * @return the standard or abbreviated name of the unit
     */
    public static String unitToTextAbbr(Unit<?> unit) {
        if (unit == null) {
            return null;
        }
        String unitString = unit.toString();

        if (unitString == null) {
            String unitName = unit.getName();
            String unitSymbol = unit.getSymbol();
            Timber.e("Unit provided did not have a toString set. Unit name: %s\n Unit symbol: %s", unitName, unitSymbol);
            return null;
        }
        Timber.d("Parsing Unit: %s", unit.toString());

        switch (unitString) {
            case "°C":
                unitString = "C";
                break;
            case "°F":
                unitString = "F";
                break;
            case "L":
                unitString = "L";
                break;
            case "fl oz":
            case "oz_fl":
                unitString = "oz_fl";
                break;
            case "gal_uk":
                unitString = "gal_uk";
                break;
            case "gal_dry_us":
                unitString = "gal_dry_us";
                break;
            case "gal":
                unitString = "gal_us";
                break;
            case "tsp":
                unitString = "tsp";
                break;
            case "g":
                unitString = "g";
                break;
            case "kg":
                unitString = "kg";
                break;
            case "oz":
                unitString = "oz";
                break;
            case "lb":
                unitString = "lb";
                break;
        }

        Timber.d("Returning unit symbol: %s", unitString);
        return unitString;
    }

    @Nullable
    public static Quantity<Mass> toMass(String scalarText, String unitText) {
        Float scalar = ValueParsers.toFloat(scalarText);
        return toMass(scalar, unitText);
    }

    @Nullable
    public static Quantity<Mass> toMass(Float scalar, String unitText) {
        if (scalar == null || unitText == null) {
            return null;
        }
        Unit<Mass> unit;
        try {
            unit = textAbbrToMassUnit(unitText);
        } catch (ClassCastException e) {
            Timber.e("Failed to cast unit text %s to Unit<Mass>", unitText);
            return null;
        }
        if (unit == null) {
            Timber.e("Failed to get Unit<Mass> from text %s", unitText);
            return null;
        }
        return Quantities.getQuantity(scalar, unit);
    }

    @Nullable
    public static Quantity<Volume> toVolume(String scalarText, String unitText) {
        Float scalar = ValueParsers.toFloat(scalarText);
        return toVolume(scalar, unitText);
    }

    @Nullable
    public static Quantity<Volume> toVolume(Float scalar, String unitText) {
        if (scalar == null || unitText == null) {
            return null;
        }
        Unit<Volume> unit;
        try {
            unit = textAbbrToVolUnit(unitText);
        } catch (ClassCastException e) {
            Timber.e("Failed to cast unit text %s to Unit<Volume>", unitText);
            return null;
        }
        if (unit == null) {
            Timber.e("Failed to get Unit<Volume> from text %s", unitText);
            return null;
        }
        return Quantities.getQuantity(scalar, unit);
    }


    public static int unitToStringResource(Unit<?> unit) {
        String textAbbr = unitToTextAbbr(unit);
        int resId;
        switch (textAbbr) {
            case "C":
                resId = R.string.DEGREES_C;
                break;
            case "F":
                resId = R.string.DEGREES_F;
                break;
            case "L":
                resId = R.string.LITER;
                break;
            case "gal_us":
                resId = R.string.GALLON_LIQUID_US;
                break;
            case "gal_dry_us":
                resId = R.string.GALLON_DRY_US;
                break;
            case "gal_uk":
                resId = R.string.GALLON_LIQUID_UK;
                break;
            case "oz_fl":
            case "oz_fl_us":
            case "oz_fl_uk":
                resId = R.string.OUNCE_LIQUID_US;
                break;
            case "g":
                resId = R.string.GRAM;
                break;
            case "kg":
                resId = R.string.KILOGRAM;
                break;
            case "oz":
                resId = R.string.OUNCE;
                break;
            case "lb":
                resId = R.string.POUND;
                break;
            default:
                resId = R.string.UNKNOWN_UNIT;
                break;
        }
        return resId;
    }


}
