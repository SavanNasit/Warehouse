package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.ChargesList;
import com.accrete.sixorbit.model.TaxList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;

/**
 * Created by agt on 22/12/17.
 */

public class QuotationChargesListAdapter extends RecyclerView.Adapter<QuotationChargesListAdapter.MyViewHolder> {
    private Activity activity;
    private List<ChargesList> chargesLists;
    private QuotationChargesAdapterListener listener;
    private ArrayList<TaxList> taxListArrayList;
    private String flag;
    private TaxSpinnerArrayAdapter taxSpinnerArrayAdapter;

    public QuotationChargesListAdapter(Activity activity, List<ChargesList> chargesLists, ArrayList<TaxList> taxListArrayList,
                                       String flag, QuotationChargesAdapterListener listener) {
        this.activity = activity;
        this.chargesLists = chargesLists;
        this.taxListArrayList = taxListArrayList;
        this.flag = flag;
        this.listener = listener;
        if (flag != null && flag.equals("1")) {
            //taxSpinnerArrayAdapter = new TaxSpinnerArrayAdapter(activity, R.layout.simple_spinner_item, taxListArrayList);
        }
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
            final ChargesList chargesList = chargesLists.get(position);
            holder.titleTextView.setText(chargesList.getTitle());
            holder.edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (flag != null && !flag.equals("1")) {
                            if (holder.edittext.getText().toString().trim() != null && holder.edittext.getText().toString().length() != 0) {
                                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                                    holder.chargeValueTextView.setText(holder.edittext.getText().toString().trim());
                                    listener.onQuotationChargeDiscountChange(position, holder.edittext.getText().toString().trim(), chargesList);
                                } else {
                                    listener.onQuotationChargeDiscountChange(position, holder.edittext.getText().toString().trim(), chargesList);
                                }
                            } else {
                                holder.chargeValueTextView.setText("0.00");
                                listener.onQuotationChargeDiscountChange(position, "0.00", chargesList);

                            }
                        } else {
                            if (holder.edittext.getText().toString().trim() != null && holder.edittext.getText().toString().length() != 0
                                    && holder.chargeTypeSpinner.getSelectedItemPosition() == 0) {
                                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                                    holder.chargeValueTextView.setText(holder.edittext.getText().toString().trim());
                                    listener.onQuotationChargeDiscountChange(position, holder.edittext.getText().toString().trim(), chargesList);
                                } else {
                                    listener.onQuotationChargeDiscountChange(position, holder.edittext.getText().toString().trim(), chargesList);
                                }
                            } else {
                                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {

                                /*holder.chargeValueTextView.setText("" + Constants.roundTwoDecimals(Constants.ParseDouble(holder.edittext.getText().toString().trim())
                                        + ((Constants.ParseDouble(holder.edittext.getText().toString()) *
                                        Constants.ParseDouble(taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue()))
                                        / 100)));*/

                                    double inputValue = Constants.ParseDouble(holder.edittext.getText().toString().trim());
                                    double taxValue = (Constants.ParseDouble(taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition())
                                            .getValue()) * inputValue) / 100;
                                    double totalValue = inputValue + taxValue;

                                    double b = ParseDouble(taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getButaprid());
                                    switch ((int) b) {
                                        case 1:
                                            totalValue = Math.round((totalValue * 100) / 100);
                                            break;
                                        case 2:
                                            totalValue = (totalValue);
                                            break;
                                        case 3:
                                            totalValue = Math.ceil(totalValue);
                                            break;
                                        case 4:
                                            totalValue = Math.floor(totalValue);
                                            break;
                                        default:
                                            totalValue = (totalValue * 100) / 100;
                                            break;
                                    }
                                    holder.chargeValueTextView.setText("" + new BigDecimal(totalValue)
                                            .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                    listener.onPreChargeDiscountTaxChange(position, holder.edittext.getText().toString().trim(), chargesList,
                                            taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue(),
                                            holder.chargeTypeSpinner.getSelectedItemPosition());
                                } else {
                                    listener.onPreChargeDiscountTaxChange(position, holder.edittext.getText().toString().trim(), chargesList,
                                            taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue(),
                                            holder.chargeTypeSpinner.getSelectedItemPosition());
                               /* activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(position);
                                    }
                                });*/
                                }
                            }
                        }
                    } else if (chargesLists != null && chargesLists.size() > 1 && position == 0) {
                        holder.edittext.requestFocus();
                    }
                }
            });

            holder.chargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                    if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                        double inputValue = Constants.ParseDouble(holder.edittext.getText().toString().trim());
                        double taxValue = (Constants.ParseDouble(taxListArrayList.get(i).getValue()) * inputValue) / 100;
                        double totalValue = inputValue + taxValue;

                        double b = ParseDouble(taxListArrayList.get(i).getButaprid());
                        switch ((int) b) {
                            case 1:
                                totalValue = Math.floor((totalValue * 100) / 100);
                                break;
                            case 2:
                                totalValue = (totalValue);
                                break;
                            case 3:
                                totalValue = Math.ceil(totalValue);
                                break;
                            case 4:
                                totalValue = Math.floor(totalValue);
                                break;
                            default:
                                totalValue = (totalValue * 100) / 100;
                                break;
                        }

                        holder.chargeValueTextView.setText("" + new BigDecimal(totalValue)
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                        listener.onPreChargeDiscountTaxChange(position, holder.edittext.getText().toString().trim(), chargesList,
                                taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue(),
                                holder.chargeTypeSpinner.getSelectedItemPosition());
                    } else if (chargesList.getEctid() != null && chargesList.getEctid().equals("1")) {
                        listener.onPreChargeDiscountTaxChange(position, holder.edittext.getText().toString().trim(), chargesList,
                                taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue(),
                                holder.chargeTypeSpinner.getSelectedItemPosition());

                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(position);
                        }
                    });*/

                    } else {
                    /*if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                        holder.chargeValueTextView.setText(holder.edittext.getText().toString().trim());
                        listener.onQuotationChargeDiscountChange(position, holder.edittext.getText().toString().trim(), chargesList);
                    } else {*/
                        listener.onPreChargeDiscountTaxChange(position, holder.edittext.getText().toString().trim(), chargesList,
                                taxListArrayList.get(holder.chargeTypeSpinner.getSelectedItemPosition()).getValue(),
                                holder.chargeTypeSpinner.getSelectedItemPosition());
                        //   }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (chargesList.getDiscountAmountValue() != null && !chargesList.getDiscountAmountValue().isEmpty()) {
                holder.chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList.getDiscountAmountValue())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            if (chargesList.getDiscountValue() != null && !chargesList.getDiscountValue().isEmpty()) {
                holder.edittext.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList.getDiscountValue())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }

            if (flag.equals("1")) {
                holder.spinnerLayout.setVisibility(View.VISIBLE);
          /*  taxSpinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
            holder.chargeTypeSpinner.setAdapter(taxSpinnerArrayAdapter);*/
                ArrayAdapter<TaxList> taxListArrayAdapter =
                        new ArrayAdapter<TaxList>(activity, R.layout.simple_spinner_item, taxListArrayList);
                taxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                holder.chargeTypeSpinner.setAdapter(taxListArrayAdapter);

                holder.chargeTypeSpinner.setSelection(chargesList.getSelectedIndex());
            } else {
                holder.spinnerLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return chargesLists.size();
    }

    public interface QuotationChargesAdapterListener {
        void onQuotationChargeDiscountChange(int position, String discountValue, ChargesList chargesList);

        void onPreChargeDiscountTaxChange(int position, String discountValue, ChargesList chargesList, String taxValue,
                                          int positionSpinner);
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
            // textInputLayout = (TextInputLayout) view.findViewById(R.id.textInputLayout);
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