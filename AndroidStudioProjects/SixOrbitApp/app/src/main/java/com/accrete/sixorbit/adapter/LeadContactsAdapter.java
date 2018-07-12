package com.accrete.sixorbit.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.interfaces.SendMobileNumberInterface;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.Lead;

import java.util.ArrayList;
import java.util.List;

import static com.accrete.sixorbit.adapter.FollowUpAdapter.getColor;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 7/7/17.
 */

public class LeadContactsAdapter extends RecyclerView.Adapter<LeadContactsAdapter.MyViewHolder> {
    public List<Lead> leadList = new ArrayList<>();
    SendMobileNumberInterface dtInterface;
    LeadFragment lf;
    String mobilenumber;
    private Context mContext;
    private Activity activity;
    private List<Contacts> contacts;
    private LeadContactsAdapterListener listener;
    private DatabaseHandler db;
    private Typeface tf;

    public LeadContactsAdapter(Activity activity, List<Contacts> leads, List<Lead> leadList, LeadContactsAdapterListener listener,
                               Context context) {
        this.mContext = context;
        this.activity = activity;
        this.contacts = leads;
        this.listener = listener;
        this.leadList = leadList;

    }

    public LeadContactsAdapter(Activity a, SendMobileNumberInterface dtInterface) {
        // TODO Auto-generated constructor stub
        activity = a;
        this.dtInterface = dtInterface;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//     constructor for interface handling

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_lead_contacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            Contacts leadContact = contacts.get(position);

            //    Lead lead = leadList.get(0);
            db = new DatabaseHandler(activity);
            tf = Typeface.createFromAsset(mContext.getAssets(), "font/Corbert-Regular.otf");


            if (leadContact != null && leadContact.getName() != null) {
                //Designation
                if (leadContact.getDesignation() != null && !leadContact.getDesignation().isEmpty() &&
                        !leadContact.getDesignation().equals("null")) {
                    holder.textViewLeadContactDesignation.setVisibility(View.VISIBLE);
                    if (leadContact.getDesignation().length() > 8) {
                        holder.textViewLeadContactDesignation.setText("(" + leadContact.getDesignation().toString().substring(0, 5) + ".." + ")");
                    } else {
                        holder.textViewLeadContactDesignation.setText("(" + leadContact.getDesignation().toString() + ")");
                    }
                } else {
                    holder.textViewLeadContactDesignation.setVisibility(View.GONE);
                }

                //Name
                if (leadContact.getName().length() > 12) {
                    holder.textViewLeadContactName.setText(leadContact.getName().toString().substring(0, 8) + "..");
                } else {
                    holder.textViewLeadContactName.setText(leadContact.getName());
                }

                //Phone Number
                if (leadContact.getPhoneNo() != null && !leadContact.getPhoneNo().isEmpty() &&
                        !leadContact.getPhoneNo().equals("null")) {
                    holder.textviewContactNumber.setText("(" + leadContact.getPhoneNo() + ")");
                    holder.textviewContactNumber.setVisibility(View.VISIBLE);
                } else {
                    holder.textviewContactNumber.setVisibility(View.GONE);
                }
                if (leadContact.getName() != null && !leadContact.getName().isEmpty() &&
                        leadContact.getName().length() > 0) {
                    holder.iconText.setText(leadContact.getName().toUpperCase().trim().substring(0, 1));
                }
          /*  if (lead.getOwnerId() != null) {
                if (lead.getOwnerId().equals(lead.getContacts().get(position).getCodeid())) {
                    holder.textViewIsOwner.setText(mContext.getString(R.string.owner));
                }
            }*/

                if (leadContact.getIsOwner() != null) {
                    if (leadContact.getIsOwner().equals("1")) {
                        holder.textViewIsOwner.setText(mContext.getString(R.string.owner));
                    }
                }

                applyClickEvents(holder, position);
                holder.imgProfile.setImageResource(R.drawable.bg_square);
                holder.imgProfile.setColorFilter(getColor(leadContact.getName().trim().toUpperCase().substring(0, 1), activity));

                if (leadContact.getEmail() != null && !leadContact.getEmail().isEmpty()) {
                    if (leadContact.getEmail().length() > 25) {
                        holder.leadContactsEmailTextView.setText(leadContact.getEmail().substring(0, 21) + "...");
                    } else {
                        holder.leadContactsEmailTextView.setText(leadContact.getEmail());
                    }
                    holder.leadContactsEmailTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.leadContactsEmailTextView.setVisibility(View.GONE);
                }

            } else {
                holder.textViewLeadContactName.setText(R.string.no_contacts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.imgLeadContactCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (contacts.get(position).getPhoneNo() != null &&
                            !contacts.get(position).getPhoneNo().isEmpty()) {
                        mobilenumber = contacts.get(position).getPhoneNo();
                        if (Constants.validCellPhone(mobilenumber)) {
                            //   sending the lead contact mobile number to call back activity(lead info activity) using interface
                            if (mContext instanceof SendMobileNumberInterface) {
                                ((SendMobileNumberInterface) mContext).sendMobileNumberLead(mobilenumber);
                            }
                            //                checking the run time permissions
                            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobilenumber));
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (checkPermissionWithRationale((Activity) mContext, new LeadFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSIONS)) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling

                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                }
                            } else {
                                mContext.startActivity(intentCall);
                            }
                        } else {
                            CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_not_valid_error)).show();
                        }
                    } else {
                        CustomisedToast.error(mContext, mContext.getString(R.string.phone_number_error)).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

        holder.imgLeadContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  listener.onEmailClicked(position);
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{contacts.get(position).getEmail()});
                // email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                // email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("plain/text");
                activity.startActivity(Intent.createChooser(email, mContext.getString(R.string.choose_email_client)));
            }
        });
    }
//

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public interface LeadContactsAdapterListener {
        void onCallClicked(int position);

        void onEmailClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewLeadContactName, iconText, textViewIsOwner, textviewContactNumber;
        public ImageView imgLeadContactCall, imgLeadContactEmail, imgProfile;
        private TextView textViewLeadContactDesignation, leadContactsEmailTextView;

        public MyViewHolder(View view) {
            super(view);
            textViewLeadContactName = (TextView) view.findViewById(R.id.lead_contacts_name);
            textViewLeadContactDesignation = (TextView) view.findViewById(R.id.lead_contacts_designation);
            imgLeadContactCall = (ImageView) view.findViewById(R.id.lead_contacts_call);
            imgLeadContactEmail = (ImageView) view.findViewById(R.id.lead_contacts_email);
            imgProfile = (ImageView) view.findViewById(R.id.lead_contacts_alphabet_bg);
            iconText = (TextView) view.findViewById(R.id.lead_contacts_alphabet);
            textViewIsOwner = (TextView) view.findViewById(R.id.lead_contacts_isowner);
            textviewContactNumber = (TextView) view.findViewById(R.id.lead_contacts_number);
            leadContactsEmailTextView = (TextView) view.findViewById(R.id.lead_contacts_email_textView);
        }
    }

}