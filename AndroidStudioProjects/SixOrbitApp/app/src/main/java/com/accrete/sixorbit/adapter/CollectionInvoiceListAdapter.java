package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.InvoiceList;
import com.accrete.sixorbit.utils.InputFilterForPercentageAndRupees;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by {Anshul} on 16/5/18.
 */

public class CollectionInvoiceListAdapter extends RecyclerView.Adapter<CollectionInvoiceListAdapter.MyViewHolder> {
    private Activity activity;
    private List<InvoiceList> invoiceLists;
    private CollectionsInvoiceListListener listener;
    private SimpleDateFormat simpleDateFormat;
    private String receiveAmount;
    private double totalAmount;

    public CollectionInvoiceListAdapter(Activity activity, List<InvoiceList> invoiceLists,
                                        CollectionsInvoiceListListener listener, String receiveAmount) {
        this.activity = activity;
        this.invoiceLists = invoiceLists;
        this.listener = listener;
        this.receiveAmount = receiveAmount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_create_collections_invoices, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final InvoiceList invoiceList = invoiceLists.get(position);
            holder.invoiceIdTextView.setText(invoiceList.getInvoiceID());
            holder.invoiceNumberTextView.setText(invoiceList.getInvoiceNumber());
            holder.totalAmountTextView.setText("Total Amount : " + activity.getString(R.string.Rs) + " "
                    + invoiceList.getTotalAmount());

            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(invoiceList.getDate());
                holder.dateTextView.setText("" + outputFormat.format(date).toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (invoiceList.getPaidAmount() != null && !invoiceList.getPaidAmount().isEmpty()) {
                String paid = "Paid : " + activity.getString(R.string.Rs) + " "
                        + invoiceList.getPaidAmount();
                Spannable paidSpannableString = new SpannableString(paid);
                int paidEnd = paidSpannableString.length();
                paidSpannableString.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.green_order)), 0,
                        paidEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                String dividerStr = " | ";
                Spannable dividerSpannableString = new SpannableString(dividerStr);
                int dividerEnd = dividerSpannableString.length();
                dividerSpannableString.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.gray_order)), 0,
                        dividerEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                String pendingStr = "Pending : " + activity.getString(R.string.Rs) + " "
                        + invoiceList.getPendingAmount();
                Spannable pendingSpannableString = new SpannableString(pendingStr);
                int end = pendingSpannableString.length();
                pendingSpannableString.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.orange_order)), 0,
                        end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.paidPendingAmountTextView.setText(TextUtils.concat(paidSpannableString,
                        dividerSpannableString, pendingSpannableString));
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onCheckUncheckItem(position, isChecked);
                }
            });


            holder.receiveAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        holder.checkBox.setChecked(true);
                        holder.receiveAmountEditText.setFilters(new InputFilter[]
                                {new InputFilterForPercentageAndRupees("0",
                                        String.valueOf(Constants.ParseDouble(invoiceList.getPendingAmount())))});

                        if (holder.receiveAmountEditText.getText().toString() != null &&
                                !holder.receiveAmountEditText.getText().toString().isEmpty()
                                && holder.receiveAmountEditText.getText().toString().length() > 0) {
                            subtractValues(position, holder.receiveAmountEditText.getText().toString());
                        }

                    } else {
                        if (Constants.ParseDouble(holder.receiveAmountEditText.getText().toString())
                                > Constants.ParseDouble(receiveAmount)) {
                            holder.receiveAmountEditText.setText("0.00");
                        } else if (totalAmount > Constants.ParseDouble(receiveAmount)) {
                            holder.receiveAmountEditText.setText("0.00");
                        } else {
                            sumValues(position, holder.receiveAmountEditText.getText().toString());
                            if (totalAmount > Constants.ParseDouble(receiveAmount)) {
                                subtractValues(position, holder.receiveAmountEditText.getText().toString());
                                holder.receiveAmountEditText.setText("0.00");
                            }
                            listener.updateAdvanceReference(totalAmount + "");
                        }

                        if (Constants.ParseDouble(holder.receiveAmountEditText.getText().toString().trim()) == 0.00) {
                            holder.checkBox.setChecked(false);
                        }
                        listener.updateReceiveAmountValues(holder.receiveAmountEditText.getText().toString(), position);
                    }

                }
            });

            holder.receiveAmountEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(true);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (holder.checkBox.isChecked()) {
                    }
                }
            });

            //On Item Click
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

    private void sumValues(int position, String value) {
        // invoiceLists.get(position).setReceiveAmount(value);
        totalAmount += Constants.ParseDouble(value);
    }

    private void subtractValues(int position, String value) {
        //   invoiceLists.get(position).setReceiveAmount(value);
        totalAmount -= Constants.ParseDouble(value);
    }


    private void applyClickEvents(MyViewHolder holder, final int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return invoiceLists.size();
    }

    public interface CollectionsInvoiceListListener {
        void onMessageRowClicked(int position);

        void onCheckUncheckItem(int position, boolean status);

        void updateAdvanceReference(String value);

        void updateReceiveAmountValues(String value, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewOuter;
        private CardView cardViewInner;
        private CheckBox checkBox;
        private TextView invoiceIdTextView;
        private TextView dateTextView;
        private TextView invoiceNumberTextView;
        private TextView totalAmountTextView;
        private TextView paidPendingAmountTextView;
        private EditText receiveAmountEditText;


        public MyViewHolder(View view) {
            super(view);
            cardViewOuter = (CardView) view.findViewById(R.id.card_view_outer);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            invoiceIdTextView = (TextView) view.findViewById(R.id.invoice_id_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
            totalAmountTextView = (TextView) view.findViewById(R.id.total_amount_textView);
            paidPendingAmountTextView = (TextView) view.findViewById(R.id.paid_pending_amount_textView);
            receiveAmountEditText = (EditText) view.findViewById(R.id.receive_amount_editText);
        }
    }
}

