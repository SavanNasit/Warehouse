package com.accrete.warehouse.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PackedItem;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/14/17.
 */


public class PackedItemAdapter extends RecyclerView.Adapter<PackedItemAdapter.MyViewHolder> {
    private Context context;
    private PackedItemAdapterListener listener;
    private List<PackedItem> packedList = new ArrayList<>();
    public PackedItemAdapter(Context context, List<PackedItem> packedList, PackedItemAdapterListener listener) {
        this.context = context;
        this.packedList = packedList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_packed, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PackedItem packed = packedList.get(position);
        holder.listRowPackedPackageId.setText(AppPreferences.getCompanyCode(context, AppUtils.COMPANY_CODE)+packed.getPacid());
        holder.listRowPackedPackageId.setPaintFlags(holder.listRowPackedPackageId.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        holder.listRowPackedOrderId.setText(packed.getOid());
        holder.listRowPackedInvoiceNumber.setText(packed.getInvoiceNo());
        holder.listRowPackedInvoiceDate.setText(packed.getInvoiceDate());
        holder.listRowPackedCustomerName.setText(packed.getCustomerName());
        holder.listRowPackedPincode.setText(packed.getZipCode());
        holder.listRowPackedExpDod.setText(packed.getToDate());

    }

    @Override
    public int getItemCount() {
        return packedList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PackedItemAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowPackedPackageId;
        private TextView listRowPackedOrderId;
        private TextView listRowPackedInvoiceNumber;
        private TextView listRowPackedInvoiceDate;
        private TextView listRowPackedCustomerName;
        private TextView listRowPackedPincode;
        private TextView listRowPackedExpDod;
        private CheckBox listRowPackedCheckbox;


        public MyViewHolder(View view) {
            super(view);
            listRowPackedPackageId = (TextView) view.findViewById(R.id.list_row_packed_package_id);
            listRowPackedOrderId = (TextView) view.findViewById(R.id.list_row_packed_order_id);
            listRowPackedInvoiceNumber = (TextView) view.findViewById(R.id.list_row_packed_invoice_number);
            listRowPackedInvoiceDate = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            listRowPackedCustomerName = (TextView) view.findViewById(R.id.list_row_packed_customer_name);
            listRowPackedPincode = (TextView) view.findViewById(R.id.list_row_packed_pincode);
            listRowPackedExpDod = (TextView) view.findViewById(R.id.list_row_packed_exp_dod);
            listRowPackedCheckbox = (CheckBox) view.findViewById(R.id.list_row_package_checkbox);
        }
    }

}
