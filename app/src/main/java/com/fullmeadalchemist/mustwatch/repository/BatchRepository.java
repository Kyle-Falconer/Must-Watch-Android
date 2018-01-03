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

package com.fullmeadalchemist.mustwatch.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.fullmeadalchemist.mustwatch.db.BatchDao;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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

    public LiveData<Long> addBatch(Batch batch) {
        MutableLiveData<Long> batchIdLiveData = new MutableLiveData<>();
        Timber.d("Adding batch to db:\n" + batch.toString());
        Observable.fromCallable(() -> batchDao.insert(batch))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(batchId -> {
                    batchIdLiveData.setValue(batchId);
                    batch.id = batchId;
                    upsertBatchIngredients(batch);
                });
        return batchIdLiveData;
    }

    public LiveData<List<Long>> addBatches(List<Batch> batches) {
        MutableLiveData<List<Long>> batchIdsLiveData = new MutableLiveData<>();
        Timber.d("Adding %d batches to db:\n%s", batches.size(), TextUtils.join("\n", batches));
        Observable.fromCallable(() -> batchDao.insertAll(batches.toArray(new Batch[batches.size()])))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(batchIdsLiveData::setValue);

        return batchIdsLiveData;
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchDao.get(id);
    }

    public LiveData<List<Batch>> getBatches(User user) {
        return batchDao.getAll(user.id);
    }

    /**
     * @param batch the Batch object to be updated
     * @return Observable number of rows updated
     */
    public LiveData<Integer> updateBatch(Batch batch) {
        MutableLiveData<Integer> updatedLiveData = new MutableLiveData<>();
        Observable.fromCallable(() -> batchDao.updateBatch(batch))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedLiveData::setValue, e -> {
                    Timber.e("Failed to update batch:\n%s", e.toString());
                });
        upsertBatchIngredients(batch);
        return updatedLiveData;
    }

    public LiveData<List<BatchIngredient>> getBatchIngredients(long batchId) {
        return batchDao.getIngredientsForBatch(batchId);
    }

    private void upsertBatchIngredients(Batch batch) {
        if (batch == null || batch.ingredients == null) {
            Timber.w("Batch Ingredients is null");
            return;
        }
        if (batch.id == null) {
            Timber.e("No batch ID for these batch ingredients!");
            return;
        }
        for (BatchIngredient batchIngredient : batch.ingredients) {
            batchIngredient.batchId = batch.id;
        }
        Observable.fromCallable(() -> batchDao.upsertBatchIngredients(batch.ingredients))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ret -> {
                    Timber.d("Inserted %d BatchIngredients into the database", ret.length);
                }, e -> {
                    Timber.e("Failed to insert batch ingredients:\n%s", e.toString());
                });
    }
}
