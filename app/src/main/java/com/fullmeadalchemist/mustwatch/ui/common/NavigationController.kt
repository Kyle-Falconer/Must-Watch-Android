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
package com.fullmeadalchemist.mustwatch.ui.common


import android.support.v4.app.FragmentManager
import com.fullmeadalchemist.mustwatch.MainActivity
import com.fullmeadalchemist.mustwatch.R

/**
 * A utility class that handles navigation in [MainActivity].
 */
class NavigationController
constructor(mainActivity: MainActivity) {
    private val containerId: Int
    private val fragmentManager: FragmentManager

    init {
        this.containerId = R.id.container
        this.fragmentManager = mainActivity.supportFragmentManager
    }


//    /*===================================
//     ===========     Meta     ===========
//     ===================================*/
//    fun navigateToAbout() {
//        val tag = "about"
//        val aboutFragment = AboutFragment()
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, aboutFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//
//    /*===================================
//    ==========       Users     ==========
//    ===================================*/
//    fun navigateToUserProfile() {
//        val tag = "user/view"
//        val userProfileFragment = UserProfileFragment()
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, userProfileFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//
//    /*===================================
//     ==========     Batches    ==========
//     ===================================*/
//    fun navigateToBatches() {
//        val tag = "batch/list"
//        val batchesFragment = BatchListFragment()
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, batchesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToEditBatch(batchId: Long) {
//        val tag = "batch/edit/" + batchId
//        val batchesFragment = BatchFormFragment()
//        val data = Bundle()
//        data.putLong(BATCH_ID, batchId)
//        batchesFragment.arguments = data
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, batchesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToAddBatch() {
//        val tag = "batch/add"
//        val batchesFragment = BatchFormFragment()
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, batchesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToBatchDetail(batchId: Long?) {
//        val tag = "batch/view/" + batchId!!
//        val batchesFragment = BatchDetailFragment()
//        val data = Bundle()
//        data.putLong(BATCH_ID, batchId)
//        batchesFragment.arguments = data
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, batchesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateFromAddBatch(batchId: Long?) {
//        Timber.i("Popping backstack")
//        fragmentManager.popBackStack()
//        navigateToBatchDetail(batchId)
//    }
//
//    fun navigateFromEditBatch(batchId: Long?) {
//        val tag = "batch/view/" + batchId!!
//        Timber.i("Popping backstack to: %s", tag)
//        fragmentManager.popBackStack(tag, 0)
//    }
//
//    fun navigateToAddLog(batchId: Long) {
//        val tag = "batch/$batchId/log/add"
//        val logFormFragment = LogFormFragment()
//        val data = Bundle()
//        data.putLong(BATCH_ID, batchId)
//        logFormFragment.arguments = data
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, logFormFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//
//    /*===================================
//     ==========     Recipes     =========
//     ===================================*/
//    fun navigateToRecipes() {
//        val tag = "recipes/list"
//        val recipeListFragment = RecipeListFragment()
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, recipeListFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToRecipeDetail(recipeId: Long?) {
//        val tag = "recipe/view/" + recipeId!!
//        val recipesFragment = RecipeDetailFragment()
//        val data = Bundle()
//        data.putLong(RECIPE_ID, recipeId)
//        recipesFragment.arguments = data
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, recipesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToCreateFromBatch(recipeId: Long?) {
//        val tag = "batch/add"
//        val batchesFragment = BatchFormFragment()
//        val data = Bundle()
//        data.putLong(RECIPE_ID, recipeId!!)
//        batchesFragment.arguments = data
//        Timber.i("Navigating to path: %s", tag)
//        fragmentManager.beginTransaction()
//                .replace(containerId, batchesFragment, tag)
//                .addToBackStack(tag)
//                .commitAllowingStateLoss()
//    }

}