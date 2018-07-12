package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.AddressList;

import java.util.ArrayList;

/**
 * Created by agt on 22/12/17.
 */

public class AddressCustomAdapter extends ArrayAdapter<AddressList> {
    private Activity activity;
    private int layoutResourceId, textViewId;
    private ArrayList<AddressList> addressListArrayList;

    public AddressCustomAdapter(Activity activity, int layoutResourceId, int textViewId,
                                ArrayList<AddressList> addressListArrayList) {
        super(activity, layoutResourceId, addressListArrayList);
        this.layoutResourceId = layoutResourceId;
        this.activity = activity;
        this.textViewId = textViewId;
        this.addressListArrayList = addressListArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder = null;
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.item);
            // get the TextView and then set the text (item name) and tag (item ID) values
            if (addressListArrayList.get(position).getSiteName() != null && !addressListArrayList.get(position).getSiteName().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getSiteName().toString().trim() + "\n");
            }
            if (addressListArrayList.get(position).getLine1() != null && !addressListArrayList.get(position).getLine1().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getLine1().toString().trim() + "\n");
            }
            if (addressListArrayList.get(position).getLine2() != null && !addressListArrayList.get(position).getLine2().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getLine2().toString().trim() + "\n");
            }
            if (addressListArrayList.get(position).getCity() != null && !addressListArrayList.get(position).getCity().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getCity().toString().trim() + "");
            }
            if (addressListArrayList.get(position).getZipCode() != null && !addressListArrayList.get(position).getZipCode().toString().trim().isEmpty()
                    && (addressListArrayList.get(position).getCity() == null || !addressListArrayList.get(position).getCity().toString().trim().isEmpty())) {
                viewHolder.textView.append(addressListArrayList.get(position).getZipCode().toString().trim() + "\n");
            } else {
                if (addressListArrayList.get(position).getZipCode() != null && !addressListArrayList.get(position).getZipCode().toString().trim().isEmpty()) {
                    viewHolder.textView.append(" - " + addressListArrayList.get(position).getZipCode().toString().trim() + "\n");
                }
            }
            if (addressListArrayList.get(position).getState() != null && !addressListArrayList.get(position).getState().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getState().toString().trim() + "\n");
            }
            if (addressListArrayList.get(position).getCountry() != null && !addressListArrayList.get(position).getCountry().toString().trim().isEmpty()) {
                viewHolder.textView.append(addressListArrayList.get(position).getCountry().toString().trim() + "\n");
            }


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

    public class ViewHolder {
        TextView textView;
    }

}