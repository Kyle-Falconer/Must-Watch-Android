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
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import com.fullmeadalchemist.mustwatch.vo.Batch
import timber.log.Timber
import dagger.android.support.AndroidSupportInjection



class BatchListFragment : Fragment() {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: BatchListViewAdapter

    private var viewModel: BatchViewModel? = null
    private var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BatchViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.batch_list, container, false)
        rootView.tag = TAG

        mAdapter = BatchListViewAdapter(object : BatchListViewAdapter.BatchClickCallback {
            override fun onClick(repo: Batch) {
                findNavController().navigate(R.id.batchDetailFragment)
                // navigationController.navigateToBatchDetail(repo.id)
            }
        })

        viewModel?.batches?.observe(this, Observer<List<Batch>> { batches ->
            // update UI
            mAdapter.dataSet = batches
            mAdapter.notifyDataSetChanged()
        })

        fab = rootView.findViewById(R.id.batches_fab)
        mRecyclerView = rootView.findViewById(R.id.batch_list)

        // http://stackoverflow.com/a/35981886/940217
        // https://code.google.com/p/android/issues/detail?id=230298
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                //FloatingActionButton floater = recyclerView.findViewById(R.id.batches_fab);
                if (fab == null) {
                    Timber.e("The FloatingActionButton is null!")
                    return
                }
                if (dy > 0 && fab!!.isShown) {
                    Timber.d("hiding FAB")
                    fab!!.hide()
                } else if (dy < 20 && !fab!!.isShown) {
                    Timber.d("showing FAB")
                    fab!!.show()
                }
            }
        })


        if (fab != null) {
            fab!!.setOnClickListener { v ->
                Timber.d("Floating Action Button was clicked!")
                Answers.getInstance().logCustom(CustomEvent("FAB Clicked"))

                findNavController().navigate(R.id.batchFormFragment)
            }
        } else {
            Timber.e("FloatingActionButton at R.id.batches_fab is null!")
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