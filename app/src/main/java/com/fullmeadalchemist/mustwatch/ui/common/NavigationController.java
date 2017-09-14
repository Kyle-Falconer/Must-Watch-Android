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
package com.fullmeadalchemist.mustwatch.ui.common;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.fullmeadalchemist.mustwatch.MainActivity;
import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.batch.detail.BatchDetailFragment;
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormFragment;
import com.fullmeadalchemist.mustwatch.ui.log.form.LogFormFragment;
import com.fullmeadalchemist.mustwatch.ui.meta.AboutFragment;
import com.fullmeadalchemist.mustwatch.ui.recipe.RecipeListFragment;
import com.fullmeadalchemist.mustwatch.ui.recipe.detail.RecipeDetailFragment;
import com.fullmeadalchemist.mustwatch.ui.user.UserProfileFragment;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;
import static com.fullmeadalchemist.mustwatch.vo.Recipe.RECIPE_ID;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private static final String TAG = NavigationController.class.getSimpleName();
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }


    /*===================================
     ===========     Meta     ===========
     ===================================*/
    public void navigateToAbout() {
        String tag = "about";
        AboutFragment aboutFragment = new AboutFragment();
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, aboutFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }


    /*===================================
    ==========       Users     ==========
    ===================================*/
    public void navigateToUserProfile() {
        String tag = "user/view";
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, userProfileFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }


    /*===================================
     ==========     Batches    ==========
     ===================================*/
    public void navigateToBatches() {
        String tag = "batch/list";
        BatchListFragment batchesFragment = new BatchListFragment();
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateToEditBatch(long batchId) {
        String tag = "batch/edit/" + batchId;
        BatchFormFragment batchesFragment = new BatchFormFragment();
        Bundle data = new Bundle();
        data.putLong(BATCH_ID, batchId);
        batchesFragment.setArguments(data);
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateToAddBatch() {
        String tag = "batch/add";
        BatchFormFragment batchesFragment = new BatchFormFragment();
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateToBatchDetail(Long batchId) {
        String tag = "batch/view/" + batchId;
        BatchDetailFragment batchesFragment = new BatchDetailFragment();
        Bundle data = new Bundle();
        data.putLong(BATCH_ID, batchId);
        batchesFragment.setArguments(data);
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateFromAddBatch(Long batchId) {
        Log.i(TAG, "Popping backstack");
        fragmentManager.popBackStack();
        navigateToBatchDetail(batchId);
    }

    public void navigateFromEditBatch(Long batchId) {
        String tag = "batch/view/" + batchId;
        Log.i(TAG, String.format("Popping backstack to: %s", tag));
        fragmentManager.popBackStack(tag, 0);
    }

    public void navigateToAddLog(Long batchId) {
        String tag = "batch/" + batchId + "/log/add";
        LogFormFragment logFormFragment = new LogFormFragment();
        Bundle data = new Bundle();
        data.putLong(BATCH_ID, batchId);
        logFormFragment.setArguments(data);
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, logFormFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }


    /*===================================
     ==========     Recipes     =========
     ===================================*/
    public void navigateToRecipes() {
        String tag = "recipes/list";
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, recipeListFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateToRecipeDetail(Long recipeId) {
        String tag = "recipe/view/" + recipeId;
        RecipeDetailFragment recipesFragment = new RecipeDetailFragment();
        Bundle data = new Bundle();
        data.putLong(RECIPE_ID, recipeId);
        recipesFragment.setArguments(data);
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, recipesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void navigateToCreateFromBatch(Long recipeId) {
        String tag = "batch/add";
        BatchFormFragment batchesFragment = new BatchFormFragment();
        Bundle data = new Bundle();
        data.putLong(RECIPE_ID, recipeId);
        batchesFragment.setArguments(data);
        Log.i(TAG, String.format("Navigating to path: %s", tag));
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }
}