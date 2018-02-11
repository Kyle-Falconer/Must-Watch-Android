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

package com.fullmeadalchemist.mustwatch.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.fullmeadalchemist.mustwatch.vo.*

@Database(version = 1,
        entities = arrayOf(
                User::class,
                Batch::class,
                BatchIngredient::class,
                LogEntry::class,
                Group::class,
                GroupMembership::class,
                Ingredient::class,
                Recipe::class))
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun batchDao(): BatchDao

    abstract fun logEntryDao(): LogEntryDao

    abstract fun groupDao(): GroupDao

    abstract fun ingredientDao(): IngredientDao

    abstract fun batchIngredientDao(): BatchIngredientDao

    abstract fun recipeDao(): RecipeDao

    companion object {

        val DATABASE_NAME = "mustwatch-db"
        /**
         * The only instance
         */
        private var dbInstance: AppDatabase? = null

        /**
         * Gets the singleton instance of SampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of SampleDatabase.
         */
        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (dbInstance == null) {
                dbInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "ex")
                        .build()
            }
            return dbInstance
        }
    }
}