package com.accrete.warehouse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackedItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 12/14/17.
 */


public class PackedItemWithoutCheckboxAdapter extends RecyclerView.Adapter<PackedItemWithoutCheckboxAdapter.MyViewHolder> {
    ArrayList<String> packageIdList = new ArrayList<>();
    private Context context;
    private PackedItemAdapterListener listener;
    private List<PackedItem> packedList = new ArrayList<>();

    public PackedItemWithoutCheckboxAdapter(Context context, List<PackedItem> packedList, PackedItemAdapterListener listener) {
        this.context = context;
        this.packedList = packedList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_out_for_delivery, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PackedItem packed = packedList.get(position);
        holder.outForDeliveryPackageId.setText(packed.getPackageId());
        holder.outForDeliveryPackageId.setPaintFlags(holder.outForDeliveryPackageId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.outForDeliveryInvoiceNo.setText("Invoice No : " + packed.getInvoiceNo());
        holder.outForDeliveryCustomerName.setText(packed.getCustomerName());

        //TODO NO GatePass ID in this process
        holder.outForDeliveryGatepassId.setVisibility(View.GONE);

        holder.outForDeliveryOrderId.setText("Order Id : " + packed.getOrderID());
        if (packed.getToDate() != null && !packed.getToDate().isEmpty()) {
            holder.outForDeliveryExpDod.setText("Exp Dod : " + packed.getToDate());
        } else {
            holder.outForDeliveryExpDod.setText("Exp Dod : N/A ");
        }

        holder.outForDeliveryUser.setText("Delivery User : " + packed.getCustomerName());

        holder.outForDeliveryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = targetFormat.parse(packed.getInvoiceDate());
            holder.outForDeliveryInvoiceDate.setText(formatter.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return packedList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position, ArrayList<String> packageIdList) {
        listener.onMessageRowClicked(position);
        listener.onExecute(packageIdList);
    }

    public interface PackedItemAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute(ArrayList<String> packageIdList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView outForDeliveryPackageId;
        private TextView outForDeliveryInvoiceNo;
        private TextView outForDeliveryInvoiceDate;
        private TextView outForDeliveryCustomerName;
        private TextView outForDeliveryGatepassId;
        private TextView outForDeliveryOrderId;
        private TextView outForDeliveryExpDod;
        private TextView outForDeliveryUser;
        private LinearLayout outForDeliveryContainer;

        public MyViewHolder(View view) {
            super(view);
            outForDeliveryPackageId = (TextView) view.findViewById(R.id.out_for_delivery_package_id);
            outForDeliveryInvoiceNo = (TextView) view.findViewById(R.id.out_for_delivery_invoice_no);
            outForDeliveryInvoiceDate = (TextView) view.findViewById(R.id.out_for_delivery_invoice_date);
            outForDeliveryCustomerName = (TextView) view.findViewById(R.id.out_for_delivery_customer_name);
            outForDeliveryGatepassId = (TextView) view.findViewById(R.id.out_for_delivery_gatepass_id);
            outForDeliveryOrderId = (TextView) view.findViewById(R.id.out_for_delivery_order_id);
            outForDeliveryExpDod = (TextView) view.findViewById(R.id.out_for_delivery_exp_dod);
            outForDeliveryUser = (TextView) view.findViewById(R.id.out_for_delivery_user);
            outForDeliveryContainer = (LinearLayout) view.findViewById(R.id.out_for_delivery_container);
        }
    }



}
