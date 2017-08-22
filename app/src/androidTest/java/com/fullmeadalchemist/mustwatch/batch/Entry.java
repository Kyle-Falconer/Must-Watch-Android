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

package com.fullmeadalchemist.mustwatch.batch;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.MainActivity;
import com.fullmeadalchemist.mustwatch.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randInt;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class Entry {

    private Resources res;
    private String volumeScalarToBetyped;
    private String flozusResourceString;
    private String flozukResourceString;
    private String galliqusResourceString;
    private String literResourceString;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void initValidString() {
        res = InstrumentationRegistry.getTargetContext().getResources();
        volumeScalarToBetyped = "5";
        flozusResourceString = res.getString(R.string.OUNCE_LIQUID_US);
        flozukResourceString = res.getString(R.string.OUNCE_LIQUID_UK);
        galliqusResourceString = res.getString(R.string.GALLON_LIQUID_US);
        literResourceString = res.getString(R.string.LITER);
    }

    @Test
    public void changeText_FlOzUs() {
        volumeScalarToBetyped = Integer.toString(randInt(1,20));

        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(flozusResourceString))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(flozusResourceString)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(flozusResourceString)));
    }

    @Test
    public void changeText_FlOzUk() {
        volumeScalarToBetyped = Integer.toString(randInt(1,20));

        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(flozukResourceString))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(flozukResourceString)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(flozukResourceString)));
    }

    @Test
    public void changeText_GalLiqUs() {
        volumeScalarToBetyped = Integer.toString(randInt(1,20));

        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(galliqusResourceString))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(galliqusResourceString)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(galliqusResourceString)));
    }

    @Test
    public void changeText_liter() {
        volumeScalarToBetyped = Integer.toString(randInt(1,20));

        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(literResourceString))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(literResourceString)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(literResourceString)));
    }

}

