package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Inventory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/6/17.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {
    private Context context;
    private InventoryAdapterListener listener;
    private List<Inventory> inventoryList = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

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

        //holder.listRowInventorySkucode.setText(inventory.getSkuCode());

        if(inventory.getInventoryID()!=null && !inventory.getInventoryID().isEmpty()
                && !inventory.getInventoryID().equals("NA")) {
            holder.listRowInventoryId.setText("ID: " + inventory.getInventoryID());

        }else{
            holder.listRowInventoryId.setVisibility(View.GONE);
        }



        if(inventory.getItem()!=null && !inventory.getItem().isEmpty()
                && !inventory.getItem().equals("NA")) {
            holder.listRowInventoryItem.setText(inventory.getItem());

        }else{
            holder.listRowInventoryItem.setText("NA");
        }


        if(inventory.getSkuCode()!=null && !inventory.getSkuCode().isEmpty()
                && !inventory.getSkuCode().equals("NA")) {
            holder.listRowInventorySkuCode.setText(inventory.getSkuCode());
        }else{
            holder.listRowInventorySkuCode.setText("NA");
        }

        if(inventory.getAvailableStock()!=null && !inventory.getAvailableStock().isEmpty()
                && !inventory.getAvailableStock().equals("NA")) {
            holder.listRowInventoryAvailableStock.setText("Available : " + inventory.getAvailableStock());
        }else{
            holder.listRowInventoryAvailableStock.setText("NA");
        }

        if(inventory.getReceivedQuantity()!=null && !inventory.getReceivedQuantity().isEmpty()
                && !inventory.getReceivedQuantity().equals("NA")) {
            holder.listRowInventoryReceivedQuantity.setText("Received : " + inventory.getReceivedQuantity());
        }else{
            holder.listRowInventoryReceivedQuantity.setText("NA");
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd,MMM,yyyy");
        try {

            if(!inventory.getManufacturingDate().equals("0000-00-00")&& inventory.getManufacturingDate()!=null && !inventory.getManufacturingDate().isEmpty()
                    && !inventory.getManufacturingDate().equals("NA")) {
                holder.listRowInventoryManufactureDate.setText("Manufacturing Date : "+dateFormat.format(simpleDateFormat.parse(inventory.getManufacturingDate())));
            }else{
                holder.listRowInventoryManufactureDate.setVisibility(View.GONE);
            }

            if(!inventory.getExpiry_date().equals("0000-00-00")&& inventory.getExpiry_date()!=null && !inventory.getExpiry_date().isEmpty()
                    && !inventory.getExpiry_date().equals("NA")) {
                holder.listRowInventoryExpDate.setText("Expiry Date : "+ dateFormat.format(simpleDateFormat.parse(inventory.getExpiry_date())));

            }else{
                holder.listRowInventoryExpDate.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        private TextView listRowInventoryItem;
        private TextView listRowInventorySkuCode;
        private TextView listRowInventoryId;
        private TextView listRowInventoryManufactureDate;
        private TextView listRowInventoryReceivedQuantity;
        private TextView listRowInventoryExpDate;
        private TextView listRowInventoryAvailableStock;


        public MyViewHolder(View view) {
            super(view);
            listRowInventoryItem = (TextView)view.findViewById( R.id.list_row_inventory_item );
            listRowInventorySkuCode = (TextView)view.findViewById( R.id.list_row_inventory_sku_code );
            listRowInventoryId = (TextView)view.findViewById( R.id.list_row_inventory_id );
            listRowInventoryManufactureDate = (TextView)view.findViewById( R.id.list_row_inventory_manufacture_date );
            listRowInventoryReceivedQuantity = (TextView)view.findViewById( R.id.list_row_inventory_received_quantity );
            listRowInventoryExpDate = (TextView)view.findViewById( R.id.list_row_inventory_exp_date );
            listRowInventoryAvailableStock = (TextView)view.findViewById( R.id.list_row_inventory_available_stock );




        }
    }

}
