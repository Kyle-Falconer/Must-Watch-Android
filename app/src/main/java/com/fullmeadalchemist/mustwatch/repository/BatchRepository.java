package com.fullmeadalchemist.mustwatch.repository;

import android.arch.lifecycle.LiveData;

import com.fullmeadalchemist.mustwatch.db.BatchDao;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Kyle on 7/22/2017.
 */
@Singleton
public class BatchRepository {
    private final BatchDao batchDao;

    @Inject
    public BatchRepository(BatchDao batchDao) {
        this.batchDao = batchDao;
    }

    public LiveData<List<Batch>> getBatches() {
        // FIXME: get only Batches from current user
        //return batchDao.loadBatchesForUser();
        return batchDao.getAll();
    }
}
