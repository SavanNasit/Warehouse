package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ManageConsignment;

import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentAdapter extends RecyclerView.Adapter<ManageConsignmentAdapter.MyViewHolder> {
    private Context context;
    private List<ManageConsignment> manageConsignmentList;
    private ManageConsignmentAdapterListener listener;

    public ManageConsignmentAdapter(Context context, List<ManageConsignment> manageConsignmentList, ManageConsignmentAdapterListener listener) {
        this.context = context;
        this.manageConsignmentList = manageConsignmentList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_manage_consignment, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ManageConsignment manageConsignment = manageConsignmentList.get(position);
        holder.listRowManageConsignmentConsignmentId.setText(manageConsignment.getConsignmentID());
        holder.listRowManageConsignmentPurchaseOrder.setText(manageConsignment.getPurchaseOrder());
        holder.listRowManageConsignmentInvoiceNumber.setText(manageConsignment.getInvoiceNumber());
        holder.listRowManageConsignmentInvoiceDate.setText(manageConsignment.getInvoiceDate());
        holder.listRowManageConsignmentPurchaseOrderDate.setText(manageConsignment.getPurchaseOrderDate());
        holder.listRowManageConsignmentVendor.setText(manageConsignment.getVendor());
        holder.listRowManageConsignmentWarehouse.setText(manageConsignment.getWarehouse());
        holder.listRowManageConsignmentReceivedOn.setText(manageConsignment.getReceivedOn());
        holder.listRowManageConsignmentStatus.setText(manageConsignment.getStatus());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return manageConsignmentList.size();

    }

    private void applyClickEvents(final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface ManageConsignmentAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView listRowManageConsignmentConsignmentId;
        private TextView listRowManageConsignmentPurchaseOrder;
        private TextView listRowManageConsignmentInvoiceNumber;
        private TextView listRowManageConsignmentInvoiceDate;
        private TextView listRowManageConsignmentPurchaseOrderDate;
        private TextView listRowManageConsignmentVendor;
        private TextView listRowManageConsignmentWarehouse;
        private TextView listRowManageConsignmentReceivedOn;
        private TextView listRowManageConsignmentStatus;
        private LinearLayout layout;


        public MyViewHolder(View view) {
            super(view);
            layout=(LinearLayout)view.findViewById(R.id.linear_layout_manage_consignment);
            listRowManageConsignmentConsignmentId = (TextView) view.findViewById(R.id.list_row_manage_consignment_consignment_id);
            listRowManageConsignmentPurchaseOrder = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order);
            listRowManageConsignmentInvoiceNumber = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_number);
            listRowManageConsignmentInvoiceDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_date);
            listRowManageConsignmentPurchaseOrderDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order_date);
            listRowManageConsignmentVendor = (TextView) view.findViewById(R.id.list_row_manage_consignment_vendor);
            listRowManageConsignmentWarehouse = (TextView) view.findViewById(R.id.list_row_manage_consignment_warehouse);
            listRowManageConsignmentReceivedOn = (TextView) view.findViewById(R.id.list_row_manage_consignment_received_on);
            listRowManageConsignmentStatus = (TextView) view.findViewById(R.id.list_row_manage_consignment_status);
        }
    }

}
