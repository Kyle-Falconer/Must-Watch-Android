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

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.fullmeadalchemist.mustwatch.ui.batch.BatchViewModel;
import com.fullmeadalchemist.mustwatch.ui.batch.detail.BatchDetailViewModel;
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormViewModel;
import com.fullmeadalchemist.mustwatch.ui.log.LogViewModel;
import com.fullmeadalchemist.mustwatch.ui.log.form.LogFormViewModel;
import com.fullmeadalchemist.mustwatch.ui.recipe.RecipeViewModel;
import com.fullmeadalchemist.mustwatch.ui.recipe.detail.RecipeDetailViewModel;
import com.fullmeadalchemist.mustwatch.ui.user.UserProfileViewModel;
import com.fullmeadalchemist.mustwatch.viewmodel.MustWatchViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel bindUserProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BatchViewModel.class)
    abstract ViewModel bindBatchListViewModel(BatchViewModel batchViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BatchFormViewModel.class)
    abstract ViewModel bindBatchFormViewModel(BatchFormViewModel batchFormViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BatchDetailViewModel.class)
    abstract ViewModel bindBatchDetailViewModel(BatchDetailViewModel batchDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LogViewModel.class)
    abstract ViewModel bindLogListViewModel(LogViewModel logViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LogFormViewModel.class)
    abstract ViewModel bindLogFormViewModel(LogFormViewModel logFormViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    abstract ViewModel bindRecipeViewModel(RecipeViewModel recipeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeDetailViewModel.class)
    abstract ViewModel bindRecipeDetailViewModel(RecipeDetailViewModel recipeDetailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MustWatchViewModelFactory factory);
}
