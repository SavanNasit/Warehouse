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
import com.accrete.warehouse.model.ViewGatepassPackages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 1/21/18.
 */

public class ViewPackageGatePassAdapter extends RecyclerView.Adapter<ViewPackageGatePassAdapter.MyViewHolder> {
    private Context context;
    private PackedAdapterListener listener;
    private List<ViewGatepassPackages> packedList = new ArrayList<>();

    public ViewPackageGatePassAdapter(Context context, List<ViewGatepassPackages> packedList, PackedAdapterListener listener) {
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
        final ViewGatepassPackages packed = packedList.get(position);
        holder.listRowPackedPackageId.setText(packed.getPackageID());
       //holder.listRowPackedPackageId.setPaintFlags(holder.listRowPackedPackageId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.listRowPackedOrderId.setText(packed.getOrderID());
        holder.listRowRunningOrdersCustomer.setText(packed.getCustomerName());
        holder.listRowPackedPincode.setText(packed.getZipCode());
        holder.listRowPackedExpDod.setText(packed.getExpDate());
        holder.listRowPackedInvoiceNumber.setVisibility(View.GONE);
        holder.listRowPackedInvoiceDate.setVisibility(View.GONE);

        holder.linearLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(position,packed.getCuid(),packed.getInvid(),packed.getChkoid(), packed.getPacid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return packedList.size();

    }

    private void applyClickEvents(final int position, String cuid, String invid, String chkoid, String pacid) {
        listener.onRowClicked(position, cuid, invid, chkoid, pacid);
    }

    public interface PackedAdapterListener {
        void onRowClicked(int position, String cuid, String invid, String chkoid, String pacid);
        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowPackedPackageId;
        private TextView listRowPackedOrderId;
        private TextView listRowPackedInvoiceNumber;
        private TextView listRowPackedInvoiceDate;
        private TextView listRowRunningOrdersCustomer;
        private TextView listRowPackedPincode;
        private TextView listRowPackedExpDod;
        private CheckBox listRowPackedCheckbox;
        private RelativeLayout linearLayoutMain;


        public MyViewHolder(View view) {
            super(view);
            listRowPackedPackageId = (TextView) view.findViewById(R.id.list_row_packed_package_id);
            listRowPackedInvoiceNumber = (TextView) view.findViewById(R.id.list_row_packed_invoice_number);
            listRowPackedInvoiceDate = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            listRowRunningOrdersCustomer = (TextView) view.findViewById(R.id.list_row_running_orders_customer);
            listRowPackedOrderId = (TextView) view.findViewById(R.id.list_row_packed_order_id);
            listRowPackedExpDod = (TextView) view.findViewById(R.id.list_row_packed_exp_dod);
           // outletCardview = (CardView)view.findViewById( R.id.outlet_cardview );
            linearLayoutMain = (RelativeLayout) view.findViewById( R.id.relativelayout_container );
            listRowPackedInvoiceNumber = (TextView)view.findViewById( R.id.list_row_packed_invoice_number );
            listRowPackedExpDod = (TextView)view.findViewById( R.id.list_row_packed_exp_dod );
           // statusTextView = (TextView)view.findViewById( R.id.status_textView );
            listRowPackedPincode=(TextView)view.findViewById(R.id.list_row_packed_pincode);

        }
    }

}
