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

package com.fullmeadalchemist.mustwatch.ui.log

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.Batch
import com.fullmeadalchemist.mustwatch.vo.LogEntry
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class LogListFragment : Fragment() {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: LogRecyclerViewAdapter

    val viewModel: MainViewModel by sharedViewModel()
    private var fab: FloatingActionButton? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.log_list, container, false)
        rootView.tag = TAG

        //        mAdapter = new LogRecyclerViewAdapter(null, batch -> {
        //            navigationController.navigateToEditBatch(batch.id);
        //        });

        val bundle = this.arguments
        if (bundle != null) {
            val batchId = bundle.getLong(Batch.BATCH_ID, java.lang.Long.MIN_VALUE)
            viewModel.getLogEntries(batchId).observe(this, Observer<List<LogEntry>> { logEntries ->
                // update UI
                if (logEntries != null) {
                    Timber.d("Got %d log entries for batch #%d", logEntries.size, batchId)
                }
                mAdapter.dataSet = logEntries
                mAdapter.notifyDataSetChanged()
            })
        }

        fab = rootView.findViewById(R.id.logs_fab)
        mRecyclerView = rootView.findViewById(R.id.recyclerView)

        // http://stackoverflow.com/a/35981886/940217
        // https://code.google.com/p/android/issues/detail?id=230298
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull view: RecyclerView, dx: Int, dy: Int) {
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
//                navigationController.navigateToAddBatch()
            }
        } else {
            Timber.e("FloatingActionButton at R.id.logs_fab is null!")
        }


        mRecyclerView.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView.layoutManager = llm
        mRecyclerView.adapter = mAdapter

        return rootView
    }

    companion object {
        private val TAG = LogListFragment::class.java.simpleName
    }
}