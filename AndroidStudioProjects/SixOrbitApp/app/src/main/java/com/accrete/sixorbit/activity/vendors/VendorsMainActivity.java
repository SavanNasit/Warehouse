package com.accrete.sixorbit.activity.vendors;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.PurchaseOrderHistoryFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorDetailTabFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorInvoiceTabFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorPendingInvoiceTabFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorTransactionTabFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorsConsignmentsFragment;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassUsersDetailListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by agt on 6/11/17.
 */

public class VendorsMainActivity extends AppCompatActivity implements View.OnClickListener, PassMobileListener,
        PassUsersDetailListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private String venId, emailId, name, walletBalance;
    private Toolbar toolbarBottom;
    private LinearLayout layoutIdEmail;
    private TextView textviewEmail;
    private TextView textEmail;
    private LinearLayout layoutIdCall;
    private TextView textviewIconCall;
    private TextView textCall;
    private LinearLayout layoutIdShare;
    private TextView textviewShare;
    private TextView textShare;
    private Typeface fontAwesomeFont;
    private String mobileNumber;
    private StringBuffer sharingTexts;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.venid))) {
            venId = getIntent().getStringExtra(getString(R.string.venid));
        }
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.email))) {
            emailId = getIntent().getStringExtra(getString(R.string.email));
        }
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.name))) {
            name = getIntent().getStringExtra(getString(R.string.name));
        }
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.wallet_balance))) {
            walletBalance = getIntent().getStringExtra(getString(R.string.wallet_balance));
        }

        if (name != null && !name.isEmpty()) {
            getSupportActionBar().setTitle(WordUtils.capitalize(name));
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

        fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");

        textviewEmail.setTypeface(fontAwesomeFont);
        textviewIconCall.setTypeface(fontAwesomeFont);
        textviewShare.setTypeface(fontAwesomeFont);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, venId, emailId, name);
        viewPager.setOffscreenPageLimit(6);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            VendorTransactionTabFragment vendorTransactionTabFragment =
                                    (VendorTransactionTabFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            vendorTransactionTabFragment.doRefresh();
                        }
                    }, 1 * 200);
                } else if (tab.getPosition() == 2) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            VendorPendingInvoiceTabFragment vendorPendingInvoiceTabFragment =
                                    (VendorPendingInvoiceTabFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            vendorPendingInvoiceTabFragment.doRefresh();
                        }
                    }, 1 * 200);
                } else if (tab.getPosition() == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            VendorInvoiceTabFragment vendorInvoiceTabFragment =
                                    (VendorInvoiceTabFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            vendorInvoiceTabFragment.doRefresh();
                        }
                    }, 1 * 200);
                } else if (tab.getPosition() == 4) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            PurchaseOrderHistoryFragment purchaseOrderHistoryFragment =
                                    (PurchaseOrderHistoryFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            purchaseOrderHistoryFragment.doRefresh();
                        }
                    }, 1 * 200);
                } else if (tab.getPosition() == 5) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            VendorsConsignmentsFragment vendorsConsignmentsFragment =
                                    (VendorsConsignmentsFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            vendorsConsignmentsFragment.doRefresh();
                        }
                    }, 1 * 200);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        layoutIdCall.setOnClickListener(this);
        layoutIdEmail.setOnClickListener(this);
        layoutIdShare.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager, String venId, String emailId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.venid), venId);
        bundle.putString(getString(R.string.email), emailId);
        bundle.putString(getString(R.string.name), name);
        bundle.putString(getString(R.string.wallet_balance), walletBalance);

        VendorDetailTabFragment vendorDetailTabFragment = new VendorDetailTabFragment();
        vendorDetailTabFragment.setArguments(bundle);

        VendorTransactionTabFragment vendorTransactionTabFragment = new VendorTransactionTabFragment();
        vendorTransactionTabFragment.setArguments(bundle);

        VendorPendingInvoiceTabFragment vendorPendingInvoiceTabFragment = new VendorPendingInvoiceTabFragment();
        vendorPendingInvoiceTabFragment.setArguments(bundle);

        PurchaseOrderHistoryFragment purchaseOrderHistoryFragment = new PurchaseOrderHistoryFragment();
        purchaseOrderHistoryFragment.setArguments(bundle);

        VendorsConsignmentsFragment vendorsConsignmentsFragment = new VendorsConsignmentsFragment();
        vendorsConsignmentsFragment.setArguments(bundle);

        VendorInvoiceTabFragment vendorInvoiceTabFragment = new VendorInvoiceTabFragment();
        vendorInvoiceTabFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(vendorDetailTabFragment, "Info");
        adapter.addFragment(vendorTransactionTabFragment, "Wallet");
        adapter.addFragment(vendorPendingInvoiceTabFragment, "Pending Invoice");
        adapter.addFragment(vendorInvoiceTabFragment, "Invoice");
        adapter.addFragment(purchaseOrderHistoryFragment, "Purchase Order");
        adapter.addFragment(vendorsConsignmentsFragment, "Consignment");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutIdCall) {
            if (mobileNumber != null && !mobileNumber.isEmpty()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    callIntent();
                } else {
                    callAction();
                }
            } else {
                Toast.makeText(VendorsMainActivity.this, "This vendor has no mobile number.", Toast.LENGTH_SHORT).show();
            }
        } else if (v == layoutIdEmail) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Vendor's info");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
            startActivity(Intent.createChooser(emailIntent, "Send email"));

        } else if (v == layoutIdShare) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Vendor's info");
            intent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
            startActivity(Intent.createChooser(intent, "Send email"));
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

    //    Lead info call action method  to call the followup contact number

    @Override
    public void passMobileOnLoad(String mobile) {
        mobileNumber = mobile;
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
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callAction();
                    } else {
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

    @Override
    public void passEmailOnLoad(StringBuffer sharingText) {
        sharingTexts = sharingText;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
