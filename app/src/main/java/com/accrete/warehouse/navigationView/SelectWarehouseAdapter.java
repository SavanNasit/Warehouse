package com.accrete.warehouse.navigationView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.accrete.warehouse.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by poonam on 11/24/17.
 */

class SelectWarehouseAdapter extends RecyclerView.Adapter<SelectWarehouseAdapter.MyViewHolder> {
    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;
    private Context context;
    private List<CheckBox> warehouseList;
    private List<String> warehouseNameList;
    private SelectWarehouseAdapterListener listener;


    public SelectWarehouseAdapter(Context context, List<String> warehouseNameList,SelectWarehouseAdapterListener listener) {
        this.context = context;
        this.warehouseNameList = warehouseNameList;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_warehouses, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //  WareHouse wareHouse = warehouseList.get(position);
        holder.textWarehouse.setText(warehouseNameList.get(position));
        holder.checkboxWarehouse.setTag(new Integer(position));
        holder.checkboxWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int clickedPos = ((Integer) cb.getTag()).intValue();

                if (cb.isChecked()) {
                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                    }

                    lastChecked = cb;
                    lastCheckedPos = clickedPos;
                } else {
                    lastChecked = null;
                }

                applyClickEvents(warehouseNameList.get(clickedPos));
            }
        });

    }

    @Override
    public int getItemCount() {
        return warehouseNameList.size();
    }

    public interface SelectWarehouseAdapterListener {
        void onMessageRowClicked(String position);
    }

    private void applyClickEvents(String warehouseName) {
                listener.onMessageRowClicked(warehouseName);
               // notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private CheckBox checkboxWarehouse;
        private TextView textWarehouse;

        public MyViewHolder(View view) {
            super(view);
            textWarehouse = (TextView) view.findViewById(R.id.select_warehouse_name);
            checkboxWarehouse = (CheckBox) view.findViewById(R.id.select_warehouse_checkbox);

        }
    }
}
