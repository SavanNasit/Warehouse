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
import com.accrete.sixorbit.model.TransactionData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;

/**
 * Created by {Anshul} on 17/5/18.
 */

public class PendingCollectionAdapter extends RecyclerView.Adapter<PendingCollectionAdapter.MyViewHolder> {
    private Activity activity;
    private List<TransactionData> transactionDataList;
    private PendingCollectionListener listener;
    private SimpleDateFormat simpleDateFormat;

    public PendingCollectionAdapter(Activity activity, List<TransactionData> transactionDataList,
                                    PendingCollectionListener listener) {
        this.activity = activity;
        this.transactionDataList = transactionDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_collection, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final TransactionData transactionData;
        try {
            transactionData = transactionDataList.get(position);


            DecimalFormat amountFormatter = new DecimalFormat("#,##,##,##,###");

            holder.transactionIdTextView.setText(transactionData.getTransactionID());
            if (transactionData.getLedger() != null && !transactionData.getLedger().isEmpty()) {
                holder.ledgerNameTextView.setText(transactionData.getLedger());
                holder.ledgerNameTextView.setVisibility(View.VISIBLE);
            } else {
                holder.ledgerNameTextView.setVisibility(View.GONE);
            }

            if (transactionData.getCreatedUser() != null && !transactionData.getCreatedUser().isEmpty()) {
                holder.createdByTextView.setText("By: " + transactionData.getCreatedUser());
                holder.createdByTextView.setVisibility(View.VISIBLE);
            } else {
                holder.createdByTextView.setVisibility(View.GONE);
            }

            if (transactionData.getTransactionMode() != null && !transactionData.getTransactionMode().isEmpty()) {
                holder.transactionModeTextView.setText("Trans. Mode: " + transactionData.getTransactionMode());
                holder.transactionModeTextView.setVisibility(View.VISIBLE);
            } else {
                holder.transactionModeTextView.setVisibility(View.GONE);
            }

            holder.amountTextView.setText("Amount " + activity.getString(R.string.Rs) + " " +
                    amountFormatter.format(roundTwoDecimals(
                            ParseDouble(transactionData.getAmount()))));

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(transactionData.getDate());
                holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
            holder.statusTextView.setText("" + transactionData.getStatus());

            if (transactionData.getStatus() != null && !transactionData.getStatus().isEmpty()) {
                if (transactionData.getStatus().toLowerCase().equals("pending")) {
                    drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
                } else if (transactionData.getStatus().toLowerCase().equals("approved")) {
                    drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
                } else {
                    drawable.setColor(activity.getResources().getColor(R.color.orange_purchase_order));
                }
            }

            //Transaction Mode Details
            if (transactionData.getTransactionModeData() != null &&
                    transactionData.getTransactionModeData().size() > 0) {
                for (int i = 0; i < transactionData.getTransactionModeData().size(); i++) {
                    if (i == 0) {
                        if (transactionData.getTransactionModeData().get(i).getValue() != null) {
                            holder.transactionModeDataTextView.setText(transactionData.getTransactionModeData().get(i).getName() + ": "
                                    + transactionData.getTransactionModeData().get(i).getValue());
                        } else {
                            holder.transactionModeDataTextView.setText(transactionData.getTransactionModeData().get(i).getName() + ": ");
                        }
                    } else {
                        if (transactionData.getTransactionModeData().get(i).getValue() != null) {
                            //For date
                            if (transactionData.getTransactionModeData().get(i).getName().toLowerCase().equals("date")) {
                                try {
                                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                                    Date date = simpleDateFormat.parse(transactionData.getTransactionModeData().get(i).getValue());
                                    holder.transactionModeDataTextView.append("\n" +
                                            transactionData.getTransactionModeData().get(i).getName() + ": "
                                            + outputFormat.format(date).toString().trim());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.transactionModeDataTextView.append("\n" + transactionData.getTransactionModeData().get(i).getName() + ": "
                                        + transactionData.getTransactionModeData().get(i).getValue());
                            }

                        } else {
                            holder.transactionModeDataTextView.append("\n" + transactionData.getTransactionModeData().get(i).getName() + ": ");
                        }
                    }
                }
                holder.transactionModeDataTextView.setVisibility(View.VISIBLE);
            } else {
                holder.transactionModeDataTextView.setVisibility(View.GONE);
            }

            //On Item Click
            //applyClickEvents(holder, position, order.getChkoid(), order.getCheckpointOrderId());

            if (position == 0) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
            } else {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
            }

            applyClickEvents(holder, position);
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
        holder.transactionIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
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

    public interface PendingCollectionListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardView;
        private TextView transactionIdTextView;
        private TextView dateTextView;
        private TextView ledgerNameTextView;
        private TextView createdByTextView;
        private TextView amountTextView;
        private TextView statusTextView;
        private TextView transactionModeTextView;
        private TextView transactionModeDataTextView;

        public MyViewHolder(View view) {
            super(view);
            transactionModeTextView = (TextView) view.findViewById(R.id.transaction_mode_textView);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardView = (CardView) view.findViewById(R.id.outlet_cardview);
            transactionIdTextView = (TextView) view.findViewById(R.id.transaction_id_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            ledgerNameTextView = (TextView) view.findViewById(R.id.ledger_name_textView);
            createdByTextView = (TextView) view.findViewById(R.id.created_by_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            transactionModeDataTextView = (TextView) view.findViewById(R.id.transaction_mode_data_textView);
        }
    }
}