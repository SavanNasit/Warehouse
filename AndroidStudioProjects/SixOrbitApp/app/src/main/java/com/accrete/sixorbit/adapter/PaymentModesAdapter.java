package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.PaymentOptionAttribute;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by {Anshul} on 3/4/18.
 */

public class PaymentModesAdapter extends RecyclerView.Adapter<PaymentModesAdapter.MyViewHolder> {
    private Activity activity;
    private List<PaymentOptionAttribute> optionAttributeList;
    private PaymentModesAdapterListener listener;
    private String flagDealerPrice;

    public PaymentModesAdapter(Activity activity, List<PaymentOptionAttribute> optionAttributeList,
                               PaymentModesAdapterListener listener) {
        this.activity = activity;
        this.optionAttributeList = optionAttributeList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_payment_mode_attributes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.titleTextView.setText(optionAttributeList.get(position).getName());
        if (optionAttributeList.get(position).getRequired() != null &&
                !optionAttributeList.get(position).getRequired().isEmpty() &&
                optionAttributeList.get(position).getRequired().equals("1")) {
            //Mandatory title
            String mainText = "" + optionAttributeList.get(position).getName();
            String colored = " *";
            Spannable spannableStringBuilder = new SpannableString(colored);
            int end = spannableStringBuilder.length();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.titleTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));
        }

        holder.textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.updateAttributes(position, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (optionAttributeList.get(position).getName().toLowerCase().contains("date")) {
            holder.textInputEditText.setFocusable(false);
            if (optionAttributeList.get(position).getValue() != null &&
                    !optionAttributeList.get(position).getValue().isEmpty()) {
                holder.textInputEditText.setText(optionAttributeList.get(position).getValue() + "");
            } else {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c);
                holder.textInputEditText.setText(formattedDate);
            }
            holder.textInputEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.textInputEditText.setEnabled(false);
                    listener.updateDate(position);
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.textInputEditText.setEnabled(true);
                        }
                    }, 4000);
                }
            });
        } else {
            holder.textInputEditText.setFocusable(true);
            if (optionAttributeList.get(position).getValue() != null) {
                holder.textInputEditText.setText(optionAttributeList.get(position).getValue() + "");
            } else {
                holder.textInputEditText.setText("");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return optionAttributeList.size();
    }

    public interface PaymentModesAdapterListener {
        void updateAttributes(int position, String value);

        void updateDate(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText textInputEditText;
        private TextView titleTextView;

        public MyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.title_textView);
            textInputEditText = (TextInputEditText) view.findViewById(R.id.textInputEditText);
        }
    }
}
