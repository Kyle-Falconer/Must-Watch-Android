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
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class Entry {

    private String volumeScalarToBetyped;
    private String unitToSelect;
    private String unitSelectedAbbr;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void initValidString() {
        volumeScalarToBetyped = "5";
        unitToSelect = "Gallon (U.S.)";
        unitSelectedAbbr = "gal";
    }

    @Test
    public void changeText_sameActivity() {
        // Navigate to the Add Batch screen
        onView((withId(R.id.batches_fab))).perform().perform(click());

        // Type add a volume amount and unit
        onView(withId(R.id.outputVolumeAmount))
                .perform(typeText(volumeScalarToBetyped), closeSoftKeyboard());
        onView(withId(R.id.outputVolumeAmountUnit)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(unitToSelect))).perform(click());

        // Submit the form
        onView(withId(R.id.button_submit))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // Check that the text was changed in the Batch detail view
        onView(withId(R.id.outputVolumeAmount))
                .check(matches(withText(volumeScalarToBetyped)));
        onView(withId(R.id.outputVolumeAmountUnit))
                .check(matches(withText(unitSelectedAbbr)));

        // Go back to the edit screen
        onView(withId(R.id.button_edit_batch))
                .perform(ViewActions.scrollTo())
                .perform(click());

        // verify the volume amount is correct
        onView(withId(R.id.outputVolumeAmount)).check(matches(withText(volumeScalarToBetyped)));

        // verify the volume unit is correct
        onView(withId(R.id.outputVolumeAmountUnit)).check(matches(withText(unitToSelect)));
    }
}

