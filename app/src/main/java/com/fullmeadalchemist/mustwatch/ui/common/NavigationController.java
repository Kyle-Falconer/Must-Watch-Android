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
package com.fullmeadalchemist.mustwatch.ui.common;


import android.support.v4.app.FragmentManager;

import com.fullmeadalchemist.mustwatch.MainActivity;
import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;

import javax.inject.Inject;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToBatches() {
        String tag = "batch/list";
        BatchListFragment batchesFragment = new BatchListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToEditBatch(int batchId) {
        String tag = "batch/edit/" + batchId;
        BatchListFragment batchesFragment = new BatchListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToAddBatch() {
        String tag = "batch/add";
        BatchListFragment batchesFragment = new BatchListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, batchesFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    public void navigateToSearch() {
//        SearchFragment searchFragment = new SearchFragment();
//        fragmentManager.beginTransaction()
//                .replace(containerId, searchFragment)
//                .commitAllowingStateLoss();
//    }
//
//    public void navigateToRepo(String owner, String name) {
//        RepoFragment fragment = RepoFragment.create(owner, name);
//        String tag = "repo" + "/" + owner + "/" + name;
//        fragmentManager.beginTransaction()
//                .replace(containerId, fragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss();
//    }

//    public void navigateToUser(String login) {
//        String tag = "user" + "/" + login;
//        UserProfileFragment userFragment = UserProfileFragment.create(login);
//        fragmentManager.beginTransaction()
//                .replace(containerId, userFragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss();
//    }
}