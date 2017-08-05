package com.fullmeadalchemist.mustwatch.ui.log;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Kyle on 7/22/2017.
 */

public class LogViewModel extends ViewModel {
    private LiveData<List<Batch>> batches;
    private BatchRepository batchRepository;
    private UserRepository userRepository;

    @Inject
    public LogViewModel(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
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
