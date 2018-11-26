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

package com.fullmeadalchemist.mustwatch.ui.batch

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.Batch.Companion.BATCH_ID
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class BatchListFragment : Fragment() {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: BatchListViewAdapter
    private var fab: FloatingActionButton? = null

    val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.batch_list, container, false)
        rootView.tag = TAG

        mAdapter = BatchListViewAdapter(object : BatchListViewAdapter.BatchClickCallback {
            override fun onClick(repo: Batch) {
                val bundle = Bundle()
                bundle.putLong(BATCH_ID, repo.id)
                findNavController().navigate(R.id.batchDetailFragment, bundle)
            }
        })

        viewModel.getCurrentUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getBatchesForUser(user.uid).observe(this, Observer<List<Batch>> { batches ->
                    // update UI
                    mAdapter.dataSet = batches
                    mAdapter.notifyDataSetChanged()
                })
            }
        })

        fab = rootView.findViewById(R.id.batches_fab)
        mRecyclerView = rootView.findViewById(R.id.batch_list)

        // http://stackoverflow.com/a/35981886/940217
        // https://code.google.com/p/android/issues/detail?id=230298
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                fab?.let {
                    if (dy > 0 && it.isShown) {
                        Timber.d("hiding FAB")
                        it.hide()
                    } else if (dy < 20 && !it.isShown) {
                        Timber.d("showing FAB")
                        it.show()
                    }
                }
            }
        })

        fab?.setOnClickListener { v ->
            Timber.d("Floating Action Button was clicked!")
            Answers.getInstance().logCustom(CustomEvent("FAB Clicked"))

            findNavController().navigate(R.id.batchFormFragment)
        }

        mRecyclerView.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView.layoutManager = llm
        mRecyclerView.adapter = mAdapter

        return rootView
    }

    companion object {
        private val TAG = BatchListFragment::class.java.simpleName
    }
}