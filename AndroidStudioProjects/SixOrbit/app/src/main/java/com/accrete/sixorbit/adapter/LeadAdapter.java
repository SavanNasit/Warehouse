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
import android.os.Handler;
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
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.SwipeRevealLayout;
import com.accrete.sixorbit.helper.ViewBinderHelper;
import com.accrete.sixorbit.interfaces.SyncLeadData;
import com.accrete.sixorbit.interfaces.sendLeadMobileNumber;
import com.accrete.sixorbit.model.Enquiry;
import com.accrete.sixorbit.model.Lead;

import java.util.HashMap;
import java.util.List;

import static com.accrete.sixorbit.adapter.FollowUpAdapter.getColor;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_LEAD_CALL_ADAPTER;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 21/6/17.
 */

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.MyViewHolder> {
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    public SyncLeadData syncLeadData;
    HashMap<String, Integer> alphaIndexer;
    String[] sections;
    sendLeadMobileNumber dtInterface;
    private Context mContext;
    private List<Lead> leads;
    private List<Enquiry> enquiries;
    private LeadAdapterListener listener;
    private Typeface tf;
    private DatabaseHandler db;
    private Lead sendLeads = new Lead();

    public LeadAdapter(Context mContext, List<Lead> leads, LeadAdapterListener listener, sendLeadMobileNumber sendLeadMobileNumber) {
        this.mContext = mContext;
        this.leads = leads;
        this.listener = listener;
        binderHelper.setOpenOnlyOne(true);
        this.dtInterface = sendLeadMobileNumber;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_lead, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //Please bind Swipe Reveal layout always in the first line of this method to remove issue of losing data on scroll
        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        db = new DatabaseHandler(mContext);
        final Lead lead = leads.get(position);

        holder.imageViewSync.setEnabled(false);
        tf = Typeface.createFromAsset(mContext.getAssets(), "font/Corbert-Regular.otf");
        if (sendLeads.getLeadSync() != null && sendLeads.getLeadSync().equals(mContext.getString(R.string.str_true))) {
            // holder.imageViewSync.setImageResource(R.drawable.ic_tick);
            //  holder.imageViewSync.setEnabled(false);
        } else {
            // holder.imageViewSync.setImageResource(R.drawable.ic_sync);
            // holder.imageViewSync.setEnabled(true);
        }


        if (lead.getName().length() > 25) {
            holder.textViewName.setText(lead.getName().substring(0, 22) + "...");
        } else {
            holder.textViewName.setText(lead.getName());
        }
        String strNameTV = lead.getName().toUpperCase().trim();
        //String strName = lead.getName().toString().trim();

        if (strNameTV.equals("")) {
            holder.textViewAlphabet.setText("");
            holder.textViewAlphabet.setVisibility(View.INVISIBLE);
        } else {
            if (strNameTV.length() > 0) {
                holder.textViewAlphabet.setText(strNameTV.charAt(0) + "");
                holder.textViewAlphabet.setVisibility(View.VISIBLE);
            }

        }

        if (lead.getLeasid().equals("1")) {
            holder.imageViewStatus.setImageResource(R.drawable.ic_lead_status_accent);
        } else if (lead.getLeasid().equals("2")) {
            holder.imageViewStatus.setImageResource(R.drawable.ic_lead_status_light_orange);
        } else if (lead.getLeasid().equals("3")) {
            holder.imageViewStatus.setImageResource(R.drawable.ic_lead_status_green);
        } else if (lead.getLeasid().equals("4")) {
            holder.imageViewStatus.setImageResource(R.drawable.ic_lead_status_red);
        }

        if (lead.getMobile() != null && !lead.getMobile().isEmpty()) {
            holder.textViewContactNumber.setText(lead.getMobile());
            holder.textViewContactNumber.setVisibility(View.VISIBLE);
        } else {
            holder.textViewContactNumber.setVisibility(View.GONE);
        }
        if (lead.getEmail() != null && !lead.getEmail().isEmpty()) {
            holder.textViewContactEmail.setText(lead.getEmail());
            holder.textViewContactEmail.setVisibility(View.VISIBLE);
        } else { 
            holder.textViewContactEmail.setVisibility(View.GONE);
        }

        holder.imgProfile.setImageResource(R.drawable.bg_square);
        if (strNameTV.length() > 0) {
            holder.imgProfile.setColorFilter(getColor(strNameTV.substring(0, 1), mContext));
            holder.imgProfile.setVisibility(View.VISIBLE);
        } else {
            holder.imgProfile.setColorFilter(getColor("", mContext));
            holder.imgProfile.setVisibility(View.INVISIBLE);
        }

        holder.textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomisedToast.success(mContext, "email" + position).show();
                if (lead.getEmail().isEmpty() || lead.getEmail() == null) {
                    //CustomisedToast.error(mContext,"No email Id available").show();
                    Snackbar snackbar = Snackbar
                            .make(holder.swipeLayout, mContext.getString(R.string.email_error), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.red);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{lead.getEmail()});
                    // email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    // email.putExtra(Intent.EXTRA_TEXT, "message");
                    email.setType("plain/text");
                    mContext.startActivity(Intent.createChooser(email, mContext.getString(R.string.choose_email_client)));
                }
            }
        });

        holder.textViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomisedToast.success(mContext, "call").show();
                if (lead.getMobile().isEmpty() || lead.getMobile() == null) {
                    Snackbar snackbar = Snackbar
                            .make(holder.swipeLayout, mContext.getString(R.string.phone_number_not_valid_error), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.red);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();

                } else {
                    String leadMobile = lead.getMobile();
                    dtInterface.sendLeadMobileNumber(leadMobile);
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + lead.getMobile()));
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionWithRationale((Activity) mContext, new FollowUpsFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_LEAD_CALL_ADAPTER)) {
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
                        if (leadMobile != null && !leadMobile.isEmpty()) {
                            mContext.startActivity(intentCall);
                        } else {
                            CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    holder.textViewCall.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.textViewCall.setEnabled(true);
                        }
                    }, 2000);
                }
            }
        });

        applyClickEvents(holder, position, lead.getSyncId());


    }

    private void applyClickEvents(MyViewHolder holder, final int position, final String id) {
        holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.imageViewSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSyncClicked(id);

            }
        });
    }

    @Override
    public int getItemCount() {

        return leads.size();
    }

    public void sendDataToSync() {
        syncLeadData.sendLeadDataToSync();
    }


    public interface LeadAdapterListener {
        void onMessageRowClicked(int position);

        void onSyncClicked(String id);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAlphabet, textViewName, textViewContactNumber, textViewContactEmail;
        public ImageView imageViewStatus;
        public ImageView imgProfile, textViewCall, textViewEmail, imageViewSync;
        private SwipeRevealLayout swipeLayout;
        private LinearLayout linearLayoutContainer;

        public MyViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            textViewAlphabet = (TextView) view.findViewById(R.id.lead_alphabet);
            textViewName = (TextView) view.findViewById(R.id.lead_name);
            textViewContactEmail = (TextView) view.findViewById(R.id.lead_contacts_email);
            textViewContactNumber = (TextView) view.findViewById(R.id.lead_contact_number);
            imageViewStatus = (ImageView) view.findViewById(R.id.lead_status);
            imgProfile = (ImageView) view.findViewById(R.id.lead_alphabet_bg);
            textViewCall = (ImageView) view.findViewById(R.id.lead_call);
            textViewEmail = (ImageView) view.findViewById(R.id.lead_email);
            linearLayoutContainer = (LinearLayout) view.findViewById(R.id.lead_container);
            imageViewSync = (ImageView) view.findViewById(R.id.lead_sync);
        }
    }

}