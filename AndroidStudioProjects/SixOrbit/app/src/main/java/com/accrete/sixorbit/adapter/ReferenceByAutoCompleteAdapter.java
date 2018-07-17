package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.customers.AddCustomerActivity;
import com.accrete.sixorbit.model.SearchRefferedDatum;

import java.util.ArrayList;

/**
 * Created by agt on 13/12/17.
 */

public class ReferenceByAutoCompleteAdapter extends ArrayAdapter<SearchRefferedDatum> {
    private Activity activity;
    private int layoutResourceId;
    private ArrayList<SearchRefferedDatum> refferedDatumArrayList;

    public ReferenceByAutoCompleteAdapter(Activity activity, int layoutResourceId,
                                          ArrayList<SearchRefferedDatum> refferedDatumArrayList) {
        super(activity, layoutResourceId, refferedDatumArrayList);
        this.layoutResourceId = layoutResourceId;
        this.activity = activity;
        this.refferedDatumArrayList = refferedDatumArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            SearchRefferedDatum objectItem = refferedDatumArrayList.get(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            LinearLayout nameLayout = (LinearLayout) convertView.findViewById(R.id.name_layout);
            TextView nameTitleTextView = (TextView) convertView.findViewById(R.id.name_title_textView);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_textView);
            LinearLayout companyLayout = (LinearLayout) convertView.findViewById(R.id.company_layout);
            TextView companyTitleTextView = (TextView) convertView.findViewById(R.id.company_title_textView);
            TextView companyTextView = (TextView) convertView.findViewById(R.id.company_textView);
            LinearLayout emailLayout = (LinearLayout) convertView.findViewById(R.id.email_layout);
            TextView emailTitleTextView = (TextView) convertView.findViewById(R.id.email_title_textView);
            TextView emailTextView = (TextView) convertView.findViewById(R.id.email_textView);
            LinearLayout mobileLayout = (LinearLayout) convertView.findViewById(R.id.mobile_layout);
            TextView mobileTitleTextView = (TextView) convertView.findViewById(R.id.mobile_title_textView);
            TextView mobileTextView = (TextView) convertView.findViewById(R.id.mobile_textView);
            LinearLayout typeLayout = (LinearLayout) convertView.findViewById(R.id.type_layout);
            TextView typeTitleTextView = (TextView) convertView.findViewById(R.id.type_title_textView);
            TextView typeTextView = (TextView) convertView.findViewById(R.id.type_textView);

            if (objectItem.getName().toString().trim() != null && !objectItem.getName().toString().trim().isEmpty()) {
                nameLayout.setVisibility(View.VISIBLE);
                nameTextView.setText(objectItem.getName().toString().trim());
            } else {
                nameLayout.setVisibility(View.GONE);
            }

            if (objectItem.getCompanyName().toString().trim() != null && !objectItem.getCompanyName().toString().trim().isEmpty()) {
                companyLayout.setVisibility(View.VISIBLE);
                companyTextView.setText(objectItem.getCompanyName().toString().trim());
            } else {
                companyLayout.setVisibility(View.GONE);
            }

            if (objectItem.getEmail().toString().trim() != null && !objectItem.getEmail().toString().trim().isEmpty()) {
                emailLayout.setVisibility(View.VISIBLE);
                emailTextView.setText(objectItem.getEmail().toString().trim());
            } else {
                emailLayout.setVisibility(View.GONE);
            }

            if (objectItem.getMobile().toString().trim() != null && !objectItem.getMobile().toString().trim().isEmpty()) {
                mobileLayout.setVisibility(View.VISIBLE);
                mobileTextView.setText(objectItem.getMobile().toString().trim());
            } else {
                mobileLayout.setVisibility(View.GONE);
            }

            if (objectItem.getType().toString().trim() != null && !objectItem.getType().toString().trim().isEmpty()) {
                typeLayout.setVisibility(View.VISIBLE);
                typeTextView.setText(objectItem.getType().toString().trim());
            } else {
                typeLayout.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;

    }
}