package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.AlreadyCreatedPackages;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 12/18/17.
 */

public class AlreadyCreatedPackagesAdapter extends RecyclerView.Adapter<AlreadyCreatedPackagesAdapter.MyViewHolder>{
    private Context context;
    private AlreadyCreatedPackagesAdapterListener listener;
    private List<AlreadyCreatedPackages> packedList = new ArrayList<>();
    public AlreadyCreatedPackagesAdapter(Context context, List<AlreadyCreatedPackages> packedList,AlreadyCreatedPackagesAdapterListener listener) {
        this.context = context;
        this.packedList = packedList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_already_created_package, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AlreadyCreatedPackages packed = packedList.get(position);
        holder.listRowPackedPackageId.setText(packed.getPackageId());
       //holder.listRowPackedPackageId.setPaintFlags(holder.listRowPackedPackageId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.listRowPackedInvoiceNumber.setText("Invoice No : " + packed.getInvoiceNo());
        holder.listRowRunningOrdersCustomer.setText(packed.getCustomerName());

        holder.listRowPackedOrderId.setText("Order Id : " + packed.getOrderID());
        if (packed.getToDate() != null && !packed.getToDate().isEmpty()) {
            holder.listRowPackedExpDod.setText("Exp Dod : " + packed.getToDate());
        } else {
            holder.listRowPackedExpDod.setText("Exp Dod : N/A ");
        }

        if (packed.getZipCode() != null && !packed.getZipCode().isEmpty()) {
            holder.listRowPackedPincode.setText(packed.getZipCode());
            holder.listRowPackedExpDod.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedPincode.setVisibility(View.GONE);
        }

        holder.statusTextView.setText("Payment : " + packed.getPaymentStatus());
        holder.outletCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });


        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = targetFormat.parse(packed.getInvoiceDate());
            holder.listRowPackedInvoiceDate.setText(formatter.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return packedList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface AlreadyCreatedPackagesAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView outletCardview;
        private RelativeLayout relativelayoutContainer;
        private TextView listRowPackedPackageId;
        private TextView listRowPackedInvoiceDate;
        private TextView listRowRunningOrdersCustomer;
        private TextView listRowPackedPincode;
        private TextView listRowPackedOrderId;
        private TextView listRowPackedInvoiceNumber;
        private TextView listRowPackedExpDod;
        private TextView statusTextView;

        public MyViewHolder(View view) {
            super(view);
            listRowPackedPackageId = (TextView) view.findViewById(R.id.list_row_packed_package_id);
            listRowPackedInvoiceNumber = (TextView) view.findViewById(R.id.list_row_packed_invoice_number);
            listRowPackedInvoiceDate = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            listRowRunningOrdersCustomer = (TextView) view.findViewById(R.id.list_row_running_orders_customer);
            listRowPackedOrderId = (TextView) view.findViewById(R.id.list_row_packed_order_id);
            listRowPackedExpDod = (TextView) view.findViewById(R.id.list_row_packed_exp_dod);
            outletCardview = (CardView)view.findViewById( R.id.outlet_cardview );
            relativelayoutContainer = (RelativeLayout)view.findViewById( R.id.relativelayout_container );
            listRowPackedInvoiceNumber = (TextView)view.findViewById( R.id.list_row_packed_invoice_number );
            listRowPackedExpDod = (TextView)view.findViewById( R.id.list_row_packed_exp_dod );
            statusTextView = (TextView)view.findViewById( R.id.status_textView );
            listRowPackedPincode=(TextView)view.findViewById(R.id.list_row_packed_pincode);
        }

    }

}

