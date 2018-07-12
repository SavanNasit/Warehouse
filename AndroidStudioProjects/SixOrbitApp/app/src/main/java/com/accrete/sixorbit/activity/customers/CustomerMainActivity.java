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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerDetailTabFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerInvoiceTabFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerOrderFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerPendingInvoiceTabFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerQuotationFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerWalletTabFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomersQuotationMainFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassUsersDetailListener;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class CustomerMainActivity extends AppCompatActivity implements View.OnClickListener, PassMobileListener,
        PassUsersDetailListener {
    private static final int REQUEST_MULTIPLE_PERMISSION = 123;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private String cuId, emailId, name, walletBalance;
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
    private String mobileNumber, emailAddress;
    private StringBuffer sharingTexts;
    private ImageView imageViewInfoEdit;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onClick(View v) {
        try {
            if (v == layoutIdCall) {
                layoutIdCall.setEnabled(false);
                if (mobileNumber != null && !mobileNumber.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        callIntent();
                    } else {
                        callAction();
                    }
                } else {
                    Toast.makeText(CustomerMainActivity.this, "This customer has no mobile number.", Toast.LENGTH_SHORT).show();
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
                //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer's info");
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
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
                if (sharingTexts != null) {
                    intent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
                } else {
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                }
                startActivity(Intent.createChooser(intent, "Share"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutIdShare.setEnabled(true);
                    }
                }, 3000);
            } else if (v == imageViewInfoEdit) {
                imageViewInfoEdit.setEnabled(false);
                Intent intent = new Intent(CustomerMainActivity.this, AddCustomerActivity.class);
                intent.putExtra(getString(R.string.cuid), cuId);
                intent.putExtra("task", "edit");
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageViewInfoEdit.setEnabled(true);
                    }
                }, 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager, cuId, emailId);
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                supportInvalidateOptionsMenu();
                Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                if (position == 1) {
                    if (mFragment instanceof CustomerWalletTabFragment) {
                        ((CustomerWalletTabFragment) mFragment).doRefresh();
                    }
                } else if (position == 2) {
                    if (mFragment instanceof CustomerPendingInvoiceTabFragment) {
                        ((CustomerPendingInvoiceTabFragment) mFragment).doRefresh();
                    }
                } else if (position == 3) {
                    if (mFragment instanceof CustomerInvoiceTabFragment) {
                        ((CustomerInvoiceTabFragment) mFragment).doRefresh();
                    }
                } else if (position == 4) {
                    if (mFragment instanceof CustomerQuotationFragment) {
                        ((CustomerQuotationFragment) mFragment).doRefresh();
                    }
                } else if (position == 5) {
                    //Code to be executed after desired time
                    if (mFragment instanceof CustomerOrderFragment) {
                        ((CustomerOrderFragment) mFragment).doRefresh();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layoutIdCall.setOnClickListener(this);
        layoutIdEmail.setOnClickListener(this);
        layoutIdShare.setOnClickListener(this);
        imageViewInfoEdit.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager, String cuId, String emailId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.customer_email_id), emailId);
        bundle.putString(getString(R.string.name), name);
        bundle.putString(getString(R.string.wallet_balance), walletBalance);

        CustomerDetailTabFragment customerDetailTabFragment = new CustomerDetailTabFragment();
        customerDetailTabFragment.setArguments(bundle);

        CustomerWalletTabFragment customerWalletTabFragment = new CustomerWalletTabFragment();
        customerWalletTabFragment.setArguments(bundle);

        CustomerPendingInvoiceTabFragment customerPendingInvoiceTabFragment = new CustomerPendingInvoiceTabFragment();
        customerPendingInvoiceTabFragment.setArguments(bundle);

        CustomerOrderFragment customerOrderFragment = new CustomerOrderFragment();
        customerOrderFragment.setArguments(bundle);

        CustomerQuotationFragment customerQuotationFragment = new CustomerQuotationFragment();
        customerQuotationFragment.setArguments(bundle);

        CustomerInvoiceTabFragment customerInvoiceTabFragment = new CustomerInvoiceTabFragment();
        customerInvoiceTabFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Info", "Wallet", "Pending Invoice", "Invoice", "Quotation", "Order"};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // Request code for handling the call action for lead info contacts
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

                            // Requesting Settings Page On Never ASk Again Click
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
                            //   Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;

            case REQUEST_MULTIPLE_PERMISSION:
                boolean isStartActivity = true;
                int position = 0;
                for (int permission : grantResults) {
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        //DO NOTHING
                        Log.i("TAG", "Permission is granted");
                    } else {
                        isStartActivity = false;
                        position = permission;
                    }
                }
                if (isStartActivity) {
                    CustomersQuotationMainFragment mainFragment = (CustomersQuotationMainFragment) viewPager.getAdapter()
                            .instantiateItem(viewPager, viewPager.getCurrentItem());
                    mainFragment.scanBarcode(1000);

                } else {
                    String msg = String.format(Locale.ENGLISH, "%s permission is missing ", position == 1 ? "Camera" : "Read and write");
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void passEmailOnLoad(StringBuffer stringBuffer) {
        sharingTexts = stringBuffer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            if (viewPager.getCurrentItem() == 4) {
                CustomersQuotationMainFragment mainFragment = (CustomersQuotationMainFragment) viewPager.getAdapter()
                        .instantiateItem(viewPager, viewPager.getCurrentItem());
                mainFragment.getScanCode(data.getStringExtra("scanResult"));
            }
        }
    }

    class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static final String KEY_TITLE = "fragment_title";
        private static final String KEY_FRAGMENT = "fragment";
        private List<Map<String, Object>> maps = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragmentAndTitle(Map<String, Object> map) {
            maps.add(map);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) maps.get(position).get(KEY_TITLE);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) maps.get(position).get(KEY_FRAGMENT);
        }

        @Override
        public int getCount() {
            return maps.size();
        }
    }


}
