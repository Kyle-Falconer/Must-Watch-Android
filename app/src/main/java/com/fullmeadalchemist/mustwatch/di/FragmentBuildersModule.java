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


import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.batch.detail.BatchDetailFragment;
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormFragment;
import com.fullmeadalchemist.mustwatch.ui.log.LogListFragment;
import com.fullmeadalchemist.mustwatch.ui.log.form.LogFormFragment;
import com.fullmeadalchemist.mustwatch.ui.user.UserProfileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract BatchListFragment contributeBatchListFragment();

    @ContributesAndroidInjector
    abstract BatchFormFragment contributeBatchFormFragment();

    @ContributesAndroidInjector
    abstract BatchDetailFragment contributeBatchDetailFragment();

    @ContributesAndroidInjector
    abstract LogListFragment contributeLogListFragment();

    @ContributesAndroidInjector
    abstract LogFormFragment contributeLogFormFragment();

    @ContributesAndroidInjector
    abstract UserProfileFragment contributeUserProfileFragment();
}
