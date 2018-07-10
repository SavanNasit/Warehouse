package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.accrete.warehouse.R;
import com.accrete.warehouse.model.OrderData;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.StockRequestDatum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by poonam on 6/4/18.
 */

public class RunningStockRequestAdapter  extends RecyclerView.Adapter<RunningStockRequestAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private List<StockRequestDatum> stockRequestData;
    private int mExpandedPosition = -1;
    private StockRequestDatumsAdapterListener listener;

    public RunningStockRequestAdapter(Context context, List<StockRequestDatum> stockRequestData, StockRequestDatumsAdapterListener listener) {
        this.context = context;
        this.stockRequestData = stockRequestData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_running_stock_request, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StockRequestDatum stockRequestDatum = stockRequestData.get(position);

           holder.listRowRunningStockRequestsId.setText(stockRequestDatum.getRequestID());
           holder.listRowRunningStockRequestsCreatedBy.setText(stockRequestDatum.getCreatedUser());

        holder.listRowRunningStockRequestsCreatedBy.setText(stockRequestDatum.getCreatedUser());


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

       try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(stockRequestDatum.getDueDate());
            holder.listRowRunningStockRequestsDueDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(stockRequestDatum.getCreatedTs());
            holder.listRowRunningStockRequestsCreatedTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return stockRequestData.size();
    }



    public void next(int position, StockRequestDatum stockRequestDatum) {
        listener.onExecute(stockRequestDatum.getChkid(), stockRequestDatum.getStockreqid(), position);
        notifyDataSetChanged();
    }


    public interface StockRequestDatumsAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute(String chkid, String chkoid, int position);
        void onCall(String contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout viewBackground;
        public ImageView recordFollowupIcon;
        public RelativeLayout relativelayoutContainer;
        public TextView listRowRunningStockRequestsCreatedBy;
        public TextView listRowRunningStockRequestsId;
        public TextView listRowRunningStockRequestsDueDate;
        public TextView listRowRunningStockRequestsCreatedTime;
        public View viewAttendee;
        public TextView listRowRunningOrdersAttendee;

        public MyViewHolder(View view) {
            super(view);
                viewBackground = (RelativeLayout)view.findViewById( R.id.view_background );
                recordFollowupIcon = (ImageView)view.findViewById( R.id.record_followup_icon );
                relativelayoutContainer = (RelativeLayout)view.findViewById( R.id.relativelayout_container );
                listRowRunningStockRequestsCreatedBy = (TextView)view.findViewById( R.id.list_row_running_stock_requests_created_by );
                listRowRunningStockRequestsId = (TextView)view.findViewById( R.id.list_row_running_stock_requests_id );
                listRowRunningStockRequestsDueDate = (TextView)view.findViewById( R.id.list_row_running_stock_requests_due_date );
                listRowRunningStockRequestsCreatedTime = (TextView)view.findViewById( R.id.list_row_running_stock_requests_created_time );
                listRowRunningOrdersAttendee = (TextView)view.findViewById( R.id.list_row_running_orders_attendee );
            }


    }

}
