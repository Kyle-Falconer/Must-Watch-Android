package com.fullmeadalchemist.mustwatch.ui.batch;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;

import javax.inject.Inject;

/**
 * Created by Kyle on 7/22/2017.
 */

public class BatchListFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected BatchRecyclerViewAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;

    private BatchViewModel batchViewModel;

    private BatchViewModel viewModel;
    private FloatingActionButton fab;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new BatchRecyclerViewAdapter(null);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchViewModel.class);
        viewModel.getBatches().observe(this, batches -> {
            // update UI
            mAdapter.dataSet = batches;
            mAdapter.notifyDataSetChanged();
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_list, container, false);
        rootView.setTag(TAG);

        fab = rootView.findViewById(R.id.batches_fab);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}