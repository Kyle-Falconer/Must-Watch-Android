package com.fullmeadalchemist.mustwatch.util

import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randDouble
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randFloat
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randInt
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randVolume
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundOneDecimalPlace
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundThreeDecimalPlaces
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundTwoDecimalPlaces
import com.fullmeadalchemist.mustwatch.demo.DemoGenerators.status
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Group
import com.fullmeadalchemist.mustwatch.vo.User
import java.util.*


object TestUtil {

    fun createUser(): User {
        val name = "test_user" + randInt(1, 100)
        val email = "$name@example.com"
        return User(UUID.randomUUID(), name, email)
    }

    fun createUsers(n: Int): List<User> {
        val usedIds = HashSet<Int>()
        val usersToAdd = ArrayList<User>()
        for (i in 0 until n) {
            var userN = randInt(1, 10 * n)
            while (usedIds.contains(userN)) {
                userN = randInt(1, 10 * n)
            }
            usedIds.add(userN)
            val name = "test_user$userN"
            val email = "$name@example.com"
            usersToAdd.add(User(UUID.randomUUID(), name, email))
        }
        return usersToAdd
    }


    fun createBatch(userId: UUID): Batch {
        val b = Batch()
        b.name = "Dummy Batch #" + randInt(1, 1000)
        b.createDate = GregorianCalendar.getInstance()
        b.status = status
        b.targetSgStarting = roundThreeDecimalPlaces(randDouble(1.10, 1.30))
        b.targetSgFinal = roundThreeDecimalPlaces(randDouble(0.95, 1.05))
        b.startingPh = roundTwoDecimalPlaces(randFloat(3.0f, 5.5f))
        b.startingTemp = roundOneDecimalPlace(randFloat(65f, 75f))
        b.outputVolume = randVolume
        b.targetABV = roundTwoDecimalPlaces(randFloat(0.08f, 0.17f))
        b.userId = userId
        b.notes = String.format("Dummy Batch \"%s\"", b.name)
        return b
    }

    fun createGroup(name: String): Group {
        return Group(name)
    }

}