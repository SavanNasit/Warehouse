package com.accrete.warehouse.fragment.runningorders;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.CreatePackageActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.ScannerActivity;
import com.accrete.warehouse.adapter.RunningOrderExecuteAdapter;
import com.accrete.warehouse.fragment.createpackage.AlreadyCreatedPackagesActivity;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Charge;
import com.accrete.warehouse.model.CustomerInfo;
import com.accrete.warehouse.model.Measurement;
import com.accrete.warehouse.model.OrderData;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.model.TransportMode;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.AppPreferences.roundTwoDecimals;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_FOR_CAMERA;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 11/28/17.
 */

public class RunningOrdersExecuteActivity extends AppCompatActivity implements RunningOrderExecuteAdapter.PendingItemsAdapterListener, View.OnClickListener{
    PendingItems pendingItems = new PendingItems();
    List<SelectOrderItem> selectOrderItems = new ArrayList<>();
    List<PendingItems> selectedItemList = new ArrayList<>();
    HashMap<OrderData, Integer> packageData = new HashMap<OrderData, Integer>();
    List<CustomerInfo> customerInfoList = new ArrayList<>();
    private PackageDetailsList packageDetails = new PackageDetailsList();
    private ArrayList<OrderData> packageDetailsList = new ArrayList<>();
    private List<TransportMode> transportationDataArrayList = new ArrayList<>();
    private EditText pendingItemsEdtScan;
    private ImageView pendingItemsImgScan;
    private SwipeRefreshLayout pendingItemsSwipeRefreshLayout;
    private RecyclerView pendingItemsRecyclerView;
    private TextView pendingItemsEmptyView;
    private RunningOrderExecuteAdapter pendingItemsAdapter;
    private List<OrderData> orderDataList = new ArrayList<>();
    private String chkid, chkoid;
    private List<SelectOrderItem> selectOrderItemList = new ArrayList<>();
    private int posToupdate;
    private boolean flagScan;
    private ArrayList<String> measurementNameArrayList = new ArrayList<>();
    private ArrayList<Measurement> measurementsArrayList = new ArrayList<>();
    private double previousConversionRate;
    private String flagToCallApi;
    private FloatingActionButton floatingActionButtonAlreadyCreatedPackage;
    private Toolbar toolbar;
    private CardView cardView;
    private LinearLayout runningExecuteOrdersCustomerDetailsLayout;
    private TextView runningExecuteOrdersCustomerDetailsName;
    private TextView runningExecuteOrdersCustomerDetailsEmail;
    private TextView runningExecuteOrdersCustomerDetailsMobile;
    private List<Charge> dynamicChargesList = new ArrayList<>();
    private String orderId;

    public void getData(String str) {
        // Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        pendingItemsEdtScan.setText(str);
        for (int i = 0; i < orderDataList.size(); i++) {
            if (pendingItemsEdtScan.getText().toString().trim().equals(orderDataList.get(i).getIsid())) {
                if (Integer.parseInt(orderDataList.get(i).getItemQuantity()) == 0) {
                    Toast.makeText(this, "No item available", Toast.LENGTH_SHORT).show();
                } else {
                    if (orderDataList.get(i).getMeaid() != null && !orderDataList.get(i).getMeaid().isEmpty()) {
                        bottomSheetAddItemQuantity(orderDataList.get(i), i);
                    } else {
                        orderDataList.get(i).setUsedQuantity(String.valueOf(Integer.valueOf(orderDataList.get(i).getUsedQuantity()) + 1));
                        flagScan = true;
                        posToupdate = i;
                        pendingItemsAdapter.notifyDataSetChanged();
                        getAllocatedQuantity(Integer.valueOf(orderDataList.get(i).getUsedQuantity()), orderDataList, posToupdate);
                    }
                }
                pendingItemsEdtScan.setText("");
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_execute_orders);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (getIntent().getStringExtra("chkid") != null) {
            chkid = getIntent().getStringExtra("chkid");
            chkoid = getIntent().getStringExtra("chkoid");
            orderId = getIntent().getStringExtra("orderId");
            flagToCallApi = getIntent().getStringExtra("flag");
        }


        findViews();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("notifyOrderInfo"));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(orderDataList.size()>0){
                orderDataList.clear();
            }

            if(packageDetailsList.size()>0){
                packageDetailsList.clear();
            }

            if(packageData.size()>0){
                packageData.clear();
            }
            executeSelectedItems();
        }
    };

    private void findViews() {
        cardView = (CardView) findViewById(R.id.card_view);
        runningExecuteOrdersCustomerDetailsLayout = (LinearLayout) findViewById(R.id.running_execute_orders_customer_details_layout);
        runningExecuteOrdersCustomerDetailsName = (TextView) findViewById(R.id.running_execute_orders_customer_details_name);
        runningExecuteOrdersCustomerDetailsEmail = (TextView) findViewById(R.id.running_execute_orders_customer_details_email);
        runningExecuteOrdersCustomerDetailsMobile = (TextView) findViewById(R.id.running_execute_orders_customer_details_mobile);
        pendingItemsEdtScan = (EditText) findViewById(R.id.pending_items_edt_scan);
        pendingItemsImgScan = (ImageView) findViewById(R.id.pending_items_img_scan);
        floatingActionButtonAlreadyCreatedPackage = (FloatingActionButton) findViewById(R.id.running_execute_ordres_fab);
        //pendingItemsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pending_items_swipe_refresh_layout);
        pendingItemsRecyclerView = (RecyclerView) findViewById(R.id.pending_items_recycler_view);
        pendingItemsEmptyView = (TextView) findViewById(R.id.pending_items_empty_view);
        pendingItemsAdapter = new RunningOrderExecuteAdapter(this, orderDataList, this, 0, posToupdate, flagScan);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pendingItemsRecyclerView.setLayoutManager(mLayoutManager);
        pendingItemsRecyclerView.setHasFixedSize(true);
        pendingItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingItemsRecyclerView.setNestedScrollingEnabled(false);
        pendingItemsRecyclerView.setAdapter(pendingItemsAdapter);
        floatingActionButtonAlreadyCreatedPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAlreadyCreatedPackages = new Intent(RunningOrdersExecuteActivity.this, AlreadyCreatedPackagesActivity.class);
                intentAlreadyCreatedPackages.putExtra("chkoid", chkoid);
                intentAlreadyCreatedPackages.putExtra("flag", flagToCallApi);
                startActivity(intentAlreadyCreatedPackages);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(""+orderId);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (orderDataList.size() > 0) {
            pendingItemsRecyclerView.setVisibility(View.VISIBLE);
            pendingItemsEmptyView.setVisibility(View.GONE);
        } else {
            pendingItemsRecyclerView.setVisibility(View.GONE);
            pendingItemsEmptyView.setVisibility(View.VISIBLE);
            pendingItemsEmptyView.setText(getString(R.string.no_data_available));
        }

        pendingItemsImgScan.setOnClickListener(this);
        pendingItemsEdtScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < orderDataList.size(); i++) {
                    if (pendingItemsEdtScan.getText().toString().trim().equals(orderDataList.get(i).getIsid())) {
                        if (Integer.parseInt(orderDataList.get(i).getItemQuantity()) == 0) {
                            Toast.makeText(RunningOrdersExecuteActivity.this, "No item available", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(orderDataList.get(i).getUsedQuantity()) >= Integer.parseInt(orderDataList.get(i).getItemQuantity())) {
                                Toast.makeText(RunningOrdersExecuteActivity.this, "Quantity is greater than ordered quantity", Toast.LENGTH_SHORT).show();
                            } else {


                                if (orderDataList.get(i).getMeaid() != null && !orderDataList.get(i).getMeaid().isEmpty()) {
                                    bottomSheetAddItemQuantity(orderDataList.get(i), i);
                                } else {
                                    orderDataList.get(i).setUsedQuantity(String.valueOf(Integer.valueOf(orderDataList.get(i).getUsedQuantity()) + 1));
                                    flagScan = true;
                                    posToupdate = i;
                                    pendingItemsAdapter.notifyDataSetChanged();
                                    getAllocatedQuantity(Integer.valueOf(orderDataList.get(i).getUsedQuantity()), orderDataList, posToupdate);
                                }
                            }
                        }
                        pendingItemsEdtScan.setText("");
                    }
                }
            }
        });


        LinearLayout linearLayoutPackageDetails = (LinearLayout) findViewById(R.id.pending_items_package_details);
        linearLayoutPackageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packageDetailsList != null && packageDetailsList.size() > 0) {
                    Intent intentCreatePackage = new Intent(RunningOrdersExecuteActivity.this, CreatePackageActivity.class);
                    intentCreatePackage.putExtra("chkoid", chkoid);
                    intentCreatePackage.putExtra("flag", flagToCallApi);
                    intentCreatePackage.putParcelableArrayListExtra("packageDetails", packageDetailsList);
                    intentCreatePackage.putParcelableArrayListExtra("transportData",(ArrayList<? extends Parcelable>) transportationDataArrayList);
                    intentCreatePackage.putExtra("customerName", getIntent().getStringExtra("customerName"));
                    intentCreatePackage.putExtra("customerEmail", getIntent().getStringExtra("customerEmail"));
                    intentCreatePackage.putExtra("customerMobile",getIntent().getStringExtra("customerMobile"));
                    intentCreatePackage.putExtra("customerBillingAddress",getIntent().getStringExtra("customerBillingAddress"));
                    intentCreatePackage.putExtra("customerDeliveryAddress",getIntent().getStringExtra("customerDeliveryAddress"));
                    intentCreatePackage.putParcelableArrayListExtra("dynamicCharges",(ArrayList<? extends Parcelable>) dynamicChargesList);
                    startActivityForResult(intentCreatePackage, 100);
                } else {
                    Toast.makeText(RunningOrdersExecuteActivity.this, "Please add one or more items to create package", Toast.LENGTH_SHORT).show();
                }
            }
        });

        executeSelectedItems();

        if (flagToCallApi.equals("runningOrder")) {
            runningExecuteOrdersCustomerDetailsLayout.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            if (getIntent().getStringExtra("customerName") != null && !getIntent().getStringExtra("customerName").isEmpty()) {
                runningExecuteOrdersCustomerDetailsName.setText(getIntent().getStringExtra("customerName"));
                runningExecuteOrdersCustomerDetailsName.setVisibility(View.VISIBLE);
            } else {
                runningExecuteOrdersCustomerDetailsName.setVisibility(View.GONE);
            }
            if (getIntent().getStringExtra("customerEmail") != null && !getIntent().getStringExtra("customerEmail").isEmpty()) {
                runningExecuteOrdersCustomerDetailsEmail.setText(getIntent().getStringExtra("customerEmail"));
                runningExecuteOrdersCustomerDetailsEmail.setVisibility(View.VISIBLE);
            } else {
                runningExecuteOrdersCustomerDetailsEmail.setVisibility(View.GONE);
            }
            if (getIntent().getStringExtra("customerMobile") != null && !getIntent().getStringExtra("customerMobile").isEmpty()) {
                runningExecuteOrdersCustomerDetailsMobile.setText(getIntent().getStringExtra("customerMobile"));
                runningExecuteOrdersCustomerDetailsMobile.setVisibility(View.VISIBLE);
            } else {
                runningExecuteOrdersCustomerDetailsMobile.setVisibility(View.GONE);
            }
        } else {
            runningExecuteOrdersCustomerDetailsLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMessageRowClicked(int position) {

    }


    @Override
    public void onExecute(final OrderData orderDataList, final int position) {
        // executeSelectedItems(isid, oiid,maximumQuantity,position);
        bottomSheetAddItemQuantity(orderDataList, position);
    }

    private void bottomSheetAddItemQuantity(final OrderData orderDataList, final int position) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(R.layout.dialog_create_package_add_quantity);
        ImageView dialogCreatePackageImage;
        TextView dialogCreatePackageInventory;
        TextView listRowOrderItemVendor;
        TextView dialogCreatePackageQuantityAvailable;
        final AutoCompleteTextView dialogCreatePackageQuantityAllocated;
        final AutoCompleteTextView dialogCreatePackageQuantityUnit;
        TextView listRowOrderItemAddQuantity;
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialogCreatePackageImage = (ImageView) dialog.findViewById(R.id.dialog_create_package_image);
        dialogCreatePackageInventory = (TextView) dialog.findViewById(R.id.dialog_create_package_inventory);
        listRowOrderItemVendor = (TextView) dialog.findViewById(R.id.list_row_order_item_vendor);
        dialogCreatePackageQuantityAvailable = (TextView) dialog.findViewById(R.id.dialog_create_package_quantity_available);
        dialogCreatePackageQuantityAllocated = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_create_package_quantity_allocated);
        dialogCreatePackageQuantityUnit = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_create_package_quantity_unit);
        listRowOrderItemAddQuantity = (TextView) dialog.findViewById(R.id.list_row_order_item_add_quantity);
        if (orderDataList.getImage() != null && !orderDataList.getImage().isEmpty()) {
            dialogCreatePackageImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(orderDataList.getImage())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dialogCreatePackageImage);
        } else {
            dialogCreatePackageImage.setVisibility(View.GONE);
        }

        dialogCreatePackageInventory.setText(orderDataList.getExecuteItemData().getInventory());
        dialogCreatePackageQuantityAvailable.setText(orderDataList.getExecuteItemData().getAvailableQuantity());
        dialogCreatePackageQuantityAllocated.setText(orderDataList.getExecuteItemData().getAllocatedQuantity());
        dialogCreatePackageQuantityUnit.setText(orderDataList.getExecuteItemData().getUnit());

        if (orderDataList.getMeaid() != null && !orderDataList.getMeaid().isEmpty()) {
            dialogCreatePackageQuantityAllocated.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            if (measurementNameArrayList.size() > 0) {
                measurementNameArrayList.clear();
            }
            for (int i = 0; i < orderDataList.getMeasurements().size(); i++) {
                measurementNameArrayList.add(orderDataList.getMeasurements().get(i).getName());
                measurementsArrayList.add(orderDataList.getMeasurements().get(i));
            }

            final ArrayAdapter measurementArrayAdapter =
                    new ArrayAdapter<String>(this, R.layout.simple_spinner_item, measurementNameArrayList);
            measurementArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            dialogCreatePackageQuantityUnit.setAdapter(measurementArrayAdapter);

            dialogCreatePackageQuantityUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    for (int i = 0; i < orderDataList.getMeasurements().size(); i++) {
                        if (orderDataList.getMeasurements().get(i).getSelected()) {
                            previousConversionRate = Double.parseDouble(orderDataList.getMeasurements().get(i).getConversionRate());
                            orderDataList.getMeasurements().get(i).setSelected(false);
                        }
                    }

                    orderDataList.setCurrentConversionRate(measurementsArrayList.get(pos).getConversionRate());
                    Log.d("conversionRate", String.valueOf(orderDataList.getCurrentConversionRate()));
                    orderDataList.setItemUnit(measurementArrayAdapter.getItem(pos).toString());
                    orderDataList.getMeasurements().get(pos).setSelected(true);
                    orderDataList.getExecuteItemData().setUnit(orderDataList.getItemUnit());
                    if (dialogCreatePackageQuantityAllocated.getText().toString() != null &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty()) {
                        orderDataList.getExecuteItemData().setAllocatedQuantity(calculateQuantity(
                                Double.valueOf(dialogCreatePackageQuantityAllocated.getText().toString()),
                                Double.parseDouble(measurementsArrayList.get(pos).getConversionRate()), previousConversionRate));
                        dialogCreatePackageQuantityAllocated.setText(orderDataList.getExecuteItemData().getAllocatedQuantity());
                        orderDataList.setItemQuantity(calculateQuantity(Double.valueOf(orderDataList.getItemQuantity()), Double.parseDouble(measurementsArrayList.get(pos).getConversionRate()), previousConversionRate));
                    } else {
                        dialogCreatePackageQuantityAllocated.setText("0");
                    }

                }
            });


        }

        dialogCreatePackageQuantityUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreatePackageQuantityUnit.showDropDown();
            }
        });


        dialogCreatePackageQuantityAllocated.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialogCreatePackageQuantityAllocated.getWindowToken(), 0);
                    return true;
                }
                return false;

            }
        });

        listRowOrderItemAddQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderDataList.getMeaid() != null && !orderDataList.getMeaid().isEmpty()) {
                    if (dialogCreatePackageQuantityAllocated.getText().toString() != null && !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty()
                            && Double.parseDouble(dialogCreatePackageQuantityAllocated.getText().toString()) == 0.00) {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    } else if (dialogCreatePackageQuantityAllocated.getText().toString() != null && !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty()
                            && Double.parseDouble(dialogCreatePackageQuantityAllocated.getText().toString()) != 0 && (Double.parseDouble(dialogCreatePackageQuantityAllocated.getText().toString()) > (Double.parseDouble(orderDataList.getExecuteItemData().getAllocatedQuantity())))) {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Quantity is greater than ordered quantity", Toast.LENGTH_SHORT).show();
                    } else if (dialogCreatePackageQuantityAllocated.getText().toString() != null
                            && !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty() && Double.parseDouble(dialogCreatePackageQuantityAllocated.getText().toString()) > 0) {
                        orderDataList.setUsedQuantity(dialogCreatePackageQuantityAllocated.getText().toString());
                        packageData.put(orderDataList, position);
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Item added Successfully", Toast.LENGTH_SHORT).show();
                        Set<OrderData> keyProductSet = packageData.keySet();
                        packageDetailsList = new ArrayList<OrderData>(keyProductSet);
                        pendingItemsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (dialogCreatePackageQuantityAllocated.getText().toString() != null &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty() &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().equals("null")
                            && Integer.parseInt(dialogCreatePackageQuantityAllocated.getText().toString()) == 0) {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    } else if (dialogCreatePackageQuantityAllocated.getText().toString() != null &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty() &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().equals("null")
                            && Integer.parseInt(dialogCreatePackageQuantityAllocated.getText().toString()) != 0
                            && orderDataList.getExecuteItemData().getAllocatedQuantity() != null
                            && !orderDataList.getExecuteItemData().getAllocatedQuantity().isEmpty()
                            && Integer.parseInt(dialogCreatePackageQuantityAllocated.getText().toString()) >
                            Integer.parseInt(orderDataList.getExecuteItemData().getAllocatedQuantity())) {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Quantity is greater than ordered quantity", Toast.LENGTH_SHORT).show();
                    } else if (dialogCreatePackageQuantityAllocated.getText().toString() != null
                            && !dialogCreatePackageQuantityAllocated.getText().toString().isEmpty() &&
                            !dialogCreatePackageQuantityAllocated.getText().toString().equals("null") &&
                            Integer.parseInt(dialogCreatePackageQuantityAllocated.getText().toString()) > 0) {
                        orderDataList.setUsedQuantity(dialogCreatePackageQuantityAllocated.getText().toString());
                        packageData.put(orderDataList, position);
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Item added Successfully", Toast.LENGTH_SHORT).show();
                        Set<OrderData> keyProductSet = packageData.keySet();
                        packageDetailsList = new ArrayList<OrderData>(keyProductSet);
                        pendingItemsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(RunningOrdersExecuteActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        dialog.show();
    }

    private String calculateQuantity(double allocatedQuantity, double selectedConversionRate, double previousConversionRate) {
        Log.d("allocated quantity", allocatedQuantity + "   " + selectedConversionRate + "  " + previousConversionRate);
        double allocatedQuantityValue = (allocatedQuantity / selectedConversionRate) * previousConversionRate;
        return String.valueOf(roundTwoDecimals(allocatedQuantityValue));
    }

    @Override
    public void onAddDirectToPackage(OrderData orderDataList, int position) {
        packageData.put(orderDataList, position);
        Toast.makeText(RunningOrdersExecuteActivity.this, "Item added Successfully", Toast.LENGTH_SHORT).show();
        Set<OrderData> keyProductSet = packageData.keySet();
        packageDetailsList = new ArrayList<OrderData>(keyProductSet);
    }

    @Override
    public void onRemoveDirectFromPackage(OrderData orderDataList, int position) {
        if (packageData.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                packageData.remove(orderDataList, position);
            } else {
                for (Iterator<Map.Entry<OrderData, Integer>> it = packageData.entrySet().iterator();
                     it.hasNext(); ) {
                    Map.Entry<OrderData, Integer> entry = it.next();
                    if (entry.getValue().equals(position)) {
                        it.remove();
                    }
                }
            }
            Set<OrderData> keyProductSet = packageData.keySet();
            packageDetailsList = new ArrayList<OrderData>(keyProductSet);
        }
    }

    private void executeSelectedItems() {
        task = getString(R.string.execute_order_task);
        if (AppPreferences.getIsLogin(RunningOrdersExecuteActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(RunningOrdersExecuteActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(RunningOrdersExecuteActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(RunningOrdersExecuteActivity.this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call;
        if (flagToCallApi.equals("runningOrder")) {
            call = apiService.executeSelectedItem(version, key, task, userId, accessToken, chkid, chkoid);
        } else {
            call = apiService.executeRequestSelectedItem(version, key, task, userId, accessToken, chkid, chkoid);
        }

        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {

                    if (apiResponse.getSuccess()) {

                        if(transportationDataArrayList!=null && transportationDataArrayList.size()>0){
                            transportationDataArrayList.clear();
                        }

                        if(dynamicChargesList!=null && dynamicChargesList.size()>0){
                            dynamicChargesList.clear();
                        }


                        pendingItemsAdapter.notifyDataSetChanged();
                        pendingItemsRecyclerView.setVisibility(View.VISIBLE);
                        pendingItemsEmptyView.setVisibility(View.GONE);

                        transportationDataArrayList.addAll(apiResponse.getData().getTransprotModes());
                        if(apiResponse.getData().getExecuteOrderTaxesEdit()) {
                            dynamicChargesList.addAll(apiResponse.getData().getCharges());
                        }

                        for (OrderData orderData : apiResponse.getData().getOrderData()) {
                            orderData.setUsedQuantity("0");
                            for (int i = 0; i < orderData.getMeasurements().size(); i++) {
                                if (orderData.getMeasurements().get(i).getSelected()) {
                                    orderData.setPreviousConversionRate(Double.parseDouble(orderData.getMeasurements()
                                            .get(i).getConversionRate()));
                                }
                            }
                            orderData.setCurrentConversionRate(String.valueOf(orderData.getPreviousConversionRate()));
                            orderData.setPreviousUnit(orderData.getItemUnit());
                            orderDataList.add(orderData);
                        }


                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            pendingItemsEmptyView.setText(getString(R.string.no_data_available));
                            pendingItemsRecyclerView.setVisibility(View.GONE);
                            pendingItemsEmptyView.setVisibility(View.VISIBLE);

                        } else if (apiResponse.getSuccessCode().equals("20004")) {
                            pendingItemsEmptyView.setText(getString(R.string.no_data_available));
                            pendingItemsRecyclerView.setVisibility(View.GONE);
                            pendingItemsEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("wh:RunningOrder Execute", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionWithRationale((Activity) RunningOrdersExecuteActivity.this, new RunningOrdersFragment(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA)) {
                if (ActivityCompat.checkSelfPermission(RunningOrdersExecuteActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        } else {
            scanBarcode();
        }
    }

    public void scanBarcode() {
        Log.d("PERMISSION", "Camera3");
        Intent intentScan = new Intent(RunningOrdersExecuteActivity.this, ScannerActivity.class);
        startActivityForResult(intentScan, 1000);
    }

    public void getAllocatedQuantity(int allocatedQuantity, List<OrderData> orderData, int position) {
        posToupdate = position;
        pendingItemsAdapter = new RunningOrderExecuteAdapter(RunningOrdersExecuteActivity.this, orderDataList, this, allocatedQuantity, posToupdate, false);
        pendingItemsRecyclerView.setAdapter(pendingItemsAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG_REQUEST", " " + requestCode + " " + resultCode);
        if (resultCode == 1000) {
            getData(data.getStringExtra("scanResult"));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_CAMERA: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("PERMISSION", "Camera");
                        scanBarcode();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        } else {
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;

        }
    }

    public void askUserToAllowPermissionFromSetting() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
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
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}
