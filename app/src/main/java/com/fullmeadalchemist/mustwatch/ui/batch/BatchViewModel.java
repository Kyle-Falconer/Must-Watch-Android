package com.fullmeadalchemist.mustwatch.ui.batch;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Kyle on 7/22/2017.
 */

public class BatchViewModel extends ViewModel {
    private LiveData<List<Batch>> batches;
    private BatchRepository batchRepository;

    @Inject
    public BatchViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;

    }

    public LiveData<List<Batch>> getBatches() {
        // FIXME: get only batches for current user
        return batchRepository.getBatches();
    }
}
