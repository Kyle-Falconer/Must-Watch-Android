package com.fullmeadalchemist.mustwatch.ui.batch;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.vo.Batch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 7/23/2017.
 */

public class BatchRecyclerViewAdapter extends RecyclerView.Adapter<BatchRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = BatchRecyclerViewAdapter.class.getSimpleName();
    List<Batch> dataSet;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public BatchRecyclerViewAdapter(List<Batch> dataSet) {
        if (dataSet == null)
            dataSet = new ArrayList<>();
        dataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        //View v = LayoutInflater.from(viewGroup.getContext())
        //        .inflate(R.layout.batch_item_list, viewGroup, false);


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.batch_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        final Batch b = dataSet.get(position);

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getBatchNumberTextView().setText("Batch " + b.id);
//
//        Calendar createDate = mDataSet.get(position).getCreateDate();
//        String formattedCreateDate = calendarToLocaleDateLong(b.getCreateDate());
//        if (createDate != null) {
//            viewHolder.getBatchCreateDateTextView().setText(formattedCreateDate);
//        }
//
//        viewHolder.getBatchStatusTextView().setText(b.getStatusString());
//
//        viewHolder.itemView.setOnClickListener(v -> {
//            Log.d(TAG, String.format("Element for batch #%s was clicked.", b.getBatch_no()));
//            routeTo(BATCH_DETAIL_VIEW, b);
//        });
//
//
//        viewHolder.getBatchTargetAbvTextView().setText(String.format("%s%%", b.getTargetABV()));
//
//        String outVol = b.getFormattedOutputVolume();
//        if (outVol != null) {
//            viewHolder.getOutputVolumeTextView().setText(outVol);
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView batchNumberTextView;
        private final TextView batchCreateDateTextView;
        private final TextView batchStatusTextView;
        private final TextView batchTargetAbvTextView;
        private final TextView outputVolumeTextView;

        public ViewHolder(View v) {
            super(v);
            batchNumberTextView = (TextView) v.findViewById(R.id.batchCardNumTextView);
            batchStatusTextView = (TextView) v.findViewById(R.id.batchCardStatusTextView);
            batchTargetAbvTextView = (TextView) v.findViewById(R.id.batchCardTargetAbvTextView);
            outputVolumeTextView = (TextView) v.findViewById(R.id.batchCardOutputVolTextView);
            batchCreateDateTextView = (TextView) v.findViewById(R.id.batchCardCreateDateTextView);
        }

        public TextView getBatchNumberTextView() {
            return batchNumberTextView;
        }

        public TextView getBatchStatusTextView() {
            return batchStatusTextView;
        }

        public TextView getBatchTargetAbvTextView() {
            return batchTargetAbvTextView;
        }

        public TextView getBatchCreateDateTextView() {
            return batchCreateDateTextView;
        }

        public TextView getOutputVolumeTextView() {
            return outputVolumeTextView;
        }
    }
}
