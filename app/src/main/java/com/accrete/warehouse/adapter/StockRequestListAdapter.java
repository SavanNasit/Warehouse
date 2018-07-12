package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.accrete.warehouse.R;
import com.accrete.warehouse.model.StockRequestList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by poonam on 7/3/18.
 */

public class StockRequestListAdapter extends RecyclerView.Adapter<StockRequestListAdapter.MyViewHolder> {
    private Activity activity;
    private List<StockRequestList> stockRequestLists;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private PurchaseOrderAdapter.PurchaseOrderAdapterListener listener;
    private Typeface fontAwesomeFont;

    public StockRequestListAdapter(Activity activity, List<StockRequestList> stockRequestLists,
                                PurchaseOrderAdapter.PurchaseOrderAdapterListener listener) {
        this.activity = activity;
        this.stockRequestLists = stockRequestLists;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_receive_stock_request, parent, false);
        return new MyViewHolder(itemView);
    }

    //To deal with empty string of amount
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final StockRequestList stockRequestList = stockRequestLists.get(position);

        if (stockRequestList.getRequest() != null && !stockRequestList.getRequest().isEmpty()) {
            holder.listRowReceiveStockRequestId.setText(stockRequestList.getRequest());
           // holder.listRowReceiveStockRequestId.setMovementMethod(LinkMovementMethod.getInstance());
           // holder.listRowReceiveStockRequestId.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        } else {
            holder.listRowReceiveStockRequestId.setText("NA");
        }

        holder.listRowReceiveStockStatus.setText(stockRequestList.getStatus());
        holder.listRowReceiveStockCreatedBy.setText(stockRequestList.getCreatedUser());

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(stockRequestList.getDueDate());
            holder.listRowReceiveStockDueDate.setText("Due date : " + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(stockRequestList.getCreatedTs());
            holder.payableAmountTextView.setText("" + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.listRowReceiveStockStatus.setBackgroundResource(R.drawable.tags_rounded_corner);
        holder.listRowReceiveStockSwipeText.setBackgroundResource(R.drawable.tags_rounded_corner);

       GradientDrawable drawable = (GradientDrawable) holder.listRowReceiveStockStatus.getBackground();
        if (stockRequestList.getStockreqsid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.orange_order));
            holder.listRowReceiveStockStatus.setText("Created");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.assigned_green));
            holder.listRowReceiveStockStatus.setText("Assigned");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.processed_green));
            holder.listRowReceiveStockStatus.setText("Processed");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.listRowReceiveStockStatus.setText("Packed");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.processed_green));
            holder.listRowReceiveStockStatus.setText("Received");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("6")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_order));
            holder.listRowReceiveStockStatus.setText("Cancelled");
            holder.listRowReceiveStockSwipeText.setEnabled(false);
            holder.listRowReceiveStockSwipeText.setVisibility(View.GONE);
        } else if (stockRequestList.getStockreqsid().equals("7")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.listRowReceiveStockStatus.setText("Partially Assigned ");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        } else if (stockRequestList.getStockreqsid().equals("8")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_order));
            holder.listRowReceiveStockStatus.setText("Partially Processed");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        }else if (stockRequestList.getStockreqsid().equals("9")) {
            drawable.setColor(activity.getResources().getColor(R.color.processed_green));
            holder.listRowReceiveStockStatus.setText("Partially Packed");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        }else if (stockRequestList.getStockreqsid().equals("10")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.listRowReceiveStockStatus.setText("Partially Received");
            holder.listRowReceiveStockSwipeText.setEnabled(true);
            holder.listRowReceiveStockSwipeText.setVisibility(View.VISIBLE);
        }

        //Receive
        GradientDrawable drawableReceive = (GradientDrawable) holder.listRowReceiveStockSwipeText.getBackground();
        drawableReceive.setColor(Color.TRANSPARENT);

       /* holder.textViewReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, POReceiveConsignmentActivity.class);
                intent.putExtra(activity.getString(R.string.purOrId), purchaseOrder.getPurorid());
                activity.startActivity(intent);
            }
        });*/
        //Click Item
      //  applyClickEvents(holder, position, stockRequestList.getOrderId(), stockRequestList.getPurchaseOrderId());
    }

    @Override
    public int getItemCount() {
        return stockRequestLists.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final String orderId, final String orderText) {
        holder.childLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, orderId, orderText);
            }
        });
    }

    public interface PurchaseOrderAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout viewBackground;
        private ImageView listRowReceiveStockRequestList;
        public RelativeLayout relativelayoutContainer;
        private LinearLayout childLayout;
        private LinearLayout leftLayout;
        private TextView listRowReceiveStockRequestId;
        private TextView listRowReceiveStockDueDate;
        private TextView payableAmountTextView;
        private TextView listRowReceiveStockStatus;
        private TextView listRowReceiveStockCreatedBy;
        private TextView listRowReceiveStockSwipeText;

        public MyViewHolder(View view) {
            super(view);
            viewBackground = (RelativeLayout)view.findViewById( R.id.view_background );
            listRowReceiveStockRequestList = (ImageView)view.findViewById( R.id.list_row_receive_stock_request_list );
            relativelayoutContainer = (RelativeLayout)view.findViewById( R.id.relativelayout_container );
            childLayout = (LinearLayout)view.findViewById( R.id.child_layout );
            leftLayout = (LinearLayout)view.findViewById( R.id.left_layout );
            listRowReceiveStockRequestId = (TextView)view.findViewById( R.id.list_row_receive_stock_request_id );
            listRowReceiveStockDueDate = (TextView)view.findViewById( R.id.list_row_receive_stock_due_date );
            payableAmountTextView = (TextView)view.findViewById( R.id.payable_amount_textView );
            listRowReceiveStockStatus = (TextView)view.findViewById( R.id.list_row_receive_stock_status );
            listRowReceiveStockCreatedBy = (TextView)view.findViewById( R.id.list_row_receive_stock_created_by );
            listRowReceiveStockSwipeText = (TextView)view.findViewById( R.id.list_row_receive_stock_swipe_text );
        }
    }


}