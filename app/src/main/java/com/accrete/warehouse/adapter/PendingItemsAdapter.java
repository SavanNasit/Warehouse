package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PendingItems;

import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class PendingItemsAdapter extends RecyclerView.Adapter<PendingItemsAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private List<PendingItems> pendingItems;
    private int mExpandedPosition = -1;
    private PendingItemsAdapterListener listener;
    private int qty,posToUpdate;
    private boolean flagScan;

    public PendingItemsAdapter(Context context, List<PendingItems> pendingItems, PendingItemsAdapterListener listener, int quantity,int posToUpdate,boolean flagScan) {
        this.context = context;
        this.pendingItems = pendingItems;
        this.listener = listener;
        this.qty = quantity;
        this.posToUpdate =posToUpdate;
        this.flagScan=flagScan;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PendingItems pendingItem = pendingItems.get(position);
        holder.listRowPendingItemsBatchNumber.setText(pendingItem.getIsid());
        holder.listRowPendingItemsItem.setText(pendingItem.getItemVariationName());
        if(qty>0 && position==posToUpdate){
            holder.listRowPendingItemsQuantity.setText(String.valueOf(Integer.parseInt(pendingItems.get(posToUpdate).getItemQuantityDuplicate())-qty));
        }else if(flagScan && position == posToUpdate){
            holder.listRowPendingItemsQuantity.setText(pendingItems.get(posToUpdate).getItemQuantity());
        }else {
            holder.listRowPendingItemsQuantity.setText(pendingItem.getItemQuantityDuplicate());
        }
        holder.listRowPendingItemsStatus.setText(pendingItem.getItemStatus());
        holder.listRowPendingItemsUnit.setText(pendingItem.getItemUnit());

        if (pendingItem.getItemSkuCode() != null && !pendingItem.getItemSkuCode().isEmpty()) {
            holder.listRowPendingItemsSkuCode.setText(pendingItem.getItemSkuCode());
        } else {
            holder.listRowPendingItemsSkuCode.setVisibility(View.GONE);
        }

        if (pendingItem.getItemStatus().equals("Executed")) {
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.GRAY);
        } else {
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.parseColor("#3C9971"));
        }


        holder.pendingItemsExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pendingItem.getItemStatus().equals("Executed")) {
                    Toast.makeText(context, "Item already executed", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onExecute(pendingItem.getIsid(), pendingItem.getOiid(), pendingItem.getItemQuantity(),position);
                }

            }
        });

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return pendingItems.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PendingItemsAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute(String isid, String oiid, String itemQuantity,int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowPendingItemsStatus;
        private TextView listRowPendingItemsItem;
        private TextView listRowPendingItemsBatchNumber;
        private TextView listRowPendingItemsSkuCode;
        private TextView listRowPendingItemsQuantity;
        private TextView listRowPendingItemsUnit;
        private LinearLayout pendingItemsExecute;

        public MyViewHolder(View view) {
            super(view);
            listRowPendingItemsStatus = (TextView) view.findViewById(R.id.list_row_pending_items_status);
            listRowPendingItemsItem = (TextView) view.findViewById(R.id.list_row_pending_items_item);
            listRowPendingItemsBatchNumber = (TextView) view.findViewById(R.id.list_row_pending_items_batch_number);
            listRowPendingItemsSkuCode = (TextView) view.findViewById(R.id.list_row_pending_items_sku_code);
            listRowPendingItemsQuantity = (TextView) view.findViewById(R.id.list_row_pending_items_quantity);
            pendingItemsExecute = (LinearLayout) view.findViewById(R.id.pending_items_execute);
            listRowPendingItemsUnit = (TextView) view.findViewById(R.id.list_row_pending_items_quantity_unit);

        }
    }

}
