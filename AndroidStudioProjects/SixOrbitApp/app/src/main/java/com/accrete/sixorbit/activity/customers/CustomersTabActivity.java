package com.accrete.sixorbit.activity.customers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomersMainTabFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by agt on 18/12/17.
 */

public class CustomersTabActivity extends AppCompatActivity implements PassMobileListener, PassUsersEmailListener,
        PassSharingTextListener, View.OnClickListener {
    private String mobileNumber, emailAddress;
    private String sharingTexts;
    private AppBarLayout htabAppbar;
    private CollapsingToolbarLayout htabCollapseToolbar;
    private Toolbar toolbar;
    private ImageView imageViewInfoEdit;
    private FrameLayout container;
    private Toolbar toolbarBottom;
    private LinearLayout layoutIdCall;
    private TextView textviewIconCall;
    private TextView textCall;
    private LinearLayout layoutIdEmail;
    private TextView textviewEmail;
    private TextView textEmail;
    private LinearLayout layoutIdShare;
    private TextView textviewShare;
    private TextView textShare;
    private String cuId, emailId, name, walletBalance;
    private Typeface fontAwesomeFont;
    private int redirectionInDetail = 0;
    private DatabaseHandler databaseHandler;

    @Override
    public void passUsersEmailOnLoad(String email) {
        emailAddress = email;
    }

    @Override
    public void onClick(View v) {
        if (v == layoutIdCall) {
            layoutIdCall.setEnabled(false);
            if (mobileNumber != null && !mobileNumber.isEmpty()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    callIntent();
                } else {
                    callAction();
                }
            } else {
                Toast.makeText(CustomersTabActivity.this, "This customer has no mobile number.", Toast.LENGTH_SHORT).show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutIdCall.setEnabled(true);
                }
            }, 3000);
        } else if (v == layoutIdEmail) {
            layoutIdEmail.setEnabled(false);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            if (emailAddress != null && !emailAddress.isEmpty()) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            }
            // emailIntent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
            startActivity(Intent.createChooser(emailIntent, "Send email"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutIdEmail.setEnabled(true);
                }
            }, 3000);
        } else if (v == layoutIdShare) {
            layoutIdShare.setEnabled(false);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Customer's info");
            intent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
            startActivity(Intent.createChooser(intent, "Share"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutIdShare.setEnabled(true);
                }
            }, 3000);
        } else if (v == imageViewInfoEdit) {
            imageViewInfoEdit.setEnabled(false);
            if (AppPreferences.getBoolean(CustomersTabActivity.this, AppUtils.ISADMIN) ||
                    databaseHandler.checkUsersPermission(getString(R.string.customers_view_details_permission))) {
                Intent intent = new Intent(CustomersTabActivity.this, AddCustomerActivity.class);
                intent.putExtra(getString(R.string.cuid), cuId);
                intent.putExtra("task", "edit");
                //startActivity(intent);
                startActivityForResult(intent, 111);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageViewInfoEdit.setEnabled(true);
                    }
                }, 3000);
            } else {
                Toast.makeText(CustomersTabActivity.this, "Sorry, you've no permission to edit a customer.", Toast.LENGTH_SHORT).show();
            }
        }
    }


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
    private void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber == null || mobileNumber == "") {
            Toast.makeText(getApplicationContext(), "No Number", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intentCall);
        }
    }

    @Override
    public void passMobileOnLoad(String mobile) {
        mobileNumber = mobile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_main);

        databaseHandler = new DatabaseHandler(CustomersTabActivity.this);
        findViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.cuid))) {
            cuId = getIntent().getStringExtra(getString(R.string.cuid));
        }

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.customer_email_id))) {
            emailId = getIntent().getStringExtra(getString(R.string.customer_email_id));
        }

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.name))) {
            name = getIntent().getStringExtra(getString(R.string.name));
        }
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.wallet_balance))) {
            walletBalance = getIntent().getStringExtra(getString(R.string.wallet_balance));
        }
        if (getIntent() != null && getIntent().hasExtra("redirect")) {
            redirectionInDetail = getIntent().getIntExtra("redirect", 0);
        }
        if (name != null && !name.isEmpty()) {
            String titleName = null;
            if (name.length() > 25) {
                titleName = name.substring(0, 22) + "...";
                getSupportActionBar().setTitle(titleName);
            } else {
                getSupportActionBar().setTitle(name);
            }

        }

        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        layoutIdEmail = (LinearLayout) findViewById(R.id.layout_id_email);
        textviewEmail = (TextView) findViewById(R.id.textview_email);
        textEmail = (TextView) findViewById(R.id.text_email);
        layoutIdCall = (LinearLayout) findViewById(R.id.layout_id_call);
        textviewIconCall = (TextView) findViewById(R.id.textview_icon_call);
        textCall = (TextView) findViewById(R.id.text_call);
        layoutIdShare = (LinearLayout) findViewById(R.id.layout_id_share);
        textviewShare = (TextView) findViewById(R.id.textview_share);
        textShare = (TextView) findViewById(R.id.text_share);
        imageViewInfoEdit = (ImageView) findViewById(R.id.imageView_info_edit);
        imageViewInfoEdit.setVisibility(View.VISIBLE);
        imageViewInfoEdit.setEnabled(true);

        fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        textviewEmail.setTypeface(fontAwesomeFont);
        textviewIconCall.setTypeface(fontAwesomeFont);
        textviewShare.setTypeface(fontAwesomeFont);

        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomersMainTabFragment customersMainTabFragment = new CustomersMainTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.customer_email_id), emailId);
        bundle.putString(getString(R.string.name), name);
        bundle.putString(getString(R.string.wallet_balance), walletBalance);
        bundle.putInt("redirectionInDetail", redirectionInDetail);

        customersMainTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, customersMainTabFragment);
        fragmentTransaction.commit();

        layoutIdCall.setOnClickListener(this);
        layoutIdEmail.setOnClickListener(this);
        layoutIdShare.setOnClickListener(this);
        imageViewInfoEdit.setOnClickListener(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callAction();

                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

                            //Requesting Settings Page On Never ASk Again Click
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
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callAction();
                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            //Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment instanceof CustomersMainTabFragment) {
                ((CustomersMainTabFragment) currentFragment).refreshFragment();
                if (data != null && data.hasExtra(getString(R.string.email))) {
                    emailAddress = data.getStringExtra(getString(R.string.email));
                    ((CustomersMainTabFragment) currentFragment).getEmailAddress(emailAddress);
                }
            }
        }
    }

    @Override
    public void passSharingTextOnLoad(String str) {
        if (sharingTexts != null) {
            sharingTexts = null;
        }
        sharingTexts = str;
    }
}
