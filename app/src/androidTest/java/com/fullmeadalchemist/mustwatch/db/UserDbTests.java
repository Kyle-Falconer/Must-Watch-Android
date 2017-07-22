package com.fullmeadalchemist.mustwatch.db;

import android.support.test.runner.AndroidJUnit4;


import com.fullmeadalchemist.mustwatch.db.DbTest;
import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Kyle on 7/22/2017.
 */

@RunWith(AndroidJUnit4.class)
public class UserDbTests extends DbTest {

    @Test
    public void insertAndLoad() throws InterruptedException {
        final User user = TestUtil.createUser("foo", "foo@email.com");
        db.userDao().insert(user);

        final User loaded = getValue(db.userDao().findByEmail(user.email));
        assertThat(loaded.name, is("foo"));
        assertThat(loaded.email, is("foo@email.com"));

        final User replacement = TestUtil.createUser("foo2", "foo2@email.com");
        db.userDao().insert(replacement);

        final User loadedReplacement = getValue(db.userDao().findByEmail("foo2@email.com"));
        assertThat(loadedReplacement.name, is("foo2"));
        assertThat(loadedReplacement.email, is("foo2@email.com"));
    }

}
