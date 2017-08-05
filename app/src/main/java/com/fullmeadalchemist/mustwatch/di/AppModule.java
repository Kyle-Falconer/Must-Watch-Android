/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.fullmeadalchemist.mustwatch.MustWatchPreferences;
import com.fullmeadalchemist.mustwatch.db.AppDatabase;
import com.fullmeadalchemist.mustwatch.db.BatchDao;
import com.fullmeadalchemist.mustwatch.db.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.fullmeadalchemist.mustwatch.db.AppDatabase.DATABASE_NAME;

@Module(includes = ViewModelModule.class)
class AppModule {
//    @Singleton @Provides
//    GithubService provideGithubService() {
//        return new Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
//                .build()
//                .create(GithubService.class);
//    }

    @Singleton
    @Provides
    AppDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, AppDatabase.class, DATABASE_NAME).build();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(AppDatabase db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    BatchDao provideBatchDao(AppDatabase db) {
        return db.batchDao();
    }

    @Singleton
    @Provides
    MustWatchPreferences provideMustWatchPreferences(Application app) {
        return new MustWatchPreferences(app);
    }
}
