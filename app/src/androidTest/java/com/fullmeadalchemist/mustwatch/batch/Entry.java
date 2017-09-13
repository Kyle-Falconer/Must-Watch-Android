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

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private Resources res;

    @Before
    public void initValidString() {
        res = InstrumentationRegistry.getTargetContext().getResources();
    }

    @Test
    public void changeText_volumeFlOzUs() {
        changeText_volume(res.getString(R.string.OUNCE_LIQUID_US));
    }

    @Test
    public void changeText_volumeFlOzUk() {
        changeText_volume(res.getString(R.string.OUNCE_LIQUID_UK));
    }

    @Test
    public void changeText_volumeGalUs() {
        changeText_volume(res.getString(R.string.GALLON_LIQUID_US));
    }

    @Test
    public void changeText_volumeGalUk() {
        changeText_volume(res.getString(R.string.GALLON_LIQUID_UK));
    }

    @Test
    public void changeText_volumeLiter() {
        changeText_volume(res.getString(R.string.LITER));
    }


    public void changeText_volume(String volumeResourceString) {
        String volumeScalarToBetyped;
        volumeScalarToBetyped = Integer.toString(randInt(1, 20));

        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(volumeResourceString))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(volumeResourceString)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withSpinnerText(volumeResourceString)));
    }

}

