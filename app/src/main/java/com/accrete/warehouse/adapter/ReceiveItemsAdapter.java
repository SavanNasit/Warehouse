package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ReceiveSubItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/19/17.
 */

public class ReceiveItemsAdapter extends RecyclerView.Adapter<ReceiveItemsAdapter.MyViewHolder> {

    private Context context;
    private ReceiveItemsAdapterListener listener;
    private List<ReceiveSubItems> receiveSubItemsList = new ArrayList<>();

    public ReceiveItemsAdapter(Context context, List<ReceiveSubItems>receiveSubItems ,ReceiveItemsAdapterListener listener) {
        this.context = context;
        this.receiveSubItemsList = receiveSubItems;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_receive_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ReceiveSubItems receiveSubItems = receiveSubItemsList.get(position);

    }

    @Override
    public int getItemCount() {
        return receiveSubItemsList.size();

    }

    private void applyClickEvents(DeliveredAdapter.MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface ReceiveItemsAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowReceiveItemsItemVariation;
        private TextView listRowReceiveItemsReceivingQuantity;
        private TextView listRowReceiveUnit;
        private TextView listRowReceiveItemsComment;
        private TextView listRowReceiveItemsExpiryDate;
        private TextView listRowReceiveItemsRejectedQuantity;
        private TextView listRowReceiveItemsReason;

        public MyViewHolder(View view) {
            super(view);
            listRowReceiveItemsItemVariation = (TextView)view.findViewById( R.id.list_row_receive_items_item_variation );
            listRowReceiveItemsReceivingQuantity = (TextView)view.findViewById( R.id.list_row_receive_items_receiving_quantity );
            listRowReceiveUnit = (TextView)view.findViewById( R.id.list_row_receive_unit );
            listRowReceiveItemsComment = (TextView)view.findViewById( R.id.list_row_receive_items_comment );
            listRowReceiveItemsExpiryDate = (TextView)view.findViewById( R.id.list_row_receive_items_expiry_date );
            listRowReceiveItemsRejectedQuantity = (TextView)view.findViewById( R.id.list_row_receive_items_rejected_quantity );
            listRowReceiveItemsReason = (TextView)view.findViewById( R.id.list_row_receive_items_reason );
        }
    }

}
