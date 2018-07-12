package com.accrete.sixorbit.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.SwipeRevealLayout;
import com.accrete.sixorbit.helper.ViewBinderHelper;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.model.Customers;

import java.text.DecimalFormat;
import java.util.List;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_CUSTOMER_MOBILE_CALL_ADAPTER;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 1/11/17.
 */

public class CustomerMainFragmentAdapter extends RecyclerView.Adapter<CustomerMainFragmentAdapter.MyViewHolder> {
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private Context mContext;
    private List<Customers> customersList;
    private CustomersListener listener;
    private String mobileNumber, customerEmail;
    private Typeface tf;
    private String CopiedText = "";

    public CustomerMainFragmentAdapter(Context mContext, List<Customers> customersList, CustomersListener listener) {
        this.mContext = mContext;
        this.customersList = customersList;
        this.listener = listener;
        binderHelper.setOpenOnlyOne(true);
        if (mContext != null) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "font/Corbert-Regular.otf");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_customers, parent, false);

        return new MyViewHolder(itemView);
    }

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {
            //Please bind Swipe Reveal layout always in the first line of this method to remove issue of losing data ons croll
            binderHelper.bind(holder.swipeLayout, String.valueOf(holder.getAdapterPosition()));

            //  int safePosition = holder.getAdapterPosition();

            final Customers customers = customersList.get(position);
            holder.imageViewSync.setEnabled(false);
            if (customers.getCompanyName() != null && !customers.getCompanyName().isEmpty()) {
                holder.textViewName.setText("" + WordUtils.capitalize(customers.getCompanyName().toString().trim()));
                if (customers.getCompanyName().length() > 25) {
                    holder.textViewName.setText(holder.textViewName.getText().toString().substring(0, 22) + "...");
                }
            } else if (customers.getFname() != null && !customers.getFname().isEmpty() && customers.getLname() == null || customers.getLname().isEmpty()) {
                holder.textViewName.setText("" + WordUtils.capitalize(customers.getFname().toString().trim()));
                if (customers.getFname().length() > 25) {
                    holder.textViewName.setText(holder.textViewName.getText().toString().substring(0, 22) + "...");
                }
            } else if (customers.getLname() != null && !customers.getLname().isEmpty() && customers.getFname() == null || customers.getFname().isEmpty()) {
                holder.textViewName.setText("" + WordUtils.capitalize(customers.getLname().toString().trim()));
                if (customers.getLname().length() > 25) {
                    holder.textViewName.setText(holder.textViewName.getText().toString().substring(0, 22) + "...");
                }
            } else if (customers.getFname() != null && !customers.getFname().isEmpty() && customers.getLname() != null && !customers.getLname().isEmpty()) {
                holder.textViewName.setText("" + WordUtils.capitalize(customers.getFname().toString().trim()) + " " +
                        WordUtils.capitalize(customers.getLname().toString().trim()));
                if ((customers.getFname() + customers.getLname()).length() > 25) {
                    holder.textViewName.setText(holder.textViewName.getText().toString().substring(0, 22) + "...");
                }
            }

            //Email
            if (customers.getEmail() != null && !customers.getEmail().isEmpty()) {
                holder.textViewContactEmail.setText(customers.getEmail());
                holder.textViewContactEmail.setVisibility(View.VISIBLE);
            } else {
                holder.textViewContactEmail.setVisibility(View.INVISIBLE);
            }

            //Contact Number
            if (customers.getMobile() != null && !customers.getMobile().isEmpty()) {
                holder.textViewContactNumber.setText(customers.getMobile());
                holder.textViewContactNumber.setVisibility(View.VISIBLE);
            } else {
                holder.textViewContactNumber.setVisibility(View.GONE);
            }

            if ((customers.getEmail() != null && !customers.getEmail().isEmpty()) ||
                    (customers.getMobile() != null && !customers.getMobile().isEmpty())) {
                holder.emailLayout.setVisibility(View.VISIBLE);
            } else {
                holder.emailLayout.setVisibility(View.INVISIBLE);
            }

            applyClickEvents(holder, holder.getAdapterPosition(), customers.getSalutationId());

            double amount = Double.parseDouble(customers.getWalletBalance());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

            holder.textViewWAmount.setText(mContext.getString(R.string.Rs) + " " + formatter.format(amount));

            if (Double.parseDouble(customers.getWalletBalance()) < 0) {
                holder.textViewWAmount.setTextColor(mContext.getResources().getColor(R.color.lightRed));
            } else {
                holder.textViewWAmount.setTextColor(mContext.getResources().getColor(R.color.green));
            }
            holder.textViewEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (customers.getEmail() == null || customers.getEmail().isEmpty()) {
                            Snackbar snackbar = Snackbar
                                    .make(holder.swipeLayout, mContext.getString(R.string.email_error), Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundResource(R.color.red);
                            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        } else {
                            if ((customers.getFname() != null && !customers.getFname().isEmpty()) ||
                                    (customers.getLname() != null && !customers.getLname().isEmpty())) {
                                CopiedText = CopiedText + "Name : " + WordUtils.capitalize(customers.getFname().toString().trim()) +
                                        WordUtils.capitalize(customers.getLname().toString().trim()) + "\n";
                            } else if (customers.getCompanyName() != null && !customers.getCompanyName().isEmpty()) {
                                CopiedText = CopiedText + "Name : " + WordUtils.capitalize(customers.getCompanyName().toString().trim()) + "\n";
                            }
                            if (customers.getEmail() != null && !customers.getEmail().isEmpty()) {
                                CopiedText = CopiedText + "Email : " + customers.getEmail().toString().trim() + "\n";
                            }
                            if (customers.getMobile() != null && !customers.getMobile().isEmpty()) {
                                CopiedText = CopiedText + "Phone : " + customers.getMobile().toString().trim() + "\n";
                            }
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{customers.getEmail().toString().trim()});
                            email.putExtra(Intent.EXTRA_SUBJECT, "Customer's Details");
                            email.setType("plain/text");
                            email.putExtra(Intent.EXTRA_TEXT, CopiedText);
                            mContext.startActivity(Intent.createChooser(email, mContext.getString(R.string.choose_email_client)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.textViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (customers.getMobile() == null || customers.getMobile().isEmpty()) {
                            Snackbar snackbar = Snackbar
                                    .make(holder.swipeLayout, mContext.getString(R.string.phone_number_not_valid_error), Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundResource(R.color.red);
                            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();

                        } else {
                            String customerMobile = customers.getMobile();
                            listener.customerMobile(customerMobile);
                            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (checkPermissionWithRationale((Activity) mContext, new FollowUpsFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CUSTOMER_MOBILE_CALL_ADAPTER)) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    //   mContext.startActivity(intentCall);
                                }
                            } else {
                                if (customerMobile != null && !customerMobile.isEmpty()) {
                                    mContext.startActivity(intentCall);
                                } else {
                                    CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //binderHelper.bind(holder.swipeLayout, customers.toString());
       /* if (holder.swipeLayout.isOpened()) {
            holder.swipeLayout.close(true);
        }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyClickEvents(final MyViewHolder holder, final int position, final String id) {
        holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }

    public void updateList(List<Customers> list) {
        customersList = list;
        notifyDataSetChanged();
    }

    public interface CustomersListener {
        void onMessageRowClicked(int position);

        void customerMobile(String mobile);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAlphabet, textViewName, textViewContactNumber, textViewContactEmail, textViewWAmount;
        public ImageView imageViewStatus;
        public ImageView imgProfile, textViewCall, textViewEmail, imageViewSync;
        private SwipeRevealLayout swipeLayout;
        private LinearLayout linearLayoutContainer, emailLayout;

        public MyViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            emailLayout = (LinearLayout) itemView.findViewById(R.id.email_layout);
            textViewAlphabet = (TextView) view.findViewById(R.id.customer_details_alphabet);
            textViewName = (TextView) view.findViewById(R.id.customer_details_name);
            textViewContactEmail = (TextView) view.findViewById(R.id.customer_details_contacts_email);
            textViewContactNumber = (TextView) view.findViewById(R.id.customer_details_contact_number);
            imageViewStatus = (ImageView) view.findViewById(R.id.customer_details_status);
            imgProfile = (ImageView) view.findViewById(R.id.customer_details_alphabet_bg);
            textViewCall = (ImageView) view.findViewById(R.id.customer_details_call);
            textViewEmail = (ImageView) view.findViewById(R.id.customer_details_email);
            linearLayoutContainer = (LinearLayout) view.findViewById(R.id.customer_details_container);
            imageViewSync = (ImageView) view.findViewById(R.id.customer_details_sync);
            textViewWAmount = (TextView) view.findViewById(R.id.customer_details_amount);
            //Typeface
         /*   textViewName.setTypeface(tf);
            textViewContactEmail.setTypeface(tf);
            textViewContactNumber.setTypeface(tf);
            textViewAlphabet.setTypeface(tf);*/
        }
    }

}