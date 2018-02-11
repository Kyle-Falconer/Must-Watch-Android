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

package com.fullmeadalchemist.mustwatch.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.fullmeadalchemist.mustwatch.MustWatchPreferences;
import com.fullmeadalchemist.mustwatch.db.AppDatabase;
import com.fullmeadalchemist.mustwatch.db.BatchDao;
import com.fullmeadalchemist.mustwatch.db.BatchIngredientDao;
import com.fullmeadalchemist.mustwatch.db.IngredientDao;
import com.fullmeadalchemist.mustwatch.db.LogEntryDao;
import com.fullmeadalchemist.mustwatch.db.RecipeDao;
import com.fullmeadalchemist.mustwatch.db.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;



@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    AppDatabase provideDb(Application app) {
        // fixme: change to reference AppDatabase.DATABASE_NAME
        return Room.databaseBuilder(app, AppDatabase.class, "mustwatch-db").build();
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
    LogEntryDao provideLogEntryDao(AppDatabase db) {
        return db.logEntryDao();
    }

    @Singleton
    @Provides
    IngredientDao provideIngredientDao(AppDatabase db) {
        return db.ingredientDao();
    }

    @Singleton
    @Provides
    BatchIngredientDao provideBatchIngredientDao(AppDatabase db) {
        return db.batchIngredientDao();
    }

    @Singleton
    @Provides
    RecipeDao provideRecipeDao(AppDatabase db) {
        return db.recipeDao();
    }

    @Singleton
    @Provides
    MustWatchPreferences provideMustWatchPreferences(Application app) {
        return new MustWatchPreferences(app);
    }
}
