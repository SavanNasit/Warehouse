package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.SelectOrderItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 11/29/17.
 */

public  class SelectOrderItemAdapter extends RecyclerView.Adapter<SelectOrderItemAdapter.MyViewHolder> {
    List<SelectOrderItem> selectOrderItems;
    Activity activity;
    private Context context;
    private int mExpandedPosition = -1;
    private SelectOrderItemsAdapterListener listener;


    public SelectOrderItemAdapter(Context context, List<SelectOrderItem> selectOrderItems, SelectOrderItemsAdapterListener listener) {
        this.context = context;
        this.selectOrderItems = selectOrderItems;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_order_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SelectOrderItem selectOrderItem = selectOrderItems.get(position);
        holder.listRowOrderItemInventory.setText(selectOrderItem.getInventory());


        holder.listRowOrderItemAvailableQuantity.setText("Avl.Quantity: "+ selectOrderItem.getAvailableQuantity());
        holder.textViewUnit.setText(selectOrderItem.getUnit());
        holder.listRowOrderItemName.setText(selectOrderItem.getInventoryName());
        holder.listRowOrderItemEdtAllotQuantity.setText(selectOrderItem.getAllocatedQuantity());


        if(selectOrderItem.getVendor()!=null && !selectOrderItem.getVendor().isEmpty()){
            holder.listRowOrderItemVendor.setVisibility(View.VISIBLE);
            holder.listRowOrderItemVendor.setText(selectOrderItem.getVendor());
        }else{
            holder.listRowOrderItemVendor.setVisibility(View.GONE);
        }

        if(selectOrderItem.getRemark()!=null && !selectOrderItem.getRemark().isEmpty()){
            holder.listRowOrderItemRemarks.setVisibility(View.VISIBLE);
            holder.listRowOrderItemRemarks.setText(selectOrderItem.getRemark());
            holder.listRowOrderItemRemarks.setText("Remarks: "+ selectOrderItem.getRemark());
        }else{
            holder.listRowOrderItemRemarks.setVisibility(View.GONE);
        }

        holder.orderItemExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemExecute(selectOrderItem.getInventoryName(),selectOrderItem.getAllocatedQuantity(),
                        holder.listRowOrderItemEdtAllotQuantity.getText().toString(),selectOrderItem.getUnit());
            }
        });

        applyClickEvents(holder, position);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(selectOrderItem.getPurchasedOn());
            holder.listRowOrderItemPurchasedOn.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return selectOrderItems.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onItemRowClicked(position);
    }

    public interface SelectOrderItemsAdapterListener{
        void onItemRowClicked(int position);
        void onItemExecute(String inventoryName, String allocatedQuantity, String quantity, String unit);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowOrderItemPurchasedOn;
        private TextView listRowOrderItemVendor;
        private TextView listRowOrderItemInventory;
        private TextView listRowOrderItemAvailableQuantity;
        private TextView listRowOrderItemName;
        private EditText listRowOrderItemEdtAllotQuantity;
        private TextView listRowOrderItemRemarks;
        private LinearLayout orderItemExecute;
        private TextView textViewUnit;

        public MyViewHolder(View view) {
            super(view);

            listRowOrderItemPurchasedOn = (TextView) view.findViewById(R.id.list_row_order_item_purchased_on);

            listRowOrderItemVendor = (TextView) view.findViewById(R.id.list_row_order_item_vendor);
            listRowOrderItemInventory = (TextView) view.findViewById(R.id.list_row_order_item_inventory);
            listRowOrderItemAvailableQuantity = (TextView) view.findViewById(R.id.list_row_order_item_available_quantity);
            listRowOrderItemEdtAllotQuantity = (EditText) view.findViewById(R.id.list_row_order_item_edt_allot_quantity);
            listRowOrderItemRemarks = (TextView) view.findViewById(R.id.list_row_order_item_remarks);
            orderItemExecute = (LinearLayout) view.findViewById(R.id.order_item_execute);
            textViewUnit = (TextView) view.findViewById(R.id.list_row_order_item_unit);
            listRowOrderItemName = (TextView)view.findViewById(R.id.list_row_order_item_item_name);
        }
    }

}
