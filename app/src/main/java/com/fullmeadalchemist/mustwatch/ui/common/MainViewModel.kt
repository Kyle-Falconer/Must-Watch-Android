package com.fullmeadalchemist.mustwatch.ui.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.fullmeadalchemist.mustwatch.core.MustWatchPreferences
import com.fullmeadalchemist.mustwatch.repository.*
import com.fullmeadalchemist.mustwatch.vo.*
import timber.log.Timber
import java.util.*

import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val batchRepository: BatchRepository,
        private val userRepository: UserRepository,
        private val logEntryRepository: LogEntryRepository,
        private val ingredientRepository: IngredientRepository,
        private val recipeRepository: RecipeRepository,
        private val preferences: MustWatchPreferences
) : ViewModel() {

    var cachedUserId : UUID? = null
    var logEntry: LogEntry? = null
    var selectedBatchId: Long? = null
    var selectedRecipeId: Long? = null
    var batch: Batch? = null
    var recipe: Recipe? = null

    val isFirstLaunch: LiveData<Boolean>
        get() = preferences.isFirstLaunch()

    internal fun getCurrentUser(): LiveData<User> {
        return userRepository.getCurrentUser()
    }

    internal fun getUser(uid: UUID?): LiveData<User> {
        return userRepository.getUser(uid!!)
    }

    internal fun getBatch(id: Long?): LiveData<Batch> {
        return batchRepository.getBatch(id)
    }

    internal fun getRecipe(recipeId: Long?): LiveData<Recipe> {
        return recipeRepository.getRecipe(recipeId!!)
    }

    internal fun getRecipes(uid: UUID?): LiveData<List<Recipe>> {
        return recipeRepository.getRecipes(uid)
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

    internal fun updateLogEntryTime(hourOfDay: Int, minute: Int) {
        if (logEntry == null) {
            Timber.e("Failed to update log entry time")
            return
        }
        logEntry?.let { entry ->
            entry.entryDate.set(Calendar.HOUR, hourOfDay)
            entry.entryDate.set(Calendar.MINUTE, minute)
        }
    }

    internal fun updateLogEntryDate(year: Int, month: Int, dayOfMonth: Int) {
        if (logEntry == null) {
            Timber.e("Failed to update log entry date")
            return
        }
        logEntry?.let { entry ->
            entry.entryDate.set(Calendar.YEAR, year)
            entry.entryDate.set(Calendar.MONTH, month)
            entry.entryDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    fun getYeasts(): LiveData<List<Ingredient>> {
        return ingredientRepository.getYeasts()
    }

    fun getSugars(): LiveData<List<Ingredient>> {
        return ingredientRepository.getSugars()
    }

    fun getStabilizers(): LiveData<List<Ingredient>> {
        return ingredientRepository.getStabilizers()
    }

    fun getNutrients(): LiveData<List<Ingredient>> {
        return ingredientRepository.getNutrients()
    }

    fun saveNewBatch(batch: Batch): LiveData<Long> {
        return batchRepository.addBatch(batch)
    }

    fun updateBatch(batch: Batch): LiveData<Int> {
        return batchRepository.updateBatch(batch)
    }

    fun getBatchIngredients(batchId: Long): LiveData<List<BatchIngredient>> {
        return batchRepository.getBatchIngredients(batchId)
    }

    fun addIngredientToBatch(batchIngredient: BatchIngredient) : LiveData<Long> {
        if (batchIngredient.batchId == null){
            batchIngredient.batchId = batch?.id
        }
        return batchRepository.addIngredient(batchIngredient)
    }

    fun addIngredientsToBatch(batchIngredients: List<BatchIngredient>?) : LiveData<List<Long>> {
        if (batchIngredients != null) {
            for (ingredient in batchIngredients) {
                if (ingredient.batchId == null) {
                    ingredient.batchId = batch?.id
                }
            }
        }

        return batchRepository.addIngredients(batchIngredients)
    }
}
