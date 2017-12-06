package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ManageGatepass;

import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageGatepassAdapter extends RecyclerView.Adapter<ManageGatepassAdapter.MyViewHolder> {

    private Context context;
    private List<ManageGatepass> gatepassList;
    private ManageGatepassAdapterrListener listener;

    public ManageGatepassAdapter(Context context, List<ManageGatepass> gatepassList, ManageGatepassAdapterrListener listener) {
        this.context = context;
        this.gatepassList = gatepassList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_manage_gatepass, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ManageGatepass manageGatepass = gatepassList.get(position);
        holder.listRowManageGatepassGatepassId.setText("(" + manageGatepass.getGatepassID() + ")");
        holder.listRowManageGatepassShippingCompanyName.setText(manageGatepass.getShippingCompanyName());
        holder.listRowManageGatepassGenerateOn.setText(manageGatepass.getGeneratedOn());
        holder.listRowManageGatepassPackages.setText(manageGatepass.getPackages());
        holder.listRowManageGatepassDeliveryUser.setText(manageGatepass.getDeliveryUser());
        holder.listRowManageGatepassStatus.setText(manageGatepass.getGatepassStatus());
        holder.listRowManageGatepassShippingType.setText(manageGatepass.getShippingType());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               applyClickEvents(position);
           }
        });
    }

    @Override
    public int getItemCount() {
        return gatepassList.size();

    }

    private void applyClickEvents(final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface ManageGatepassAdapterrListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowManageGatepassShippingCompanyName;
        private TextView listRowManageGatepassGatepassId;
        private TextView listRowManageGatepassGenerateOn;
        private TextView listRowManageGatepassPackages;
        private TextView listRowManageGatepassDeliveryUser;
        private TextView listRowManageGatepassStatus;
        private TextView listRowManageGatepassShippingType;
        private LinearLayout layout;

        public MyViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.linear_layout_manage_gatepass);
            listRowManageGatepassShippingCompanyName = (TextView) view.findViewById(R.id.list_row_manage_gatepass_shipping_company_name);
            listRowManageGatepassGatepassId = (TextView) view.findViewById(R.id.list_row_manage_gatepass_gatepass_id);
            listRowManageGatepassGenerateOn = (TextView) view.findViewById(R.id.list_row_manage_gatepass_generate_on);
            listRowManageGatepassPackages = (TextView) view.findViewById(R.id.list_row_manage_gatepass_packages);
            listRowManageGatepassDeliveryUser = (TextView) view.findViewById(R.id.list_row_manage_gatepass_delivery_user);
            listRowManageGatepassStatus = (TextView) view.findViewById(R.id.list_row_manage_gatepass_status);
            listRowManageGatepassShippingType = (TextView) view.findViewById(R.id.list_row_manage_gatepass_shipping_type);

        }
    }

}
