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
import android.util.Log;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Volume;

import tec.units.ri.quantity.Quantities;

import static systems.uom.common.Imperial.GALLON_UK;
import static systems.uom.common.USCustomary.FAHRENHEIT;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.GALLON_DRY;
import static systems.uom.common.USCustomary.GALLON_LIQUID;
import static systems.uom.common.USCustomary.LITER;
import static systems.uom.common.USCustomary.OUNCE;
import static systems.uom.common.USCustomary.POUND;
import static tec.units.ri.unit.Units.CELSIUS;
import static tec.units.ri.unit.Units.GRAM;
import static tec.units.ri.unit.Units.KILOGRAM;


public class UnitMapper {

    private static final String TAG = UnitMapper.class.getSimpleName();

    /**
     * Convert standard unit names into jscience Units.
     * <p>
     * Used for serialization and deserialization.
     *
     * @param abbr the standard or abbreviated name of the unit
     * @return the Unit that maps to the abbreviation, such as 'L' -> LITER
     */
    public static Unit<?> textAbbrToUnit(String abbr) {
        Unit<?> unit;
        switch (abbr) {
            case "L":
                unit = LITER;
                break;
            case "gal_us":
                unit = GALLON_LIQUID;
                break;
            case "gallon_dry_us":
                unit = GALLON_DRY;
                break;
            case "gal_uk":
                unit = GALLON_UK;
                break;
            case "lb":
                unit = POUND;
                break;
            case "oz":
                unit = OUNCE;
                break;
            case "oz_fl":
                unit = FLUID_OUNCE;
                break;
            case "oz_fl_uk":
                unit = FLUID_OUNCE;
                // FIXME: OUNCE_LIQUID_UK is not public
                // https://github.com/unitsofmeasurement/uom-systems/blob/master/common-java8/src/main/java/systems/uom/common/Imperial.java#L229
                // unit = OUNCE_LIQUID_UK;
                break;
            case "kg":
                unit = KILOGRAM;
                break;
            case "g":
                unit = GRAM;
                break;
            case "F":
                unit = FAHRENHEIT;
                break;
            case "C":
                unit = CELSIUS;
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
        if (qty == null){
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
        String unitName = unit.getName();
        String unitSymbol = unit.getSymbol();
        String unitString = unit.toString();
        if (unitName == null && unitSymbol == null) {
            unitName = unitString;
        }
        Log.d(TAG, String.format("Parsing Unit: %s\nUnit name: %s\n Unit symbol: %s", unit.toString(), unitName, unitSymbol));
        if (unitSymbol == null) {
            switch (unitName) {
                case "°C":
                    unitSymbol = "C";
                    break;
                case "°F":
                    unitSymbol = "F";
                    break;
                case "g":
                    // FIXME: this comes from the unitString
                    unitSymbol = "g";
                    break;
                case "Liter":
                    unitSymbol = "L";
                    break;
                case "Fluid Ounze":
                    unitSymbol = "oz_fl";
                    break;
                case "oz":
                    // FIXME: this comes from the unitString
                    unitSymbol = "oz";
                    break;
                case "gal_uk":
                    // FIXME: this comes from the unitString
                    unitSymbol = "gal_uk";
                    break;
                case "US gallon":
                    unitSymbol = "gal_us";
                    break;
            }
        }
        Log.d(TAG, String.format("Returning unit symbol: %s", unitSymbol));
        return unitSymbol;
    }

    //
    @Nullable
    public static Quantity<Volume> toVolume(String floatText, String unitText) {
        Float scalar = ValueParsers.toFloat(floatText);
        if (scalar == null || unitText == null) {
            return null;
        }
        Unit<Volume> unit;
        try {
            unit = (Unit<Volume>) textAbbrToUnit(unitText);
        } catch (ClassCastException e) {
            Log.e(TAG, String.format("Failed to cast unit text %s to Unit<Volume>", unitText));
            return null;
        }
        if (unit == null){
            Log.e(TAG, String.format("Failed to get Unit<Volume> from text %s", unitText));
            return null;
        }
        return Quantities.getQuantity(scalar, unit);
    }
}
