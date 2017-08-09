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

package com.fullmeadalchemist.mustwatch.ui.batch.form;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;


public class BatchFormViewModel extends ViewModel {

    protected Batch batch;
    private BatchRepository batchRepository;
    private UserRepository userRepository;

    @Inject
    public BatchFormViewModel(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Batch> getBatch(Long id) {
        return batchRepository.getBatch(id);
    }

    public void addBatch(Batch batch) {
        batchRepository.addBatch(batch);
    }

    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public void updateBatch() {
        batchRepository.updateBatch(batch);
    }

    public void saveNewBatch() {
        batchRepository.addBatch(batch);
    }
}
