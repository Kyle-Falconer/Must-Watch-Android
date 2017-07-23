package com.fullmeadalchemist.mustwatch.db;

import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.Group;
import com.fullmeadalchemist.mustwatch.vo.GroupMembership;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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

    @Test
    public void insertAndLoadGroup() throws InterruptedException {
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.name));
        assertThat(group1_loaded.name, is("Group Foo"));
    }

    @Test
    public void insertAndLoadGroupWith10Members() throws InterruptedException {
        int n = 10;
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.name));
        assertThat(group1_loaded.name, is("Group Foo"));

        for (int i = 0; i < n; i++) {
            User member = TestUtil.createUser("foo" + i, "foo" + i + "@email.com");
            db.userDao().insert(member);
        }

        for (int i = 0; i < n; i++) {
            User member_loaded = getValue(db.userDao().findByEmail("foo" + i + "@email.com"));
            assertThat(member_loaded.name, is("foo" + i));
            GroupMembership membership = new GroupMembership();
            membership.userId = member_loaded.id;
            membership.groupId = group1_loaded.id;
            membership.accessRead = true;
            db.groupDao().insert(membership);
        }
        List<User> members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.id));
        assertThat(members_loaded.size(), is(n));
    }


    @Test
    public void insertAndLoadGroupWith100Members() throws InterruptedException {
        int n = 100;
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.name));
        assertThat(group1_loaded.name, is("Group Foo"));

        for (int i = 0; i < n; i++) {
            User member = TestUtil.createUser("foo" + i, "foo" + i + "@email.com");
            db.userDao().insert(member);
        }

        for (int i = 0; i < n; i++) {
            User member_loaded = getValue(db.userDao().findByEmail("foo" + i + "@email.com"));
            assertThat(member_loaded.name, is("foo" + i));
            GroupMembership membership = new GroupMembership();
            membership.userId = member_loaded.id;
            membership.groupId = group1_loaded.id;
            membership.accessRead = true;
            db.groupDao().insert(membership);
        }

        List<User> members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.id));
        assertThat(members_loaded.size(), is(n));
    }
}
