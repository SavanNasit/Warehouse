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
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.OrderReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by {Anshul} on 7/6/18.
 */

public class OrderReferencesAdapter extends RecyclerView.Adapter<OrderReferencesAdapter.MyViewHolder> {
    private Activity activity;
    private List<OrderReference> orderReferenceList;
    private OrderReferencesListener listener;
    private SimpleDateFormat simpleDateFormat;

    public OrderReferencesAdapter(Activity activity, List<OrderReference> orderReferenceList,
                                  OrderReferencesListener listener) {
        this.activity = activity;
        this.orderReferenceList = orderReferenceList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_order_references, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final OrderReference orderReference = orderReferenceList.get(position);

        //Reference Id
        if (orderReference.getInvoiceReferenceID() != null &&
                !orderReference.getInvoiceReferenceID().isEmpty() &&
                !orderReference.getInvoiceReferenceID().equals("null")) {
            holder.referenceIdTextView.setText("" + orderReference.getInvoiceReferenceID());
            holder.referenceIdTextView.setVisibility(View.VISIBLE);
        } else {
            holder.referenceIdTextView.setVisibility(View.GONE);
        }

        //Customer Name
        if (orderReference.getCustomer() != null &&
                !orderReference.getCustomer().isEmpty() &&
                !orderReference.getCustomer().equals("null")) {
            holder.customerNameTextView.setText("" + orderReference.getCustomer());
            holder.customerNameTextView.setVisibility(View.VISIBLE);
        } else {
            holder.customerNameTextView.setVisibility(View.GONE);
        }

        //Order Id
        if (orderReference.getOrderID() != null &&
                !orderReference.getOrderID().isEmpty() &&
                !orderReference.getOrderID().equals("null")) {
            holder.orderIdTextView.setText("" + orderReference.getOrderID());
            holder.orderIdTextView.setVisibility(View.VISIBLE);
        } else {
            holder.orderIdTextView.setVisibility(View.GONE);
        }

        //Amount
        if (orderReference.getAmount() != null &&
                !orderReference.getAmount().isEmpty() &&
                !orderReference.getAmount().equals("null")) {
            holder.amountTextView.setText("Amount " + activity.getString(R.string.Rs) + " " +
                    new BigDecimal(Constants.roundTwoDecimals
                            (Constants.ParseDouble(orderReference.getAmount())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
            holder.amountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.amountTextView.setVisibility(View.GONE);
        }

        //Used Amount
        if (orderReference.getUsedAmount() != null &&
                !orderReference.getUsedAmount().isEmpty() &&
                !orderReference.getUsedAmount().equals("null")) {
            holder.usedAmountTextView.setText("Used Amt. " + activity.getString(R.string.Rs) + " " +
                    new BigDecimal(Constants.roundTwoDecimals
                            (Constants.ParseDouble(orderReference.getUsedAmount())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
            holder.usedAmountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.usedAmountTextView.setVisibility(View.GONE);
        }

        //Balance
        if (orderReference.getBalanceAmount() != null &&
                !orderReference.getBalanceAmount().isEmpty() &&
                !orderReference.getBalanceAmount().equals("null")) {
            holder.balanceAmountTextView.setText("Balance " + activity.getString(R.string.Rs) + " " +
                    new BigDecimal(Constants.roundTwoDecimals
                            (Constants.ParseDouble(orderReference.getBalanceAmount())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
            holder.balanceAmountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.balanceAmountTextView.setVisibility(View.GONE);
        }

        //Transaction Mode
        if (orderReference.getTransactionModeName() != null &&
                !orderReference.getTransactionModeName().isEmpty() &&
                !orderReference.getTransactionModeName().equals("null")) {
            holder.transactionModeTextView.setText("Trans. Mode " + orderReference.getTransactionModeName());
            holder.transactionModeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.transactionModeTextView.setVisibility(View.GONE);
        }

        //Transaction Mode
        if (orderReference.getTransactionModeName() != null &&
                !orderReference.getTransactionModeName().isEmpty() &&
                !orderReference.getTransactionModeName().equals("null")) {
            holder.transactionModeTextView.setText("Trans. Mode " + orderReference.getTransactionModeName());
            holder.transactionModeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.transactionModeTextView.setVisibility(View.GONE);
        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (orderReference.getReferenceDate() != null && !orderReference.getReferenceDate().isEmpty() &&
                !orderReference.getReferenceDate().contains("0000")) {
            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(orderReference.getReferenceDate());
                holder.referenceDateTextView.setText(" " + outputFormat.format(date).toString().trim());
                holder.referenceDateTextView.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder.referenceDateTextView.setVisibility(View.GONE);
        }

        //Transaction Mode Details
        if (orderReference.getTransactionData() != null &&
                orderReference.getTransactionData().size() > 0) {
            for (int i = 0; i < orderReference.getTransactionData().size(); i++) {
                if (i == 0) {
                    if (orderReference.getTransactionData().get(i).getValue() != null) {
                        holder.transactionModeDetailsTextView.setText(orderReference.getTransactionData().get(i).getName() + ": "
                                + orderReference.getTransactionData().get(i).getValue());
                    } else {
                        holder.transactionModeDetailsTextView.setText(orderReference.getTransactionData().get(i).getName() + ": ");
                    }
                } else {
                    if (orderReference.getTransactionData().get(i).getValue() != null) {
                        //For date
                        if (orderReference.getTransactionData().get(i).getName().toLowerCase().equals("date")) {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                                Date date = simpleDateFormat.parse(orderReference.getTransactionData().get(i).getValue());
                                holder.transactionModeDetailsTextView.append("\n" +
                                        orderReference.getTransactionData().get(i).getName() + ": "
                                        + outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            holder.transactionModeDetailsTextView.append("\n" + orderReference.getTransactionData().get(i).getName() + ": "
                                    + orderReference.getTransactionData().get(i).getValue());
                        }

                    } else {
                        holder.transactionModeDetailsTextView.append("\n" + orderReference.getTransactionData().get(i).getName() + ": ");
                    }
                }
            }
            holder.transactionModeDetailsTextView.setVisibility(View.VISIBLE);
        } else {
            holder.transactionModeDetailsTextView.setVisibility(View.GONE);
        }

        //Status
        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
        holder.statusTextView.setText("" + orderReference.getStatus());

        if (orderReference.getInvrsid()!= null && !orderReference.getInvrsid().isEmpty()) {
            if (orderReference.getInvrsid().equals("1")) {
                drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            } else if (orderReference.getInvrsid().equals("2")) {
                drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            } else if (orderReference.getInvrsid().equals("3")) {
                drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            } else if (orderReference.getInvrsid().equals("5")) {
                drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            } else if (orderReference.getInvrsid().equals("7")) {
                drawable.setColor(activity.getResources().getColor(R.color.yellow_color));
            } else {
                drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            }
        }

        //On Item Click
        applyClickEvents(holder, position);

        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._4sdp);
            setMargins(holder.cardview, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardview, 0, topMargin, 0, 0);
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
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return orderReferenceList.size();
    }

    public interface OrderReferencesListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardview;
        private TextView referenceIdTextView;
        private TextView referenceDateTextView;
        private TextView customerNameTextView;
        private TextView orderIdTextView;
        private TextView transactionModeTextView;
        private TextView transactionModeDetailsTextView;
        private TextView amountTextView;
        private TextView usedAmountTextView;
        private TextView balanceAmountTextView;
        private TextView statusTextView;


        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardview = (CardView) view.findViewById(R.id.cardview);
            referenceIdTextView = (TextView) view.findViewById(R.id.reference_id_textView);
            referenceDateTextView = (TextView) view.findViewById(R.id.reference_date_textView);
            customerNameTextView = (TextView) view.findViewById(R.id.customer_name_textView);
            orderIdTextView = (TextView) view.findViewById(R.id.orderId_textView);
            transactionModeTextView = (TextView) view.findViewById(R.id.transaction_mode_textView);
            transactionModeDetailsTextView = (TextView) view.findViewById(R.id.transaction_mode_details_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            usedAmountTextView = (TextView) view.findViewById(R.id.used_amount_textView);
            balanceAmountTextView = (TextView) view.findViewById(R.id.balance_amount_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
        }
    }
}


