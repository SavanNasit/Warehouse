package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Collection;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 8/12/17.
 */

public class CustomerOrderCollectionsAdapter extends RecyclerView.Adapter<CustomerOrderCollectionsAdapter.MyViewHolder> {
    private Activity activity;
    private List<Collection> collectionList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private CollectionAdapterListener listener;

    public CustomerOrderCollectionsAdapter(Activity activity, List<Collection> collectionList, String venId,
                                           CollectionAdapterListener listener) {
        this.activity = activity;
        this.collectionList = collectionList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collections_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Collection collection = collectionList.get(position);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(collection.getDate());
            holder.dateTextView.setText("  |  ");
            holder.dateTextView.append(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (collection.getTransactionId() != null && !collection.getTransactionId().isEmpty()) {
            holder.transactionIdTextView.setVisibility(View.VISIBLE);
            holder.transactionIdTextView.setText(collection.getTransactionId());
        } else {
            holder.transactionIdTextView.setVisibility(View.GONE);
        }

        if (collection.getInvoiceRefrenceId() != null && !collection.getInvoiceRefrenceId().isEmpty()) {
            holder.referenceIdTextView.setVisibility(View.VISIBLE);
            holder.referenceIdTextView.setText(collection.getInvoiceRefrenceId());
        } else {
            holder.referenceIdTextView.setVisibility(View.GONE);
        }

        if (collection.getApprovedUser() != null && !collection.getApprovedUser().isEmpty()) {
            holder.approvedUserTextView.setVisibility(View.VISIBLE);
            holder.approvedUserTextView.setText("By: " + collection.getApprovedUser());
        } else {
            holder.approvedUserTextView.setVisibility(View.GONE);
        }

        holder.transactionModeTextView.setText(collection.getTransactionMode());
        holder.transactionTypeTextView.setText(collection.getType());
        holder.statusTextView.setText(collection.getStatus());
        holder.amountTextView.setText("Total: " + activity.getString(R.string.Rs) + " " +
                formatter.format(Double.parseDouble(collection.getAmount())) + "");

        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

        if (collection.getStatus().equals("Pending")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
        } else {
            drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
        }

        //On Item Click
        applyClickEvents(holder, position);

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public interface CollectionAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout leftLayout;
        private TextView transactionIdTextView;
        private TextView referenceIdTextView;
        private TextView amountTextView;
        private TextView dateTextView;
        private TextView transactionModeTextView;
        private TextView transactionTypeTextView;
        private TextView statusTextView;
        private RelativeLayout mainLayout;
        private TextView approvedUserTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            transactionIdTextView = (TextView) view.findViewById(R.id.transactionId_textView);
            referenceIdTextView = (TextView) view.findViewById(R.id.referenceId_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            transactionModeTextView = (TextView) view.findViewById(R.id.transaction_mode_textView);
            transactionTypeTextView = (TextView) view.findViewById(R.id.transaction_type_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            approvedUserTextView = (TextView) view.findViewById(R.id.approved_user_textView);
        }
    }
}

