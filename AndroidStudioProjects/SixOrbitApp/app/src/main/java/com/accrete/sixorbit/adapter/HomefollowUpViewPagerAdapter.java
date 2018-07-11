package com.accrete.sixorbit.adapter;

/**
 * Created by amp on 14/9/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.FollowupInfoActivity;
import com.accrete.sixorbit.fragment.Drawer.HomeFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.RecordFollowUpFragment;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.interfaces.SendRecentFollowUpMobileInterface;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_VIEWPAGER_FOLLOWUP_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class HomefollowUpViewPagerAdapter extends PagerAdapter {
    private final FragmentManager fragmentManager;
    // Declare Variables
    private Context context;
    private LinearLayout viewPagerCallButton, viewPagerEmailButton, viewPagerRecordFollowUpButton;
    private LinearLayout recentFollowUp;
    private LayoutInflater inflater;
    private SendRecentFollowUpMobileInterface fInterface;
    private int position;
    private String leadContactMobile;
    private String companyCode;
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();
    private List<FollowUp> limitFollowUps = new ArrayList<>();
    private FollowUp contactDetails = new FollowUp();
    private DatabaseHandler db;
    private Lead lead = new Lead();

    public HomefollowUpViewPagerAdapter(Context context, List<FollowUp> limitFollowUps,
                                        FragmentManager fragmentManager,
                                        SendRecentFollowUpMobileInterface sendRecentFollowUpMobileInterface) {
        this.context = context;
        this.fInterface = sendRecentFollowUpMobileInterface;
        this.limitFollowUps = limitFollowUps;
        this.fragmentManager = fragmentManager;
        if (AppPreferences.getModes(context, AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(context, AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }
    }

    @Override
    public int getCount() {
        return limitFollowUps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // Declare Variables
        TextView textviewfollowupviewPagerName;
        TextView textviewfollowupviewPagerDate;
        TextView textviewfollowupviewPagerLeadId;

        db = new DatabaseHandler(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.followup_viewpager_home, container,
                false);
        try {
            // Locate the TextViews in followup_viewpager_home.xml
            textviewfollowupviewPagerName = (TextView) itemView.findViewById(R.id.textView_followup_viewpager_name);
            textviewfollowupviewPagerDate = (TextView) itemView.findViewById(R.id.textView_followup_viewpager_date);
            textviewfollowupviewPagerLeadId = (TextView) itemView.findViewById(R.id.textView_followup_viewpager_leadid);
            viewPagerCallButton = (LinearLayout) itemView.findViewById(R.id.textView_followup_viewpager_callbutton);
            viewPagerEmailButton = (LinearLayout) itemView.findViewById(R.id.textView_followup_viewpager_emailbutton);
            viewPagerRecordFollowUpButton = (LinearLayout) itemView.findViewById(R.id.textView_followup_viewpager_rec_followp_button);

            recentFollowUp = (LinearLayout) itemView.findViewById(R.id.recent_followup);
            contactDetails = db.getContactDetails(limitFollowUps.get(position).getLeadId());
            recentFollowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FollowupInfoActivity.class);
                    intent.putExtra(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                    intent.putExtra(context.getString(R.string.syncid), limitFollowUps.get(position).getSyncId());
                    context.startActivity(intent);
                }
            });

            viewPagerCallButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (contactDetails != null && contactDetails.getContactPersonMobile() != null) {
                        leadContactMobile = contactDetails.getContactPersonMobile();
                    } else {
                        leadContactMobile = limitFollowUps.get(position).getContactPersonMobile();
                    }
                    fInterface.sendRecentFollowupNumber(leadContactMobile);

                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + leadContactMobile));
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionWithRationale((Activity) context, new HomeFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_VIEWPAGER_FOLLOWUP_CALL_PERMISSIONS)) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                        }
                    } else {

                        if (leadContactMobile != null && !leadContactMobile.isEmpty()) {
                            context.startActivity(intentCall);
                        } else {
                            CustomisedToast.error(context, context.getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });


            viewPagerEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String leadContactEmail = null;
                    if (limitFollowUps.get(position).getContactPersonEmail() == null) {
                        CustomisedToast.error(context, context.getString(R.string.email_error)).show();
                    } else {
                        leadContactEmail = limitFollowUps.get(position).getContactPersonEmail();
                    }
                    if (leadContactEmail == null) {
                        CustomisedToast.error(context, "Email Id is not present").show();
                    } else {
                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                context.getString(R.string.mail_to), leadContactEmail, null));
                        email.putExtra(Intent.EXTRA_SUBJECT, "");
                        email.putExtra(Intent.EXTRA_TEXT, "");
                        context.startActivity(Intent.createChooser(email, context.getString(R.string.choose_email_client)));
                    }
                }
            });

            viewPagerRecordFollowUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordFollowUpFragment fragment = new RecordFollowUpFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    if (limitFollowUps.get(position).getLeasid() != null
                            && !limitFollowUps.get(position).getLeasid().isEmpty()) {
                        //In lead status 3 & 4 are for converted and cancelled leads
                        if (limitFollowUps.get(position).getLeasid().equals("4")) {
                            Toast.makeText(context, "Sorry, this lead is in cancelled state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        } else if (limitFollowUps.get(position).getLeasid().equals("3")) {
                            Toast.makeText(context, "Sorry, this lead is in converted state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (limitFollowUps.get(position).getLeadId() != null && !limitFollowUps.get(position).getLeadId().isEmpty()) {
                                bundle.putString(context.getString(R.string.lead_id), limitFollowUps.get(position).getLeadId());
                            }
                            if (limitFollowUps.get(position).getFoid() != null && !limitFollowUps.get(position).getFoid().isEmpty()) {
                                bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                            } else if (limitFollowUps.get(position).getSyncId() != null && !limitFollowUps.get(position).getSyncId().isEmpty()) {
                                bundle.putString(context.getString(R.string.sync_id), limitFollowUps.get(position).getSyncId());
                            }
                            fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment, context.getString(R.string.record_follow_up_fragment_tag));
                            ft.addToBackStack(null).commit();
                        }
                    } else if (limitFollowUps.get(position).getQosid() != null
                            && !limitFollowUps.get(position).getQosid().isEmpty()) {
                        //In quotation status 1 & 3 are for converted and cancelled quotations
                        if (limitFollowUps.get(position).getQosid().equals("3")) {
                            Toast.makeText(context, "Sorry, this quotation is in cancelled state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        } else if (limitFollowUps.get(position).getQosid().equals("1")) {
                            Toast.makeText(context, "Sorry, this quotation is in converted state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (limitFollowUps.get(position).getLeadId() != null && !limitFollowUps.get(position).getLeadId().isEmpty()) {
                                bundle.putString(context.getString(R.string.lead_id), limitFollowUps.get(position).getLeadId());
                            }
                            // bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                            if (limitFollowUps.get(position).getFoid() != null && !limitFollowUps.get(position).getFoid().isEmpty()) {
                                bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                            } else if (limitFollowUps.get(position).getSyncId() != null && !limitFollowUps.get(position).getSyncId().isEmpty()) {
                                bundle.putString(context.getString(R.string.sync_id), limitFollowUps.get(position).getSyncId());
                            }
                            fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment, context.getString(R.string.record_follow_up_fragment_tag));
                            ft.addToBackStack(null).commit();
                        }
                    } else if (limitFollowUps.get(position).getChkosid() != null
                            && !limitFollowUps.get(position).getChkosid().isEmpty()) {
                        //In order status 5 status is for delivered order
                        if (limitFollowUps.get(position).getChkosid().equals("5")) {
                            Toast.makeText(context, "Sorry, this order is in delivered state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        }
                        //In order status 6 status is for rejected order
                        else if (limitFollowUps.get(position).getChkosid().equals("6")) {
                            Toast.makeText(context, "Sorry, this order is in rejected state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        }
                        //In order status 7 status is for executed order
                        else if (limitFollowUps.get(position).getChkosid().equals("7")) {
                            Toast.makeText(context, "Sorry, this order is in executed state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        }
                        //In order status 10 status is for cancelled order
                        else if (limitFollowUps.get(position).getChkosid().equals("10")) {
                            Toast.makeText(context, "Sorry, this order is in cancelled state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        }
                        //In order status 11 status is for deleted order
                        else if (limitFollowUps.get(position).getChkosid().equals("11")) {
                            Toast.makeText(context, "Sorry, this order is in deleted state so you can't have follow up",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (limitFollowUps.get(position).getLeadId() != null && !limitFollowUps.get(position).getLeadId().isEmpty()) {
                                bundle.putString(context.getString(R.string.lead_id), limitFollowUps.get(position).getLeadId());
                            }
                            // bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                            if (limitFollowUps.get(position).getFoid() != null && !limitFollowUps.get(position).getFoid().isEmpty()) {
                                bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                            } else if (limitFollowUps.get(position).getSyncId() != null && !limitFollowUps.get(position).getSyncId().isEmpty()) {
                                bundle.putString(context.getString(R.string.sync_id), limitFollowUps.get(position).getSyncId());
                            }
                            fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment, context.getString(R.string.record_follow_up_fragment_tag));
                            ft.addToBackStack(null).commit();
                        }
                    } else {
                        if (limitFollowUps.get(position).getLeadId() != null && !limitFollowUps.get(position).getLeadId().isEmpty()) {
                            bundle.putString(context.getString(R.string.lead_id), limitFollowUps.get(position).getLeadId());
                        }
                        // bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                        if (limitFollowUps.get(position).getFoid() != null && !limitFollowUps.get(position).getFoid().isEmpty()) {
                            bundle.putString(context.getString(R.string.foid), limitFollowUps.get(position).getFoid());
                        } else if (limitFollowUps.get(position).getSyncId() != null && !limitFollowUps.get(position).getSyncId().isEmpty()) {
                            bundle.putString(context.getString(R.string.sync_id), limitFollowUps.get(position).getSyncId());
                        }
                        fragment.setArguments(bundle);
                        ft.replace(R.id.frame_container, fragment, context.getString(R.string.record_follow_up_fragment_tag));
                        ft.addToBackStack(null).commit();
                    }
                }
            });


            //TODO - Updated On 11th may
            lead = db.getLead(limitFollowUps.get(position).getLeadId(), context.getString(R.string.leaid));

            if (lead.getName() != null && !lead.getName().isEmpty()
                    && !lead.getName().equals("null")) {
                textviewfollowupviewPagerName.setText(WordUtils.capitalize(lead.getName().toString().trim()));
            } else if (limitFollowUps.get(position).getName() != null &&
                    !limitFollowUps.get(position).getName().isEmpty()
                    && !limitFollowUps.get(position).getName().equals("null")) {
                textviewfollowupviewPagerName.setText(WordUtils.capitalize(limitFollowUps.get(position).getName().toString().trim()));
            } else if (limitFollowUps.get(position).getContactPerson() != null &&
                    !limitFollowUps.get(position).getContactPerson().isEmpty()
                    && !limitFollowUps.get(position).getContactPerson().equals("null")) {
                textviewfollowupviewPagerName.setText(WordUtils.capitalize(limitFollowUps.get(position).getContactPerson().toString().trim()));
            } else {
                textviewfollowupviewPagerName.setText("");
            }

            // Capture position and set to the TextViews
            /*if (limitFollowUps.get(position).getName() != null && !limitFollowUps.get(position).getName().isEmpty()
                    && !limitFollowUps.get(position).getName().equals("null")) {
                textviewfollowupviewPagerName.setText(limitFollowUps.get(position).getName());
                Log.e("name", limitFollowUps.get(position).getName());
            } else if (limitFollowUps.get(position).getContactPerson() != null && !limitFollowUps.get(position).getContactPerson().isEmpty()
                    && !limitFollowUps.get(position).getContactPerson().equals("null")) {
                textviewfollowupviewPagerName.setText(limitFollowUps.get(position).getContactPerson());
                Log.e("cp", limitFollowUps.get(position).getContactPerson());
            } else {
                textviewfollowupviewPagerName.setText("");

            }*/
            companyCode = AppPreferences.getCompanyCode(context, AppUtils.COMPANY_CODE);
            //Lead Id
            if (limitFollowUps.get(position).getLeadId() != null && !limitFollowUps.get(position).getLeadId().isEmpty()
                    && !limitFollowUps.get(position).getLeadId().equals("null")) {
                textviewfollowupviewPagerLeadId.setText(limitFollowUps.get(position).getLeadIdR());
                textviewfollowupviewPagerLeadId.setVisibility(View.VISIBLE);
            }
            //Enquiry Id
            else if (limitFollowUps.get(position).getEnid() != null && !limitFollowUps.get(position).getEnid().isEmpty()
                    && !limitFollowUps.get(position).getEnid().equals("null")) {
                textviewfollowupviewPagerLeadId.setText(limitFollowUps.get(position).getEnquiryId());
                textviewfollowupviewPagerLeadId.setVisibility(View.VISIBLE);
            }
            //Quotation Id
            else if (limitFollowUps.get(position).getQoid() != null && !limitFollowUps.get(position).getQoid().isEmpty()
                    && !limitFollowUps.get(position).getQoid().equals("null")) {
                textviewfollowupviewPagerLeadId.setText(limitFollowUps.get(position).getQuotationId());
                textviewfollowupviewPagerLeadId.setVisibility(View.VISIBLE);
            }
            //Purchase Order Id
            else if (limitFollowUps.get(position).getPurorid() != null && !limitFollowUps.get(position).getPurorid().isEmpty()
                    && !limitFollowUps.get(position).getPurorid().equals("null")) {
                textviewfollowupviewPagerLeadId.setText(limitFollowUps.get(position).getPurchaseOrderId());
                textviewfollowupviewPagerLeadId.setVisibility(View.VISIBLE);
            }
            //Sales Order Id
            else if (limitFollowUps.get(position).getChkoid() != null && !limitFollowUps.get(position).getChkoid().isEmpty()
                    && !limitFollowUps.get(position).getChkoid().equals("null")) {
                textviewfollowupviewPagerLeadId.setText(limitFollowUps.get(position).getOrderId());
                textviewfollowupviewPagerLeadId.setVisibility(View.VISIBLE);
            } else {
                textviewfollowupviewPagerLeadId.setVisibility(View.GONE);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

            if (limitFollowUps.get(position).getCommid() != null
                    && !limitFollowUps.get(position).getCommid().equals("null")
                    && limitFollowUps.get(position).getContactPerson() != null && !limitFollowUps.get(position).getContactPerson().isEmpty()
                    && !limitFollowUps.get(position).getContactPerson().equals("null")) {

                if (limitFollowUps.get(position).getContactPerson().trim().length() > 8) {
                    textviewfollowupviewPagerDate.setText(" " + communicationModeList.get(Integer.parseInt(limitFollowUps.get(position).getCommid()) - 1)
                            + " " + context.getString(R.string.with) + " "
                            + limitFollowUps.get(position).getContactPerson().trim().substring(0, 8) + "..");
                } else {
                    textviewfollowupviewPagerDate.setText(" " + communicationModeList.get(Integer.parseInt(limitFollowUps.get(position).getCommid()) - 1)
                            + " " + context.getString(R.string.with) + " "
                            + limitFollowUps.get(position).getContactPerson().trim());
                }

            } else if (limitFollowUps.get(position).getCommid() != null
                    && !limitFollowUps.get(position).getCommid().equals("null")
                    && limitFollowUps.get(position).getContactPerson() == null) {
                textviewfollowupviewPagerDate.setText("" + communicationModeList.get(Integer.parseInt(limitFollowUps.get(position).getCommid()) - 1));
            } else if (limitFollowUps.get(position).getCommid() == null
                    && limitFollowUps.get(position).getContactPerson() != null
                    && !limitFollowUps.get(position).getContactPerson().isEmpty()
                    && !limitFollowUps.get(position).getContactPerson().equals("null")) {

                if (limitFollowUps.get(position).getContactPerson().trim().length() > 8) {
                    textviewfollowupviewPagerDate.setText(context.getString(R.string.call) + " " + context.getString(R.string.with) + " "
                            + limitFollowUps.get(position).getContactPerson().trim().substring(0, 8) + "..");
                } else {
                    textviewfollowupviewPagerDate.setText(context.getString(R.string.call) + " " + context.getString(R.string.with) + " "
                            + limitFollowUps.get(position).getContactPerson().trim());
                }
            } else if (limitFollowUps.get(position).getCommid() != null) {
                textviewfollowupviewPagerDate.setText(communicationModeList.get(Integer.parseInt(limitFollowUps.get(position).getCommid()) - 1) + "");
            } else {
                textviewfollowupviewPagerDate.setText(context.getString(R.string.call) + "");
            }


            try {

                Date pastDate = simpleDateFormat.parse(limitFollowUps.get(position).getScheduledDate());
                Date currentDate = new Date();

                Calendar now = Calendar.getInstance();

                long nowInMillis = SystemClock.uptimeMillis();

                Calendar now_calendar = Calendar.getInstance();
                now_calendar.setTimeInMillis(nowInMillis);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(pastDate.getTime() - currentDate.getTime());

                long minutes = TimeUnit.MILLISECONDS.toMinutes(pastDate.getTime() - currentDate.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(pastDate.getTime() - currentDate.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(pastDate.getTime() - currentDate.getTime());
                if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                    if (hours == 1) {
                        textviewfollowupviewPagerDate.append(
                                " " + context.getString(R.string.today_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate));
                    } else
                        textviewfollowupviewPagerDate.append(" " + context.getString(R.string.today_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate));
                } else {
                    if (pastDate.getDate() - currentDate.getDate() == 1) {
                        textviewfollowupviewPagerDate.append(" " + context.getString(R.string.tommorow_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate));
                    } else
                        textviewfollowupviewPagerDate.append(" " + " at " + new SimpleDateFormat("hh:mm a").format(pastDate)
                                + ", " + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            // Add followup_viewpager_home.xml to ViewPager
            container.addView(itemView);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove followup_viewpager_home.xml username ViewPager
        container.removeView((CardView) object);

    }

}
