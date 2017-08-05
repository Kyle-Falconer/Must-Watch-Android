package com.fullmeadalchemist.mustwatch.repository;

import android.arch.lifecycle.LiveData;
import android.text.TextUtils;
import android.util.Log;

import com.fullmeadalchemist.mustwatch.db.BatchDao;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class BatchRepository {
    private static final String TAG = BatchRepository.class.getSimpleName();

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


    public void addBatch(Batch batch) {
        Log.d(TAG, "Adding batch to db: " + batch.toString());
        Observable.<Long>fromCallable(() -> batchDao.insert(batch))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void addBatches(List<Batch> batches) {
        Log.d(TAG, String.format("Adding %d batch to db: ", batches.size(), TextUtils.join("\n", batches)));
        Observable.fromCallable(() -> batchDao.insertAll(batches.toArray(new Batch[batches.size()])))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchDao.get(id);
    }
}
