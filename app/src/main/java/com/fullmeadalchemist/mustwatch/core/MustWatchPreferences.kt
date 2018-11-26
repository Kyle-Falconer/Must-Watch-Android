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
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.fullmeadalchemist.mustwatch.MustWatchApp
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*

interface MustWatchPreferences {
    fun getCurrentUserId(): UUID?
    fun setCurrentUserId(uid: UUID)
    fun isFirstLaunch(): LiveData<Boolean>
}

class MustWatchPreferencesImpl(private val application: Application) : MustWatchPreferences {

    private var currentUserID: UUID? = null

    override fun getCurrentUserId(): UUID? {
        currentUserID = null
        val pSharedPref = application.getSharedPreferences(MustWatchApp.MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val storedUid = pSharedPref.getString(CURRENT_USER_ID, null)
            if (storedUid != null) {
                Timber.d("Got User ID %s from shared preferences as the current User ID.", storedUid)
                currentUserID = UUID.fromString(storedUid)
            } else {
                Timber.d("Found no User ID in shared preferences.")
            }
        } else {
            Timber.e("Could not get shared preferences")
        }
        return currentUserID
    }

    override fun setCurrentUserId(uid: UUID) {
        val pSharedPref = application.getSharedPreferences(MustWatchApp.MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val editor = pSharedPref.edit()
            editor.putString(CURRENT_USER_ID, uid.toString()).apply()
            Timber.d("Stored User ID %s in shared preferences as the current User ID.", uid.toString())
        }
    }

    override fun isFirstLaunch(): LiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            val pSharedPref = application.getSharedPreferences(MustWatchApp.MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE)
            if (pSharedPref != null) {
                val editor = pSharedPref.edit()
                val isFirstLaunch = pSharedPref.getBoolean(FIRST_LAUNCH_KEY, true)
                if (isFirstLaunch) {
                    Timber.d("Detected that this is the first launch.")
                    editor.putBoolean(FIRST_LAUNCH_KEY, false).apply()
                }
                response.postValue(isFirstLaunch)
            }
        }
        return response
    }

    companion object {
        private const val CURRENT_USER_ID = "CURRENT_USER_ID"
        private const val FIRST_LAUNCH_KEY = "FIRST_LAUNCH"
    }
}
