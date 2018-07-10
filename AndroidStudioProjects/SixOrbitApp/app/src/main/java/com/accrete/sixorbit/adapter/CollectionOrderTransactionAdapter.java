package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.CollectionTransactionData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by {Anshul} on 4/6/18.
 */

public class CollectionOrderTransactionAdapter extends RecyclerView.Adapter<CollectionOrderTransactionAdapter.MyViewHolder> {
    private Activity activity;
    private List<CollectionTransactionData> transactionDataList;
    private MyCollectionsListener listener;
    private SimpleDateFormat simpleDateFormat;

    public CollectionOrderTransactionAdapter(Activity activity, List<CollectionTransactionData> transactionDataList,
                                             MyCollectionsListener listener) {
        this.activity = activity;
        this.transactionDataList = transactionDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_collection_transaction, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final CollectionTransactionData collectionData = transactionDataList.get(position);

            holder.transactionIdTextView.setText(collectionData.getTransactionID());
            if (collectionData.getVoucherID() != null && !collectionData.getVoucherID().isEmpty()
                    && !collectionData.getVoucherID().equals("null")) {
                holder.voucherIdTextView.setText(collectionData.getVoucherID());
                holder.voucherIdTextView.setVisibility(View.VISIBLE);
            } else if (collectionData.getReferenceID() != null && !collectionData.getReferenceID().isEmpty()
                    && !collectionData.getReferenceID().equals("null")) {
                holder.voucherIdTextView.setText("Ref. Id: " + collectionData.getReferenceID());
                holder.voucherIdTextView.setVisibility(View.VISIBLE);
            } else {
                holder.voucherIdTextView.setVisibility(View.GONE);
            }

            holder.transactionModeTextView.setText(collectionData.getTransactionMode());

            //Transaction Mode Details
            if (collectionData.getInvoiceTransactionData() != null &&
                    collectionData.getInvoiceTransactionData().size() > 0) {
                for (int i = 0; i < collectionData.getInvoiceTransactionData().size(); i++) {
                    if (i == 0) {
                        if (collectionData.getInvoiceTransactionData().get(i).getValue() != null) {
                            holder.transactionModeDetailsTextView.setText(collectionData.getInvoiceTransactionData().get(i).getName() + ": "
                                    + collectionData.getInvoiceTransactionData().get(i).getValue());
                        } else {
                            holder.transactionModeDetailsTextView.setText(collectionData.getInvoiceTransactionData().get(i).getName() + ": ");
                        }
                    } else {
                        if (collectionData.getInvoiceTransactionData().get(i).getValue() != null) {
                            //For date
                            if (collectionData.getInvoiceTransactionData().get(i).getName() != null
                                    && !collectionData.getInvoiceTransactionData().get(i).getName().isEmpty()
                                    && collectionData.getInvoiceTransactionData().get(i).getName().toLowerCase().equals("date")) {
                                try {
                                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                                    Date date = simpleDateFormat.parse(collectionData.getInvoiceTransactionData().get(i).getValue());
                                    holder.transactionModeDetailsTextView.append("\n" +
                                            collectionData.getInvoiceTransactionData().get(i).getName() + ": "
                                            + outputFormat.format(date).toString().trim());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.transactionModeDetailsTextView.append("\n" + collectionData.getInvoiceTransactionData().get(i).getName() + ": "
                                        + collectionData.getInvoiceTransactionData().get(i).getValue());
                            }

                        } else {
                            holder.transactionModeDetailsTextView.append("\n" + collectionData.getInvoiceTransactionData().get(i).getName() + ": ");
                        }
                    }
                }
                holder.transactionModeDetailsTextView.setVisibility(View.VISIBLE);
            } else {
                holder.transactionModeDetailsTextView.setVisibility(View.GONE);
            }

            double amount = Double.parseDouble(collectionData.getAmount());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            if (collectionData.getTransactionType().toLowerCase().equals("credit")) {
                holder.amountTextView.setText("CR \n" + activity.getString(R.string.Rs) + " " + formatter.format(amount));
                holder.amountTextView.setTextColor(activity.getResources().getColor(R.color.green));
            } else {
                holder.amountTextView.setTextColor(activity.getResources().getColor(R.color.lightRed));
                holder.amountTextView.setText("DR \n" + activity.getString(R.string.Rs) + " " + formatter.format(amount));
            }

            //On Item Click
            applyClickEvents(holder, position);

            if (position == 0) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardview, 0, topMargin, 0, 0);
            } else if (position == transactionDataList.size() - 1) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardview, 0, topMargin, 0, topMargin);
            } else {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardview, 0, topMargin, 0, 0);
            }

            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
            holder.statusTextView.setText("" + collectionData.getStatus());

            if (collectionData.getInvtsid() != null && !collectionData.getInvtsid().isEmpty()) {
                if (collectionData.getInvtsid().equals("1")) {
                    drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
                } else if (collectionData.getInvtsid().equals("2")) {
                    drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
                } else if (collectionData.getInvtsid().equals("3")) {
                    drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
                } else if (collectionData.getInvtsid().equals("5")) {
                    drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
                } else if (collectionData.getInvtsid().equals("7")) {
                    drawable.setColor(activity.getResources().getColor(R.color.yellow_color));
                } else {
                    drawable.setColor(activity.getResources().getColor(R.color.gray_order));
                }
            }

            //TODO Approved User
            if (collectionData.getApprovedUser() != null && !collectionData.getApprovedUser().isEmpty()) {
                holder.approvedByTextView.setVisibility(View.VISIBLE);
                holder.approvedByTextView.setText("Approved By: " + collectionData.getApprovedUser());
            } else {
                holder.approvedByTextView.setVisibility(View.GONE);
            }

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (collectionData.getDate() != null && !collectionData.getDate().isEmpty() &&
                    !collectionData.getDate().contains("0000")) {
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(collectionData.getDate());
                    holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
                    holder.dateTextView.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                holder.dateTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return transactionDataList.size();
    }

    public interface MyCollectionsListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardview;
        private TextView transactionIdTextView;
        private TextView dateTextView;
        private TextView voucherIdTextView;
        private TextView transactionModeTextView;
        private TextView transactionModeDetailsTextView;
        private TextView amountTextView;
        private TextView statusTextView;
        private TextView approvedByTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardview = (CardView) view.findViewById(R.id.cardview);
            transactionIdTextView = (TextView) view.findViewById(R.id.transaction_id_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            voucherIdTextView = (TextView) view.findViewById(R.id.voucher_id_textView);
            transactionModeTextView = (TextView) view.findViewById(R.id.transaction_mode_textView);
            transactionModeDetailsTextView = (TextView) view.findViewById(R.id.transaction_mode_details_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            approvedByTextView = (TextView) view.findViewById(R.id.approved_by_textView);
        }
    }
}