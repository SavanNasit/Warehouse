package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ChargesList3;

import java.util.ArrayList;

/**
 * Created by agt on 25/12/17.
 */

public class QuotationChargesThreeSpinnerCheckAdapter extends ArrayAdapter<ChargesList3> implements View.OnClickListener {
    private Activity activity;
    private int layoutResourceId;
    private ArrayList<ChargesList3> list3ArrayList;
    private ArrayList<Boolean> checks = new ArrayList<Boolean>();
    private QuotationChargesThreeSpinnerCheckAdapter myAdapter;
    private QuotationChargesThreeAdapterListener listener;

    public QuotationChargesThreeSpinnerCheckAdapter(Activity activity, int layoutResourceId, ArrayList<ChargesList3> list3ArrayList,
                                                    QuotationChargesThreeAdapterListener listener) {
        super(activity, layoutResourceId, list3ArrayList);
        this.layoutResourceId = layoutResourceId;
        this.activity = activity;
        this.list3ArrayList = list3ArrayList;
        this.myAdapter = this;
        this.listener = listener;
        for (int i = 0; i < list3ArrayList.size(); i++) {
            checks.add(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(final int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.spinner_row_checkbox, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // get the TextView and then set the text (item name) and tag (item ID) values
            viewHolder.textView.setText(list3ArrayList.get(position).getTitle());

            if ((position == 0)) {
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }


            viewHolder.checkBox.setTag(Integer.valueOf(position));

            // Set a listener for the checkbox
            viewHolder.checkBox.setOnClickListener(this);

            //Sets the state of CB, since we have the list of checked CB
            viewHolder.checkBox.setChecked(checks.get(position));


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        boolean state = checks.get(index.intValue());
        checks.set(index.intValue(), !state);
        listener.onQuotationChargeThree(index, !state);
    }

    public interface QuotationChargesThreeAdapterListener {
        void onQuotationChargeThree(int position, boolean state);
    }

    public class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

}