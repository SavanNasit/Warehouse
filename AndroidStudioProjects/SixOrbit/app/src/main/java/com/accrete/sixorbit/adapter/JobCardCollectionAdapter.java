package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.JobcardCollectionData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;

/**
 * Created by {Anshul} on 30/5/18.
 */

public class JobCardCollectionAdapter extends RecyclerView.Adapter<JobCardCollectionAdapter.MyViewHolder> {
    private Activity activity;
    private List<JobcardCollectionData> collectionDataList;
    private JobCardCollectionsListener listener;
    private SimpleDateFormat simpleDateFormat;

    public JobCardCollectionAdapter(Activity activity, List<JobcardCollectionData> collectionDataList,
                                    JobCardCollectionsListener listener) {
        this.activity = activity;
        this.collectionDataList = collectionDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_my_collections, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final JobcardCollectionData collectionData = collectionDataList.get(position);
        DecimalFormat amountFormatter = new DecimalFormat("#,##,##,##,###");

        holder.customerNameTextView.setText(collectionData.getName());
        holder.billedAmountTextView.setText("Billed Amt. " + activity.getString(R.string.Rs) + " " +
                amountFormatter.format(roundTwoDecimals(
                        ParseDouble(collectionData.getBilledAmount()))));
        holder.paidAmountTextView.setText("Paid " + activity.getString(R.string.Rs) + " " +
                amountFormatter.format(roundTwoDecimals(
                        ParseDouble(collectionData.getPaidAmount()))));
        holder.orderAmountTextView.setText("Order Amt. " + activity.getString(R.string.Rs) + " " +
                amountFormatter.format(roundTwoDecimals(
                        ParseDouble(collectionData.getOrderAmount()))));

        if (collectionData.getChekPointOrderID() != null &&
                !collectionData.getChekPointOrderID().isEmpty()) {
            holder.orderIdTextView.setText(collectionData.getChekPointOrderID());
        } else if (collectionData.getInvoiceID() != null &&
                !collectionData.getInvoiceID().isEmpty()) {
            holder.orderIdTextView.setText(collectionData.getInvoiceID());

            holder.billedAmountTextView.setText("" + collectionData.getInvoiceNumber());
            holder.orderAmountTextView.setText("Payable " + activity.getString(R.string.Rs) + " " +
                    amountFormatter.format(roundTwoDecimals(
                            ParseDouble(collectionData.getPayableAmount()))));

        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(collectionData.getCreatedTs());
            holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //On Item Click
        applyClickEvents(holder, position);

        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClicked(position);
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return collectionDataList.size();
    }

    public interface JobCardCollectionsListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardView;
        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView customerNameTextView;
        private TextView billedAmountTextView;
        private TextView paidAmountTextView;
        private TextView orderAmountTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardView = (CardView) view.findViewById(R.id.outlet_cardview);
            orderIdTextView = (TextView) view.findViewById(R.id.order_id_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            customerNameTextView = (TextView) view.findViewById(R.id.customer_name_textView);
            billedAmountTextView = (TextView) view.findViewById(R.id.billed_amount_textView);
            paidAmountTextView = (TextView) view.findViewById(R.id.paid_amount_textView);
            orderAmountTextView = (TextView) view.findViewById(R.id.order_amount_textView);
        }
    }
}

