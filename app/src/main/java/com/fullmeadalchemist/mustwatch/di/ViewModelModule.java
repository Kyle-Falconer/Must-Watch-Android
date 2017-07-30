package com.fullmeadalchemist.mustwatch.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.fullmeadalchemist.mustwatch.ui.batch.BatchViewModel;
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
    abstract ViewModel bindSearchViewModel(BatchViewModel batchViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MustWatchViewModelFactory factory);
}
