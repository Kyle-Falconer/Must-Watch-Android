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

package com.fullmeadalchemist.mustwatch.core;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.repository.BatchRepository;
import com.fullmeadalchemist.mustwatch.repository.SugarRepository;
import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Sugar;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.generateDummyBatchesWithData;

public class HeadlessLoadingFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = HeadlessLoadingFragment.class.getSimpleName();
    @Inject
    BatchRepository batchRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    SugarRepository sugarRepository;
    private LifecycleOwner lifecycleContext;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "Startup tasks running...");
        lifecycleContext = this;
        populateDb();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Finished! Shutting down headless fragment.");
    }

    private void populateDb() {

        // Dummy data loader
        Observable.create(emitter -> {
            userRepository.getCurrentUserId().observe(lifecycleContext, userId -> {
                batchRepository.getBatches().observe(lifecycleContext, batches -> {
                    if (batches == null || batches.size() == 0) {
                        Log.d(TAG, "Got user with no batches; generating batches...");
                        List<Batch> dummyBatches = generateDummyBatchesWithData(userId, 20);
                        batchRepository.addBatches(dummyBatches);
                    } else {
                        Log.d(TAG, String.format("Got user with %d batches.", batches.size()));
                    }
                });
            });
        }).subscribeOn(Schedulers.io()).subscribe();

        // Sugars loader
        Observable.create(emitter -> {
            sugarRepository.getSugarEntries().observe(lifecycleContext, sugars -> {
                if (sugars == null || sugars.size() == 0) {
                    Log.i(TAG, "Populating the database with Sugar data");
                    JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.sugars);
                    Sugar[] jsonObj = reader.constructUsingGson(Sugar[].class);
                    sugarRepository.addSugars(jsonObj);
                } else {
                    Log.d(TAG, "Sugars already found in the database.");
                }
            });
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
