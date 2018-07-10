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
import com.accrete.sixorbit.model.Vendors;

import java.text.DecimalFormat;
import java.util.List;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_CUSTOMER_MOBILE_CALL_ADAPTER;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by agt on 6/11/17.
 */

public class VendorsMainFragmentAdapter extends RecyclerView.Adapter<VendorsMainFragmentAdapter.MyViewHolder> {
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private Context mContext;
    private List<Vendors> vendorsList;
    private VendorsListener listener;
    private String mobileNumber, customerEmail;
    private Typeface tf;
    private String CopiedText = "";

    public VendorsMainFragmentAdapter(Context mContext, List<Vendors> vendorsList, VendorsListener listener) {
        this.mContext = mContext;
        this.vendorsList = vendorsList;
        this.listener = listener;
        binderHelper.setOpenOnlyOne(true);
        if (mContext != null && tf == null) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "font/Corbert-Regular.otf");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_customers, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            //Please bind Swipe Reveal layout always in the first line of this method to remove issue of losing data onscroll
            binderHelper.bind(holder.swipeLayout, String.valueOf(holder.getAdapterPosition()));

            final Vendors vendors = vendorsList.get(position);
            holder.imageViewSync.setEnabled(false);

            if (vendors.getName() != null && !vendors.getName().isEmpty()) {
                holder.textViewName.setText("" + WordUtils.capitalize(vendors.getName().toString().trim()));
                if (vendors.getName().length() > 26) {
                    holder.textViewName.setText(holder.textViewName.getText().toString().substring(0, 23) + "...");
                }
            }

            //Email
            if (vendors.getVendorEmail() != null && !vendors.getVendorEmail().isEmpty()) {
                holder.textViewContactEmail.setText(vendors.getVendorEmail());
                holder.textViewContactEmail.setVisibility(View.VISIBLE);
            } else {
                holder.textViewContactEmail.setVisibility(View.INVISIBLE);
            }

            //Contact Number
            if (vendors.getMobile() != null && !vendors.getMobile().isEmpty()) {
                holder.textViewContactNumber.setText(vendors.getMobile());
                holder.textViewContactNumber.setVisibility(View.VISIBLE);
            } else {
                holder.textViewContactNumber.setVisibility(View.GONE);
            }

            if ((vendors.getVendorEmail() != null && !vendors.getVendorEmail().isEmpty()) ||
                    (vendors.getMobile() != null && !vendors.getMobile().isEmpty())) {
                holder.emailLayout.setVisibility(View.VISIBLE);
            } else {
                holder.emailLayout.setVisibility(View.INVISIBLE);
            }

            double amount = Double.parseDouble(vendors.getWalletBalance());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

            holder.textViewWAmount.setText(mContext.getString(R.string.Rs) + " " + formatter.format(amount));
            if (Double.parseDouble(vendors.getWalletBalance()) < 0) {
                holder.textViewWAmount.setTextColor(mContext.getResources().getColor(R.color.green));
            } else {
                holder.textViewWAmount.setTextColor(mContext.getResources().getColor(R.color.lightRed));
            }

            applyClickEvents(holder, position, vendors.getStid());
       /* holder.textViewWAmount.setText(mContext.getString(R.string.Rs) + " " +
                String.format("%.0f", Double.parseDouble(customers.getWalletBalance())));*/

            holder.textViewEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //CustomisedToast.success(mContext, "email" + position).show();
                    if (vendors.getVendorEmail().isEmpty() || vendors.getVendorEmail() == null) {
                        //CustomisedToast.error(mContext,"No email Id available").show();
                        Snackbar snackbar = Snackbar
                                .make(holder.swipeLayout, mContext.getString(R.string.email_error), Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundResource(R.color.red);
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                    } else {
                        if (vendors.getName() != null && !vendors.getName().isEmpty()) {
                            CopiedText = CopiedText + "Name : " + WordUtils.capitalize(vendors.getName()) + "\n";
                        }
                        if (vendors.getVendorEmail() != null && !vendors.getVendorEmail().isEmpty()) {
                            CopiedText = CopiedText + "Email : " + vendors.getVendorEmail() + "\n";
                        }
                        if (vendors.getMobile() != null && !vendors.getMobile().isEmpty()) {
                            CopiedText = CopiedText + "Phone : " + vendors.getMobile() + "\n";
                        }
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{vendors.getVendorEmail()});
                        email.putExtra(Intent.EXTRA_SUBJECT, "Vendor's Details");
                        email.setType("plain/text");
                        email.putExtra(android.content.Intent.EXTRA_TEXT, CopiedText);
                        mContext.startActivity(Intent.createChooser(email, mContext.getString(R.string.choose_email_client)));
                    }
                }
            });

            holder.textViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //CustomisedToast.success(mContext, "call").show();
                    if (vendors.getMobile().isEmpty() || vendors.getMobile() == null) {
                        Snackbar snackbar = Snackbar
                                .make(holder.swipeLayout, mContext.getString(R.string.phone_number_not_valid_error), Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundResource(R.color.red);
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();

                    } else {
                        String customerMobile = vendors.getMobile();
                        listener.vendorsMobile(customerMobile);
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
                }
            });
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
        return vendorsList.size();
    }

    public void updateList(List<Vendors> list) {
        vendorsList = list;
        notifyDataSetChanged();
    }

    public interface VendorsListener {
        void onMessageRowClicked(int position);

        void vendorsMobile(String mobile);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAlphabet, textViewName, textViewContactNumber, textViewContactEmail, textViewWAmount;
        public ImageView imageViewStatus;
        public ImageView imgProfile, textViewCall, textViewEmail, imageViewSync;
        private SwipeRevealLayout swipeLayout;
        private LinearLayout linearLayoutContainer;
        private LinearLayout emailLayout;

        public MyViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
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
            emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);

            //Typefaces
         /*   textViewName.setTypeface(tf);
            textViewContactEmail.setTypeface(tf);
            textViewContactNumber.setTypeface(tf);
            textViewAlphabet.setTypeface(tf);*/
        }
    }

}