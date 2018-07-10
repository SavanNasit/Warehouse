package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.AddressList;

import java.util.List;

/**
 * Created by agt on 28/12/17.
 */

public class SiteAddressAdapter extends RecyclerView.Adapter<SiteAddressAdapter.MyViewHolder> {
    private Activity activity;
    private List<AddressList> addressLists;
    private SiteAddressItemClickListener listener;
    private int lastCheckedPosition = -1;
    private String addressType;

    public SiteAddressAdapter(Activity activity, List<AddressList> addressLists,
                              SiteAddressItemClickListener listener, String addressType) {
        this.activity = activity;
        this.addressLists = addressLists;
        this.listener = listener;
        this.addressType = addressType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_radio_group, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final AddressList addressList = addressLists.get(position);
        String addressText = "";
        if (addressList.getSiteName() != null && !addressList.getSiteName().toString().trim().isEmpty()) {
            holder.addressNameTextView.setText(addressText + addressList.getSiteName().toString().trim() + "");
            holder.addressNameTextView.setVisibility(View.VISIBLE);
        } else {
            holder.addressNameTextView.setVisibility(View.GONE);
        }
        if (addressList.getLine1() != null && !addressList.getLine1().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine1().toString().trim() + ", ";
        }
        if (addressList.getLine2() != null && !addressList.getLine2().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine2().toString().trim() + ", ";
        }
        if (addressList.getCity() != null && !addressList.getCity().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCity().toString().trim() + "";
        }
        /*if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()
                && (addressList.getCity() == null || !addressList.getCity().toString().trim().isEmpty())) {
            addressText = addressText + addressList.getZipCode().toString().trim() + ", ";
        } else {
            if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
                addressText = addressText + " - " + addressList.getZipCode().toString().trim() + ", ";
            }
        }*/
        if (addressList.getState() != null && !addressList.getState().toString().trim().isEmpty() &&
                !addressText.isEmpty()) {
            addressText = addressText + "\n" + addressList.getState().toString().trim() + ", ";
        } else {
            addressText = addressText + "" + addressList.getState().toString().trim() + ", ";
        }
        if (addressList.getCountry() != null && !addressList.getCountry().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCountry().toString().trim() + "";
        }
        if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
            addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "";
        }
        holder.addressTextView.setText(addressText);
      /* if(position == lastCheckedPosition){
           holder.radioBtnImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.radio_btn_blue_on));
       }*/
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.siteAddressItemClick(position);
            }
        });

        if (addressList.isCheckedSite()) {
            holder.radioBtnImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.radio_btn_blue_on));
        } else {
            holder.radioBtnImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.radio_btn_blue_off));
        }

        if (addressList.getPhoneNo() != null && !addressList.getPhoneNo().isEmpty()) {
            holder.phoneTextView.setText(addressList.getPhoneNo());
            holder.phoneTextView.setVisibility(View.VISIBLE);
        } else {
            holder.phoneTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addressLists.size();
    }

    public interface SiteAddressItemClickListener {
        void siteAddressItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //    private RadioGroup radioGroup;
        //     private RadioButton radioButton;
        private LinearLayout mainLayout;
        private ImageView radioBtnImageView;
        private TextView addressTextView, phoneTextView, addressNameTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            //    radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
            //    radioButton = (RadioButton) view.findViewById(R.id.radioButton);
            addressTextView = (TextView) view.findViewById(R.id.address_textView);
            addressNameTextView = (TextView) view.findViewById(R.id.address_name_textView);
            phoneTextView = (TextView) view.findViewById(R.id.phone_textView);
            radioBtnImageView = (ImageView) view.findViewById(R.id.radio_btn_imageView);
            radioBtnImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                    //because of this blinking problem occurs so
                    //i have a suggestion to add notifyDataSetChanged();
                    //   notifyItemRangeChanged(0, list.length);//blink list problem
                    //  notifyDataSetChanged();
                    listener.siteAddressItemClick(getAdapterPosition());
                }
            });

        }
    }
}
