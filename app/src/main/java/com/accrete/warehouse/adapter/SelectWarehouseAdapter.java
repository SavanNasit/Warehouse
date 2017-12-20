package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.WarehouseList;

import java.util.List;


/**
 * Created by poonam on 11/24/17.
 */

public class SelectWarehouseAdapter extends RecyclerView.Adapter<SelectWarehouseAdapter.MyViewHolder> {
    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;
    private Context context;
    private List<WarehouseList> warehouseLists;
    private SelectWarehouseAdapterListener listener;


    public SelectWarehouseAdapter(Context context, List<WarehouseList> warehouseList, SelectWarehouseAdapterListener listener) {
        this.context = context;
        this.warehouseLists = warehouseList;
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
        final WarehouseList warehouse = warehouseLists.get(position);
        holder.textWarehouse.setText(warehouse.getName());
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

                applyClickEvents(warehouseLists.get(clickedPos).getName(),warehouseLists.get(clickedPos).getChkid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return warehouseLists.size();
    }

    public interface SelectWarehouseAdapterListener {
        void onMessageRowClicked(String warehouseName, String warehouseChkId);
    }

    private void applyClickEvents(String warehouseName,String warehouseChkId) {
                listener.onMessageRowClicked(warehouseName,warehouseChkId);
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
