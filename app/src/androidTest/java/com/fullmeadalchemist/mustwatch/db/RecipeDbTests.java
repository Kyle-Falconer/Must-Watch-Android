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

package com.fullmeadalchemist.mustwatch.db;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.core.JSONResourceReader;
import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;
import com.fullmeadalchemist.mustwatch.vo.Recipe;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class RecipeDbTests extends DbTest {

    private long uid;

    @Before
    public void init() {
        Resources res = InstrumentationRegistry.getTargetContext().getResources();

        User user = TestUtil.createUser();
        uid = db.userDao().insert(user);

        JSONResourceReader ingredientsReader = new JSONResourceReader(res, R.raw.ingredients);
        Ingredient[] ingredients = ingredientsReader.constructUsingGson(Ingredient[].class);
        db.ingredientDao().insertAll(ingredients);

        JSONResourceReader recipesReader = new JSONResourceReader(res, R.raw.recipes);
        Recipe[] recipes = recipesReader.constructUsingGson(Recipe[].class);
        db.recipeDao().insertAll(recipes);
    }


    @Test
    public void insertAndLoad() throws InterruptedException {
        final Batch batch = TestUtil.createBatch(uid);
        db.batchDao().insert(batch);

        final List<Batch> loaded_batch = getValue(db.batchDao().loadBatchesForUser(uid));
        assertThat(loaded_batch.size(), is(1));
        assertThat(loaded_batch.get(0).getCreateDate().getTime(), is(batch.getCreateDate().getTime()));
    }
}
