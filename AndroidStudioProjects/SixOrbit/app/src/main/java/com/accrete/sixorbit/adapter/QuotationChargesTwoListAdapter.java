package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.ChargesList2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by agt on 22/12/17.
 */

public class QuotationChargesTwoListAdapter extends RecyclerView.Adapter<QuotationChargesTwoListAdapter.MyViewHolder> {
    private Activity activity;
    private List<ChargesList2> chargesList2List;
    private SimpleDateFormat simpleDateFormat;
    private QuotationCharges2AdapterListener listener;
    private DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

    public QuotationChargesTwoListAdapter(Activity activity, List<ChargesList2> chargesList2List,
                                          QuotationCharges2AdapterListener listener) {
        this.activity = activity;
        this.chargesList2List = chargesList2List;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_quotation_charges, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        try {
            final ChargesList2 chargesList2 = chargesList2List.get(position);
            holder.titleTextView.setText(chargesList2.getTitle());

            holder.edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (holder.edittext.getText().toString().trim() != null && holder.edittext.getText().toString().length() != 0) {
                            if (chargesList2.getEctid() != null && chargesList2.getEctid().equals("2")) {
                                holder.chargeValueTextView.setText(holder.edittext.getText().toString().trim());
                                listener.onQuotationChargeTwoDiscount(position, holder.edittext.getText().toString().trim(), chargesList2);
                            } else {
                                listener.onQuotationChargeTwoDiscount(position, holder.edittext.getText().toString().trim(), chargesList2);
                            }
                        } else {
                            listener.onQuotationChargeTwoDiscount(position, "0.00", chargesList2);
                        }
                    }
                }
            });

           /* holder.edittext.addTextChangedListener(
                    new TextWatcher() {
                        private final long DELAY = 1000; // milliseconds
                        private Timer timer = new Timer();

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(final Editable s) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            // TODO: do what you need here (refresh list)
                                            // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                            if (holder.edittext.getText().toString().trim() != null && holder.edittext.getText().toString().length() != 0) {
                                                if (chargesList2.getEctid() != null && chargesList2.getEctid().equals("2")) {
                                                    holder.chargeValueTextView.setText(holder.edittext.getText().toString().trim());
                                                    listener.onQuotationChargeTwoDiscount(position, holder.edittext.getText().toString().trim(), chargesList2);
                                                } else {
                                                    listener.onQuotationChargeTwoDiscount(position, holder.edittext.getText().toString().trim(), chargesList2);
                                                }
                                            }
                                        }
                                    },
                                    DELAY
                            );
                        }
                    }
            );*/


            if (chargesList2.getDiscountValue() != null && !chargesList2.getDiscountValue().isEmpty()) {
                holder.edittext.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList2.getDiscountValue())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            if (chargesList2.getDiscountAmountValue() != null && !chargesList2.getDiscountAmountValue().isEmpty()) {
                holder.chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList2.getDiscountAmountValue())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chargesList2List.size();
    }

    public interface QuotationCharges2AdapterListener {
        void onQuotationChargeTwoDiscount(int position, String discountValue, ChargesList2 chargesList2);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private TextView titleTextView;
        //  private TextInputLayout textInputLayout;
        private EditText edittext;
        private RelativeLayout spinnerLayout;
        private Spinner chargeTypeSpinner;
        private TextView chargeValueTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            titleTextView = (TextView) view.findViewById(R.id.title_textView);
            //    textInputLayout = (TextInputLayout) view.findViewById(R.id.textInputLayout);
            edittext = (EditText) view.findViewById(R.id.edittext);
            spinnerLayout = (RelativeLayout) view.findViewById(R.id.spinner_layout);
            chargeTypeSpinner = (Spinner) view.findViewById(R.id.charge_type_spinner);
            chargeValueTextView = (TextView) view.findViewById(R.id.charge_value_textView);

            spinnerLayout.setVisibility(View.GONE);
            chargeValueTextView.setText("0");
            edittext.setText("0");
            edittext.setSelection(1);
        }
    }
}
