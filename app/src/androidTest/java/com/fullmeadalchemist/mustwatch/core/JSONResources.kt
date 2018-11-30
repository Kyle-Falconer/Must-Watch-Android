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

package com.fullmeadalchemist.mustwatch.core

import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import com.fullmeadalchemist.mustwatch.vo.Ingredient
import com.fullmeadalchemist.mustwatch.vo.Recipe

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class JSONResources {

    lateinit var res: Resources
    private var packageName: String? = null

    @Before
    fun init() {
        res = InstrumentationRegistry.getTargetContext().resources
        packageName = InstrumentationRegistry.getTargetContext().packageName
    }

    /**
     * Verify that each of the ingredients defined in the resource files map to a corresponding
     * string resource.
     */
    @Test
    fun loadIngredients() {
        val reader = JSONResourceReader(res, R.raw.ingredients)
        val ingredients = reader.constructFromJson(Array<Ingredient>::class.java)
        assertNotNull(ingredients)
        assertTrue(ingredients.isNotEmpty())

        for (ingredient in ingredients) {
            if ("SUGAR" == ingredient.type.toString()) {
                ingredient.totalPct?.let {pct ->
                    assertTrue(pct > 0)
                }
                ingredient.density?.let {density ->
                    assertTrue(density > 0)
                }
            }
            assertStringResourceNotNull(ingredient.id)
        }
    }

    @Test
    fun loadRecipes() {
        val reader = JSONResourceReader(res, R.raw.recipes)
        val recipes = reader.constructFromJson(Array<Recipe>::class.java)
        assertThat(recipes, `is`(not(nullValue())))
        assertTrue(recipes.isNotEmpty())

        for ((_, _, _, _, _, _, _, _, _, _, _, _, ingredients) in recipes) {
            assertThat<List<BatchIngredient>>(ingredients, `is`(not(nullValue())))
            assertTrue(ingredients!!.isNotEmpty())
        }
    }

    private fun assertStringResourceNotNull(stringId: String) {
        try {
            val resId = res.getIdentifier(stringId, "string", packageName)
            assertThat(res.getString(resId), `is`(not(nullValue())))
        } catch (e: Resources.NotFoundException) {
            println("\"$stringId\" not found in strings.xml")
            throw e
        }
    }
}
