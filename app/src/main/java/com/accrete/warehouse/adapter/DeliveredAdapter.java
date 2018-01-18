package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Delivered;
import com.accrete.warehouse.model.OutForDelivery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/11/17.
 */

public class DeliveredAdapter extends RecyclerView.Adapter<DeliveredAdapter.MyViewHolder> {

    private Context context;
    private DeliveredAdapterListener listener;
    private List<OutForDelivery> deliveredList = new ArrayList<>();

    public DeliveredAdapter(Context context, List<OutForDelivery> deliveredList,DeliveredAdapterListener listener) {
        this.context = context;
        this.deliveredList = deliveredList;
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
        OutForDelivery outForDelivery = deliveredList.get(position);
        holder.outForDeliveryPackageId.setText(outForDelivery.getPackageId());
        holder.outForDeliveryPackageId.setPaintFlags(holder.outForDeliveryPackageId.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        holder.outForDeliveryInvoiceNo.setText("Invoice No : "+outForDelivery.getInvoiceNumber());
        holder.outForDeliveryInvoiceDate.setText(outForDelivery.getInvoiceDate());
        holder.outForDeliveryCustomerName.setText(outForDelivery.getCustomerName());
        holder.outForDeliveryGatepassId.setText(outForDelivery.getGatePassId());
        holder.outForDeliveryOrderId.setText(outForDelivery.getOrderId());
        holder.outForDeliveryExpDod.setText("Exp Dod : "+outForDelivery.getExpdod());
        holder.outForDeliveryUser.setText("Delivery User : "+outForDelivery.getDeliveryUser());


        holder.outForDeliveryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return deliveredList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface DeliveredAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
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
