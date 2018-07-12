package com.accrete.sixorbit.activity.Settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amp on 23/10/17.
 */

public class ProfileActivity extends AppCompatActivity {
    String mobile;
    String userBio;
    String empId;
    String reportTo;
    String designation;
    String dateOfBirth;
    String joined;
    String lastSeen;
    String realName;
    String roles;
    String userName;
    String userId, imageURL;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout linearLayout;
    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileMessage;
    private Toolbar toolbarTop;
    private ImageView imageViewLeadInfoEdit;
    private LinearLayout profilePersonalDetailsLayout;
    private TextView textviewPersonalDetails;
    private LinearLayout profilePersonalDetailsDobLayout;
    private TextView textviewPersonalDob;
    private TextView textviewPersonalDobValue;
    private LinearLayout profilePersonalDetailsPhoneLayout;
    private TextView textviewPersonalPhone;
    private TextView textviewPersonalPhoneValue;
    private LinearLayout profilePersonalDetailsAddressLayout;
    private TextView textviewPersonalAddress;
    private TextView textviewPersonalAddressValue;
    private LinearLayout profileEmploymentDetailsLayout;
    private TextView textviewEmploymentDetails;
    private LinearLayout profileEmployeeDetailsEidLayout;
    private TextView textviewEmployeeEid;
    private TextView textviewEmployeeEidValue;
    private LinearLayout profileEmployeeDetailsDesignationLayout;
    private TextView textviewEmployeeDesignation;
    private TextView textviewEmployeeDesignationValue;
    private LinearLayout profileEmployeeDetailsReportLayout;
    private TextView textviewEmployeeReportTo;
    private TextView textviewEmployeeReportToValue;
    private LinearLayout profileAccountDetailsLayout;
    private TextView textviewAccountDetails;
    private LinearLayout profileAccountDetailsJoinedLayout;
    private TextView textviewAccountJoined;
    private TextView textviewAccountJoinedValue;
    private LinearLayout profileAccountDetailsLastSeenLayout;
    private TextView textviewAccountLastseen;
    private TextView textviewAccountLastseenValue;
    private LinearLayout profileAccountDetailsRealNameLayout;
    private TextView textviewAccountRealName;
    private TextView textviewAccountRealNameValue;
    private LinearLayout profileAccountDetailsRolesLayout;
    private TextView textviewAccountRoles, textviewAccountRolesValue;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseHandler(this);
        findViews();
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTop.setTitle("");
        setSupportActionBar(toolbarTop);
        collapsingToolbar.setTitleEnabled(false);

        toolbarTop.setNavigationIcon(R.drawable.ic_back_arrow);

        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallback();
            }
        });

        setData();

    }

    private void setData() {
        String address = "";
        userName = AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_NAME);
        userBio = AppPreferences.getUserBio(getApplicationContext(), AppUtils.USER_BIO);
        imageURL = AppPreferences.getPhoto(getApplicationContext(), AppUtils.USER_PHOTO);
        userId = AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_ID);
        dateOfBirth = AppPreferences.getUserDateOfBirth(getApplicationContext(), AppUtils.USER_DOB);
        mobile = AppPreferences.getUserMobile(getApplicationContext(), AppUtils.USER_MOBILE);
        empId = AppPreferences.getUserEmployeeID(getApplicationContext(), AppUtils.USER_EMP_ID);
        designation = AppPreferences.getUserDesignation(getApplicationContext(), AppUtils.USER_DESIGNATION);
        reportTo = AppPreferences.getUserReportTo(getApplicationContext(), AppUtils.USER_REPORT_TO);
        joined = AppPreferences.getUserJoined(getApplicationContext(), AppUtils.USER_JOINED);
        lastSeen = AppPreferences.getUserLastSeen(getApplicationContext(), AppUtils.USER_LAST_SEEN);
        realName = AppPreferences.getUserRealName(getApplicationContext(), AppUtils.USER_REAL_NAME);
        roles = AppPreferences.getUserRoles(getApplicationContext(), AppUtils.USER_ROLES);

        //Address Line 1
        if (AppPreferences.getUserAddressLine1(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_ONE) != null &&
                !AppPreferences.getUserAddressLine1(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_ONE).isEmpty()) {
            address = address + AppPreferences.getUserAddressLine1(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_ONE) + ", ";
        }

        //Address Line 2
        if (AppPreferences.getUserAddressLine2(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_TWO) != null &&
                !AppPreferences.getUserAddressLine2(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_TWO).isEmpty()) {
            address = address + AppPreferences.getUserAddressLine2(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_TWO) + ", ";
        }

        //City & Zip Code
        if (AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY) != null &&
                !AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY).toString().trim().isEmpty() &&
                AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE) != null &&
                !AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE).toString().trim().isEmpty()) {
            address = address +
                    AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY).toString().trim() + " - " +
                    AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE)
                            .toString().trim() + "," + "\n";
        } else if (AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY) != null &&
                !AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY).toString().trim().isEmpty()) {
            address = address +
                    AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY).toString().trim() + ", ";
        } else if (AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE) != null &&
                !AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE).toString().trim().isEmpty()) {
            address = address +
                    AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE)
                            .toString().trim() + ", ";
        }

        /*//Zip Code
        if (AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE) != null &&
                !AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE).toString().trim().isEmpty()
                && (AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY) == null ||
                AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY).toString().trim().isEmpty())) {
            address = address + AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE)
                    .toString().trim() + "," + "\n";
        } else {
            if (AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE) != null && !AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE).toString().trim().isEmpty()) {
                address = address + " - " +
                        AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE).toString().trim()
                        + "," + "\n";
            }
        }*/

        //State
        if (AppPreferences.getUserState(getApplicationContext(), AppUtils.USER_STATE) != null &&
                !AppPreferences.getUserState(getApplicationContext(), AppUtils.USER_STATE).isEmpty()) {
            address = address + AppPreferences.getUserState(getApplicationContext(), AppUtils.USER_STATE) + ", ";
        }

        //Country
        if (AppPreferences.getUserCountry(getApplicationContext(), AppUtils.USER_COUNTRY) != null &&
                !AppPreferences.getUserCountry(getApplicationContext(), AppUtils.USER_COUNTRY).isEmpty()) {
            address = address + AppPreferences.getUserCountry(getApplicationContext(), AppUtils.USER_COUNTRY) + " ";
        }

        List<ChatContacts> chatContacts = db.getAllAssignee();

        if (userName != null && !userName.isEmpty() && !userName.equals("null")) {
            profileName.setText(userName);
            profileName.setVisibility(View.VISIBLE);
        } else {
            profileName.setVisibility(View.GONE);

        }
        if (userBio != null && !userBio.isEmpty() && !userBio.equals("null")) {
            profileMessage.setText(userBio);
            profileMessage.setVisibility(View.VISIBLE);
        } else {
            profileMessage.setVisibility(View.GONE);
        }
        if (dateOfBirth != null && !dateOfBirth.isEmpty() && !dateOfBirth.equals("null") && !dateOfBirth.equals("0000-00-00")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                textviewPersonalDobValue.setText(dateFormat.format(format.parse(dateOfBirth)));
                profilePersonalDetailsDobLayout.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            profilePersonalDetailsDobLayout.setVisibility(View.GONE);
        }
        if (mobile != null && !mobile.isEmpty() && !mobile.equals("null")) {
            textviewPersonalPhoneValue.setText(mobile);
            profilePersonalDetailsPhoneLayout.setVisibility(View.VISIBLE);
        } else {
            profilePersonalDetailsPhoneLayout.setVisibility(View.GONE);
        }
        if (empId != null && !empId.isEmpty() && !empId.equals("null")) {
            textviewEmployeeEidValue.setText(empId);
            profileEmployeeDetailsEidLayout.setVisibility(View.VISIBLE);
        } else {
            profileEmployeeDetailsEidLayout.setVisibility(View.GONE);
        }
        if (designation != null && !designation.isEmpty() && !designation.equals("null")) {
            textviewEmployeeDesignationValue.setText(designation);
            profileEmployeeDetailsDesignationLayout.setVisibility(View.VISIBLE);
        } else {
            profileEmployeeDetailsDesignationLayout.setVisibility(View.GONE);
        }
        if (reportTo != null && !reportTo.isEmpty() && !reportTo.equals("null") && !reportTo.equals("0")) {
            for (int i = 0; i < chatContacts.size(); i++) {
                if (Integer.valueOf(reportTo) == chatContacts.get(i).getUid()) {
                    textviewEmployeeReportToValue.setText(chatContacts.get(i).getName());
                }
            }
            profileEmployeeDetailsReportLayout.setVisibility(View.VISIBLE);

        } else {
            profileEmployeeDetailsReportLayout.setVisibility(View.GONE);
        }
        if (joined != null && !joined.isEmpty() && !joined.equals("null") && !joined.equals("0000-00-00")) {
            textviewAccountJoinedValue.setText(joined);
            profileAccountDetailsJoinedLayout.setVisibility(View.VISIBLE);
        } else {
            profileAccountDetailsJoinedLayout.setVisibility(View.GONE);
        }
        if (lastSeen != null && !lastSeen.isEmpty() && !lastSeen.equals("null")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                textviewAccountLastseenValue.setText(dateFormat.format(format.parse(lastSeen)));
                profileAccountDetailsLastSeenLayout.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            profileAccountDetailsLastSeenLayout.setVisibility(View.GONE);
        }

        if (realName != null && !realName.isEmpty() && !realName.equals("null")) {
            textviewAccountRealNameValue.setText(realName);
            profileAccountDetailsRealNameLayout.setVisibility(View.VISIBLE);
        } else {
            profileAccountDetailsRealNameLayout.setVisibility(View.GONE);
        }


        String rolesReplace = roles;
        char[] myNameChars = rolesReplace.toCharArray();
        try {
            myNameChars = Arrays.copyOfRange(myNameChars, 1, myNameChars.length - 1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        rolesReplace = String.valueOf(myNameChars);

        if (roles != null && !roles.isEmpty() && !roles.equals("null") && !roles.equals("[null]") && myNameChars.length > 0) {
            textviewAccountRolesValue.setText(rolesReplace.replace(",", ",\n"));
            profileAccountDetailsRolesLayout.setVisibility(View.VISIBLE);
        } else {
            profileAccountDetailsRolesLayout.setVisibility(View.GONE);
        }
        if (address != null && !address.isEmpty() && !address.equals("null")) {
            textviewPersonalAddressValue.setText(address);
            profilePersonalDetailsAddressLayout.setVisibility(View.VISIBLE);
        } else {
            profilePersonalDetailsAddressLayout.setVisibility(View.GONE);
        }


        Glide.with(getApplicationContext())
                .load(imageURL)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.icon_neutral_profile)
                .into(profileImage);

        imageViewLeadInfoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEditLead = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intentEditLead.putExtra("event", "profile");
                startActivityForResult(intentEditLead, AppUtils.PROFILE_REQUEST_CODE);
            }
        });

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    toolbarTop.setTitle(userName);
                    showOption();
                } else if (isShow) {
                    isShow = false;
                    toolbarTop.setTitle(" ");
                    hideOption();
                }
            }

            private void hideOption() {
                linearLayout.setVisibility(View.VISIBLE);
            }

            private void showOption() {
                linearLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRestart() {  // After a pause OR at startup
        super.onRestart();
        //Refresh your stuff here
        initView();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e("Inside Bitmap", String.valueOf(image));
        return image;
    }

    public void setCallback() {
        Intent resultIntent = new Intent();
        setResult(1000, resultIntent);
        finish();
    }

    private void findViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileMessage = (TextView) findViewById(R.id.profile_message);
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        imageViewLeadInfoEdit = (ImageView) findViewById(R.id.imageView_lead_info_edit);
        profilePersonalDetailsLayout = (LinearLayout) findViewById(R.id.profile_personal_details_layout);
        textviewPersonalDetails = (TextView) findViewById(R.id.textview_personal_details);
        profilePersonalDetailsDobLayout = (LinearLayout) findViewById(R.id.profile_personal_details_dob_layout);
        textviewPersonalDob = (TextView) findViewById(R.id.textview_personal_dob);
        textviewPersonalDobValue = (TextView) findViewById(R.id.textview_personal_dob_value);
        profilePersonalDetailsPhoneLayout = (LinearLayout) findViewById(R.id.profile_personal_details_phone_layout);
        textviewPersonalPhone = (TextView) findViewById(R.id.textview_personal_phone);
        textviewPersonalPhoneValue = (TextView) findViewById(R.id.textview_personal_phone_value);
        profilePersonalDetailsAddressLayout = (LinearLayout) findViewById(R.id.profile_personal_details_address_layout);
        textviewPersonalAddress = (TextView) findViewById(R.id.textview_personal_address);
        textviewPersonalAddressValue = (TextView) findViewById(R.id.textview_personal_address_value);
        profileEmploymentDetailsLayout = (LinearLayout) findViewById(R.id.profile_employment_details_layout);
        textviewEmploymentDetails = (TextView) findViewById(R.id.textview_employment_details);
        profileEmployeeDetailsEidLayout = (LinearLayout) findViewById(R.id.profile_employee_details_eid_layout);
        textviewEmployeeEid = (TextView) findViewById(R.id.textview_employee_eid);
        textviewEmployeeEidValue = (TextView) findViewById(R.id.textview_employee_eid_value);
        profileEmployeeDetailsDesignationLayout = (LinearLayout) findViewById(R.id.profile_employee_details_designation_layout);
        textviewEmployeeDesignation = (TextView) findViewById(R.id.textview_employee_designation);
        textviewEmployeeDesignationValue = (TextView) findViewById(R.id.textview_employee_designation_value);
        profileEmployeeDetailsReportLayout = (LinearLayout) findViewById(R.id.profile_employee_details_report_layout);
        textviewEmployeeReportTo = (TextView) findViewById(R.id.textview_employee_report_to);
        textviewEmployeeReportToValue = (TextView) findViewById(R.id.textview_employee_report_to_value);
        profileAccountDetailsLayout = (LinearLayout) findViewById(R.id.profile_account_details_layout);
        textviewAccountDetails = (TextView) findViewById(R.id.textview_account_details);
        profileAccountDetailsJoinedLayout = (LinearLayout) findViewById(R.id.profile_account_details_joined_layout);
        textviewAccountJoined = (TextView) findViewById(R.id.textview_account_joined);
        textviewAccountJoinedValue = (TextView) findViewById(R.id.textview_account_joined_value);
        profileAccountDetailsLastSeenLayout = (LinearLayout) findViewById(R.id.profile_account_details_last_seen_layout);
        textviewAccountLastseen = (TextView) findViewById(R.id.textview_account_lastseen);
        textviewAccountLastseenValue = (TextView) findViewById(R.id.textview_account_lastseen_value);
        profileAccountDetailsRealNameLayout = (LinearLayout) findViewById(R.id.profile_account_details_real_name_layout);
        textviewAccountRealName = (TextView) findViewById(R.id.textview_account_real_name);
        textviewAccountRealNameValue = (TextView) findViewById(R.id.textview_account_real_name_value);
        profileAccountDetailsRolesLayout = (LinearLayout) findViewById(R.id.profile_account_details_roles_layout);
        textviewAccountRoles = (TextView) findViewById(R.id.textview_account_roles);
        textviewAccountRolesValue = (TextView) findViewById(R.id.textview_account_roles_value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtils.PROFILE_REQUEST_CODE) {
            setData();
        }
    }
}
