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

        final User loaded = getValue(db.userDao().findByEmail(user.email));
        assertThat(loaded.name, is(user.name));
        assertThat(loaded.email, is(user.email));

        final User replacement = TestUtil.createUser();
        db.userDao().insert(replacement);

        final User loadedReplacement = getValue(db.userDao().findByEmail(replacement.email));
        assertThat(loadedReplacement.name, is(replacement.name));
        assertThat(loadedReplacement.email, is(replacement.email));
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

        List<User> usersToAdd = TestUtil.createUsers(n);
        db.userDao().insertAll(usersToAdd);

        for (User member : usersToAdd) {
            User member_loaded = getValue(db.userDao().findByEmail(member.email));
            assertThat(member_loaded.name, is(member.name));
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

        List<User> usersToAdd = TestUtil.createUsers(n);
        db.userDao().insertAll(usersToAdd);

        for (User member : usersToAdd) {
            User member_loaded = getValue(db.userDao().findByEmail(member.email));
            assertThat(member_loaded.name, is(member.name));
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
