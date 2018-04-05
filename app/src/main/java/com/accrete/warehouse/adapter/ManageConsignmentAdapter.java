package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Consignment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentAdapter extends RecyclerView.Adapter<ManageConsignmentAdapter.MyViewHolder> {
    private Context context;
    private List<Consignment> consignmentList;
    private ManageConsignmentAdapterListener listener;

    public ManageConsignmentAdapter(Context context, List<Consignment> consignmentList, ManageConsignmentAdapterListener listener) {
        this.context = context;
        this.consignmentList = consignmentList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_manage_consignment, parent, false);
        return new MyViewHolder(itemView);
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");

        Consignment manageConsignment = consignmentList.get(position);
        holder.listRowManageConsignmentConsignmentId.setText("ID : "+manageConsignment.getConsignmentId());
        holder.listRowManageConsignmentConsignmentId.setPaintFlags(holder.listRowManageConsignmentConsignmentId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (manageConsignment.getPurchaseOrderID() != null && !manageConsignment.getPurchaseOrderID().isEmpty()) {
            holder.listRowManageConsignmentPurchaseOrder.setText("PO : " + manageConsignment.getPurchaseOrderID());
        }else{
            holder.listRowManageConsignmentPurchaseOrder.setVisibility(View.GONE);
        }

        if (manageConsignment.getPurchaseNumber() != null && !manageConsignment.getPurchaseNumber().isEmpty()
                && !manageConsignment.getPurchaseNumber().equals("NA")) {
            holder.listRowManageConsignmentInvoiceNumber.setText ("Invoice : " + manageConsignment.getPurchaseNumber());
        }else{
            holder.listRowManageConsignmentInvoiceNumber.setText("Invoice : " + "NA");
        }
        //  holder.listRowManageConsignmentInvoiceDate.setText(manageConsignment.getInvoiceDate());
        //  holder.listRowManageConsignmentPurchaseOrderDate.setText(manageConsignment.getPurchaseOrderDate());
        if (manageConsignment.getVendor() != null && !manageConsignment.getVendor().isEmpty()) {
            if(manageConsignment.getVendor().length()>25){
                holder.listRowManageConsignmentVendor.setText(capitalize(manageConsignment.getVendor().substring(0,25)+"..."));
            }else{
                holder.listRowManageConsignmentVendor.setText(capitalize(manageConsignment.getVendor()));
            }
        }
        // holder.listRowManageConsignmentWarehouse.setText(manageConsignment.getWarehouse());

        try {
            holder.listRowManageConsignmentReceivedOn.setText("Received on: " + (outputFormat.format(simpleDateFormat.parse(
                    manageConsignment.getCreatedTs()))) + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Statuses
        holder.listRowManageConsignmentStatus.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.listRowManageConsignmentStatus.getBackground();
        if (manageConsignment.getIscsid() != null) {
            if (manageConsignment.getIscsid().equals("1")) {
                drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Active");
            } else if (manageConsignment.getIscsid().equals("2")) {
                drawable.setColor(context.getResources().getColor(R.color.red_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Inactive");
            } else if (manageConsignment.getIscsid().equals("3")) {
                drawable.setColor(context.getResources().getColor(R.color.gray_order));
                holder.listRowManageConsignmentStatus.setText("Delete");
            } else if (manageConsignment.getIscsid().equals("4")) {
                drawable.setColor(context.getResources().getColor(R.color.gray_order));
                holder.listRowManageConsignmentStatus.setText("Freezed");
            } else if (manageConsignment.getIscsid().equals("5")) {
                drawable.setColor(context.getResources().getColor(R.color.blue_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Payment Approved");
            }
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consignmentList.size();
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
        // private TextView listRowManageConsignmentInvoiceDate;
        // private TextView listRowManageConsignmentPurchaseOrderDate;
        private TextView listRowManageConsignmentVendor;
        //   private TextView listRowManageConsignmentWarehouse;
        private TextView listRowManageConsignmentReceivedOn;
        private TextView listRowManageConsignmentStatus;
        private LinearLayout layout;


        public MyViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.linear_layout_manage_consignment);
            listRowManageConsignmentConsignmentId = (TextView) view.findViewById(R.id.list_row_manage_consignment_consignment_id);
            listRowManageConsignmentPurchaseOrder = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order);
            listRowManageConsignmentInvoiceNumber = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_number);
            //   listRowManageConsignmentInvoiceDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_date);
            //   listRowManageConsignmentPurchaseOrderDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order_date);
            listRowManageConsignmentVendor = (TextView) view.findViewById(R.id.list_row_manage_consignment_vendor);
            //   listRowManageConsignmentWarehouse = (TextView) view.findViewById(R.id.list_row_manage_consignment_warehouse);
            listRowManageConsignmentReceivedOn = (TextView) view.findViewById(R.id.list_row_manage_consignment_received_on);
            listRowManageConsignmentStatus = (TextView) view.findViewById(R.id.list_row_manage_consignment_status);
        }
    }

}
