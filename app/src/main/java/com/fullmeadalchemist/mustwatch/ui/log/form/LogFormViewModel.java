package com.fullmeadalchemist.mustwatch.ui.log.form;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.List;

import javax.inject.Inject;


public class LogFormViewModel extends ViewModel {

    private BatchRepository batchRepository;
    private UserRepository userRepository;

    @Inject
    public LogFormViewModel(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchRepository.getBatch(id);
    }

    public LiveData<List<Batch>> getBatches() {
        return batchRepository.getBatches();
    }

    public void addBatch(Batch batch) {
        batchRepository.addBatch(batch);
    }

    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

}
