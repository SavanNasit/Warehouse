package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/6/17.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {
    private Context context;
    private InventoryAdapterListener listener;
    private List<Inventory> inventoryList = new ArrayList<>();

    public InventoryAdapter(Context context, List<Inventory> inventoryList, InventoryAdapterListener listener) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_inventory, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Inventory inventory = inventoryList.get(position);
        holder.listRowInventoryId.setText("Id: " + inventory.getInventoryID());
        holder.listRowInventoryItem.setText(inventory.getItem());
        //holder.listRowInventorySkucode.setText(inventory.getSkuCode());
        holder.listRowInventoryReceivedQuantity.setText("Total stock : " + inventory.getReceivedQuantity());
        holder.listRowInventoryAvailableStock.setText("Available : " + inventory.getAvailableStock());

        //applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface InventoryAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowInventoryId;
        private TextView listRowInventoryItem;
        //private TextView listRowInventorySkucode;
        private TextView listRowInventoryReceivedQuantity;
        private TextView listRowInventoryAvailableStock;


        public MyViewHolder(View view) {
            super(view);
            listRowInventoryId = (TextView) view.findViewById(R.id.list_row_inventory_id);
            listRowInventoryItem = (TextView) view.findViewById(R.id.list_row_inventory_item);
            //listRowInventorySkucode = (TextView) view.findViewById(R.id.list_row_inventory_skucode);
            listRowInventoryReceivedQuantity = (TextView) view.findViewById(R.id.list_row_inventory_received_quantity);
            listRowInventoryAvailableStock = (TextView) view.findViewById(R.id.list_row_inventory_available_stock);
        }
    }

}
