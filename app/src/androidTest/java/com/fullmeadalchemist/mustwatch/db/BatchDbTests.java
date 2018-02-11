package com.fullmeadalchemist.mustwatch.db;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.core.JSONResourceReader;
import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;
import com.fullmeadalchemist.mustwatch.vo.Ingredient;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import tec.units.ri.quantity.Quantities;

import static com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static systems.uom.common.USCustomary.FLUID_OUNCE;
import static systems.uom.common.USCustomary.POUND;

@RunWith(AndroidJUnit4.class)
public class BatchDbTests extends DbTest {

    @Before
    public void init() {
        Resources res = InstrumentationRegistry.getTargetContext().getResources();
        String packageName = InstrumentationRegistry.getTargetContext().getPackageName();
        JSONResourceReader reader = new JSONResourceReader(res, R.raw.ingredients);
        Ingredient[] ingredients = reader.constructUsingGson(Ingredient[].class);
        db.ingredientDao().insertAll(ingredients);
    }

    @Test
    public void insertAndLoad() throws InterruptedException {

        final User user = TestUtil.createUser();
        db.userDao().insert(user);
        final User loaded_user = getValue(db.userDao().findByEmail(user.getEmail()));
        assertThat(loaded_user.getName(), is(user.getName()));

        final Batch batch = TestUtil.createBatch(loaded_user.getId());
        db.batchDao().insert(batch);

        final List<Batch> loaded_batch = getValue(db.batchDao().loadBatchesForUser(loaded_user.getId()));
        assertThat(loaded_batch.size(), is(1));
        assertThat(loaded_batch.get(0).getCreateDate().getTime(), is(batch.getCreateDate().getTime()));
    }

    @Test
    public void insertAndLoad10LogEntries() throws InterruptedException {
        int n = 10;
        final User user = TestUtil.createUser();
        db.userDao().insert(user);
        final User loaded_user = getValue(db.userDao().findByEmail(user.getEmail()));
        assertThat(loaded_user.getName(), is(user.getName()));

        final Batch batch = TestUtil.createBatch(loaded_user.getId());
        db.batchDao().insert(batch);

        // loadLogEntriesForBatch
        final List<Batch> loaded_batches = getValue(db.batchDao().loadBatchesForUser(loaded_user.getId()));
        assertThat(loaded_batches.size(), is(1));
        final Batch loaded_batch = loaded_batches.get(0);
        assertThat(loaded_batch.getCreateDate().getTime(), is(batch.getCreateDate().getTime()));

        for (int i = 0; i < n; i++) {
            LogEntry entry = new LogEntry();
            entry.setBatchId(loaded_batch.getId());
            entry.setNote(String.valueOf(i));
            db.logEntryDao().insert(entry);
        }

        final List<LogEntry> entries_loaded = getValue(db.logEntryDao().loadAllByBatchIds(loaded_batch.getId()));
        assertThat(entries_loaded.size(), is(n));
    }

    @Test
    public void insertAndLoadWithTwoIngredients() throws InterruptedException {

        final User user = TestUtil.createUser();
        db.userDao().insert(user);
        final User loaded_user = getValue(db.userDao().findByEmail(user.getEmail()));
        assertThat(loaded_user.getName(), is(user.getName()));

        final Batch batch = TestUtil.createBatch(loaded_user.getId());
        db.batchDao().insert(batch);

        final List<Batch> loaded_batch = getValue(db.batchDao().loadBatchesForUser(loaded_user.getId()));
        assertThat(loaded_batch.size(), is(1));

        Batch added_batch = loaded_batch.get(0);
        assertThat(added_batch.getCreateDate().getTime(), is(batch.getCreateDate().getTime()));

        Ingredient cloverHoneyIngredient = getValue(db.ingredientDao().getById("HONEY__CLOVER"));
        BatchIngredient ingredient1 = new BatchIngredient();
        ingredient1.setBatchId(added_batch.getId());
        ingredient1.setIngredientId(cloverHoneyIngredient.getId());
        ingredient1.setQuantityMass(Quantities.getQuantity(2, POUND));
        db.batchIngredientDao().insert(ingredient1);

        Ingredient grapeJuiceIngredient = getValue(db.ingredientDao().getById("WELCHS_GRAPE_JUICE"));
        BatchIngredient ingredient2 = new BatchIngredient();
        ingredient2.setBatchId(added_batch.getId());
        ingredient2.setIngredientId(grapeJuiceIngredient.getId());
        ingredient2.setQuantityVol(Quantities.getQuantity(64, FLUID_OUNCE));
        db.batchIngredientDao().insert(ingredient2);

        List<BatchIngredient> addedBatchIngredients = getValue(db.batchIngredientDao().getIngredientsForBatch(added_batch.getId()));
        assertThat(addedBatchIngredients.size(), is(2));
        assertThat(addedBatchIngredients.get(0).getBatchId(), is(added_batch.getId()));
        assertThat(addedBatchIngredients.get(0).getIngredientId(), is(ingredient1.getIngredientId()));

        assertThat(addedBatchIngredients.get(1).getBatchId(), is(added_batch.getId()));
        assertThat(addedBatchIngredients.get(1).getIngredientId(), is(ingredient2.getIngredientId()));
    }

}
