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
    private boolean selectedPosition;
    private String positionEnabled;


    public SelectWarehouseAdapter(Context context, List<WarehouseList> warehouseList,
                                  SelectWarehouseAdapterListener listener, boolean selectedPosition, String pos) {
        this.context = context;
        this.warehouseLists = warehouseList;
        this.listener = listener;
        this.selectedPosition = selectedPosition;
        this.positionEnabled = pos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_warehouses, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final WarehouseList warehouse = warehouseLists.get(position);
        holder.textWarehouse.setText(warehouse.getName());
       /* if(selectedPosition) {
            if(positionEnabled.equals(warehouse.getChkid())){
                holder.checkboxWarehouse.setChecked(true);
            }
        }*/
     /*   if (positionEnabled.equals(warehouse.getChkid())) {
            holder.checkboxWarehouse.setChecked(true);
        } else {
            holder.checkboxWarehouse.setChecked(false);
        }*/
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

                applyClickEvents(warehouseLists.get(clickedPos).getName(), warehouseLists.get(clickedPos).getChkid(), warehouseLists.get(clickedPos).getOrderCount()
                        , warehouseLists.get(clickedPos).getPackageCount(), warehouseLists.get(clickedPos).getGatepassCount(), warehouseLists.get(clickedPos).getConsignmentCount()
                        , warehouseLists.get(clickedPos).getReceiveConsignmentCount());
            }
        });


    }

    @Override
    public int getItemCount() {
        return warehouseLists.size();
    }

    private void applyClickEvents(String name, String chkid, String orderCount, String packageCount, String gatepassCount, String consignmentCount, String receiveConsignmentCount) {
        listener.onMessageRowClicked(name, chkid, orderCount, packageCount, gatepassCount, consignmentCount, receiveConsignmentCount);
         notifyDataSetChanged();
    }

    public interface SelectWarehouseAdapterListener {
        void onMessageRowClicked(String name,
                                 String chkid,
                                 String orderCount,
                                 String packageCount,
                                 String gatepassCount,
                                 String consignmentCount,
                                 String receiveConsignmentCount);
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
