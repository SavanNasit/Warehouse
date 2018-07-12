package com.accrete.sixorbit.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.FollowupInfoActivity;
import com.accrete.sixorbit.fragment.Drawer.followups.RecordFollowUpFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadInfoFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.FlipAnimator;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.interfaces.SendFollowUpMobileInterface;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class FollowUpAdapter extends RecyclerView.Adapter<FollowUpAdapter.MyViewHolder> {
    private static int currentSelectedIndex = -1;
    private static int color;
    int count = 0;
    SendFollowUpMobileInterface dtInterface;
    String leadContactMobile;
    String companyCode;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private FragmentManager fragmentManager;
    private Context mContext;
    private List<FollowUp> followUps;
    private FollowUpAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private int mExpandedPosition = -1;
    private RecyclerView r1;
    private Lead contactDetails = new Lead();
    private DatabaseHandler db;
    //private String[] communicationMode = {"Mail", "Phone Call", "Skype Call"};
    private Lead lead = new Lead();
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();

    public FollowUpAdapter(Activity mContext, List<FollowUp> FollowUps, FollowUpAdapterListener listener,
                           RecyclerView recyclerview, FragmentManager fragmentManager, SendFollowUpMobileInterface dtInterface) {
        this.mContext = mContext;
        this.followUps = FollowUps;
        this.listener = listener;
        this.r1 = recyclerview;
        selectedItems = new SparseBooleanArray();
        this.fragmentManager = fragmentManager;
        animationItemsIndex = new SparseBooleanArray();
        this.dtInterface = dtInterface;


        if (mContext != null && AppPreferences.getModes(mContext, AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(mContext, AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }
    }

    public static int getColor(String stringAlphabets, Context mContext) {
        Log.d("colorAlphabets", stringAlphabets);
        switch (stringAlphabets) {
            case "A":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "B":
                color = mContext.getResources().getColor(R.color.blue);
                break;
            case "C":
                color = mContext.getResources().getColor(R.color.green);
                break;
            case "D":
                color = mContext.getResources().getColor(R.color.orange);
                break;
            case "E":
                color = mContext.getResources().getColor(R.color.red);
                break;
            case "F":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "G":
                color = mContext.getResources().getColor(R.color.blue);
                break;
            case "H":
                color = mContext.getResources().getColor(R.color.green);
                break;
            case "I":
                color = mContext.getResources().getColor(R.color.orange);
                break;
            case "J":
                color = mContext.getResources().getColor(R.color.red);
                break;
            case "K":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "L":
                color = mContext.getResources().getColor(R.color.blue);
                break;
            case "M":
                color = mContext.getResources().getColor(R.color.green);
                break;
            case "N":
                color = mContext.getResources().getColor(R.color.orange);
                break;
            case "O":
                color = mContext.getResources().getColor(R.color.red);
                break;
            case "P":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "Q":
                color = mContext.getResources().getColor(R.color.blue);
                break;
            case "R":
                color = mContext.getResources().getColor(R.color.green);
                break;
            case "S":
                color = mContext.getResources().getColor(R.color.orange);
                break;
            case "T":
                color = mContext.getResources().getColor(R.color.red);
                break;
            case "U":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "V":
                color = mContext.getResources().getColor(R.color.blue);
                break;
            case "W":
                color = mContext.getResources().getColor(R.color.green);
                break;
            case "X":
                color = mContext.getResources().getColor(R.color.orange);
                break;
            case "Y":
                color = mContext.getResources().getColor(R.color.red);
                break;
            case "Z":
                color = mContext.getResources().getColor(R.color.blueTurquoise);
                break;
            case "":
                color = mContext.getResources().getColor(R.color.violet);
                break;
        }
        return color;
    }

    public void next(int position) {
        FollowUp FollowUp = followUps.get(position);
        //FollowUp.setRead(true);
        followUps.set(position, FollowUp);

        RecordFollowUpFragment fragment = new RecordFollowUpFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        if (followUps.get(position).getLeadId() != null && !followUps.get(position).getLeadId().isEmpty()) {
            bundle.putString(mContext.getString(R.string.lead_id), followUps.get(position).getLeadId());
        }
        if (followUps.get(position).getFoid() != null && !followUps.get(position).getFoid().isEmpty()) {
            bundle.putString(mContext.getString(R.string.foid), followUps.get(position).getFoid());
        } else if (followUps.get(position).getSyncId() != null && !followUps.get(position).getSyncId().isEmpty()) {
            bundle.putString(mContext.getString(R.string.sync_id), followUps.get(position).getSyncId());
        }
        fragment.setArguments(bundle);
        ft.replace(R.id.frame_container, fragment, mContext.getString(R.string.record_follow_up_fragment_tag));
        ft.addToBackStack(null).commit();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_follow_up, parent, false);
        companyCode = AppPreferences.getCompanyCode(mContext, AppUtils.COMPANY_CODE);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FollowUp FollowUp = followUps.get(position);
        holder.setIsRecyclable(false);
        try {
            // displaying text view data
            db = new DatabaseHandler(mContext);
            lead = db.getLead(FollowUp.getLeadId(), mContext.getString(R.string.leaid));
            //Get Single Lead And Display name. Here we will display updated name
            if (FollowUp.getLeadId() != null && !FollowUp.getLeadId().isEmpty() && !FollowUp.getLeadId().equals("null") &&
                    lead.getName() != null && !lead.getName().isEmpty()) {
                if (FollowUp.getFollowupCommunicationMode() != null && !FollowUp.getFollowupCommunicationMode().isEmpty()
                        && !FollowUp.getFollowupCommunicationMode().equals("null")) {
                    if (lead.getName() != null && !lead.getName().isEmpty()
                            && !lead.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode() + " with " +
                                lead.getName().toString().trim()));
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode() + " with " +
                                FollowUp.getContactPerson().toString().trim()));
                    } else {
                        holder.from
                                .setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode()));
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else if (FollowUp.getCommid() != null && !FollowUp.getCommid().isEmpty()) {
                    if (lead.getName() != null && !lead.getName().isEmpty()
                            && !lead.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1) + " with " +
                                lead.getName().toString().trim()));
                    /*holder.from.setText(WordUtils.capitalize(communicationMode[Integer.parseInt(FollowUp.getCommid()) - 1] + " with " +
                            lead.getName().toString().trim()));*/
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1) + " with " +
                                FollowUp.getContactPerson().toString().trim()));
                    /*holder.from.setText(WordUtils.capitalize(communicationMode[Integer.parseInt(FollowUp.getCommid()) - 1] + " with " +
                            FollowUp.getContactPerson().toString().trim()));*/
                    } else {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode()));
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else {
                    if (lead.getName() != null && !lead.getName().isEmpty()
                            && !lead.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(lead.getName().toString().trim()));
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getContactPerson().toString().trim()));
                    } else {
                        holder.from.setVisibility(View.GONE);
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }
            } else {
                if (FollowUp.getFollowupCommunicationMode() != null && !FollowUp.getFollowupCommunicationMode().isEmpty()
                        && !FollowUp.getFollowupCommunicationMode().equals("null")) {
                    if (FollowUp.getName() != null && !FollowUp.getName().isEmpty()
                            && !FollowUp.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode() + " with " +
                                FollowUp.getName().toString().trim()));
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode() + " with " +
                                FollowUp.getContactPerson().toString().trim()));
                    } else {
                        holder.from
                                .setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode()));
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else if (FollowUp.getCommid() != null && !FollowUp.getCommid().isEmpty()) {
                    if (FollowUp.getName() != null && !FollowUp.getName().isEmpty()
                            && !FollowUp.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1) + " with " +
                                FollowUp.getName().toString().trim()));
                    /*holder.from.setText(WordUtils.capitalize(communicationMode[Integer.parseInt(FollowUp.getCommid()) - 1] + " with " +
                            FollowUp.getName().toString().trim()));*/
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1) + " with " +
                                FollowUp.getContactPerson().toString().trim()));
                    /*holder.from.setText(WordUtils.capitalize(communicationMode[Integer.parseInt(FollowUp.getCommid()) - 1] + " with " +
                            FollowUp.getContactPerson().toString().trim()));*/
                    } else {
                        holder.from
                                .setText(WordUtils.capitalize(FollowUp.getFollowupCommunicationMode()));
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else {
                    if (FollowUp.getName() != null && !FollowUp.getName().isEmpty()
                            && !FollowUp.getName().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getName().toString().trim()));
                    } else if (FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && !FollowUp.getContactPerson().equals("null")) {
                        holder.from.setText(WordUtils.capitalize(FollowUp.getContactPerson().toString().trim()));
                    } else {
                        holder.from.setVisibility(View.GONE);
                        holder.content.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }
            }


            count++;
            if (FollowUp.getLeadId() != null && !FollowUp.getLeadId().isEmpty() && !FollowUp.getLeadId().equals("null")
                    && FollowUp.getLeadIdR() != null && !FollowUp.getLeadIdR().isEmpty() && !FollowUp.getLeadIdR().equals("null")) {
                //holder.content.setText(AppPreferences.getCompanyCode(mContext, AppUtils.COMPANY_CODE) + "LE" + String.format("%06d", Integer.parseInt(FollowUp.getLeadId())));
                holder.content.setText(FollowUp.getLeadIdR());
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setTypeface(Typeface.DEFAULT);
            } else if (FollowUp.getEnid() != null && !FollowUp.getEnid().isEmpty() && !FollowUp.getEnid().equals("null")
                    && FollowUp.getEnquiryId() != null && !FollowUp.getEnquiryId().isEmpty() && !FollowUp.getEnquiryId().equals("null")) {
                //holder.content.setText(AppPreferences.getCompanyCode(mContext, AppUtils.COMPANY_CODE) + "EN" + String.format("%06d", Integer.parseInt(FollowUp.getEnid())));
                holder.content.setText(FollowUp.getEnquiryId());
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setTypeface(Typeface.DEFAULT);
            } else if (FollowUp.getQoid() != null && !FollowUp.getQoid().isEmpty() && !FollowUp.getQoid().equals("null")
                    && FollowUp.getQuotationId() != null && !FollowUp.getQuotationId().isEmpty() && !FollowUp.getQuotationId().equals("null")) {
                //  holder.content.setText(AppPreferences.getCompanyCode(mContext, AppUtils.COMPANY_CODE) + "QU" + String.format("%06d", Integer.parseInt(FollowUp.getQoid())));
                holder.content.setText(FollowUp.getQuotationId());
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setTypeface(Typeface.DEFAULT);
            } else if (FollowUp.getPurorid() != null && !FollowUp.getPurorid().isEmpty() && !FollowUp.getPurorid().equals("null")
                    && FollowUp.getPurchaseOrderId() != null && !FollowUp.getPurchaseOrderId().isEmpty() && !FollowUp.getPurchaseOrderId().equals("null")) {
                holder.content.setText(FollowUp.getPurchaseOrderId());
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setTypeface(Typeface.DEFAULT);
            } else if (FollowUp.getChkoid() != null && !FollowUp.getChkoid().isEmpty() && !FollowUp.getChkoid().equals("null")
                    && FollowUp.getOrderId() != null && !FollowUp.getOrderId().isEmpty() && !FollowUp.getOrderId().equals("null")) {
                holder.content.setText(FollowUp.getOrderId());
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setTypeface(Typeface.DEFAULT);
            } else {
                holder.content.setVisibility(View.GONE);
            }

            //Check Permission of Record Follow UP
            if (AppPreferences.getBoolean(mContext, AppUtils.ISADMIN)
                    || db.checkUsersPermission(mContext.getString(R.string.followup_take_permission))) {
                if (FollowUp != null && FollowUp.getFosid() != null && FollowUp.getFosid().equals("1")) {
                    if (FollowUp.getLeasid() != null
                            && !FollowUp.getLeasid().isEmpty()
                            && !FollowUp.getLeasid().equals("4")
                            && !FollowUp.getLeasid().equals("3")) {
                        //In lead status 3 & 4 are for converted and cancelled leads
                        holder.isSwipeToRecord = true;
                    } else if (FollowUp.getQosid() != null
                            && !FollowUp.getQosid().isEmpty()
                            && !FollowUp.getQosid().equals("1")
                            && !FollowUp.getQosid().equals("3")) {
                        //In quotations status 1 & 3 are for converted and cancelled quotations
                        holder.isSwipeToRecord = true;
                    } else if (FollowUp.getChkosid() != null
                            && !FollowUp.getChkosid().isEmpty()
                            && !FollowUp.getChkosid().equals("5")
                            && !FollowUp.getChkosid().equals("6")
                            && !FollowUp.getChkosid().equals("7")
                            && !FollowUp.getChkosid().equals("10")
                            && !FollowUp.getChkosid().equals("11")) {
                        //In orders status 1 & 3 are for converted and cancelled orders
                        holder.isSwipeToRecord = true;
                    } else if (FollowUp.getEnsid() != null
                            && !FollowUp.getEnsid().isEmpty()
                            && !FollowUp.getEnsid().equals("5")
                            && !FollowUp.getEnsid().equals("1")
                            && !FollowUp.getEnsid().equals("3")) {
                        //In orders status 5, 1 & 3 are for on hold, converted and cancelled enquiries
                        holder.isSwipeToRecord = true;
                    } else if ((db.getLeadStatusId(FollowUp.getLeadId())) != null
                            && !(db.getLeadStatusId(FollowUp.getLeadId())).isEmpty()
                            && !(db.getLeadStatusId(FollowUp.getLeadId())).equals("4")
                            && !(db.getLeadStatusId(FollowUp.getLeadId())).equals("3")) {
                        holder.isSwipeToRecord = true;
                    } else {
                        holder.isSwipeToRecord = false;
                    }
                } else {
                    holder.isSwipeToRecord = false;
                }
            } else {
                holder.isSwipeToRecord = false;
                Toast.makeText(mContext, "You have no permission to take any followup.", Toast.LENGTH_SHORT).show();
            }
            applyIconAnimation(holder, position);
            applyProfilePicture(holder, FollowUp);
            //applyClickEvents(holder, position);

            holder.callTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (followUps != null) {
                            contactDetails = db.getLead(followUps.get(position).getLeaid(), "leaid");
                            if (FollowUp != null && FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                                    && FollowUp.getContactPersonMobile() != null && !FollowUp.getContactPersonMobile().isEmpty()) {
                                leadContactMobile = FollowUp.getContactPersonMobile();

                            } else if (contactDetails != null && contactDetails.getMobile() != null && !contactDetails.getMobile().isEmpty()) {
                                leadContactMobile = contactDetails.getMobile();
                            } else if (contactDetails != null && contactDetails.getMobile() == null || contactDetails.getMobile().isEmpty()
                                    && contactDetails.getContacts() != null && contactDetails.getContacts().size() > 0) {

                                try {
                                    for (int i = 0; i < contactDetails.getContacts().size(); i++) {
                                        if (contactDetails.getContacts().get(i).getPhoneNo() != null && !contactDetails.getContacts().get(i).getPhoneNo().isEmpty()) {
                                            leadContactMobile = contactDetails.getContacts().get(i).getPhoneNo();
                                        } else {
                                            CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_error), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    //  CustomisedToast.error(mContext, "There is no contact.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_error), Toast.LENGTH_SHORT).show();

                            }

                            //   leadMobileNumber = leadLists.getMobile();
                            //  Log.e("Lead Number Mobile", leadMobileNumber + "");
                            if (leadContactMobile != null && dtInterface != null && !leadContactMobile.isEmpty()) {
                                dtInterface.sendMobileNumber(leadContactMobile);
                            }
                            Log.e("Mobile", leadContactMobile + "");
                            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + leadContactMobile));
                            if (leadContactMobile != null && !leadContactMobile.isEmpty()) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (checkPermissionWithRationale((Activity) mContext, new LeadInfoFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS)) {
                                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                    }
                                } else {
                                    if (leadContactMobile != null && !leadContactMobile.isEmpty()) {
                                        mContext.startActivity(intentCall);
                                    } else {

                                    }
                                }
                            } else {
                                CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_error), Toast.LENGTH_SHORT).show();

                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            holder.emailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Intent email = new Intent(Intent.ACTION_SEND);
                    //  contactDetails = db.getContactDetails(followUps.get(position).getLeadId());
                    String leadContactEmail = "";
                    /*if (contactDetails != null && contactDetails.getContactPersonEmail() != null) {
                        leadContactEmail = contactDetails.getContactPersonEmail();
                    } else {
                        leadContactEmail = FollowUp.getContactPersonEmail();
                    }*/


                    if (FollowUp != null && FollowUp.getContactPerson() != null && !FollowUp.getContactPerson().isEmpty()
                            && FollowUp.getContactPersonEmail() != null && !FollowUp.getContactPersonEmail().isEmpty()) {
                        leadContactEmail = FollowUp.getContactPersonEmail();

                    } else if (contactDetails != null && contactDetails.getEmail() != null && !contactDetails.getEmail().isEmpty()) {
                        leadContactEmail = contactDetails.getEmail();
                    } else if (contactDetails != null && contactDetails.getContacts() != null && contactDetails.getContacts().size() > 0 && contactDetails.getEmail() == null) {
                        for (int i = 0; i < contactDetails.getContacts().size(); i++) {
                            if (contactDetails.getContacts().get(i).getEmail() != null && !contactDetails.getContacts().get(i).getEmail().isEmpty()) {
                                leadContactEmail = contactDetails.getContacts().get(i).getEmail();
                            } else {
                                CustomisedToast.error(mContext, mContext.getString(R.string.email_error), Toast.LENGTH_SHORT).show();

                            }

                        }
                    } else {
                        CustomisedToast.error(mContext, mContext.getString(R.string.email_error), Toast.LENGTH_SHORT).show();

                    }

                    if (followUps.get(position).getContactPersonEmail() == null) {
                        CustomisedToast.error(mContext, mContext.getString(R.string.email_error)).show();
                    } else {
                        Log.e("contactPersonEmail", leadContactEmail + "");
                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                mContext.getString(R.string.mail_to), leadContactEmail, null));
                        email.putExtra(Intent.EXTRA_SUBJECT, "");
                        email.putExtra(Intent.EXTRA_TEXT, "");
                        mContext.startActivity(Intent.createChooser(email, mContext.getString(R.string.choose_email_client)));
                    }
                }
            });
            holder.followupInfoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check Permission of viewing details of Follow UP
                    if (AppPreferences.getBoolean(mContext, AppUtils.ISADMIN) || db.checkUsersPermission(mContext.getString(R.string.followup_view_details_permission))) {
                        FollowUp FollowUp = followUps.get(position);
                        followUps.set(position, FollowUp);
                        Intent intent = new Intent(mContext, FollowupInfoActivity.class);
                        intent.putExtra(mContext.getString(R.string.foid), FollowUp.getFoid());
                        intent.putExtra("syncId", FollowUp.getSyncId());
                        mContext.startActivity(intent);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "You have no permission to view details of any followup.", Toast.LENGTH_SHORT).show();

                    }

                }
            });

            applyClickEvents(holder, position);

            //Set Image Icon
            if (FollowUp.getFollowupCommunicationMode() != null && !FollowUp.getFollowupCommunicationMode().isEmpty()
                    && !FollowUp.getFollowupCommunicationMode().equals("null")) {
                if (FollowUp.getFollowupCommunicationMode().toLowerCase().contains("phone")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.call));
                } else if (FollowUp.getFollowupCommunicationMode().toLowerCase().contains("skype")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.skype));
                } else if (FollowUp.getFollowupCommunicationMode().toLowerCase().contains("mail")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.mail));
                }
            } else if (FollowUp.getCommid() != null && !FollowUp.getCommid().isEmpty()
                    && !FollowUp.getCommid().equals("null")) {
                if (communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1).getName().toLowerCase().contains("phone")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.call));
                } else if (communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1).getName().toLowerCase().contains("skype")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.skype));
                } else if (communicationModeList.get(Integer.parseInt(FollowUp.getCommid()) - 1).getName().toLowerCase().contains("mail")) {
                    holder.communicationImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.mail));
                }
            }

            //attendee
            if (FollowUp.getAssignedUser() != null && !FollowUp.getAssignedUser().isEmpty() && FollowUp.getAssignedUser() != "") {
                try {
                    //ChatContacts chatContacts = databaseHandler.getUserData(Integer.parseInt(followUp.getAssignedUid()));
                    //assignedToValueTextView.setText(chatContacts.getName());
                    try {
                        if (Constants.isNumeric(FollowUp.getAssignedUser())) {
                            ChatContacts chatContacts = db.getUserData(Integer.parseInt(FollowUp.getAssignedUser()));
                            if (chatContacts.getName() != null && !chatContacts.getName().isEmpty()) {
                                holder.txtAttendee.setText("Attendee: " + chatContacts.getName());
                                holder.txtAttendee.setVisibility(View.VISIBLE);
                            }
                        } else {
                            String assignedUser = "";
                            JSONObject jsonObject = new JSONObject(FollowUp.getAssignedUser());
                            assignedUser = jsonObject.getString(mContext.getString(R.string.name));
                            if (assignedUser != null && !assignedUser.isEmpty()) {
                                holder.txtAttendee.setText("Attendee: " + assignedUser);
                                holder.txtAttendee.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (FollowUp.getAssignedUser() != null && !FollowUp.getAssignedUser().isEmpty()) {
                            holder.txtAttendee.setText("Attendee: " + FollowUp.getAssignedUser());
                            holder.txtAttendee.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (FollowUp.getAssignedUid() != null && !FollowUp.getAssignedUid().isEmpty() && FollowUp.getAssignedUid() != "") {
                try {
                    ChatContacts chatContacts = db.getUserData(Integer.parseInt(FollowUp.getAssignedUid()));
                    if (chatContacts.getName() != null && !chatContacts.getName().isEmpty()) {
                        holder.txtAttendee.setText("Attendee: " + chatContacts.getName());
                        holder.txtAttendee.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                holder.txtAttendee.setVisibility(View.GONE);
            }


            //Overdue
            holder.overdueTextView.setBackgroundResource(R.drawable.followup_status_taken_bg);
            GradientDrawable gradientDrawable = (GradientDrawable) holder.overdueTextView.getBackground();
            gradientDrawable.setColor(Color.parseColor("#404040"));
            holder.overdueTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.overdueTextView.setText("Overdue");
            holder.overdueTextView.setVisibility(View.GONE);

            //set Date time and status
            LayerDrawable layerDrawable = (LayerDrawable) mContext.getResources()
                    .getDrawable(R.drawable.followup_border);
            GradientDrawable layerGradientDrawable = (GradientDrawable) layerDrawable
                    .findDrawableByLayerId(R.id.gradientDrawable);

            if (FollowUp != null && FollowUp.getFosid() != null && FollowUp.getFosid().equals("1")) {
                //Set status text
                holder.statusTextView.setBackgroundResource(R.drawable.followup_status_taken_bg);
                GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
                drawable.setColor(Color.parseColor("#BFBFBF"));
                holder.statusTextView.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.statusTextView.setText("Pending");

                layerGradientDrawable.setStroke((int) mContext.getResources().getDimension(R.dimen._4sdp), mContext.getResources().getColor(R.color.yellow_divider));

                //  holder.relativeLayoutContainer.setBackgroundColor(Color.parseColor("#ececec"));
                setDateTime(FollowUp.getScheduledDate(), holder.subject, holder.overdueTextView, layerGradientDrawable);
            } else {
                //Set status text
                holder.statusTextView.setBackgroundResource(R.drawable.followup_status_taken_bg);
                GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
                drawable.setColor(Color.parseColor("#404040"));
                holder.statusTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.statusTextView.setText("Taken");

                layerGradientDrawable.setStroke((int) mContext.getResources().getDimension(R.dimen._4sdp), mContext.getResources().getColor(R.color.Green_ForestGreen));

                if (FollowUp.getTakenOn() != null && !FollowUp.getTakenOn().isEmpty()) {
                    setDateTime(FollowUp.getTakenOn(), holder.subject, null, null);
                }
            }

            holder.mainContainer.setBackground(mContext.getResources().getDrawable(R.drawable.followup_border));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.frameLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                // collapse any currently expanded items
                if (mExpandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(mExpandedPosition);
                }
                listener.onMessageRowClicked(position);
                //  notifyDataSetChanged();

            }
        });
    }


    private void applyProfilePicture(MyViewHolder holder, FollowUp FollowUp) {
        holder.imgProfile.setImageResource(R.drawable.bg_square);
        holder.imgProfile.setColorFilter(getColor(holder.iconText.getText().toString().toUpperCase().substring(0), mContext));
        holder.iconText.setVisibility(View.VISIBLE);
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(followUps.get(position).getFoid());
    }

    @Override
    public int getItemCount() {
        //    if (filterList.size() == 0) {
        return followUps.size();
        //   } else {
        //      return filterList.size();
        //  }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public void setDateTime(String dateTime, TextView scheduledTextView, TextView overdueTextView, GradientDrawable gradientDrawable) {
        try {
            if (dateTime != null && !dateTime.isEmpty() && !dateTime.contains("0000")) {
                Date pastDate = simpleDateFormat.parse(dateTime);
                Date currentDate = new Date();

                Calendar now = Calendar.getInstance();

                long nowInMillis = SystemClock.uptimeMillis();

                Calendar now_calendar = Calendar.getInstance();
                now_calendar.setTimeInMillis(nowInMillis);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(pastDate.getTime() - currentDate.getTime());

                if (seconds < 0) {
                    seconds = TimeUnit.MILLISECONDS.toSeconds(currentDate.getTime() - pastDate.getTime());
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(currentDate.getTime() - pastDate.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(currentDate.getTime() - pastDate.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - pastDate.getTime());
                    if (seconds < 60) {
                        if (seconds == 0) {
                            scheduledTextView.setText(mContext.getString(R.string.just_now));
                        } else if (seconds == 1) {
                            scheduledTextView.setText(seconds + " " + (mContext.getString(R.string.second_ago)));
                        } else {
                            scheduledTextView.setText(seconds + " " + (mContext.getString(R.string.seconds_ago)));
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            scheduledTextView.setText(minutes + " " + mContext.getString(R.string.minute_ago));
                        } else
                            scheduledTextView.setText(minutes + " " + mContext.getString(R.string.minutes_ago));
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            scheduledTextView.setText(hours + " " + mContext.getString(R.string.hour_ago));
                        } else
                            scheduledTextView.setText(hours + " " + mContext.getString(R.string.hours_ago));
                    } else {
                        if (currentDate.getDate() - pastDate.getDate() == 1) {
                            scheduledTextView.setText(R.string.yesterday);
                        } else {
                            scheduledTextView.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                        }
                        if (overdueTextView != null) {
                            overdueTextView.setVisibility(View.VISIBLE);
                            if (gradientDrawable != null) {
                                gradientDrawable.setStroke((int) mContext.getResources().getDimension(R.dimen._4sdp), mContext.getResources().getColor(R.color.red));
                            }
                        }
                    }
                } else {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(pastDate.getTime() - currentDate.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(pastDate.getTime() - currentDate.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(pastDate.getTime() - currentDate.getTime());
                    if (seconds < 60) {
                        if (seconds == 0) {
                            scheduledTextView.setText(mContext.getString(R.string.just_now));
                        } else if (seconds == 1) {
                            scheduledTextView.setText(seconds + " " + (mContext.getString(R.string.second_ago)));
                        } else {
                            scheduledTextView.setText(seconds + " " + (mContext.getString(R.string.seconds_ago)));
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            scheduledTextView.setText(minutes + " " + mContext.getString(R.string.minute_later));
                        } else
                            scheduledTextView.setText(minutes + " " + mContext.getString(R.string.minutes_later));
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            scheduledTextView.setText(hours + " " + mContext.getString(R.string.hour_later));
                        } else
                            scheduledTextView.setText(hours + " " + mContext.getString(R.string.hours_later));
                    } else {
                        if (pastDate.getDate() - currentDate.getDate() == 1) {
                            scheduledTextView.setText(R.string.tommorow);
                        } else
                            scheduledTextView.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                }
            } else {
                scheduledTextView.setText("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public interface FollowUpAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView from, subject, content, iconText, statusTextView, txtAttendee, overdueTextView,
                callTextView, followupInfoTextView, emailTextView;
        public ImageView imgProfile, communicationImageView;
        public LinearLayout contentContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        public FrameLayout frameLayout;
        public RelativeLayout relativeLayoutContainer;
        public boolean isSwipeToRecord;
        public LinearLayout mainContainer;
        public RelativeLayout viewBackground;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            content = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            frameLayout = (FrameLayout) view.findViewById(R.id.action_on_follow_up_container);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            relativeLayoutContainer = (RelativeLayout) view.findViewById(R.id.relativelayout_container);
            callTextView = (TextView) view.findViewById(R.id.call_textView);
            emailTextView = (TextView) view.findViewById(R.id.email_textView);
            followupInfoTextView = (TextView) view.findViewById(R.id.followupInfo_textView);
            communicationImageView = (ImageView) view.findViewById(R.id.communication_imageView);
            statusTextView = (TextView) view.findViewById(R.id.status_value_textView);
            txtAttendee = (TextView) view.findViewById(R.id.txt_attendee);
            overdueTextView = (TextView) view.findViewById(R.id.overdue_textView);
            mainContainer = (LinearLayout) view.findViewById(R.id.mainContainer);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);

        }
    }

}