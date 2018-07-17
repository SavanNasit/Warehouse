package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.TaxList;

import java.util.ArrayList;

/**
 * Created by agt on 1/3/18.
 */

public class TaxSpinnerArrayAdapter extends ArrayAdapter<TaxList> {
    private TaxList taxList;
    private Context context;
    private int layoutId;
    private ArrayList<TaxList> itemsArrayList;

    public TaxSpinnerArrayAdapter(Context context, int layoutId, ArrayList<TaxList> itemsArrayList) {
        super(context, layoutId, itemsArrayList);
        this.context = context;
        this.layoutId = layoutId;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public TaxList getItem(int position) {
        return itemsArrayList.get(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            taxList = new TaxList();
            holder.textView = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TaxList taxList = (TaxList) itemsArrayList.get(position);
        holder.textView.setText(taxList.getName());
        return convertView;
    }

    private class ViewHolder {
        public TextView textView;
    }

}