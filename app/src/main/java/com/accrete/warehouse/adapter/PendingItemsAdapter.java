package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PendingItems;

import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class PendingItemsAdapter extends RecyclerView.Adapter<PendingItemsAdapter.MyViewHolder> {
    private Context context;
    private List<PendingItems> pendingItems;
    private int mExpandedPosition = -1;
    private PendingItemsAdapterListener listener;
    Activity activity;

    public PendingItemsAdapter(Context context, List<PendingItems> pendingItems, PendingItemsAdapterListener listener) {
        this.context = context;
        this.pendingItems = pendingItems;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_items, parent, false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PendingItems pendingItem = pendingItems.get(position);
        holder.listRowPendingItemsBatchNumber.setText("("+pendingItem.getBatchNumber()+")");
        holder.listRowPendingItemsItem.setText(pendingItem.getItem());
        holder.listRowPendingItemsSkuCode.setText(pendingItem.getSKUCode());
        holder.listRowPendingItemsQuantity.setText(pendingItem.getQuantity());
        holder.listRowPendingItemsStatus.setText(pendingItem.getStatus());
/*
        if(pendingItem.getStatus().equals("executed")){
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.GREEN);
        }else{
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.RED);
        }*/


        holder.pendingItemsExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExecute();
            }
        });

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return pendingItems.size();

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowPendingItemsStatus;
        private TextView listRowPendingItemsItem;
        private TextView listRowPendingItemsBatchNumber;
        private TextView listRowPendingItemsSkuCode;
        private TextView listRowPendingItemsQuantity;
        private LinearLayout pendingItemsExecute;

        public MyViewHolder(View view) {
            super(view);
            listRowPendingItemsStatus = (TextView)view.findViewById( R.id.list_row_pending_items_status );
            listRowPendingItemsItem = (TextView)view.findViewById( R.id.list_row_pending_items_item );
            listRowPendingItemsBatchNumber = (TextView)view.findViewById( R.id.list_row_pending_items_batch_number );
            listRowPendingItemsSkuCode = (TextView)view.findViewById( R.id.list_row_pending_items_sku_code );
            listRowPendingItemsQuantity = (TextView)view.findViewById( R.id.list_row_pending_items_quantity );
            pendingItemsExecute = (LinearLayout)view.findViewById( R.id.pending_items_execute );

        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
                listener.onMessageRowClicked(position);
    }

    public interface PendingItemsAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

}
