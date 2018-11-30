/*
 * Copyright (c) 2018 Full Mead Alchemist, LLC.
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

package com.fullmeadalchemist.mustwatch.users

import com.fullmeadalchemist.mustwatch.vo.Group
import com.fullmeadalchemist.mustwatch.vo.GroupMembership
import com.fullmeadalchemist.mustwatch.vo.User

import org.junit.Test

import java.util.UUID

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

class UserTest {
    @Test
    @Throws(Exception::class)
    fun group_creation_isCorrect() {
        val john_smith = User(UUID.randomUUID(), "John Smith", "johnsmith@gmail.com")
        val bob_smith = User(UUID.randomUUID(), "Bob Smith", "bobsmith@gmail.com")
        val jane_smith = User(UUID.randomUUID(), "Jane Smith", "janesmith@gmail.com")

        val group1 = Group("Numero Uno")

//        // first user on a group should be admin
//        val jsmith_group1_membership = john_smith.getMembershipForGroup(group1)
//        assertNotNull(jsmith_group1_membership)
//        assertTrue(jsmith_group1_membership.isAdmin())
//
//        // check that you cannot add a duplicate member
//        assertFalse(group1.addMember(john_smith, john_smith))
//        assertEquals(group1.getNumberOfMembers(), 1)
//        assertTrue(group1.userIsInGroup(john_smith))
//        assertFalse(group1.userIsInGroup(bob_smith))
//
//        // check that a user who has no permissions cannot add another member to the group
//        assertFalse(group1.addMember(john_smith, bob_smith))
//        assertEquals(group1.getNumberOfMembers(), 1)
//        assertTrue(group1.userIsInGroup(john_smith))
//        assertFalse(group1.userIsInGroup(bob_smith))
//
//        // check that a user who is an admin can add another member to the group
//        assertTrue(group1.addMember(bob_smith, john_smith))
//        assertEquals(group1.getNumberOfMembers(), 2)
//        assertTrue(group1.userIsInGroup(john_smith))
//        assertTrue(group1.userIsInGroup(bob_smith))
//
//        // chceck that a member of the group who is not an admin cannot add members to the group
//        assertFalse(group1.addMember(jane_smith, bob_smith))
//        assertEquals(group1.getNumberOfMembers(), 2)
//        assertTrue(group1.userIsInGroup(bob_smith))
//        assertFalse(group1.userIsInGroup(jane_smith))
//
//        // check that members can be removed by others
//        assertTrue(group1.removeMember(bob_smith, john_smith))
//        assertEquals(group1.getNumberOfMembers(), 1)
//
//        // check that members can be removed by themselves
//        assertTrue(group1.removeMember(john_smith, john_smith))
//        assertEquals(group1.getNumberOfMembers(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun group_attributes_isCorrect() {
        val (uid, name, email) = User(UUID.randomUUID(), "John Smith", "jsmith@gmail.com")

        val group1 = Group("Numero Uno")
        assertEquals(group1.name, "Numero Uno")

        group1.name = "Numero Dos"
        assertEquals(group1.name, "Numero Dos")

//        assertEquals(group1.getNumberOfMembers(), 1)
    }
}
