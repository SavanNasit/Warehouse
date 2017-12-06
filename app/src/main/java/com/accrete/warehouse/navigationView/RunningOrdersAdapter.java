package com.accrete.warehouse.navigationView;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.RunningOrders;

import java.util.List;

/**
 * Created by poonam on 11/27/17.
 */

public class RunningOrdersAdapter extends RecyclerView.Adapter<RunningOrdersAdapter.MyViewHolder> {
    private Context context;
    private List<RunningOrders> runningOrdersList;
    private int mExpandedPosition = -1;
    private RunningOrdersAdapterListener listener;
    Activity activity;


    public RunningOrdersAdapter(Context context, List<RunningOrders> ordersAdapterList,RunningOrdersAdapterListener listener) {
        this.context = context;
        this.runningOrdersList = ordersAdapterList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_running_orders, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
      final RunningOrders runningOrders = runningOrdersList.get(position);
        holder.listRowRunningOrdersOrderId.setText("("+runningOrders.getOrderID()+")");
        holder.listRowRunningOrdersCustomer.setText(runningOrders.getCustomer());
        holder.listRowRunningOrdersDate.setText(runningOrders.getDate());
        holder.listRowRunningOrdersMobile.setText(runningOrders.getMobile());
        holder.listRowRunningOrdersExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExecute();
            }
        });

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return runningOrdersList.size();

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView listRowRunningOrdersOrderId;
        private TextView listRowRunningOrdersCustomer;
        private TextView listRowRunningOrdersDate;
        private TextView listRowRunningOrdersMobile;
        private LinearLayout listRowRunningOrdersExecute;
        private FrameLayout frameLayoutRunningOrders;


        public MyViewHolder(View view) {
            super(view);
            listRowRunningOrdersOrderId = (TextView)view.findViewById( R.id.list_row_running_orders_order_id );
            listRowRunningOrdersCustomer = (TextView)view.findViewById( R.id.list_row_running_orders_customer );
            listRowRunningOrdersDate = (TextView)view.findViewById( R.id.list_row_running_orders_date );
            frameLayoutRunningOrders=(FrameLayout)view.findViewById(R.id.frame_container_running_orders);
            listRowRunningOrdersMobile = (TextView)view.findViewById(R.id.list_row_running_orders_customer_mobile);
            listRowRunningOrdersExecute = (LinearLayout)view.findViewById(R.id.running_orders_execute);

        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.frameLayoutRunningOrders.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                // collapse any currently expanded items
                if (mExpandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(mExpandedPosition);
                }

                listener.onMessageRowClicked(position);
                //  notifyDataSetChanged();

            }
        });
    }


    public interface RunningOrdersAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

}
