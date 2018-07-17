package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.CustomerOutstandingCollection;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;

/**
 * Created by {Anshul} on 27/6/18.
 */

public class CustomerwiseCollectionsAdapter extends
        RecyclerView.Adapter<CustomerwiseCollectionsAdapter.MyViewHolder> {
    private Activity activity;
    private List<CustomerOutstandingCollection> collectionDataList;
    private CollectionsListener listener;
    private SimpleDateFormat simpleDateFormat;

    public CustomerwiseCollectionsAdapter(Activity activity, List<CustomerOutstandingCollection>
            collectionDataList, CollectionsListener listener) {
        this.activity = activity;
        this.collectionDataList = collectionDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_customerwise_collections, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final CustomerOutstandingCollection collectionData = collectionDataList.get(position);

            if (collectionData.getCustomerName() != null &&
                    !collectionData.getCustomerName().isEmpty()) {
                holder.nameTextView.setText(collectionData.getCustomerName().toString().trim());
                holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.nameTextView.setVisibility(View.VISIBLE);
            } else {
                holder.nameTextView.setVisibility(View.GONE);
            }

            DecimalFormat amountFormatter = new DecimalFormat("#,##,##,##,###");
            holder.payableAmountTextView.setText("Payable " + activity.getString(R.string.Rs) + " " +
                    amountFormatter.format(roundTwoDecimals(
                            ParseDouble(collectionData.getPayableAmount()))));
            holder.pendingAmountTextView.setText("Pending " + activity.getString(R.string.Rs) + " " +
                    amountFormatter.format(roundTwoDecimals(
                            ParseDouble(collectionData.getPendingAmount()))));

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = null;
                if (collectionData.getCreatedTs() != null &&
                        !collectionData.getCreatedTs().isEmpty() &&
                        !collectionData.getCreatedTs().contains("0000") &&
                        !collectionData.getCreatedTs().equals("0")) {
                    date = simpleDateFormat.parse(collectionData.getCreatedTs());
                    holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
                } else if (collectionData.getCreatedTs() != null &&
                        !collectionData.getCreatedTs().isEmpty() &&
                        !collectionData.getCreatedTs().contains("0000") &&
                        !collectionData.getCreatedTs().equals("0")) {
                    date = simpleDateFormat.parse(collectionData.getCreatedTs());
                    holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
                } else if (collectionData.getCreatedTs() != null &&
                        !collectionData.getCreatedTs().isEmpty() &&
                        !collectionData.getCreatedTs().contains("0000") &&
                        !collectionData.getCreatedTs().equals("0")) {
                    date = simpleDateFormat.parse(collectionData.getCreatedTs());
                    holder.dateTextView.setText(" " + outputFormat.format(date).toString().trim());
                }
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
        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
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

    public interface CollectionsListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardView;
        private TextView nameTextView;
        private TextView dateTextView;
        private TextView payableAmountTextView;
        private TextView pendingAmountTextView;


        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardView = (CardView) view.findViewById(R.id.outlet_cardview);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            pendingAmountTextView = (TextView) view.findViewById(R.id.pending_amount_textView);
        }
    }
}