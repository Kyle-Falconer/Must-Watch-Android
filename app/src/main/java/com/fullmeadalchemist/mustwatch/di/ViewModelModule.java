package com.fullmeadalchemist.mustwatch.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.fullmeadalchemist.mustwatch.ui.batch.BatchViewModel;
import com.fullmeadalchemist.mustwatch.ui.batch.detail.BatchDetailViewModel;
import com.fullmeadalchemist.mustwatch.ui.batch.form.BatchFormViewModel;
import com.fullmeadalchemist.mustwatch.ui.log.LogViewModel;
import com.fullmeadalchemist.mustwatch.ui.log.form.LogFormViewModel;
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
    abstract ViewModelProvider.Factory bindViewModelFactory(MustWatchViewModelFactory factory);
}
