package com.fullmeadalchemist.mustwatch.core

import android.arch.persistence.room.Room
import com.fullmeadalchemist.mustwatch.db.AppDatabase
import com.fullmeadalchemist.mustwatch.repository.*
import com.fullmeadalchemist.mustwatch.ui.batch.detail.BatchDetailViewModel
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormViewModel
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.ui.recipe.RecipeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    single<BatchIngredientRepository> { BatchIngredientRepositoryImpl(get()) }
    single<BatchRepository> { BatchRepositoryImpl(get()) }
    single<IngredientRepository> { IngredientRepositoryImpl(get()) }
    single<LogEntryRepository> { LogEntryRepositoryImpl(get()) }
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<MustWatchPreferences> { MustWatchPreferencesImpl(get()) }

    single<AppDatabase>() { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()}

    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { BatchDetailViewModel(get(), get(), get()) }
    viewModel { BatchFormViewModel(get(), get(), get(), get(), get()) }
    viewModel { RecipeViewModel(get(), get()) }
}
