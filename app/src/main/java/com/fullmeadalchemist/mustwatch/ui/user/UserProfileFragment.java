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

package com.fullmeadalchemist.mustwatch.ui.user;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.UserDetailFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;

import javax.inject.Inject;

public class UserProfileFragment extends LifecycleFragment implements Injectable {
    private static final String UID_KEY = "uid";
    private static final String TAG = UserProfileFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    UserDetailFragmentBinding dataBinding;
    private UserProfileViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.user_detail_fragment,
                container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel.class);
        viewModel.getUser().observe(this, user -> {
            Log.d(TAG, String.format("Got user from db: %s", user));
            if (user != null) {
                if (user.isAnon()) {
                    user.name = "Anonymous";
                }
                dataBinding.setUser(user);
            }
        });
    }
}
