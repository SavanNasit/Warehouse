package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.AllocateConsignment;
import com.accrete.warehouse.model.ConsumptionConsignment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.accrete.warehouse.utils.AppPreferences.roundTwoDecimals;

/**
 * Created by poonam on 7/3/18.
 */

public class ConsumptionItemAdapter extends RecyclerView.Adapter<ConsumptionItemAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private List<ConsumptionConsignment> consumptionConsignmentList;
    private int mExpandedPosition = -1;
    private ConsumptionItemAdapterListener listener;

    public ConsumptionItemAdapter(Context context, List<ConsumptionConsignment> consumptionConsignments, ConsumptionItemAdapterListener listener) {
        this.context = context;
        this.consumptionConsignmentList = consumptionConsignments;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_consumption, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ConsumptionConsignment consumptionConsignment = consumptionConsignmentList.get(position);
        holder.listRowAllocationItemName.setText(consumptionConsignment.getVariationName().trim());
        holder.listRowAllocationOrderId.setText(consumptionConsignment.getOrderID());
        holder.listRowAllocationCustomerName.setText(consumptionConsignment.getCustomerName());
        holder.listRowAllocationSkuCode.setText(consumptionConsignment.getInternalCode());
        holder.listRowAllocationHsn.setText("HSN : "+consumptionConsignment.getHsnCode());
        holder.listRowAllocationAllocatedStock.setText("Allocated Qty : "+String.valueOf(roundTwoDecimals(Double.valueOf(consumptionConsignment.getAllocatedQty()))));
        holder.listRowAllocationInventoryId.setText("Inventory ID : "+consumptionConsignment.getIsidNumber());

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = targetFormat.parse(consumptionConsignment.getCreatedTs());
            holder.listRowAllocationDate.setText(formatter.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return consumptionConsignmentList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

    }

    public interface ConsumptionItemAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewOuter;
        private CardView cardViewInner;
        private TextView listRowAllocationItemName;
        private TextView listRowAllocationDate;
        private TextView listRowAllocationOrderId;
        private TextView listRowAllocationCustomerName;
        private TextView listRowAllocationSkuCode;
        private TextView listRowAllocationHsn;
        private TextView listRowAllocationAllocatedStock;
        private TextView listRowAllocationInventoryId;

        public MyViewHolder(View view) {
            super(view);
            cardViewOuter = (CardView) view.findViewById(R.id.card_view_outer);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
            listRowAllocationItemName = (TextView) view.findViewById(R.id.list_row_allocation_item_name);
            listRowAllocationDate = (TextView) view.findViewById(R.id.list_row_allocation_date);
            listRowAllocationOrderId = (TextView) view.findViewById(R.id.list_row_allocation_order_id);
            listRowAllocationCustomerName = (TextView) view.findViewById(R.id.list_row_allocation_customer_name);
            listRowAllocationSkuCode = (TextView) view.findViewById(R.id.list_row_allocation_sku_code);
            listRowAllocationHsn = (TextView) view.findViewById(R.id.list_row_allocation_hsn);
            listRowAllocationAllocatedStock = (TextView) view.findViewById(R.id.list_row_allocation_allocated_stock);
            listRowAllocationInventoryId = (TextView) view.findViewById(R.id.list_row_allocation_inventory_id);

        }
    }

}
