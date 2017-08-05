package com.fullmeadalchemist.mustwatch.ui.batch.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;


public class BatchDetailViewModel extends ViewModel {

    private BatchRepository batchRepository;
    private UserRepository userRepository;

    @Inject
    public BatchDetailViewModel(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchRepository.getBatch(id);
    }

    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

}
