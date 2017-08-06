package com.fullmeadalchemist.mustwatch.ui.batch.form;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.databinding.BatchFormFragmentBinding;
import com.fullmeadalchemist.mustwatch.di.Injectable;
import com.fullmeadalchemist.mustwatch.ui.batch.BatchListFragment;
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController;

import javax.inject.Inject;

import static com.fullmeadalchemist.mustwatch.vo.Batch.BATCH_ID;


public class BatchFormFragment extends LifecycleFragment implements Injectable {

    private static final String TAG = BatchListFragment.class.getSimpleName();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    BatchFormFragmentBinding dataBinding;
    private BatchFormViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.batch_form_fragment,
                container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BatchFormViewModel.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Log.i(TAG, "Got Batch ID %d from the NavigationController. Acting as a Batch Editor.");

            long batchId = bundle.getLong(BATCH_ID, Long.MIN_VALUE);
            if (batchId != Long.MIN_VALUE) {
                viewModel.getBatch(batchId).observe(this, batch -> {
                    Log.i(TAG, String.format("Loaded Batch with ID %d:\n%s", batch.id, batch));
                    viewModel.batch = batch;
                    dataBinding.setBatch(batch);
                });
            }
        } else {
            Log.i(TAG, "No Batch ID was received. Acting as a Batch Creation form.");
        }

        return dataBinding.getRoot();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (viewModel.batch != null) {
            // We're in "edit" mode, so set the title accordingly
            TextView formTitle = getActivity().findViewById(R.id.batchFormTitleTV);
            if (formTitle != null) {
                String title = getResources().getString(R.string.edit_batch_title);
                title = String.format(title, viewModel.batch.id);
                formTitle.setText(title);
            }
        }
    }



}
