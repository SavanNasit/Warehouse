package com.accrete.warehouse.navigationView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.domain.DomainActivity;
import com.accrete.warehouse.fragment.HomeFragment;
import com.accrete.warehouse.fragment.ManageConsignmentFragment;
import com.accrete.warehouse.fragment.ManageGatePassFragment;
import com.accrete.warehouse.fragment.ManagePackagesFragment;
import com.accrete.warehouse.fragment.ReceiveConsignmentFragment;
import com.accrete.warehouse.fragment.RunningOrdersExecuteFragment;
import com.accrete.warehouse.fragment.RunningOrdersFragment;
import com.accrete.warehouse.model.ManageGatepass;
import com.accrete.warehouse.model.RunningOrders;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;


public class DrawerActivity extends AppCompatActivity implements SelectWarehouseAdapter.SelectWarehouseAdapterListener {
    public static Drawer drawer;
    private FragmentRefreshListener fragmentRefreshListener;
    private AccountHeader headerResult = null;
    private ArrayList<String> drawerItemList = new ArrayList();
    private String domainName;
    private ProfileDrawerItem profile;
    private ProgressBar progressBarSelectWarehouse;
    private AlertDialog dialogSelectWarehouse;
    private SelectWarehouseAdapter mAdapter;
    private String stringWarehouseName;
    private boolean doubleBackToExitPressedOnce = false;
    private DrawerInterface drawerInterfaceToSend;

    public DrawerActivity() {
    }


    public void setCallback(DrawerInterface drawerInterface) {
        this.drawerInterfaceToSend = drawerInterface;
    }
    public static void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPreferences.setIsUserFirstTime(DrawerActivity.this, AppUtils.USER_FIRST_TIME, true);
        setContentView(R.layout.activity_drawer);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment f = HomeFragment.newInstance(getString(R.string.home_fragment));
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
        domainName = AppPreferences.getDomain(DrawerActivity.this, AppUtils.DOMAIN);
        if (domainName.length() > 1)
            domainName = domainName.substring(0, domainName.length() - 1);
        profile = new ProfileDrawerItem().withIcon(R.drawable.ic_chevron_right).withName("Poonam").withEmail("kukreti.winnie57@gmail.com").withIdentifier(101);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstanceState)
                .build();


   /*     drawerItemList.add("Warehouse 1");
        drawerItemList.add("Warehouse 2");
        drawerItemList.add("Warehouse 3");
        drawerItemList.add("Warehouse 4");
        drawerItemList.add("Warehouse 5");
        drawerItemList.add("Warehouse 6");*/


        //Create the drawer


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(false)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_home).withIdentifier(0)
                                .withName("Home").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_warehouse).withIdentifier(1)
                                .withName("Select Warehouse").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_running_orders).withIdentifier(2)
                                .withName("Running Orders").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_manage_package).withIdentifier(3)
                                .withName("Manage Package").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_manage_gatepass).withIdentifier(4)
                                .withName("Manage Gate Pass").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_manage_consignment).withIdentifier(5)
                                .withName("Manage Consignment").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_receive_consignment).withIdentifier(6)
                                .withName("Receive Consignment").withSelectable(true),
                        new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(7)
                                .withName("Logout").withSelectable(true),
                        new PrimaryDrawerItem().withName(getString(R.string.connected_to)).withDescription(domainName),
                        new PrimaryDrawerItem().withName("").withDescription(""))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 0) {
                                Fragment f = HomeFragment.newInstance(getString(R.string.home_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 1) {
                                dialogSelectWarehouse();
                            } else if (drawerItem.getIdentifier() == 2) {
                                Fragment f = RunningOrdersFragment.newInstance(getString(R.string.running_orders_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 3) {
                                Fragment f = ManagePackagesFragment.newInstance(getString(R.string.manage_packages_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 4) {
                                Fragment manageGatePassFragment = ManageGatePassFragment.newInstance(getString(R.string.manage_gatepass_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, manageGatePassFragment).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 5) {
                                Fragment manageConsignmentFragment = ManageConsignmentFragment.newInstance(getString(R.string.manage_consignment_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, manageConsignmentFragment).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 6) {
                                Fragment receiveConsignmentFragment = ReceiveConsignmentFragment.newInstance(getString(R.string.recieve_consignment_fragment));
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, receiveConsignmentFragment).commitAllowingStateLoss();
                            } else if (drawerItem.getIdentifier() == 7) {
                                Intent intentDomain = new Intent(DrawerActivity.this, DomainActivity.class);
                                startActivity(intentDomain);
                                finish();
                            }

                            if (intent != null) {
                                DrawerActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();


      /*  PrimaryDrawerItem item = null;
        for (int i = 0; i < drawerItemList.size(); i++) {
            item = new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(i)
                    .withName(drawerItemList.get(i)).withSelectable(true);
            drawer.addItem(item); }*/

        if (drawerInterfaceToSend != null) {
            drawerInterfaceToSend.sendDrawer(drawer);
        }
    }


    private void dialogSelectWarehouse() {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_select_warehouse, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectWarehouse = builder.create();
        dialogSelectWarehouse.setCanceledOnTouchOutside(true);
        TextView textViewOk = (TextView) dialogView.findViewById(R.id.btn_ok);
        TextView textViewCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBarSelectWarehouse = (ProgressBar) dialogView.findViewById(R.id.dialog_select_warehouse_progress_bar);
        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.select_dialog_recycler_view);
        List<String> warehouseList = new ArrayList<>();
        warehouseList.add("Jp Nagar");
        warehouseList.add("Kolkata");
        warehouseList.add("Bangalore");
        warehouseList.add("Delhi");
        warehouseList.add("Agra");
        warehouseList.add("Sri Nagar & Jammu Kashmir ......");
        warehouseList.add("Sri Nagar & Jammu Kashmir ...... Sri Nagar & Jammu Kashmir");

        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerActivity.this, " You have selected Warehouse: " + stringWarehouseName, Toast.LENGTH_SHORT).show();
                dialogSelectWarehouse.dismiss();
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectWarehouse.dismiss();

            }
        });
        mAdapter = new SelectWarehouseAdapter(getApplicationContext(), warehouseList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        dialogSelectWarehouse.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectWarehouse.isShowing()) {
            dialogSelectWarehouse.show();
        }
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

    @Override
    public void onBackPressed() {
        //handle the back press, close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (currentFragment instanceof RunningOrdersFragment) {
                getSupportFragmentManager().popBackStack();
                drawer.deselect(2);
                drawer.setSelection(0);
            } else if (currentFragment instanceof ManagePackagesFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(3);
                drawer.setSelection(0);
            } else if (currentFragment instanceof ManageGatePassFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(4);
                drawer.setSelection(0);
            } else if (currentFragment instanceof ManageConsignmentFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(5);
                drawer.setSelection(0);

            } else if (currentFragment instanceof ReceiveConsignmentFragment) {
                // add your code here
                getSupportFragmentManager().popBackStack();
                drawer.deselect(6);
                drawer.setSelection(0);

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

    private List<String> category() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < drawerItemList.size() - 1; i++) {
            PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(items.get(i));
            items.add(String.valueOf(item2));
        }

        return items;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMessageRowClicked(String strWarehouseName) {
        stringWarehouseName = strWarehouseName;
        AppPreferences.setWarehouseName(DrawerActivity.this, AppUtils.WAREHOUSE_NAME, strWarehouseName);
        //setCallback(strWarehouseName);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof HomeFragment) {
            if (getFragmentRefreshListener() != null) {
                Log.d("warehouse", strWarehouseName);
                getFragmentRefreshListener().onRefresh(strWarehouseName);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG_REQUEST", " " + requestCode + " " + resultCode);
        if (resultCode == 1000) {
            Fragment newCurrentFragment = getSupportFragmentManager().findFragmentById(R.id.running_orders_container);
            if (newCurrentFragment instanceof RunningOrdersExecuteFragment) {
                Log.e("NEW RUNNING ORDER", " " + requestCode + " " + resultCode);
                ((RunningOrdersExecuteFragment) newCurrentFragment).getData(data.getStringExtra("scanResult"));
            }
        }
    }


    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener {
        void onRefresh(String warehouseName);
    }

}