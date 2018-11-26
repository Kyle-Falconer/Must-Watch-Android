package com.fullmeadalchemist.mustwatch.ui.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.fullmeadalchemist.mustwatch.core.MustWatchPreferences
import com.fullmeadalchemist.mustwatch.repository.BatchRepository
import com.fullmeadalchemist.mustwatch.repository.LogEntryRepository
import com.fullmeadalchemist.mustwatch.repository.RecipeRepository
import com.fullmeadalchemist.mustwatch.repository.UserRepository
import com.fullmeadalchemist.mustwatch.vo.*
import timber.log.Timber
import java.util.*

import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val batchRepository: BatchRepository,
        private val userRepository: UserRepository,
        private val logEntryRepository: LogEntryRepository,
        private val recipeRepository: RecipeRepository,
        private val preferences: MustWatchPreferences
) : ViewModel() {

    var logEntry: LogEntry? = null
    var selectedBatchId : Long? = null
    var batch : Batch? = null

    val isFirstLaunch: LiveData<Boolean>
        get() = preferences.isFirstLaunch()

    internal fun getCurrentUser():LiveData<User> {
        return userRepository.getCurrentUser()
    }

    internal fun getUser(uid: UUID?): LiveData<User> {
        return userRepository.getUser(uid!!)
    }

    internal fun getRecipe(recipeId: Long?): LiveData<Recipe> {
        return recipeRepository.getRecipe(recipeId!!)
    }

    internal fun getRecipeIngredients(recipeId: Long?): LiveData<List<BatchIngredient>> {
        return recipeRepository.getRecipeIngredients(recipeId)
    }

    internal fun getBatchesForUser(userId: UUID?): LiveData<List<Batch>> {
        return batchRepository.getBatchesForUserId(userId!!)
    }

    internal fun getLogEntries(batchId: Long): LiveData<List<LogEntry>> {
        return logEntryRepository.getLogEntries(batchId)
    }

    internal fun addLogEntry(logEntry: LogEntry) {
        logEntryRepository.addLogEntry(logEntry)
    }

    internal fun updateLogEntryTime(hourOfDay:Int, minute:Int){
        if (logEntry == null){
            Timber.e("Failed to update log entry time")
            return
        }
        logEntry?.let{ entry ->
            entry.entryDate.set(Calendar.HOUR, hourOfDay)
            entry.entryDate.set(Calendar.MINUTE, minute)
        }
    }

    internal fun updateLogEntryDate(year:Int, month:Int, dayOfMonth:Int){
        if (logEntry == null){
            Timber.e("Failed to update log entry date")
            return
        }
        logEntry?.let{ entry ->
            entry.entryDate.set(Calendar.YEAR, year)
            entry.entryDate.set(Calendar.MONTH, month)
            entry.entryDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }
}
