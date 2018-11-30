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

package com.fullmeadalchemist.mustwatch.batch

import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.fullmeadalchemist.mustwatch.R

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withSpinnerText
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.fullmeadalchemist.mustwatch.StartActivity
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randInt
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.AllOf.allOf

@RunWith(AndroidJUnit4::class)
class Entry {

    @Rule
    var mActivityRule = ActivityTestRule<StartActivity>(StartActivity::class.java)

    private var res: Resources? = null

    @Before
    fun initValidString() {
        res = InstrumentationRegistry.getTargetContext().resources
    }

    @Test
    fun changeText_volumeFlOzUs() {
        changeText_volume(res!!.getString(R.string.OUNCE_LIQUID_US))
    }

    @Test
    fun changeText_volumeFlOzUk() {
        changeText_volume(res!!.getString(R.string.OUNCE_LIQUID_UK))
    }

    @Test
    fun changeText_volumeGalUs() {
        changeText_volume(res!!.getString(R.string.GALLON_LIQUID_US))
    }

    @Test
    fun changeText_volumeGalUk() {
        changeText_volume(res!!.getString(R.string.GALLON_LIQUID_UK))
    }

    @Test
    fun changeText_volumeLiter() {
        changeText_volume(res!!.getString(R.string.LITER))
    }


    fun changeText_volume(volumeResourceString: String) {
        val volumeScalarToBetyped: String = Integer.toString(randInt(1, 20))

        // Navigate to the Add Batch screen
        onView(withId(R.id.batches_fab)).perform().perform(click())

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard())
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`(volumeResourceString))).perform(click())

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click())

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)))
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(volumeResourceString)))

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click())

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)))

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(volumeResourceString)))
    }

}

