/*
 * Copyright (c) 2017 Full Mead Alchemist, LLC.
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

package com.fullmeadalchemist.mustwatch;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.MustWatchApp.MUST_WATCH_SHARED_PREFS;

/**
 * Created by Kyle on 8/5/2017.
 */

public class MustWatchPreferences {

    private static final String TAG = MustWatchPreferences.class.getSimpleName();
    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";


    private final Application app;

    @Inject
    public MustWatchPreferences(Application app) {
        this.app = app;
    }

    public Long getCurrentUserID() {
        // FIXME: pull this from persistent storage.
        SharedPreferences pSharedPref = app.getSharedPreferences(MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            Long stored_id = pSharedPref.getLong(CURRENT_USER_ID, Long.MIN_VALUE);
            if (stored_id != Long.MIN_VALUE) {
                Log.d(TAG, String.format("Got User ID %d from shared preferences as the current User ID.", stored_id));
                return stored_id;
            } else {
                Log.d(TAG, "Found no User ID in shared preferences.");
                return null;
            }
        }
        Log.e(TAG, "Could not get shared preferences");
        return null;
    }

    public void setCurrentUserId(Long id) {
        SharedPreferences pSharedPref = app.getSharedPreferences(MUST_WATCH_SHARED_PREFS, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            SharedPreferences.Editor editor = pSharedPref.edit();
            //editor.remove(CURRENT_USER_ID).commit();
            editor.putLong(CURRENT_USER_ID, id).apply();
            Log.d(TAG, String.format("Stored User ID %d in shared preferences as the current User ID.", id));
        }
    }
}
