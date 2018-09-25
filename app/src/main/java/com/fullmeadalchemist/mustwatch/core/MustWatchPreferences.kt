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

package com.fullmeadalchemist.mustwatch.core

import android.app.Application
import android.content.Context
import com.fullmeadalchemist.mustwatch.MustWatchApp
import timber.log.Timber
import javax.inject.Inject

class MustWatchPreferences {

    @Inject
    lateinit var application: Application

    private var currentUserID: Long? = null

    fun getCurrentUserId(): Long? {
        currentUserID = null
        val pSharedPref = application.getSharedPreferences(MustWatchApp.MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val storedId = pSharedPref.getLong(CURRENT_USER_ID, java.lang.Long.MIN_VALUE)
            if (storedId != java.lang.Long.MIN_VALUE) {
                Timber.d("Got User ID %d from shared preferences as the current User ID.", storedId)
                currentUserID = storedId
            } else {
                Timber.d("Found no User ID in shared preferences.")
            }
        }
        Timber.e("Could not get shared preferences")
        return currentUserID
    }

    fun setCurrentUserId(id: Long?) {
        if (id == null) {
            Timber.w("Attempted to set current user to null")
            return
        }
        val pSharedPref = application.getSharedPreferences(MustWatchApp.MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val editor = pSharedPref.edit()
            //editor.remove(CURRENT_USER_ID).commit();
            editor.putLong(CURRENT_USER_ID, id).apply()
            Timber.d("Stored User ID %d in shared preferences as the current User ID.", id)
        }
    }

    companion object {
        private const val CURRENT_USER_ID = "CURRENT_USER_ID"
    }

}
