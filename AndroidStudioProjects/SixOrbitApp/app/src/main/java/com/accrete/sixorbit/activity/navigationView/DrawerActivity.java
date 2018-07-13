package com.accrete.sixorbit.activity.navigationView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.Settings.ProfileActivity;
import com.accrete.sixorbit.activity.Settings.SettingsActivity;
import com.accrete.sixorbit.activity.chats.ContactsActivity;
import com.accrete.sixorbit.activity.company.SwitchingCompanyActivity;
import com.accrete.sixorbit.activity.domain.DomainActivity;
import com.accrete.sixorbit.fragment.Drawer.ChatListFragment;
import com.accrete.sixorbit.fragment.Drawer.HomeFragment;
import com.accrete.sixorbit.fragment.Drawer.activityFeeds.ActivityFeedsFragment;
import com.accrete.sixorbit.fragment.Drawer.collections.CollectionsMainFragment;
import com.accrete.sixorbit.fragment.Drawer.collections.CreateCollectionSelectCustomerFragment;
import com.accrete.sixorbit.fragment.Drawer.collections.CustomerWiseCollectionsFragment;
import com.accrete.sixorbit.fragment.Drawer.collections.InvoiceWiseCollectionsMainFragment;
import com.accrete.sixorbit.fragment.Drawer.collections.MyTransactionsFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerMainFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomersMainTabFragment;
import com.accrete.sixorbit.fragment.Drawer.enquiry.EnquiryMainFragment;
import com.accrete.sixorbit.fragment.Drawer.enquiry.ManageEnquiryFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.RecordFollowUpFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadFragment;
import com.accrete.sixorbit.fragment.Drawer.orders.ManageOrderFragment;
import com.accrete.sixorbit.fragment.Drawer.orders.OrderMainFragment;
import com.accrete.sixorbit.fragment.Drawer.orders.OrderTemplatesFragment;
import com.accrete.sixorbit.fragment.Drawer.quotation.ManageQuotationFragment;
import com.accrete.sixorbit.fragment.Drawer.quotation.QuotationMainFragment;
import com.accrete.sixorbit.fragment.Drawer.quotation.QuotationTemplatesFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorMainFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.VendorsMainTabFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.CompanyData;
import com.accrete.sixorbit.receiver.ApiResultReceiver;
import com.accrete.sixorbit.receiver.CallLogBroadcastReceiver;
import com.accrete.sixorbit.receiver.NetworkChangeReceiver;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.service.ApiCallService;
import com.accrete.sixorbit.service.ChatContactsService;
import com.accrete.sixorbit.service.ChatService;
import com.accrete.sixorbit.service.PushNotificationsTimeService;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_STORAGE_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_VIEWPAGER_FOLLOWUP_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_CUSTOMER_MOBILE_CALL_ADAPTER;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_LEAD_CALL_ADAPTER;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class DrawerActivity extends AppCompatActivity implements ApiResultReceiver.Receiver {
    private static final String TAG = "DrawerActivity";
    private static final int REQUEST_MULTIPLE_PERMISSION = 123;
    public static Context context;
    public static Drawer drawer;
    private DrawerInterface drawerInterfaceToSend;
    private boolean doubleBackToExitPressedOnce = false, backToChatList;
    private String domainName, userId, userName, imageURL, email, view, nameTextView, companyName;
    private Uri imageUri;
    private PrimaryDrawerItem drawerItemFeeds, drawerItemFollowUps, drawerItemLeads, drawerItemCustomers,
            drawerItemVendors, drawerItemQuotation, drawerItemChat, drawerItemJobCard, drawerItemHome,
            drawerItemBlank, drawerItemSettings, drawerItemLogOut, drawerItemConnectedDomain,
            drawerItemOrder, drawerItemCollections, drawerItemCompanyName, drawerItemSwitchCompany,
            drawerItemEnquiry;
    //save our header or drawer
    private AccountHeader headerResult = null;
    private DatabaseHandler databaseHandler;
    private IProfile profile;
    private ApiResultReceiver mReceiver;
    private NetworkChangeReceiver networkChangeReceiver;
    private FragmentRefreshListener fragmentRefreshListener;
    private View dialogView;
    private ImageView imageViewLoader;
    private android.support.v7.app.AlertDialog dialogSwitchCompany;
    private Spinner companySpinner;
    private ArrayList<CompanyData> companyDataArrayList = new ArrayList<>();
    private ArrayAdapter arrayAdapterCompanies;

    public DrawerActivity() {
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    public void setCallback(DrawerInterface drawerInterface) {
        this.drawerInterfaceToSend = drawerInterface;
    }

    private void addImageFlag() {
        //Add Profile Pic and set boolean false
        AppPreferences.setBoolean(DrawerActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);
    }

    private void setRemovedImageFlag() {
        //Remove Profile Pic and set boolean false
        AppPreferences.setBoolean(DrawerActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        databaseHandler = new DatabaseHandler(this);
        AppPreferences.setIsUserFirstTime(DrawerActivity.this, AppUtils.USER_FIRST_TIME, true);

        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent intent_bgservice = new Intent(this, ChatService.class);
        startService(intent_bgservice);
        // Alert Service
        startService(new Intent(this, PushNotificationsTimeService.class));
        //Register service
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        //Getting values from Global Shared Preferences
        userId = AppPreferences.getUserId(DrawerActivity.this, AppUtils.USER_ID);
        userName = AppPreferences.getUserName(DrawerActivity.this, AppUtils.USER_NAME);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (!AppPreferences.getIsLogin(DrawerActivity.this, AppUtils.ISLOGIN)) {
            Intent intentDomain = new Intent(DrawerActivity.this, DomainActivity.class);
            startActivity(intentDomain);
            finish();
        } else {
            email = AppPreferences.getEmail(DrawerActivity.this, AppUtils.USER_EMAIL);

            //Get Image URI From Url and set
            getImageUri();

            // Create the AccountHeader
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.color.colorPrimary)
                    .addProfiles(profile)
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            Intent intentDomain = new Intent(DrawerActivity.this, ProfileActivity.class);
                            startActivity(intentDomain);
                            return false;
                        }
                    })
                    .withSelectionListEnabledForSingleProfile(false)
                    .withSavedInstance(savedInstanceState)
                    .build();

            // domainName = domainsp.getString("domain", "");
            domainName = AppPreferences.getDomain(DrawerActivity.this, AppUtils.LAST_DOMAIN);
            if (domainName != null && !domainName.toString().trim().isEmpty() && domainName.length() > 1) {
                domainName = domainName.substring(0, domainName.length());
            }

            if (AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_NAME) != null &&
                    !AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_NAME).isEmpty()) {
                companyName = AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_NAME);
            }

            //Home Drawer Item
            drawerItemHome = new PrimaryDrawerItem().withName(getString(R.string.home))
                    .withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSelectable(true);

            //Follow ups Drawer Item
            drawerItemFollowUps = new PrimaryDrawerItem().withName(getString(R.string.follow_up_fragment))
                    .withIcon(FontAwesome.Icon.faw_bookmark).withIdentifier(3).withSelectable(true);

            //Leads Drawer Item
            drawerItemLeads = new PrimaryDrawerItem().withName(getString(R.string.lead_fragment))
                    .withIcon(FontAwesome.Icon.faw_book).withIdentifier(2).withSelectable(true);

            //Customers Drawer Item
            drawerItemCustomers = new PrimaryDrawerItem().withName(getString(R.string.customer_fragment))
                    .withIcon(FontAwesome.Icon.faw_user).withIdentifier(5).withSelectable(true);

            //Vendors Drawer Item
            drawerItemVendors = new PrimaryDrawerItem().withName(getString(R.string.vendors))
                    .withIcon(FontAwesome.Icon.faw_user_secret).withIdentifier(6).withSelectable(true);

            //Quotation Drawer Item
            drawerItemQuotation = new PrimaryDrawerItem().withName(getString(R.string.quotation))
                    .withIcon(FontAwesome.Icon.faw_quote_left).withIdentifier(7).withSelectable(true);

            //Chat Drawer Item
            drawerItemChat = new PrimaryDrawerItem().withName(getString(R.string.chat_fragment))
                    .withIcon(FontAwesome.Icon.faw_snapchat).withIdentifier(8).withSelectable(true);

            //Job Card Drawer Item
            drawerItemJobCard = new PrimaryDrawerItem().withName(getString(R.string.jobcard))
                    .withIcon(FontAwesome.Icon.faw_volume_control_phone).withIdentifier(10).withSelectable(false);

            //Activity feeds Drawer Item
            drawerItemFeeds = new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(4)
                    .withName(getString(R.string.activity_feeds)).withSelectable(true);
            //.withBadge("0").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));

            //Blank Drawer Item
            drawerItemBlank = new PrimaryDrawerItem().withName("").withDescription("")
                    .withSelectable(false).withSelectable(false).withOnDrawerItemClickListener(null)
                    .withEnabled(false);

            //Connected Domain Drawer Item
            drawerItemConnectedDomain = new PrimaryDrawerItem().withName(getString(R.string.connected_to))
                    .withDescription(domainName).withSelectable(false).withOnDrawerItemClickListener(null);

            //Logout Drawer Item
            drawerItemLogOut = new PrimaryDrawerItem().withName(getString(R.string.logout))
                    .withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(11).withSelectable(true);

            //Settings Drawer Item
            drawerItemSettings = new PrimaryDrawerItem().withName(getString(R.string.settings))
                    .withIcon(FontAwesome.Icon.faw_cog).withIdentifier(9).withSelectable(true);

            //Order Drawer Item
            drawerItemOrder = new PrimaryDrawerItem().withName(getString(R.string.order))
                    .withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(12).withSelectable(true);

            //Collections Drawer Item
            drawerItemCollections = new PrimaryDrawerItem().withName(getString(R.string.collections))
                    .withIcon(FontAwesome.Icon.faw_inr).withIdentifier(13).withSelectable(true);

            //TODO Added on 25th June
            //Company Name Drawer Item
            drawerItemCompanyName = new PrimaryDrawerItem().withName(getString(R.string.your_company))
                    .withDescription(companyName).withSelectable(false).withOnDrawerItemClickListener(null);

            //Switch Company Drawer Item
            drawerItemSwitchCompany = new PrimaryDrawerItem().withName(getString(R.string.switch_company))
                    .withIcon(FontAwesome.Icon.faw_toggle_on).withIdentifier(14).withSelectable(false);

            //Enquiry Drawer Item
            drawerItemEnquiry = new PrimaryDrawerItem().withName(getString(R.string.enquiry))
                    .withIcon(FontAwesome.Icon.faw_info).withIdentifier(15).withSelectable(true);


            //Create the drawer
            drawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withHasStableIds(false)
                    .withItemAnimator(new AlphaCrossFadeAnimator())
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            drawerItemHome, drawerItemLeads, drawerItemFollowUps, drawerItemEnquiry,
                            drawerItemQuotation, drawerItemOrder, drawerItemCollections,
                            drawerItemCustomers, drawerItemVendors, drawerItemFeeds, drawerItemChat,
                            drawerItemSettings, drawerItemLogOut, drawerItemConnectedDomain,
                            drawerItemCompanyName, drawerItemSwitchCompany, drawerItemBlank)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            return drawerItemsClickListener(drawerItem);
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .withShowDrawerOnFirstLaunch(false)
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {
                        @Override
                        public void onDrawerOpened(View view) {
                        }

                        @Override
                        public void onDrawerClosed(View view) {
                        }

                        @Override
                        public void onDrawerSlide(View view, float v) {
                            hideSoftKeyboard(DrawerActivity.this);
                        }
                    })
                    .build();

            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.customers_permission_menu))) {
                drawer.removeItem(5);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.vendors_permission_menu))) {
                drawer.removeItem(6);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.quotation_permission_menu))) {
                drawer.removeItem(7);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.order_permission_menu))) {
                drawer.removeItem(12);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.lead_permission_menu))) {
                drawer.removeItem(2);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.followup_permission_menu))) {
                drawer.removeItem(3);
            }
            if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.collections_permission_menu_first)) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.collections_permission_menu_second))) {
                drawer.removeItem(13);
            }
           /*if (!AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) &&
                    !databaseHandler.checkUsersPermission(getString(R.string.enquiry_permission_menu))) {*/
                drawer.removeItem(15);
            //}

            //Get Intent Data
            if (getIntent().hasExtra(getString(R.string.back_to_chat_list))) {
                backToChatList = getIntent().getBooleanExtra(getString(R.string.back_to_chat_list), false);
            }
            if (getIntent() != null && getIntent().hasExtra("manage_quotations")) {
                view = getIntent().getStringExtra("manage_quotations");
            } else if (getIntent() != null && getIntent().hasExtra("manage_orders")) {
                view = getIntent().getStringExtra("manage_orders");
            } else if (getIntent() != null && getIntent().hasExtra("manage_collections")) {
                view = getIntent().getStringExtra("manage_collections");
            }


            if ("com.accrete.sixorbit.custom.name".equals(getIntent().getAction())) {
                // do Fragment transactions here
                Log.d("print", "2");

                // QuotationTemplatesFragment quotationTemplatesFragment = new QuotationTemplatesFragment();
                if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.quotation_add_permission))) {
                    FragmentManager fragmentManagerCreateQuotation = getSupportFragmentManager();
                    FragmentTransaction fragmentTransactionCreateQuotation = fragmentManagerCreateQuotation.beginTransaction();
                    fragmentTransactionCreateQuotation.replace(R.id.frame_container, new QuotationTemplatesFragment(), getString(R.string.create_quotation));
                    fragmentTransactionCreateQuotation.addToBackStack(null);
                    fragmentTransactionCreateQuotation.commit();
                    //  Bundle args = new Bundle();
                    // args.putString("event", "drawer");
                    // quotationTemplatesFragment.setArguments(args);
                } else {
                    Toast.makeText(DrawerActivity.this, "Sorry, you've no permission to add quotation.", Toast.LENGTH_SHORT).show();
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                }

            } else if (backToChatList == true) {
                Fragment f = ChatListFragment.newInstance(getString(R.string.chatlist_fragment_tag));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
            } else if (view != null && view.equals("view_quotations")) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                drawer.setSelection(7);
                if (currentFragment instanceof ManageQuotationFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else if (currentFragment instanceof QuotationMainFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else {
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    openManageQuotations();
                }
            } else if (view != null && view.equals("view_orders")) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                drawer.setSelection(12);
                if (currentFragment instanceof ManageOrderFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else {
                    hideSoftKeyboard(DrawerActivity.this);
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    openManageOrders();
                }
            } else if (view != null && view.equals("view_transactions")) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                drawer.setSelection(13);
                if (currentFragment instanceof MyTransactionsFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else {
                    hideSoftKeyboard(DrawerActivity.this);
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    openMyTransactions();
                }
            } else if (view != null && view.equals("view_collections")) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                drawer.setSelection(13);
                if (currentFragment instanceof InvoiceWiseCollectionsMainFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else {
                    hideSoftKeyboard(DrawerActivity.this);
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    openMyCollections();
                }
            } else if (view != null && view.equals("create_collection")) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                drawer.setSelection(13);
                if (currentFragment instanceof CreateCollectionSelectCustomerFragment) {
                    hideSoftKeyboard(DrawerActivity.this);
                } else {
                    hideSoftKeyboard(DrawerActivity.this);
                    Fragment f = HomeFragment.newInstance(getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    openCreateNewCollection();
                }
            } else {
                Fragment f = HomeFragment.newInstance(getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
            }


            mReceiver = new ApiResultReceiver(new Handler());
            mReceiver.setReceiver(this);


            //starting bg service to fetch data
            startAPIBackgroundService();

            if (drawerInterfaceToSend != null) {
                drawerInterfaceToSend.sendDrawer(drawer);
            }
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                askStoragePermission();
            } else {

                //starting bg service for assignee
                startAssigneeBackgroundService();
            }


            //To updateCount counts of Activity feeds
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //Code to be executed after desired time
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (AppPreferences.getNotificationReadTime(DrawerActivity.this, AppUtils.NOTIFICATION_READ_TIME) != null) {
                        String str = AppPreferences.getNotificationReadTime(DrawerActivity.this, AppUtils.NOTIFICATION_READ_TIME);
                        String currentDate = df.format(c.getTime());
                        int a = databaseHandler.getCountsUnreadActivityFeeds(str, currentDate);
                        if (a > 0) {
                            a = a - 1;
                        }
                        AppPreferences.setNotificationRead(DrawerActivity.this, AppUtils.NOTIFICATION_READ, String.valueOf(a));
                    }
                    //    onUpdate();
                }
            }, 3 * 1000);

            //Check Permission to view details of lead
            if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.lead_view_permission))) {

            }
        }

    }

    private void getImageUri() {
        imageURL = AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO);
        ChatContacts chatContacts = null;
        try {
            if (userId != null && !userId.isEmpty()) {
                chatContacts = databaseHandler.getUserData(Integer.valueOf(userId));
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        if (chatContacts != null && chatContacts.getImagePath() != null && !chatContacts.getImagePath().isEmpty()) {
            File file = new File(chatContacts.getImagePath());
            imageUri = Uri.fromFile(file);
            try {
                if (getBitmapFromUri(imageUri) == null) {
                    imageUri = Uri.parse("android.resource://" + DrawerActivity.this.getPackageName() + "/drawable/icon_neutral_profile");
                    setRemovedImageFlag();
                } else {
                    imageUri = Uri.fromFile(file);
                    addImageFlag();
                    Log.e("UserImage", "From DB" + imageUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
                imageUri = Uri.parse("android.resource://" + DrawerActivity.this.getPackageName() + "/drawable/icon_neutral_profile");
                setRemovedImageFlag();
            }

        } else {
            if (!imageURL.isEmpty() && !imageURL.equals("null")) {
                imageUri = Uri.parse(imageURL);
                addImageFlag();
            } else {
                imageUri = Uri.parse("android.resource://" + DrawerActivity.this.getPackageName() + "/drawable/icon_neutral_profile");
                setRemovedImageFlag();
            }

        }

        if (chatContacts != null && chatContacts.getName() != null && !chatContacts.getName().toString().trim().isEmpty()) {
            nameTextView = chatContacts.getName().trim();
        } else {
            nameTextView = userName;
        }

        if (imageURL != null && !imageURL.isEmpty() && !imageURL.equals("null")) {
            profile = new ProfileDrawerItem().withIcon(imageURL).withName(nameTextView)
                    .withEmail(email).withIdentifier(101);
        } else {
            profile = new ProfileDrawerItem().withIcon(imageUri).withName(nameTextView)
                    .withEmail(email).withIdentifier(101);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO) != null &&
                        !(AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO)).equals("null") &&
                        !AppPreferences.getBoolean(DrawerActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG)) {
                    if (AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO).contains("http")) {
                        profile.withIcon(Uri.parse(
                                AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO)));
                    } else {
                        profile.withIcon(stopRotationInImage(AppPreferences.getPhoto(DrawerActivity.this, AppUtils.USER_PHOTO)));
                    }
                    headerResult.updateProfile(profile);
                } else {
                    //     profile.withIcon(imageUri);
                    headerResult.updateProfile(profile);
                }
            }
        }, 500);


    }

    private void startAPIBackgroundService() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_SYNC, null, DrawerActivity.this, ApiCallService.class);
                intent.putExtra(getString(R.string.receiver), mReceiver);
                startService(intent);
            }
        });
        thread.start();
    }

    /*public void onUpdate() {
        if (AppPreferences.getNotificationRead(this, AppUtils.NOTIFICATION_READ) != null) {
            String value = AppPreferences.getNotificationRead(this, AppUtils.NOTIFICATION_READ);
            if (!value.equals("0")) {
                drawerItemFeeds.withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(4)
                        .withName(getString(R.string.activity_feeds)).withSelectable(true)
                        .withBadge("" + value).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));
            } else {
                drawerItemFeeds.withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(4)
                        .withName(getString(R.string.activity_feeds)).withSelectable(true);
            }
            drawer.updateItem(drawerItemFeeds);
        }


        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof ActivityFeedsFragment) {
            // add your code here
            if (drawer.getCurrentSelection() != 4)
                drawer.setSelectionAtPosition(4, false);
        }

    }*/

    private void startAssigneeBackgroundService() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent_service = new Intent(Intent.ACTION_SYNC, null, DrawerActivity.this, ChatContactsService.class);
                intent_service.putExtra(getString(R.string.receiver), mReceiver);
                startService(intent_service);
            }
        });
        thread.start();
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved nameTextView the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        //add the values which need to be saved nameTextView the accountHeader to the bundle
        //   outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
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

//    method to request the settings page for permissions

    @Override
    public void onBackPressed() {
        //handle the back press, close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (currentFragment instanceof ChatListFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(8);
                drawer.setSelection(1);
            } else if (currentFragment instanceof VendorMainFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(6);
                drawer.setSelection(1);
            } else if (currentFragment instanceof CustomerMainFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(5);
                drawer.setSelection(1);
            } else if (currentFragment instanceof FollowUpsFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(3);
                drawer.setSelection(1);

            } else if (currentFragment instanceof ActivityFeedsFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(4);
                drawer.setSelection(1);
            } else if (currentFragment instanceof LeadFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(2);
                drawer.setSelection(1);
            } else if (currentFragment instanceof RecordFollowUpFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                //drawer.deselect(3);
                //drawer.setSelection(3);
            } else if (currentFragment instanceof QuotationMainFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(7);
                drawer.setSelection(1);
            } else if (currentFragment instanceof ManageQuotationFragment) {
                super.onBackPressed();
                return;

            } else if (currentFragment instanceof OrderMainFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(12);
                drawer.setSelection(1);
            } else if (currentFragment instanceof ManageOrderFragment) {
                super.onBackPressed();
                return;

            } else if (currentFragment instanceof ManageEnquiryFragment) {
                super.onBackPressed();
                return;

            } else if (currentFragment instanceof MyTransactionsFragment) {
                super.onBackPressed();
                return;
            } else if (currentFragment instanceof InvoiceWiseCollectionsMainFragment) {
                super.onBackPressed();
                return;
            } else if (currentFragment instanceof CustomerWiseCollectionsFragment) {
                super.onBackPressed();
                return;
            } else if (currentFragment instanceof CollectionsMainFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(13);
                drawer.setSelection(1);
            } else if (currentFragment instanceof CreateCollectionSelectCustomerFragment) {
                super.onBackPressed();
                return;
            } else if (currentFragment instanceof QuotationTemplatesFragment) {
                if ("com.accrete.sixorbit.custom.name".equals(getIntent().getAction())) {
                    drawer.deselect(7);
                    drawer.setSelection(1);
                    QuotationMainFragment quotationMainFragment = (QuotationMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.quotation));
                    if (quotationMainFragment != null && quotationMainFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = quotationMainFragment.newInstance(getString(R.string.quotation));
                        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    super.onBackPressed();
                    return;
                }

            } else if (currentFragment instanceof OrderTemplatesFragment) {
                if ("com.accrete.sixorbit.custom.name".equals(getIntent().getAction())) {
                    drawer.deselect(12);
                    drawer.setSelection(1);
                    OrderMainFragment orderMainFragment = (OrderMainFragment) getSupportFragmentManager().
                            findFragmentByTag(getString(R.string.order));
                    if (orderMainFragment != null && orderMainFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = orderMainFragment.newInstance(getString(R.string.order));
                        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    super.onBackPressed();
                    return;
                }

            } else if (currentFragment instanceof EnquiryMainFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(15);
                drawer.setSelection(1);
            } else {

                if (doubleBackToExitPressedOnce) {
                    this.doubleBackToExitPressedOnce = false;
                    super.onBackPressed();
                    return;
                } else {

                    Toast.makeText(this, getString(R.string.click_back_to_exit), Toast.LENGTH_SHORT).show();
                    doubleBackToExitPressedOnce = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 3 * 1000);
                }
            }
        }

    }

//    method to request the settings page for permissions

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.navigation_view_activity));
        hideSoftKeyboard(DrawerActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSoftKeyboard(DrawerActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for call permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted

//                     Handling  Followup  Allow onclick in runtime permissions
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (f instanceof FollowUpsFragment) {
                            ((FollowUpsFragment) f).callAction();
                        }
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
                            // handling never ask again and re directing to settings page.
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;
            case REQUEST_CODE_ASK_VIEWPAGER_FOLLOWUP_CALL_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for call permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted


                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (f instanceof HomeFragment) {

                            ((HomeFragment) f).callAction();
//
                        }
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

                            // handling never ask again and re directing to settings page.
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;
            //            Request code for handling the add contacts to lead nameTextView phonebook

            case REQUEST_CODE_ASK_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions

                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Read Contact permission granted");

                        //permissions are  granted

//                        Handling  AddLead nameTextView phonebook  Allow onclick in runtime permissions
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (f instanceof LeadFragment) {
                            ((LeadFragment) f).addLeadFromPhonebook();
                        }
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.


                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

                            // handling never ask again and re directing to settings page.

                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;
            //            Request code for handling the add contacts to lead nameTextView phonebook

            case REQUEST_CODE_LEAD_CALL_ADAPTER: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Read Contact permission granted");
                        //permissions are  granted
                        // process the normal flow
                        //                     Handling  Followup  Allow onclick in runtime permissions
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (f instanceof LeadFragment) {

                            ((LeadFragment) f).callAction();

                        }
                        //else any one or both the permissions are not granted
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
                            // handling never ask again and re directing to settings page.
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }


            break;

            case REQUEST_CODE_CUSTOMER_MOBILE_CALL_ADAPTER: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Read Contact permission granted");
                        //permissions are  granted
                        // process the normal flow
                        //                     Handling  Followup  Allow onclick in runtime permissions
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (f instanceof CustomerMainFragment) {

                            ((CustomerMainFragment) f).callCustomer();

                        } else if (f instanceof VendorMainFragment) {
                            ((VendorMainFragment) f).callVendor();
                        }
                        //else any one or both the permissions are not granted
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
                            // handling never ask again and re directing to settings page.
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;
            case REQUEST_CODE_ASK_STORAGE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions

                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "WRITE_EXTERNAL_STORAGE permission granted");

                        //permissions are  granted
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        if (databaseHandler.getAllAssignee().size() == 0) {
                            //Perform Your Operation
                            startAssigneeBackgroundService();
                            if (fragment instanceof ChatListFragment) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        if (!backToChatList) {
                                            Intent intent = new Intent(DrawerActivity.this, ContactsActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }, 1000);
                            }
                        } else {
                            //Perform Your Operation
                            startAssigneeBackgroundService();
                            if (fragment instanceof ChatListFragment) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 0ms
                                        if (!backToChatList) {
                                            Intent intent = new Intent(DrawerActivity.this, ContactsActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }, 0);
                            }
                        }
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "READ_EXTERNAL_STORAGE permission granted");

                        //permissions are  granted
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

                        if (databaseHandler.getAllAssignee().size() == 0) {
                            //Perform Your Operation
                            startAssigneeBackgroundService();
                            if (fragment instanceof ChatListFragment) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        if (!backToChatList) {
                                            Intent intent = new Intent(DrawerActivity.this, ContactsActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }, 1000);
                            }
                        } else {
                            //Perform Your Operation
                            startAssigneeBackgroundService();
                            if (fragment instanceof ChatListFragment) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 0ms
                                        if (!backToChatList) {
                                            Intent intent = new Intent(DrawerActivity.this, ContactsActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }, 0);
                            }
                        }

                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else if (perms.get(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {


                    } else if (perms.get(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {


                    } else {
                        startAssigneeBackgroundService();
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {
                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALL_LOG)) {
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            // handling never ask again and re directing to settings page.
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
                   /* Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                    if (currentFragment instanceof AddQuotationFragment) {
                        ((AddQuotationFragment) currentFragment).scanBarcode(1100);
                    }*/
                } else {
                    String msg = String.format(Locale.ENGLISH, "%s permission is missing ", position == 1 ? "Camera" : "Read and write");
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void askStoragePermission() {
        if (checkPermissionWithRationale(this, new HomeFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG},
                REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ApiCallService.STATUS_RUNNING:
                //progressBar.setProgress(50);
                //  progressBar.setIndeterminate(true);
                break;
            case ApiCallService.STATUS_FINISHED:
                //progressBar.setIndeterminate(false);
                final ApiResponse results = resultData.getParcelable("followUp");
                Log.d("results receiver", String.valueOf(results));

                break;
            case ApiCallService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void askUserToAllowPermissionFromSetting() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(R.string.permission_required);

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
    protected void onDestroy() {
        super.onDestroy();
        //Unregister broadcast receiver
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }

        if (dialogSwitchCompany != null && dialogSwitchCompany.isShowing()) {
            dialogSwitchCompany.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (resultCode == 1000) {
            if (currentFragment instanceof LeadFragment) {
                if (getFragmentRefreshListener() != null) {
                    getFragmentRefreshListener().onRefresh();
                }
            }
        } else if (requestCode == AppUtils.FEEDS_RESULT_CODE) {

            if (currentFragment instanceof ActivityFeedsFragment) {
                ((ActivityFeedsFragment) currentFragment).onRefresh();
            }
        } else if (resultCode == AppUtils.ADD_CUSTOMER_REQUEST_CODE) {
            if (currentFragment instanceof CustomerMainFragment) {
                ((CustomerMainFragment) currentFragment).callAPI();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (currentFragment instanceof QuotationTemplatesFragment) {
            }
        }
    }

    private void openManageQuotations() {
        //Main Fragment of Quotations
        QuotationMainFragment quotationMainFragment = (QuotationMainFragment) getSupportFragmentManager().
                findFragmentByTag(getString(R.string.quotation));
        Fragment f = quotationMainFragment.newInstance(getString(R.string.quotation));
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();

        //Manage Quotations
        FragmentManager fragmentManagerManageQuotation = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionManageQuotation = fragmentManagerManageQuotation.beginTransaction();
        fragmentTransactionManageQuotation.replace(R.id.frame_container, new ManageQuotationFragment(),
                getString(R.string.manage_quotation));
        fragmentTransactionManageQuotation.addToBackStack(null);
        fragmentTransactionManageQuotation.commitAllowingStateLoss();
    }

    private void openManageOrders() {
        //Main Fragment of Order
        OrderMainFragment orderMainFragment = (OrderMainFragment) getSupportFragmentManager().
                findFragmentByTag(getString(R.string.order));
        Fragment f = orderMainFragment.newInstance(getString(R.string.order));
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();

        //Manage Orders
        FragmentManager fragmentManagerOrderQuotation = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionManageOrder = fragmentManagerOrderQuotation.beginTransaction();
        fragmentTransactionManageOrder.add(R.id.frame_container, new ManageOrderFragment(),
                getString(R.string.manage_order));
        fragmentTransactionManageOrder.addToBackStack(null);
        fragmentTransactionManageOrder.commitAllowingStateLoss();
    }

    private void openMyTransactions() {
        //Main Fragment of Collection
        CollectionsMainFragment collectionsMainFragment = (CollectionsMainFragment) getSupportFragmentManager().
                findFragmentByTag(getString(R.string.collections));
        Fragment f = collectionsMainFragment.newInstance(getString(R.string.collections));
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();

        //My Transactions
        FragmentManager fragmentManagerCollections = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionManageCollections = fragmentManagerCollections.beginTransaction();
        fragmentTransactionManageCollections.add(R.id.frame_container, new MyTransactionsFragment(),
                getString(R.string.my_transaction));
        fragmentTransactionManageCollections.addToBackStack(null);
        fragmentTransactionManageCollections.commitAllowingStateLoss();
    }

    private void openMyCollections() {
        //Main Fragment of Collection
        CollectionsMainFragment collectionsMainFragment = (CollectionsMainFragment) getSupportFragmentManager().
                findFragmentByTag(getString(R.string.collections));
        Fragment f = collectionsMainFragment.newInstance(getString(R.string.collections));
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();

        //My Collections
        FragmentManager fragmentManagerCollections = getSupportFragmentManager();
        FragmentTransaction fragmentCollections = fragmentManagerCollections.beginTransaction();
        fragmentCollections.add(R.id.frame_container, new InvoiceWiseCollectionsMainFragment(),
                getString(R.string.my_collections));
        fragmentCollections.addToBackStack(null);
        fragmentCollections.commitAllowingStateLoss();
    }

    private void openCreateNewCollection() {
        //Main Fragment of Collection
        CollectionsMainFragment collectionsMainFragment = (CollectionsMainFragment) getSupportFragmentManager().
                findFragmentByTag(getString(R.string.collections));
        Fragment f = collectionsMainFragment.newInstance(getString(R.string.collections));
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();

        //Create Collection
        FragmentManager fragmentManagerCollections = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionManageCollections = fragmentManagerCollections.beginTransaction();
        fragmentTransactionManageCollections.add(R.id.frame_container, new CreateCollectionSelectCustomerFragment(),
                getString(R.string.create_collection));
        fragmentTransactionManageCollections.addToBackStack(null);
        fragmentTransactionManageCollections.commitAllowingStateLoss();
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private Bitmap stopRotationInImage(String filePath) {
        Bitmap bitmap = null;
        try {
            File f = new File(filePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            } else {
                angle = 0;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    outstudentstreamOutputStream);

        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
        return bitmap;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            getImageUri();


            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (currentFragment instanceof HomeFragment) {
                drawer.setSelection(1);
                hideSoftKeyboard(DrawerActivity.this);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof ChatListFragment) {
                drawer.setSelection(8, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof VendorMainFragment) {
                // add your code here
                drawer.setSelection(6, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof VendorsMainTabFragment) {
                // add your code here
                drawer.setSelection(6, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof CustomerMainFragment) {
                // add your code here
                drawer.setSelection(5, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }else if (currentFragment instanceof CustomersMainTabFragment) {
                // add your code here
                drawer.setSelection(5, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof FollowUpsFragment) {
                // add your code here
                drawer.setSelection(3);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof ActivityFeedsFragment) {
                // add your code here
                drawer.setSelection(4);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof LeadFragment) {
                // add your code here
                drawer.setSelection(2);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof ManageQuotationFragment) {
                // add your code here
                //   ((ManageQuotationFragment) currentFragment).refreshFragment("");
                //drawer.setSelection(7);
            } else if (currentFragment instanceof ManageOrderFragment) {
                // add your code here
                //   ((ManageOrderFragment) currentFragment).refreshFragments();
                //drawer.setSelection(7);
            } else if (currentFragment instanceof OrderMainFragment) {
                // add your code here
                drawer.setSelection(12, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof EnquiryMainFragment) {
                // add your code here
                drawer.setSelection(15, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof CollectionsMainFragment) {
                // add your code here
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                drawer.setSelection(13, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof MyTransactionsFragment) {
                // add your code here
                drawer.setSelection(13, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof InvoiceWiseCollectionsMainFragment) {
                // add your code here
                drawer.setSelection(13, false);
                // add your code here
                // ((InvoiceWiseCollectionsMainFragment) currentFragment).refreshFragments();
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof CustomerWiseCollectionsFragment) {
                // add your code here
                drawer.setSelection(13, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (currentFragment instanceof QuotationMainFragment) {
                this.setTitle(getString(R.string.quotation));
                // add your code here
                drawer.setSelection(7, false);
                //Enable Touch Back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deselectCurrentAndSelectLastFragment(int position) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof HomeFragment) {
            drawer.deselect(position);
            drawer.setSelection(1);
            hideSoftKeyboard(DrawerActivity.this);
        } else if (currentFragment instanceof ChatListFragment) {
            drawer.deselect(position);
            drawer.setSelection(10);
        } else if (currentFragment instanceof VendorMainFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelectionAtPosition(7);
        } else if (currentFragment instanceof CustomerMainFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelectionAtPosition(7);
        } else if (currentFragment instanceof FollowUpsFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelection(3);
        } else if (currentFragment instanceof ActivityFeedsFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelectionAtPosition(9);
        } else if (currentFragment instanceof LeadFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelection(2);
        } else if (currentFragment instanceof QuotationMainFragment) {
            // add your code here
            drawer.deselect(position);
            drawer.setSelection(7);
        }
    }

    private boolean drawerItemsClickListener(IDrawerItem drawerItem) {
        if (drawerItem != null) {
            Intent intent = null;
            if (drawerItem.getIdentifier() == 1) {
                Fragment f = HomeFragment.newInstance(getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();

            } else if (drawerItem.getIdentifier() == 2) {
                //Permission Check and hide leads
                if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.lead_view_permission))) {
                    LeadFragment leadFragment = (LeadFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.lead_fragment_tag));
                    if (leadFragment != null && leadFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = LeadFragment.newInstance(getString(R.string.lead_fragment_tag));
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    deselectCurrentAndSelectLastFragment(2);
                    Toast.makeText(DrawerActivity.this, "Sorry, you've no permission to view leads.", Toast.LENGTH_SHORT).show();
                }

            } else if (drawerItem.getIdentifier() == 3) {
                //Permission Check and hide follow ups
                if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.followup_view_permission))) {
                    FollowUpsFragment followUpsFragment = (FollowUpsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.followup_fragment_tag));
                    if (followUpsFragment != null && followUpsFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = FollowUpsFragment.newInstance(getString(R.string.followup_fragment_tag));
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    deselectCurrentAndSelectLastFragment(3);
                    Toast.makeText(DrawerActivity.this, "Sorry, you've no permission to view follow ups.", Toast.LENGTH_SHORT).show();
                }
            } else if (drawerItem.getIdentifier() == 5) {
                if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.customers_view_permission))) {
                    CustomerMainFragment customerMainFragment = (CustomerMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.customer_fragment));
                    if (customerMainFragment != null && customerMainFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = CustomerMainFragment.newInstance(getString(R.string.customer_fragment));
                        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    deselectCurrentAndSelectLastFragment(5);
                    Toast.makeText(DrawerActivity.this, "Sorry, you've no permission to view customers.", Toast.LENGTH_SHORT).show();
                }
            } else if (drawerItem.getIdentifier() == 6) {
                if (AppPreferences.getBoolean(DrawerActivity.this, AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.vendors_view_permission))) {
                    VendorMainFragment vendorMainFragment = (VendorMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.vendors));
                    if (vendorMainFragment != null && vendorMainFragment.isVisible()) {
                        //DO STUFF
                    } else {
                        Fragment f = VendorMainFragment.newInstance(getString(R.string.vendors));
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                    }
                } else {
                    deselectCurrentAndSelectLastFragment(6);
                    Toast.makeText(DrawerActivity.this, "Sorry, you've no permission to view vendors.", Toast.LENGTH_SHORT).show();
                }
            } else if (drawerItem.getIdentifier() == 7) {
                QuotationMainFragment quotationMainFragment = (QuotationMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.quotation));
                if (quotationMainFragment != null && quotationMainFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = quotationMainFragment.newInstance(getString(R.string.quotation));
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

            } else if (drawerItem.getIdentifier() == 12) {
                OrderMainFragment orderMainFragment = (OrderMainFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.order));
                if (orderMainFragment != null && orderMainFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = orderMainFragment.newInstance(getString(R.string.order));
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

            } else if (drawerItem.getIdentifier() == 4) {
                ActivityFeedsFragment activityFeedsFragment = (ActivityFeedsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.activityfeeds_fragment_tag));
                if (activityFeedsFragment != null && activityFeedsFragment.isVisible()) {

                } else {
                    Fragment f = ActivityFeedsFragment.newInstance(getString(R.string.activityfeeds_fragment_tag));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                }
//                                drawer.setSelection(4, false);
            } else if (drawerItem.getIdentifier() == 8) {
                ChatListFragment chatFragment = (ChatListFragment) getSupportFragmentManager().findFragmentByTag((getString(R.string.chatlist_fragment_tag)));
                if (chatFragment != null && chatFragment.isVisible()) {

                } else {
                    Fragment f = ChatListFragment.newInstance(getString(R.string.chatlist_fragment_tag));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                }
            } else if (drawerItem.getIdentifier() == 9) {
                Intent intentSettings = new Intent(DrawerActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
            } else if (drawerItem.getIdentifier() == 10) {
                if (isPackageInstalled("com.jobkart", getPackageManager())) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.jobkart");
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(DrawerActivity.this, "Coming Soon..", Toast.LENGTH_SHORT).show();
                }
            } else if (drawerItem.getIdentifier() == 14) {
                drawer.closeDrawer();
                //TODO Added on 25th June
                if (!NetworkUtil.getConnectivityStatusString(DrawerActivity.this).equals
                        (getString(R.string.not_connected_to_internet))) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openDialogSwitchCompany();
                        }
                    }, 200);
                } else {
                    Toast.makeText(DrawerActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            } else if (drawerItem.getIdentifier() == 15) {
                drawer.closeDrawer();
                //TODO Added on 27th June
                EnquiryMainFragment enquiryMainFragment = (EnquiryMainFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.enquiry_fragment_tag));
                if (enquiryMainFragment != null && enquiryMainFragment.isVisible()) {

                } else {
                    Fragment f = EnquiryMainFragment.newInstance(getString(R.string.enquiry_fragment_tag));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                }
            } else if (drawerItem.getIdentifier() == 11) {
                CustomisedToast.success(DrawerActivity.this, getString(R.string.logout_sucessfully)).show();
                //Clear list
                AppPreferences.setModesList(DrawerActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE, null);

                //Delete files from local folder
                File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
                final File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + getString(R.string.app_name));
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        Constants.deleteRecursive(dir);
                    }
                });
                t.start();

                // CommonUtils.saveSharedSetting(DrawerActivity.this, PREF_USER_FIRST_TIME, "false");
                AppPreferences.setIsLogin(DrawerActivity.this, AppUtils.ISLOGIN, false);
                AppPreferences.setIsUserFirstTime(DrawerActivity.this, AppUtils.USER_FIRST_TIME, false);
                AppPreferences.setNotificationRead(DrawerActivity.this, AppUtils.NOTIFICATION_READ, null);
                AppPreferences.setNotificationReadTime(DrawerActivity.this, AppUtils.NOTIFICATION_READ_TIME, null);
                AppPreferences.setSearchHistory(DrawerActivity.this, AppUtils.SEARCH_HISTORY, null);
                AppPreferences.setUserId(DrawerActivity.this, AppUtils.USER_ID, null);
                AppPreferences.setUserSessionId(DrawerActivity.this, AppUtils.USER_SESSION_ID, null);
                AppPreferences.setUserName(DrawerActivity.this, AppUtils.USER_NAME, null);

                AppPreferences.setEmail(DrawerActivity.this, AppUtils.USER_EMAIL, null);
                AppPreferences.setPhoto(DrawerActivity.this, AppUtils.USER_PHOTO, "null");
                AppPreferences.setLeadsCount(DrawerActivity.this, AppUtils.LEADS_COUNT, null);
                AppPreferences.setFollowupsCount(DrawerActivity.this, AppUtils.FOLLOWUPS_COUNT, null);

                AppPreferences.setUserRealName(DrawerActivity.this, AppUtils.USER_REAL_NAME, null);
                // AppPreferences.setUserAddress(DrawerActivity.this, AppUtils.USER_ADDRESS, null);
                AppPreferences.setUserLastseen(DrawerActivity.this, AppUtils.USER_LAST_SEEN, null);
                AppPreferences.setUserJoined(DrawerActivity.this, AppUtils.USER_JOINED, null);
                AppPreferences.setUserEmployeeId(DrawerActivity.this, AppUtils.USER_EMP_ID, null);
                AppPreferences.setUserReportTo(DrawerActivity.this, AppUtils.USER_REPORT_TO, null);
                AppPreferences.setUserDesignation(DrawerActivity.this, AppUtils.USER_DESIGNATION, null);
                AppPreferences.setProfileRoles(DrawerActivity.this, AppUtils.USER_ROLES, null);
                AppPreferences.setUserName(DrawerActivity.this, AppUtils.USER_NAME, null);
                AppPreferences.setUserDateOfBirth(DrawerActivity.this, AppUtils.USER_DOB, null);
                AppPreferences.setUserAddressLine1(DrawerActivity.this, AppUtils.USER_ADDRESS_LINE_ONE, null);
                AppPreferences.setUserAddressLine2(DrawerActivity.this, AppUtils.USER_ADDRESS_LINE_TWO, null);
                AppPreferences.setUserState(DrawerActivity.this, AppUtils.USER_STATE, null);

                AppPreferences.setuserBio(DrawerActivity.this, AppUtils.USER_BIO, null);
                AppPreferences.setUserLocality(DrawerActivity.this, AppUtils.USER_LOCALITY, null);
                AppPreferences.setUserCity(DrawerActivity.this, AppUtils.USER_CITY, null);
                AppPreferences.setUserCountry(DrawerActivity.this, AppUtils.USER_COUNTRY, null);
                AppPreferences.setUserZipCode(DrawerActivity.this, AppUtils.USER_ZIP_CODE, null);
                AppPreferences.setTwoStepVerificationStatus(DrawerActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, null);
                AppPreferences.setBoolean(DrawerActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);
                AppPreferences.setUserCountryCode(DrawerActivity.this, AppUtils.USER_COUNTRY_CODE, null);
                AppPreferences.setUserStateCode(DrawerActivity.this, AppUtils.USER_STATE_CODE, null);
                AppPreferences.setUserCityCode(DrawerActivity.this, AppUtils.USER_CITY_CODE, null);
                AppPreferences.setLastCallLogTime(DrawerActivity.this, AppUtils.CALL_LOG_LAST_TIME, 0);
                AppPreferences.setString(DrawerActivity.this, AppUtils.COMPANY_NAME, null);
                AppPreferences.setString(DrawerActivity.this, AppUtils.COMPANY_ID, null);

                Intent intent_receiver = new Intent(getString(R.string.logout_user_broadcast_event));
                // You can also include some extra data.
                LocalBroadcastManager.getInstance(DrawerActivity.this).sendBroadcast(intent_receiver);

                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                nMgr.cancelAll();
                Intent intent_stop_service = new Intent(DrawerActivity.this, ChatService.class);
                stopService(intent_stop_service);
                Intent intent_stop_api_call_service = new Intent(DrawerActivity.this, ApiCallService.class);
                stopService(intent_stop_api_call_service);
                Intent intentDomain = new Intent(DrawerActivity.this, DomainActivity.class);
                startActivity(intentDomain);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(DrawerActivity.this,
                        CallLogBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(DrawerActivity.this,
                        234324243, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);

                databaseHandler.deleteDatabase();

                finish();
            } else if (drawerItem.getIdentifier() == 13) {
                CollectionsMainFragment collectionsMainFragment = (CollectionsMainFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.collections));
                if (collectionsMainFragment != null && collectionsMainFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = collectionsMainFragment.newInstance(getString(R.string.collections));
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

            }

            if (intent != null) {
                DrawerActivity.this.startActivity(intent);
            }
        }

        return false;
    }

    private void openDialogSwitchCompany() {
        View dialogView = null;
        try {
            dialogView = View.inflate(DrawerActivity.this, R.layout.dialog_switch_company, null);

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.
                    Builder(DrawerActivity.this);
            builder.setView(dialogView)
                    .setCancelable(true);
            dialogSwitchCompany = builder.create();
            dialogSwitchCompany.setCanceledOnTouchOutside(true);
            TextView submitTextView;

            companySpinner = (Spinner) dialogView.findViewById(R.id.company_spinner);
            submitTextView = (TextView) dialogView.findViewById(R.id.submit_textView);
            imageViewLoader = (ImageView) dialogView.findViewById(R.id.imageView_loader);

            //calling API
            if (!NetworkUtil.getConnectivityStatusString(DrawerActivity.this).equals
                    (getString(R.string.not_connected_to_internet))) {
                showLoader();
                getCompanies();
            }

            submitTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitTextView.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            submitTextView.setEnabled(true);
                        }
                    }, 3000);
                    //calling API
                    try {
                        if (AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID) != null
                                &&
                                !AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID).isEmpty()) {
                            if (companyDataArrayList.get(companySpinner.getSelectedItemPosition()).getCompanyId().equals
                                    (AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID))) {
                                Toast.makeText(DrawerActivity.this, "Selected company must be" +
                                        " different from current company", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!NetworkUtil.getConnectivityStatusString(DrawerActivity.this).equals
                                        (getString(R.string.not_connected_to_internet))) {
                                    showLoader();
                                    Intent intent = new Intent(DrawerActivity.this, SwitchingCompanyActivity.class);
                                    intent.putExtra("company_id", companyDataArrayList.get(companySpinner.getSelectedItemPosition())
                                            .getCompanyId());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(DrawerActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialogSwitchCompany.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            if (DrawerActivity.this != null && dialogSwitchCompany != null && !dialogSwitchCompany.isShowing()) {
                dialogSwitchCompany.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoader() {
        if (DrawerActivity.this != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (DrawerActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (DrawerActivity.this != null) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public void getCompanies() {
        try {
            task = getString(R.string.user_companies_task);
            if (AppPreferences.getIsLogin(DrawerActivity.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(DrawerActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(DrawerActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(DrawerActivity.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {

                        if (apiResponse.getSuccess()) {

                            //TODO Company Data
                            if (companyDataArrayList != null && companyDataArrayList.size() > 0) {
                                companyDataArrayList.clear();
                            }

                            if (apiResponse.getData().getCompanyData() != null) {
                                for (final CompanyData companyData : apiResponse.getData().getCompanyData()) {
                                    if (companyData != null) {
                                        companyDataArrayList.add(companyData);
                                    }
                                }
                            }


                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(DrawerActivity.this, apiResponse.getMessage());
                        } else {
                            if (DrawerActivity.this != null) {
                                //    Toast.makeText(DrawerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (companyDataArrayList.size() == 0) {
                            CompanyData companyData = new CompanyData();
                            companyData.setCompanyId((AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID)));
                            companyData.setName((AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_NAME)));
                            companyDataArrayList.add(companyData);
                        }

                        //Set Adapter
                        arrayAdapterCompanies = new ArrayAdapter(DrawerActivity.this,
                                R.layout.simple_spinner_item, companyDataArrayList);
                        arrayAdapterCompanies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        companySpinner.setAdapter(arrayAdapterCompanies);

                        for (int j = 0; j < companyDataArrayList.size(); j++) {
                            if (AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID) != null
                                    &&
                                    !AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID).isEmpty()) {
                                if (companyDataArrayList.get(j).getCompanyId().equals
                                        (AppPreferences.getString(DrawerActivity.this, AppUtils.COMPANY_ID))) {
                                    companySpinner.setSelection(j);
                                }
                            }
                        }

                        hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (DrawerActivity.this != null) {
                        Toast.makeText(DrawerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }

                    hideLoader();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }

}
