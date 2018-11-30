package com.fullmeadalchemist.mustwatch.db

import android.support.test.runner.AndroidJUnit4

import com.fullmeadalchemist.mustwatch.util.TestUtil
import com.fullmeadalchemist.mustwatch.vo.Group
import com.fullmeadalchemist.mustwatch.vo.GroupMembership
import com.fullmeadalchemist.mustwatch.vo.User

import org.junit.Test
import org.junit.runner.RunWith


import com.fullmeadalchemist.mustwatch.util.LiveDataTestUtil.getValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`


@RunWith(AndroidJUnit4::class)
class UserDbTests : DbTest() {

    @Test
    @Throws(InterruptedException::class)
    fun insertAndLoad() {
        val user = TestUtil.createUser()
        db.userDao().insert(user)

        val (_, name, email) = getValue(db.userDao().findByEmail(user.email!!))
        assertThat<String>(name, `is`<String>(user.name))
        assertThat<String>(email, `is`<String>(user.email))

        val replacement = TestUtil.createUser()
        db.userDao().insert(replacement)

        val (uuid, name1, email1) = getValue(db.userDao().findByEmail(replacement.email!!))
        assertThat<String>(name1, `is`<String>(replacement.name))
        assertThat<String>(email1, `is`<String>(replacement.email))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndLoadGroup() {
        val group1 = TestUtil.createGroup("Group Foo")
        db.groupDao().insert(group1)

        val (name) = getValue(db.groupDao().findByName(group1.name))
        assertThat(name, `is`("Group Foo"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndLoadGroupWith10Members() {
        val n = 10
        val group1 = TestUtil.createGroup("Group Foo")
        db.groupDao().insert(group1)

        val group1_loaded = getValue(db.groupDao().findByName(group1.name))
        assertThat(group1_loaded.name, `is`("Group Foo"))

        val usersToAdd = TestUtil.createUsers(n)
        db.userDao().insertAll(usersToAdd)

        for ((uuid, name, email) in usersToAdd) {
            val (uid, name1) = getValue(db.userDao().findByEmail(email!!))
            assertThat<String>(name1, `is`<String>(name))
            val membership = GroupMembership(group1_loaded.id, uuid)
            membership.userId = uid
            //            membership.setGroupId(group1_loaded.getId());
            membership.accessRead = true
            db.groupDao().insert(membership)
        }
        val members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.id))
        assertThat(members_loaded.size, `is`(n))
    }


    @Test
    @Throws(InterruptedException::class)
    fun insertAndLoadGroupWith100Members() {
        val n = 100
        val group1 = TestUtil.createGroup("Group Foo")
        db.groupDao().insert(group1)

        val group1_loaded = getValue(db.groupDao().findByName(group1.name))
        assertThat(group1_loaded.name, `is`("Group Foo"))

        val usersToAdd = TestUtil.createUsers(n)
        db.userDao().insertAll(usersToAdd)

        for ((uuid, name, email) in usersToAdd) {
            val (uid, name1) = getValue(db.userDao().findByEmail(email!!))
            assertThat<String>(name1, `is`<String>(name))
            val membership = GroupMembership(group1_loaded.id, uuid)
            membership.userId = uid
            //            membership.setGroupId(group1_loaded.getId());
            membership.accessRead = true
            db.groupDao().insert(membership)
        }

        val members_loaded = getValue(db.groupDao().loadAllMembersInGroup(group1_loaded.id))
        assertThat(members_loaded.size, `is`(n))
    }
}
