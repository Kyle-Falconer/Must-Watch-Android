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

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(AndroidJUnit4.class)
public class JSONResources {

    private Resources res;
    private String packageName;

    @Before
    public void init() {
        res = InstrumentationRegistry.getTargetContext().getResources();
        packageName = InstrumentationRegistry.getTargetContext().getPackageName();
    }

    @Test
    public void loadIngredients() {
        JSONResourceReader reader = new JSONResourceReader(res, R.raw.ingredients);
        Ingredient[] jsonObj = reader.constructUsingGson(Ingredient[].class);
        assertThat(jsonObj, is(not(nullValue())));
        assertTrue(jsonObj.length > 0);

        for (Ingredient ingredient : jsonObj) {
            if ("SUGAR".equals(ingredient.type)) {
                assertTrue(ingredient.totalPct > 0);
                if (ingredient.density != null) {
                    assertTrue(ingredient.density > 0);
                }
            }
            assertStringResourceNotNull(ingredient.id);
        }
    }



    private void assertStringResourceNotNull(String stringId) {
        try {
            int resId = res.getIdentifier(stringId, "string", packageName);
            assertThat(res.getString(resId), is(not(nullValue())));
        } catch (Resources.NotFoundException e) {
            System.out.println("\"" + stringId + "\" not found in strings.xml");
            throw e;
        }
    }
}
