package com.fullmeadalchemist.mustwatch.db;

import android.support.test.runner.AndroidJUnit4;

import com.fullmeadalchemist.mustwatch.util.TestUtil;
import com.fullmeadalchemist.mustwatch.vo.Group;
import com.fullmeadalchemist.mustwatch.vo.GroupMembership;
import com.fullmeadalchemist.mustwatch.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class UserDbTests extends DbTest {

    @Test
    public void insertAndLoad() throws InterruptedException {
        final User user = TestUtil.createUser();
        db.userDao().insert(user);

        final User loaded = getValue(db.userDao().findByEmail(user.getEmail()));
        assertThat(loaded.getName(), is(user.getName()));
        assertThat(loaded.getEmail(), is(user.getEmail()));

        final User replacement = TestUtil.createUser();
        db.userDao().insert(replacement);

        final User loadedReplacement = getValue(db.userDao().findByEmail(replacement.getEmail()));
        assertThat(loadedReplacement.getName(), is(replacement.getName()));
        assertThat(loadedReplacement.getEmail(), is(replacement.getEmail()));
    }

    @Test
    public void insertAndLoadGroup() throws InterruptedException {
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.getName()));
        assertThat(group1_loaded.getName(), is("Group Foo"));
    }

    @Test
    public void insertAndLoadGroupWith10Members() throws InterruptedException {
        int n = 10;
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.getName()));
        assertThat(group1_loaded.getName(), is("Group Foo"));

        List<User> usersToAdd = TestUtil.createUsers(n);
        db.userDao().insertAll(usersToAdd);

        for (User member : usersToAdd) {
            User member_loaded = getValue(db.userDao().findByEmail(member.getEmail()));
            assertThat(member_loaded.getName(), is(member.getName()));
            GroupMembership membership = new GroupMembership();
            membership.setUserId(member_loaded.getId());
            membership.setGroupId(group1_loaded.getId());
            membership.setAccessRead(true);
            db.groupDao().insert(membership);
        }
        List<User> members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.getId()));
        assertThat(members_loaded.size(), is(n));
    }


    @Test
    public void insertAndLoadGroupWith100Members() throws InterruptedException {
        int n = 100;
        final Group group1 = TestUtil.createGroup("Group Foo");
        db.groupDao().insert(group1);

        final Group group1_loaded = getValue(db.groupDao().findByName(group1.getName()));
        assertThat(group1_loaded.getName(), is("Group Foo"));

        List<User> usersToAdd = TestUtil.createUsers(n);
        db.userDao().insertAll(usersToAdd);

        for (User member : usersToAdd) {
            User member_loaded = getValue(db.userDao().findByEmail(member.getEmail()));
            assertThat(member_loaded.getName(), is(member.getName()));
            GroupMembership membership = new GroupMembership();
            membership.setUserId(member_loaded.getId());
            membership.setGroupId(group1_loaded.getId());
            membership.setAccessRead(true);
            db.groupDao().insert(membership);
        }

        List<User> members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.getId()));
        assertThat(members_loaded.size(), is(n));
    }
}
