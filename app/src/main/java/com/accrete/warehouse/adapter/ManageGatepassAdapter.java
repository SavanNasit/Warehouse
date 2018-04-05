package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.GatepassList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageGatepassAdapter extends RecyclerView.Adapter<ManageGatepassAdapter.MyViewHolder> {

    private Context context;
    private List<GatepassList> gatepassList;
    private ManageGatepassAdapterrListener listener;

    public ManageGatepassAdapter(Context context, List<GatepassList> gatepassList, ManageGatepassAdapterrListener listener) {
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
        final GatepassList manageGatepass = gatepassList.get(position);

        if (manageGatepass.getCreatedOn() != null && !manageGatepass.getCreatedOn().isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date startDate = targetFormat.parse(manageGatepass.getCreatedOn());
                holder.listRowManageGatepassGenerateOn.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.listRowManageGatepassGatepassId.setText("(" + manageGatepass.getGatePassId() + ")");
        holder.listRowManageGatepassShippingCompanyName.setText(manageGatepass.getShippingCompanyName());
        holder.listRowManageGatepassPackages.setText("Pkg: " + manageGatepass.getPackages());
        holder.listRowManageGatepassDeliveryUser.setText(manageGatepass.getUserName());
        if (manageGatepass.getGatepassStatus() != null && !manageGatepass.getGatepassStatus().isEmpty()) {
            holder.listRowManageGatepassStatus.setText("Status: " + manageGatepass.getGatepassStatus());
        } else {
            holder.listRowManageGatepassStatus.setText("");
        }
        if (manageGatepass.getShippingType() != null && !manageGatepass.getShippingType().isEmpty()) {
            holder.listRowManageGatepassShippingType.setText("Type: " + manageGatepass.getShippingType());
            holder.listRowManageGatepassShippingType.setVisibility(View.VISIBLE);
        } else {
            holder.listRowManageGatepassShippingType.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(position, manageGatepass.getGatepassStatus());
            }
        });
    }

    @Override
    public int getItemCount() {
        return gatepassList.size();

    }

    private void applyClickEvents(final int position, String status) {
        listener.onMessageRowClicked(position, status);
    }

    public interface ManageGatepassAdapterrListener {
        void onMessageRowClicked(int position, String status);

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
            layout = (LinearLayout) view.findViewById(R.id.main_layout);
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
