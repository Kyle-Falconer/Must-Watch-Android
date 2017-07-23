package com.fullmeadalchemist.mustwatch.db;

import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.LogEntry;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Kyle on 7/22/2017.
 */

@RunWith(AndroidJUnit4.class)
public class BatchDbTests extends DbTest {

    @Test
    public void insertAndLoad() throws InterruptedException {

        final User user = TestUtil.createUser("foo", "foo@email.com");
        db.userDao().insert(user);
        final User loaded_user = getValue(db.userDao().findByEmail(user.email));
        assertThat(loaded_user.name, is("foo"));

        final Batch batch = TestUtil.createBatch();
        batch.createDate = new Date(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        batch.userId = loaded_user.id;
        db.batchDao().insert(batch);

        final List<Batch> loaded_batch = getValue(db.batchDao().loadBatchesForUser(loaded_user.id));
        assertThat(loaded_batch.size(), is(1));
        assertThat(loaded_batch.get(0).createDate.getTime(), is(batch.createDate.getTime()));
    }

    @Test
    public void insertAndLoad10LogEntries() throws InterruptedException {
        int n = 10;
        final User user = TestUtil.createUser("foo", "foo@email.com");
        db.userDao().insert(user);
        final User loaded_user = getValue(db.userDao().findByEmail(user.email));
        assertThat(loaded_user.name, is("foo"));

        final Batch batch = TestUtil.createBatch();
        batch.createDate = new Date(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        batch.userId = loaded_user.id;
        db.batchDao().insert(batch);

        // loadLogEntriesForBatch
        final List<Batch> loaded_batches = getValue(db.batchDao().loadBatchesForUser(loaded_user.id));
        assertThat(loaded_batches.size(), is(1));
        final Batch loaded_batch = loaded_batches.get(0);
        assertThat(loaded_batch.createDate.getTime(), is(batch.createDate.getTime()));

        for (int i = 0; i < n; i++) {
            LogEntry entry = new LogEntry();
            entry.batchId = loaded_batch.id;
            entry.note = String.valueOf(i);
            db.batchDao().insert(entry);
        }

        final List<LogEntry> entries_loaded = getValue(db.batchDao().loadLogEntriesForBatch(loaded_batch.id));
        assertThat(entries_loaded.size(), is(n));
    }
}
