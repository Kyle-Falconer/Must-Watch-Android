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

package com.fullmeadalchemist.mustwatch.di

import android.app.Application
import android.arch.persistence.room.Room
import com.fullmeadalchemist.mustwatch.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class RoomModule {

    @Inject lateinit var application: Application

    private val db: AppDatabase = Room.databaseBuilder(application,
            AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()

    @Singleton
    @Provides
    fun provideDb(): AppDatabase {
        return db
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideBatchDao(db: AppDatabase): BatchDao {
        return db.batchDao()
    }

    @Singleton
    @Provides
    fun provideLogEntryDao(db: AppDatabase): LogEntryDao {
        return db.logEntryDao()
    }

    @Singleton
    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao {
        return db.ingredientDao()
    }

    @Singleton
    @Provides
    fun provideBatchIngredientDao(db: AppDatabase): BatchIngredientDao {
        return db.batchIngredientDao()
    }

    @Singleton
    @Provides
    fun provideRecipeDao(db: AppDatabase): RecipeDao {
        return db.recipeDao()
    }

}