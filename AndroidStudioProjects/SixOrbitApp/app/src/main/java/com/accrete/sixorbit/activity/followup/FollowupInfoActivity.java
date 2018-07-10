package com.accrete.sixorbit.activity.followup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class FollowupInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FollowUpInfoActivity";
    Lead lead = new Lead();
    private String companyCode, assignedUser;
    private CollapsingToolbarLayout collapsingToolbar;
    private String CopyText, CopiedText = " ";
    private Toolbar toolbar;
    private LinearLayout leadLayout;
    private TextView leadTextView;
    private TextView leadValueTextView;
    private LinearLayout takenOnLayout;
    private TextView takenOnTextView;
    private TextView takenOnValueTextView;
    private LinearLayout schdeuledDateLayout;
    private TextView schdeuledDateTextView;
    private TextView schdeuledDateValueTextView;
    private LinearLayout alertOnLayout;
    private TextView alertOnTextView;
    private TextView alertOnValueTextView;
    private LinearLayout commentLayout;
    private TextView commentTextView;
    private TextView commentValueTextView;
    private LinearLayout feedbackLayout;
    private TextView feedbackTextView;
    private TextView feedbackValueTextView;
    private LinearLayout contactPersonLayout;
    private TextView contactPersonTextView;
    private TextView contactPersonValueTextView;
    private LinearLayout commModeLayout;
    private TextView commModeTextView;
    private TextView commModeValueTextView;
    private LinearLayout parentFollowupLayout;
    private TextView parentFollowupTextView;
    private TextView parentFollowupValueTextView;
    private LinearLayout outcomeLayout;
    private TextView outcomeTextView;
    private TextView outcomeValueTextView;
    private LinearLayout reasonLayout;
    private TextView reasonTextView;
    private TextView reasonValueTextView;
    private LinearLayout statusLayout;
    private LinearLayout alertModeLayout;
    private TextView alertModeTextView;
    private TextView alertModeValueTextView;
    private TextView statusValueTextView;
    private LinearLayout createdByLayout;
    private TextView createdByTextView;
    private TextView createdByValueTextView;
    private LinearLayout updatedByLayout;
    private TextView updatedByTextView;
    private TextView updatedByValueTextView;
    private LinearLayout assignedToLayout;
    private TextView assignedToTextView;
    private TextView assignedToValueTextView;
    private LinearLayout createdTimeLayout;
    private TextView createdTimeTextView;
    private TextView createdTimeValueTextView;
    private LinearLayout updatedTimeLayout;
    private TextView updatedTimeTextView;
    private TextView updatedTimeValueTextView;
    private Toolbar toolbarBottom;
    private LinearLayout layoutIdCopy;
    private TextView textviewCopy;
    private TextView textCopy;
    private LinearLayout layoutIdCall;
    private TextView textviewCall;
    private TextView textCall;
    private LinearLayout layoutIdShare;
    private TextView textviewShare;
    private TextView textShare;
    private DatabaseHandler databaseHandler;
    private String foid;
    private FollowUp followUp;
    private AppBarLayout appBarLayout;
    private String syncId;
    private String[] outcome = {"Not interested", "Call later", "Price too high", "Wrong contact", "No response"};
    //private String[] communicationMode = {"Mail", "Phone Call", "Skype Call"};
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.layout_id_copy:
                CopyText = CopiedText.toString();
                copyToClipboard(CopyText);
                //CustomisedToast.error(getApplicationContext(), "Copy text will be available soon.").show();
                break;
            case R.id.layout_id_call:

                if (Build.VERSION.SDK_INT >= 23) {
                    callIntent();
                } else {
                    callActionLeadInfoFollowups();
                }

                break;

            case R.id.layout_id_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, CopiedText);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));

                break;
        }
    }

    /* Method for copying the string data*/
    public void copyToClipboard(String copyText) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copyText);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText(getString(R.string.your_data), copyText);
            clipboard.setPrimaryClip(clip);
        }

        Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.data_copied), Toast.LENGTH_SHORT);
        Log.e("Copied data", copyText);
        toast.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followup_info);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (AppPreferences.getModes(FollowupInfoActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(FollowupInfoActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }

        databaseHandler = new DatabaseHandler(this);
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a, dd MMM yyyy");

        //All Ids
        appBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        leadLayout = (LinearLayout) findViewById(R.id.lead_layout);
        leadTextView = (TextView) findViewById(R.id.lead_textView);
        leadValueTextView = (TextView) findViewById(R.id.lead_value_textView);
        takenOnLayout = (LinearLayout) findViewById(R.id.takenOn_layout);
        takenOnTextView = (TextView) findViewById(R.id.takenOn_textView);
        takenOnValueTextView = (TextView) findViewById(R.id.takenOn_value_textView);
        schdeuledDateLayout = (LinearLayout) findViewById(R.id.schdeuledDate_layout);
        schdeuledDateTextView = (TextView) findViewById(R.id.schdeuledDate_textView);
        schdeuledDateValueTextView = (TextView) findViewById(R.id.schdeuledDate_value_textView);
        alertOnLayout = (LinearLayout) findViewById(R.id.alertOn_layout);
        alertOnTextView = (TextView) findViewById(R.id.alertOn_textView);
        alertOnValueTextView = (TextView) findViewById(R.id.alertOn_value_textView);
        commentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        commentTextView = (TextView) findViewById(R.id.comment_textView);
        commentValueTextView = (TextView) findViewById(R.id.comment_value_textView);
        feedbackLayout = (LinearLayout) findViewById(R.id.feedback_layout);
        feedbackTextView = (TextView) findViewById(R.id.feedback_textView);
        feedbackValueTextView = (TextView) findViewById(R.id.feedback_value_textView);
        contactPersonLayout = (LinearLayout) findViewById(R.id.contactPerson_layout);
        contactPersonTextView = (TextView) findViewById(R.id.contactPerson_textView);
        contactPersonValueTextView = (TextView) findViewById(R.id.contactPerson_value_textView);
        commModeLayout = (LinearLayout) findViewById(R.id.commMode_layout);
        commModeTextView = (TextView) findViewById(R.id.commMode_textView);
        commModeValueTextView = (TextView) findViewById(R.id.commMode_value_textView);
        parentFollowupLayout = (LinearLayout) findViewById(R.id.parentFollowup_layout);
        parentFollowupTextView = (TextView) findViewById(R.id.parentFollowup_textView);
        parentFollowupValueTextView = (TextView) findViewById(R.id.parentFollowup_value_textView);
        outcomeLayout = (LinearLayout) findViewById(R.id.outcome_layout);
        outcomeTextView = (TextView) findViewById(R.id.outcome_textView);
        outcomeValueTextView = (TextView) findViewById(R.id.outcome_value_textView);
        reasonLayout = (LinearLayout) findViewById(R.id.reason_layout);
        reasonTextView = (TextView) findViewById(R.id.reason_textView);
        reasonValueTextView = (TextView) findViewById(R.id.reason_value_textView);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        // statusTextView = (TextView) findViewById(R.id.status_textView);
        statusValueTextView = (TextView) findViewById(R.id.status_value_textView);
        createdByLayout = (LinearLayout) findViewById(R.id.createdBy_layout);
        createdByTextView = (TextView) findViewById(R.id.createdBy_textView);
        createdByValueTextView = (TextView) findViewById(R.id.createdBy_value_textView);
        updatedByLayout = (LinearLayout) findViewById(R.id.updatedBy_layout);
        updatedByTextView = (TextView) findViewById(R.id.updatedBy_textView);
        updatedByValueTextView = (TextView) findViewById(R.id.updatedBy_value_textView);
        assignedToLayout = (LinearLayout) findViewById(R.id.assignedTo_layout);
        assignedToTextView = (TextView) findViewById(R.id.assignedTo_textView);
        assignedToValueTextView = (TextView) findViewById(R.id.assignedTo_value_textView);
        createdTimeLayout = (LinearLayout) findViewById(R.id.createdTime_layout);
        createdTimeTextView = (TextView) findViewById(R.id.createdTime_textView);
        createdTimeValueTextView = (TextView) findViewById(R.id.createdTime_value_textView);
        updatedTimeLayout = (LinearLayout) findViewById(R.id.updatedTime_layout);
        updatedTimeTextView = (TextView) findViewById(R.id.updatedTime_textView);
        updatedTimeValueTextView = (TextView) findViewById(R.id.updatedTime_value_textView);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        layoutIdCopy = (LinearLayout) findViewById(R.id.layout_id_copy);
        textviewCopy = (TextView) findViewById(R.id.textview_copy);
        textCopy = (TextView) findViewById(R.id.text_copy);
        layoutIdCall = (LinearLayout) findViewById(R.id.layout_id_call);
        textviewCall = (TextView) findViewById(R.id.textview_icon_call);
        textCall = (TextView) findViewById(R.id.textview_icon_call);
        layoutIdShare = (LinearLayout) findViewById(R.id.layout_id_share);
        textviewShare = (TextView) findViewById(R.id.textview_share);
        textShare = (TextView) findViewById(R.id.text_share);
        alertModeLayout = (LinearLayout) findViewById(R.id.alertMode_layout);
        alertModeTextView = (TextView) findViewById(R.id.alertMode_textView);
        alertModeValueTextView = (TextView) findViewById(R.id.alertMode_value_textView);
        //Set Title Texts
        leadTextView.setText(R.string.lead_title);
        takenOnTextView.setText(R.string.taken_on_title);
        schdeuledDateTextView.setText(R.string.schedule_date_title);
        alertOnTextView.setText(R.string.alert_on_title);
        commentTextView.setText(R.string.comment_title);
        feedbackTextView.setText(R.string.feedback_title);
        contactPersonTextView.setText(R.string.contact_person_title);
        commModeTextView.setText(R.string.communication_mode_title);
        parentFollowupTextView.setText(R.string.parent_id_title);
        outcomeTextView.setText(R.string.outcome_title);
        reasonTextView.setText(R.string.reason_title);
//        statusTextView.setText(R.string.status_title);
        createdByTextView.setText(R.string.createdby_title);
        updatedByTextView.setText(R.string.updatedby_title);
        assignedToTextView.setText(R.string.assignedto_title);
        createdTimeTextView.setText(R.string.createdtime_title);
        updatedTimeTextView.setText(R.string.updatedtime_title);
        alertModeTextView.setText(R.string.alertmode_title);

        textviewCopy.setTypeface(fontAwesomeFont);
        textviewCall.setTypeface(fontAwesomeFont);
        textviewShare.setTypeface(fontAwesomeFont);

        //get Data username intent first
        if (getIntent() != null) {
            foid = getIntent().getStringExtra(getString(R.string.foid));
            syncId = getIntent().getStringExtra(getString(R.string.syncid));
            //Getting data username database
            if (foid != null && !foid.isEmpty()) {
                followUp = databaseHandler.getSingleFollowUpResult(foid, getString(R.string.foid));
            } else if (syncId != null && !syncId.isEmpty()) {
                followUp = databaseHandler.getSingleFollowUpResult(syncId, getString(R.string.syncid));
            }
        }

        companyCode = AppPreferences.getCompanyCode(getApplicationContext(), AppUtils.COMPANY_CODE);

        //Check if Lead Id is empty
        if (followUp.getLeadId() != null && !followUp.getLeadId().isEmpty() &&
                followUp.getLeadIdR() != null && !followUp.getLeadIdR().isEmpty()) {
            leadValueTextView.setText(followUp.getLeadIdR());
            leadLayout.setVisibility(View.VISIBLE);
        } else
            //Check if Enquiry Id is empty
            if (followUp.getEnid() != null && !followUp.getEnid().isEmpty()) {
                leadTextView.setText("Enquiry :");
                leadValueTextView.setText(followUp.getEnquiryId());
                leadLayout.setVisibility(View.VISIBLE);
            } else
                //Check if Quotation Id is empty
                if (followUp.getQoid() != null && !followUp.getQoid().isEmpty()) {
                    leadTextView.setText("Quotation :");
                    leadValueTextView.setText(followUp.getQuotationId());
                    leadLayout.setVisibility(View.VISIBLE);
                }
                //Check if Order Id is empty or not
                else if (followUp.getChkoid() != null && !followUp.getChkoid().isEmpty()) {
                    leadTextView.setText("Order :");
                    leadValueTextView.setText(followUp.getOrderId());
                    leadLayout.setVisibility(View.VISIBLE);
                } else
                    //Check if Purchase Order Id is empty
                    if (followUp.getPurorid() != null && !followUp.getPurorid().isEmpty()) {
                        leadTextView.setText("Purchase Order :");
                        leadValueTextView.setText(followUp.getPurchaseOrderId());
                        leadLayout.setVisibility(View.VISIBLE);
                    } else {
                        leadLayout.setVisibility(View.GONE);
                    }

        if (followUp.getTakenOn() != null && !followUp.getTakenOn().equals("null") && !followUp.getTakenOn().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getTakenOn());
                takenOnValueTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            takenOnLayout.setVisibility(View.VISIBLE);

        } else {
            takenOnLayout.setVisibility(View.GONE);
        }

        if (followUp.getScheduledDate() != null && !followUp.getScheduledDate().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getScheduledDate());
                schdeuledDateValueTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            schdeuledDateLayout.setVisibility(View.VISIBLE);

        } else {
            schdeuledDateLayout.setVisibility(View.GONE);
        }


        if (followUp.getAlertOn() != null && !followUp.getAlertOn().isEmpty() && !followUp.getAlertOn().contains("0000")) {
            try {
                Date date = simpleDateFormat.parse(followUp.getAlertOn());
                alertOnValueTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            alertOnLayout.setVisibility(View.VISIBLE);

        } else {
            alertOnLayout.setVisibility(View.GONE);
        }


        if (followUp.getComment() != null && !followUp.getComment().isEmpty()) {
            commentValueTextView.setText(Html.fromHtml(followUp.getComment()));
            commentLayout.setVisibility(View.VISIBLE);

        } else {
            commentLayout.setVisibility(View.GONE);
        }


        if (followUp.getFeedback() != null && !followUp.getFeedback().isEmpty()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Spanned htmlAsSpanned = Html.fromHtml(Html.fromHtml(followUp.getFeedback(), Html.FROM_HTML_MODE_COMPACT).toString(), Html.FROM_HTML_MODE_COMPACT);
                feedbackValueTextView.setText(htmlAsSpanned);
            } else {
                feedbackValueTextView.setText(Html.fromHtml(Html.fromHtml(followUp.getFeedback()).toString()));
            }
            feedbackLayout.setVisibility(View.VISIBLE);
        } else {
            feedbackLayout.setVisibility(View.GONE);
        }


        if (followUp.getContactPerson() != null && !followUp.getContactPerson().isEmpty() && !followUp.getContactPerson().equals("null")) {
            contactPersonValueTextView.setText(followUp.getContactPerson() + "");
            contactPersonLayout.setVisibility(View.VISIBLE);
        } else {
            contactPersonLayout.setVisibility(View.GONE);
        }

        if (followUp.getAlertMode() != null && !followUp.getAlertMode().isEmpty() && !followUp.getAlertMode().equals("null")) {
            if (followUp.getAlertMode().equals("[1,2]")) {
                alertModeValueTextView.setText("Both" + "");
            } else if (followUp.getAlertMode().equals("[1]")) {
                alertModeValueTextView.setText("SMS" + "");
            } else if (followUp.getAlertMode().equals("[2]")) {
                alertModeValueTextView.setText("Mail" + "");
            } else if (followUp.getAlertMode().equals("[0]")) {
                alertModeValueTextView.setText("None" + "");
            } else {
                alertModeValueTextView.setText(followUp.getAlertMode() + "");
            }
            alertModeLayout.setVisibility(View.VISIBLE);
        } else {
            alertModeLayout.setVisibility(View.GONE);
        }

        if (followUp.getFollowupCommunicationMode() != null && !followUp.getFollowupCommunicationMode().equals("null") && !followUp.getFollowupCommunicationMode().isEmpty()) {
            commModeValueTextView.setText(followUp.getFollowupCommunicationMode() + "");
            commModeLayout.setVisibility(View.VISIBLE);
        } else {
            if (followUp.getCommid() != null && !followUp.getCommid().isEmpty()) {
                commModeValueTextView.setText(communicationModeList.get(Integer.parseInt(followUp.getCommid()) - 1).getName());
                //commModeValueTextView.setText(communicationMode[Integer.parseInt(followUp.getCommid()) - 1]);
                commModeLayout.setVisibility(View.VISIBLE);
            } else {
                commModeLayout.setVisibility(View.GONE);
            }
        }

        //TODO - Updated on 10th May
        if (followUp.getParentFollowupId() != null && !followUp.getParentFollowupId().isEmpty()) {
            /*parentFollowupValueTextView.setText(AppPreferences.getCompanyCode(FollowupInfoActivity.this, AppUtils.COMPANY_CODE)
                    + "FU" + String.format("%06d", Integer.parseInt(followUp.getParentId())));*/
            parentFollowupValueTextView.setText("" + followUp.getParentFollowupId());
            parentFollowupLayout.setVisibility(View.VISIBLE);
        } else {
            parentFollowupLayout.setVisibility(View.GONE);
        }

        if (followUp.getFollowupOutcome() != null && !followUp.getFollowupOutcome().equals("null") && !followUp.getFollowupOutcome().isEmpty()) {
            if (isNumeric(followUp.getFollowupOutcome())) {
                outcomeValueTextView.setText(outcome[Integer.parseInt(followUp.getFollowupOutcome()) - 1]);
            } else {
                outcomeValueTextView.setText(followUp.getFollowupOutcome() + "");
            }
            outcomeLayout.setVisibility(View.VISIBLE);
        } else {
            outcomeLayout.setVisibility(View.GONE);
        }
        if (followUp.getReason() != null && !followUp.getReason().isEmpty()) {
            reasonValueTextView.setText(followUp.getReason() + "");
            reasonLayout.setVisibility(View.VISIBLE);
        } else {
            reasonLayout.setVisibility(View.GONE);
        }

        if (followUp.getFosid() != null && followUp.getFosid().equals("1") && !followUp.getFosid().isEmpty()) {
            statusValueTextView.setText(getString(R.string.followup_status_pending));
            statusLayout.setBackgroundColor(getResources().getColor(R.color.yellow_divider));
            takenOnLayout.setVisibility(View.GONE);
            commentLayout.setVisibility(View.GONE);
            feedbackLayout.setVisibility(View.GONE);
            outcomeLayout.setVisibility(View.GONE);
            reasonLayout.setVisibility(View.GONE);
            updatedByLayout.setVisibility(View.GONE);
        } else {
            statusValueTextView.setText(getString(R.string.followup_status_taken));
            statusLayout.setBackgroundColor(getResources().getColor(R.color.Green_ForestGreen));
        }

        if (followUp.getCreatedUser() != null && !followUp.getCreatedUser().isEmpty() && followUp.getCreatedUser() != "") {
            try {
                JSONObject jsonObject = new JSONObject(followUp.getCreatedUser());
                createdByValueTextView.setText(jsonObject.getString(getString(R.string.name)));
            } catch (JSONException e) {
                //  e.printStackTrace();
                createdByValueTextView.setText(followUp.getCreatedUser());
            }
            createdByLayout.setVisibility(View.VISIBLE);
        } else {
            createdByLayout.setVisibility(View.GONE);
        }
        //Log.e("updated out FIA", followUp.getUpdatedUser());
        if (followUp.getUpdatedUser() != null && !followUp.getUpdatedUser().isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(followUp.getUpdatedUser());
                updatedByValueTextView.setText(jsonObject.getString(getString(R.string.name)));
            } catch (JSONException e) {
                //   e.printStackTrace();
                updatedByValueTextView.setText(followUp.getUpdatedUser());
            }

            updatedByLayout.setVisibility(View.VISIBLE);
        } else {
            updatedByLayout.setVisibility(View.GONE);
        }
        if (followUp.getAssignedUser() != null && !followUp.getAssignedUser().isEmpty() && followUp.getAssignedUser() != "") {
            try {
                try {
                    if (Constants.isNumeric(followUp.getAssignedUser())) {
                        ChatContacts chatContacts = databaseHandler.getUserData(Integer.parseInt(followUp.getAssignedUser()));
                        if (chatContacts.getName() != null && !chatContacts.getName().isEmpty()) {
                            assignedToValueTextView.setText(chatContacts.getName());
                            assignedToLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(followUp.getAssignedUser());
                        assignedUser = jsonObject.getString(getString(R.string.name));
                        if (assignedUser != null && !assignedUser.isEmpty()) {
                            assignedToValueTextView.setText(assignedUser);
                            assignedToLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (followUp.getAssignedUser() != null && !followUp.getAssignedUser().isEmpty()) {
                        assignedToValueTextView.setText(followUp.getAssignedUser());
                        assignedToLayout.setVisibility(View.VISIBLE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                assignedToLayout.setVisibility(View.GONE);
            }

        } else if (followUp.getAssignedUid() != null && !followUp.getAssignedUid().isEmpty() && followUp.getAssignedUid() != "") {
            try {
                ChatContacts chatContacts = databaseHandler.getUserData(Integer.parseInt(followUp.getAssignedUid()));
                if (chatContacts.getName() != null && !chatContacts.getName().isEmpty()) {
                    assignedToValueTextView.setText(chatContacts.getName());
                    assignedToLayout.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                assignedToLayout.setVisibility(View.GONE);
            }

        } else {
            assignedToLayout.setVisibility(View.GONE);
        }


        if (followUp.getCreatedTs() != null && !followUp.getCreatedTs().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getCreatedTs());
                createdTimeValueTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createdTimeLayout.setVisibility(View.VISIBLE);
        } else {
            createdTimeLayout.setVisibility(View.GONE);
        }

        if (followUp.getUpdatedTs() != null && !followUp.getUpdatedTs().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getUpdatedTs());
                updatedTimeValueTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            updatedTimeLayout.setVisibility(View.VISIBLE);
        } else {
            updatedTimeLayout.setVisibility(View.GONE);
        }

        //Click Listeners
        layoutIdCall.setOnClickListener(this);
        layoutIdCopy.setOnClickListener(this);
        layoutIdShare.setOnClickListener(this);


        if (followUp.getLeadId() != null && !followUp.getLeadId().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.lead_title) + " " + followUp.getLeadIdR();
        } else if (followUp.getEnquiryId() != null && !followUp.getEnquiryId().isEmpty()) {
            CopiedText = CopiedText + "\n" + "Enquiry :" + " " + followUp.getEnquiryId();
        } else if (followUp.getQoid() != null && !followUp.getQoid().isEmpty()) {
            CopiedText = CopiedText + "\n" + "Quotation :" + " " + followUp.getQuotationId();
        } else if (followUp.getChkoid() != null && !followUp.getChkoid().isEmpty()) {
            CopiedText = CopiedText + "\n" + "Order :" + " " + followUp.getOrderId();
        } else if (followUp.getPurorid() != null && !followUp.getPurorid().isEmpty()) {
            CopiedText = CopiedText + "\n" + "Purchase Order :" + " " + followUp.getPurchaseOrderId();
        }

        if (followUp.getTakenOn() != null && !followUp.getTakenOn().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.taken_on_title) + " " + followUp.getTakenOn();
        }
        if (followUp.getScheduledDate() != null && !followUp.getScheduledDate().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.schedule_date_title) + " " + followUp.getScheduledDate();
        }
        if (followUp.getAlertOn() != null && !followUp.getAlertOn().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.alert_on_title) + " " + followUp.getAlertOn();
        }
        if (followUp.getComment() != null && !followUp.getComment().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.comment_title) + " " + followUp.getComment();
        }
        if (followUp.getFeedback() != null && !followUp.getFeedback().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.feedback_title) + " " + followUp.getFeedback();
        }
        if (followUp.getContactPerson() != null && !followUp.getContactPerson().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.contact_person_title) + " " + followUp.getContactPerson();
        }
        if (followUp.getFollowupCommunicationMode() != null && !followUp.getFollowupCommunicationMode().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.communication_mode_title) + " " + followUp.getFollowupCommunicationMode();
        }
        if (followUp.getFollowupOutcome() != null && !followUp.getFollowupOutcome().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.outcome_title) + " " + followUp.getFollowupOutcome();
        }
        if (followUp.getReason() != null && !followUp.getReason().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.reason_title) + " " + followUp.getReason();
        }
        if (followUp.getAlertMode() != null && !followUp.getAlertMode().isEmpty() && !followUp.getAlertMode().equals("null")) {
            CopiedText = CopiedText + "\n" + getString(R.string.alertmode_title) + " " + alertModeValueTextView.getText().toString().trim();
        }
        if (followUp.getFosid() != null && followUp.getFosid().equals("1") && !followUp.getFosid().isEmpty()) {
            CopiedText = CopiedText + "\n" + getString(R.string.status_title) + " " +
                    getString(R.string.followup_status_pending);
        } else {
            CopiedText = CopiedText + "\n" + getString(R.string.status_title) + " " +
                    getString(R.string.followup_status_taken);
        }
        if (followUp.getCreatedUser() != null && !followUp.getCreatedUser().isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(followUp.getCreatedUser());
                CopiedText = CopiedText + "\n" + getString(R.string.createdby_title) + " " + jsonObject.getString(getString(R.string.name));
            } catch (JSONException e) {
                // e.printStackTrace();
                CopiedText = CopiedText + "\n" + getString(R.string.createdby_title) + " " + followUp.getCreatedUser();
            }
        }
        if (followUp.getUpdatedUser() != null && !followUp.getUpdatedUser().isEmpty()) {
            //     CopiedText = CopiedText + "\n" + getString(R.string.updatedby_title) + followUp.getUpdatedUser();
            try {
                JSONObject jsonObject = new JSONObject(followUp.getUpdatedUser());
                CopiedText = CopiedText + "\n" + getString(R.string.updatedby_title) + " " + jsonObject.getString(getString(R.string.name));
            } catch (JSONException e) {
                // e.printStackTrace();
                CopiedText = CopiedText + "\n" + getString(R.string.updatedby_title) + " " + followUp.getUpdatedUser();
            }
        }
        if (followUp.getAssignedUser() != null && !followUp.getAssignedUser().isEmpty()) {
            //  try {
            //   JSONObject jsonObject = new JSONObject(followUp.getAssignedUser());
            CopiedText = CopiedText + "\n" + getString(R.string.assignedto_title) + " " + assignedToValueTextView.getText().toString().trim();
           /* } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
        if (followUp.getCreatedTs() != null && !followUp.getCreatedTs().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getCreatedTs());
                CopiedText = CopiedText + "\n" + getString(R.string.createdtime_title) + " " + outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (followUp.getUpdatedTs() != null && !followUp.getUpdatedTs().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(followUp.getUpdatedTs());
                CopiedText = CopiedText + "\n" + getString(R.string.updatedtime_title) + " " + outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        setNameInToolbar();
    }

    //    Lead info call action method  to call the followup contact number

    private void callIntent() {

        if (checkPermissionWithRationale(this, new FollowUpsFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void callActionLeadInfoFollowups() {
        FollowUp contactDetails;
        contactDetails = databaseHandler.getContactDetails(followUp.getLeadId());
        String mobileNumber = "";
        if (contactDetails != null && contactDetails.getContactPersonMobile() != null) {
            mobileNumber = contactDetails.getContactPersonMobile();
        } else {
            mobileNumber = followUp.getContactPersonMobile();
        }
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber == null || mobileNumber == "") {
            Toast.makeText(getApplicationContext(), "No Number", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intentCall);
        }
        Log.e("Lead info FU mnumber", mobileNumber + "");
    }

// Run Time permissions call back for Lead contacts  call action

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {

//            Request code for handling the call action for lead info contacts

            case REQUEST_CODE_ASK_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callActionLeadInfoFollowups();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

//                       Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;

            //            Request code for handling the call action for followup contact in lead info.

            case REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted

                        callActionLeadInfoFollowups();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

//                       Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
        }
    }

    private void setNameInToolbar() {
        try {
            lead = databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid));
            if (followUp.getLeadId() != null && !followUp.getLeadId().isEmpty() && !followUp.getLeadId().equals("null") &&
                    lead.getName() != null && !lead.getName().isEmpty()) {
                collapsingToolbar.setTitle("" + lead.getName());
            } else if (followUp.getName() != null && !followUp.getName().isEmpty() && !followUp.getName().toString().equals("null")) {
                collapsingToolbar.setTitle("" + followUp.getName());
            } else if (followUp.getContactPerson() != null && !followUp.getContactPerson().isEmpty() && !followUp.getContactPerson().toString().equals("null")) {
                collapsingToolbar.setTitle(WordUtils.capitalize(followUp.getContactPerson()));
            } else {
                if (foid != null) {
                    collapsingToolbar.setTitle(AppPreferences.getCompanyCode(FollowupInfoActivity.this, AppUtils.COMPANY_CODE)
                            + "FU" + String.format("%06d", Integer.parseInt(foid)));
                }
//            AppPreferences.getCompanyCode(context, AppUtils.COMPANY_CODE) + "LE" + String.format("%06d", Integer.parseInt(limitFollowUps.get(position).getLeaid())
            }
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    // Permission settings page request method

    public void askUserToAllowPermissionFromSetting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.permission_required));
        // set dialog messageTextView
        alertDialogBuilder
                .setMessage(getString(R.string.request_permission_from_settings))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}
