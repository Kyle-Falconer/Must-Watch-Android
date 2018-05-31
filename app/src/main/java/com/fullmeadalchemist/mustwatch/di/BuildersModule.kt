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

import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormFragment
import com.fullmeadalchemist.mustwatch.ui.log.LogListFragment
import com.fullmeadalchemist.mustwatch.ui.log.form.LogFormFragment
import com.fullmeadalchemist.mustwatch.ui.meta.AboutFragment
import com.fullmeadalchemist.mustwatch.ui.recipe.RecipeListFragment
import com.fullmeadalchemist.mustwatch.ui.recipe.detail.RecipeDetailFragment
import com.fullmeadalchemist.mustwatch.ui.user.UserProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    internal abstract fun batchListFragment(): BatchListFragment

    @ContributesAndroidInjector
    internal abstract fun batchFormFragment(): BatchFormFragment

    @ContributesAndroidInjector
    internal abstract fun logListFragment(): LogListFragment

    @ContributesAndroidInjector
    internal abstract fun logFormFragment(): LogFormFragment

    @ContributesAndroidInjector
    internal abstract fun recipeListFragment(): RecipeListFragment

    @ContributesAndroidInjector
    internal abstract fun recipeDetailFragment(): RecipeDetailFragment

    @ContributesAndroidInjector
    internal abstract fun userProfileFragment(): UserProfileFragment

    @ContributesAndroidInjector
    internal abstract fun aboutFragment(): AboutFragment
}