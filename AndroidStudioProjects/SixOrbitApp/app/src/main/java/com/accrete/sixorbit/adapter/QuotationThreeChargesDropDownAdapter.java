package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ChargesList3;
import com.accrete.sixorbit.model.TaxList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 1/3/18.
 */

public class QuotationThreeChargesDropDownAdapter extends RecyclerView.Adapter<QuotationThreeChargesDropDownAdapter.MyViewHolder> {
    private Activity activity;
    private List<ChargesList3> chargesLists3;
    private QuotationChargesThreeListener listener;
    private ArrayList<TaxList> taxListArrayList;
    private String flag;

    public QuotationThreeChargesDropDownAdapter(Activity activity, List<ChargesList3> chargesLists3, ArrayList<TaxList> taxListArrayList,
                                                String flag, QuotationChargesThreeListener listener) {
        this.activity = activity;
        this.chargesLists3 = chargesLists3;
        this.taxListArrayList = taxListArrayList;
        this.flag = flag;
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
        final ChargesList3 chargesList3 = chargesLists3.get(position);
        holder.titleTextView.setText(chargesList3.getTitle());

        holder.edittext.setVisibility(View.GONE);

        /*if (chargesList3.getDiscountAmountValue() != null && !chargesList3.getDiscountAmountValue().isEmpty()) {
            holder.chargeValueTextView.setText("" + Constants.roundTwoDecimals(Constants.ParseDouble(chargesList3.getDiscountAmountValue())));
        }

        if (chargesList3.getDiscountValue() != null && !chargesList3.getDiscountValue().isEmpty()) {
            holder.edittext.setText(chargesList3.getDiscountValue() + "");
        }*/

        if (flag.equals("1")) {
            holder.spinnerLayout.setVisibility(View.VISIBLE);
            ArrayAdapter<TaxList> taxListArrayAdapter =
                    new ArrayAdapter<TaxList>(activity, R.layout.simple_spinner_item, taxListArrayList);
            taxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            holder.chargeTypeSpinner.setAdapter(taxListArrayAdapter);
        } else {
            holder.spinnerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chargesLists3.size();
    }

    public interface QuotationChargesThreeListener {
        void QuotationChargesThreeListener(int position, String discountValue, ChargesList3 chargesList3);
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