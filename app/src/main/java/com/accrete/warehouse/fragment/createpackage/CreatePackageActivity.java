package com.accrete.warehouse.fragment.createpackage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackageFile;
import com.accrete.warehouse.utils.ScalingUtilities;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.DynamicChargeAdapter;
import com.accrete.warehouse.adapter.PackageDetailsAdapter;
import com.accrete.warehouse.adapter.ReferredByTransporterNameAdapter;
import com.accrete.warehouse.adapter.SelectOrderItemAdapter;
import com.accrete.warehouse.navigationView.HomeFragment;
import com.accrete.warehouse.model.AlreadyCreatedPackages;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Charge;
import com.accrete.warehouse.model.OrderData;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.model.TransporterNameSearchDatum;
import com.accrete.warehouse.navigationView.DrawerActivity;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.CustomisedEdiText;
import com.accrete.warehouse.utils.CustomisedEdiTextListener;
import com.accrete.warehouse.utils.FilePath;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.AppPreferences.roundTwoDecimals;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.PICK_FILE_RESULT_CODE;
import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_ASK_STORAGE_PERMISSIONS;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 11/29/17.
 */

public class CreatePackageActivity extends AppCompatActivity implements PackageDetailsAdapter.PackageDetailsAdapterListener, DocumentUploaderAdapter.DocAdapterListener, PassDateToCounsellor, DynamicChargeAdapter.DynamicChargeAdapterListener, View.OnClickListener, ReferredByTransporterNameAdapter.ReferredByTransporterNameAdapterListener {
    public Toolbar toolbar;
    PackageDetailsList packageDetails = new PackageDetailsList();
    private RecyclerView ordersItemRecyclerView;
    private TextView orderItemEmptyView;
    private List<SelectOrderItem> selectOrderItemList;
    private SelectOrderItemAdapter selectOrderItemAdapter;
    private String chkoid;
    private String maximumQuantity;
    private int position;
    private LinearLayout orderItemToAddPackage;
    private RecyclerView packageDetailsRecyclerView;
    private RecyclerView dynamicChargesRecyclerView;
    private TextView packageDetailsEmptyView, packageDetailsCreatePackage;
    private TextInputEditText packageDetailsName;
    private TextInputEditText packageDetailsMobile;
    private TextInputEditText packageDetailsEmail;
    private TextInputEditText packageDetailsBillingAddress;
    private TextInputEditText packageDetailsDeliveryAddress;
    private TextInputEditText packageDetailsTaxInvoiceSerialNo;
    private TextInputEditText packageDetailsInvoiceDate;
    private AutoCompleteTextView packageDetailsInvoiceType;
    private Spinner packageDetailsTransportType, packageDetailsTransportMode;
    private TextView packageDetailsUploadDoc;
    private PackageDetailsAdapter packageDetailsAdapter;
    private List<OrderData> packageDetailsList = new ArrayList<>();
    private AllDatePickerFragment datePickerFragment;
    private String stringDateOfInvoice;
    private ArrayList<String> invoiceTypeList = new ArrayList<>();
    private ArrayList<TransportMode> transportTypeList = new ArrayList<>();
    private ArrayList<Charge> dynamicChargesList = new ArrayList<>();
    private ArrayList<String> transportMode = new ArrayList<>();
    private String strEmail, strMobile, strShippingAddress, strBillingAddress, strOrder, strType, strLocal, strInvoiceDate, strInvoiceNumber, strESugam, strDistance, strVehicleNumber, strDate, strTransportNumber, strTransportId, strMode;
    private String chkid;
    private String orderId;
    private List<PendingItems> pendingItemList = new ArrayList<>();
    private List<AlreadyCreatedPackages> packedList = new ArrayList<>();
    private AlertDialog dialogUploadDoc;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<PackageFile> uploadDocumentList = new ArrayList<>();
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private RecyclerView dialogUploadDocRecyclerView;
    private ProgressBar dialogUploadProgressBar;
    private String selectedFilePath;
    private TextInputLayout invoiceSerialNoTextInputLayout;
    private LinearLayout linearLayoutSuccess;
    private String customerName, customerMobile, customerEmail, customerBillingAddress, customerDeliveryAddress;
    private TextView packageGoBack;
    private String flagToCallApi;
    private ArrayAdapter arrayAdapterTransportMode;
    private DynamicChargeAdapter dynamicChargesAdapter;
    private TextInputEditText packageDetailsDistance;
    private TextInputEditText packageDetailsDate;
    private TextInputEditText packageDetailsTransportNumber;
    private TextInputEditText packageDetailsTransportName;
    private TextInputEditText packageDetailsVehicleNumber;
    private TextInputEditText packageDetailsE_wayNumber;
    private TextInputLayout packageDetailsDistanceLayout;
    private TextInputLayout packageDetailsDateLayout;
    private TextInputLayout packageDetailsTransportNumberLayout;
    private TextInputLayout packageDetailsTransportNameLayout;
    private TextInputLayout packageDetailsVehicleNumberLayout;
    private boolean flagToShowTransportDate;
    private String strDateForTransport;
    private Dialog dialog;
    private CustomisedEdiText transporterNameSearchEditText;
    private ReferredByTransporterNameAdapter referredByTransporterNameAdapter;
    private List<TransporterNameSearchDatum> transporterNameList = new ArrayList<>();
    private String strEwayNumber;
    private TextView btnAddImageView;
    private TextView btnUpload;
    private ArrayList<PackageFile> packageUploadDocDetails = new ArrayList<>();
    private ImageView imageViewLoader;
    private CardView cardViewTransporters, cardViewPackageDeliveryDetails;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);
        findViews();
    }

    public void doRefresh() {
    }


    private void findViews() {
        if (packageDetailsList != null && packageDetailsList.size() > 0) {
            packageDetailsList.clear();
        }

        if (dynamicChargesList != null && dynamicChargesList.size() > 0) {
            dynamicChargesList.clear();
        }

        packageDetailsList = getIntent().getParcelableArrayListExtra("packageDetails");
        customerName = getIntent().getStringExtra("customerName");
        customerMobile = getIntent().getStringExtra("customerMobile");
        customerEmail = getIntent().getStringExtra("customerEmail");
        customerBillingAddress = getIntent().getStringExtra("customerBillingAddress");
        customerDeliveryAddress = getIntent().getStringExtra("customerDeliveryAddress");
        flagToCallApi = getIntent().getStringExtra("flag");
        dynamicChargesList = getIntent().getParcelableArrayListExtra("dynamicCharges");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.create_package));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);

        packageDetailsName = (TextInputEditText) findViewById(R.id.package_details_name);
        packageDetailsMobile = (TextInputEditText) findViewById(R.id.package_details_mobile);
        packageGoBack = (TextView) findViewById(R.id.activity_go_back);
        packageDetailsEmail = (TextInputEditText) findViewById(R.id.package_details_email);
        packageDetailsBillingAddress = (TextInputEditText) findViewById(R.id.package_details_billing_address);
        packageDetailsDeliveryAddress = (TextInputEditText) findViewById(R.id.package_details_delivery_address);
        invoiceSerialNoTextInputLayout = (TextInputLayout) findViewById(R.id.invoice_serial_no_textInputLayout);
        packageDetailsTaxInvoiceSerialNo = (TextInputEditText) findViewById(R.id.package_details_tax_invoice_serial_no);
        packageDetailsInvoiceDate = (TextInputEditText) findViewById(R.id.package_details_invoice_date);
        packageDetailsInvoiceType = (AutoCompleteTextView) findViewById(R.id.package_details_invoice_type);
        packageDetailsUploadDoc = (TextView) findViewById(R.id.package_details_upload_doc);
        packageDetailsRecyclerView = (RecyclerView) findViewById(R.id.package_details_recycler_view);
        packageDetailsEmptyView = (TextView) findViewById(R.id.package_details_empty_view);
        packageDetailsCreatePackage = (TextView) findViewById(R.id.package_details_create_package);
        linearLayoutSuccess = (LinearLayout) findViewById(R.id.activity_add_package_added_succesfully);
        packageDetailsTransportType = (Spinner) findViewById(R.id.package_details_transport_type);
        packageDetailsTransportMode = (Spinner) findViewById(R.id.package_details_transport_mode);
        dynamicChargesRecyclerView = (RecyclerView) findViewById(R.id.package_details_charge_field_recycler_view);
        packageDetailsDistance = (TextInputEditText) findViewById(R.id.package_details_distance);
        packageDetailsDate = (TextInputEditText) findViewById(R.id.package_details_date);
        packageDetailsTransportNumber = (TextInputEditText) findViewById(R.id.package_details_transport_number);
        packageDetailsTransportName = (TextInputEditText) findViewById(R.id.package_details_transport_name);
        packageDetailsDistanceLayout = (TextInputLayout) findViewById(R.id.package_details_distance_text_input_layout);
        packageDetailsDateLayout = (TextInputLayout) findViewById(R.id.package_details_date_text_input_layout);
        packageDetailsTransportNumberLayout = (TextInputLayout) findViewById(R.id.package_details_transport_number_text_input_layout);
        packageDetailsTransportNameLayout = (TextInputLayout) findViewById(R.id.package_details_transport_name_text_input_layout);
        packageDetailsVehicleNumber = (TextInputEditText) findViewById(R.id.package_details_vehicle_number);
        packageDetailsVehicleNumberLayout = (TextInputLayout) findViewById(R.id.package_details_vehicle_number_text_input_layout);
        packageDetailsE_wayNumber = (TextInputEditText) findViewById(R.id.package_details_e_way_number);
        imageViewLoader = (ImageView) findViewById(R.id.imageView_loader);
        cardViewPackageDeliveryDetails = (CardView) findViewById(R.id.cardview_package_delivery_details);
        cardViewTransporters = (CardView) findViewById(R.id.cardview_transport_details);
        packageDetailsInvoiceType.setVisibility(View.GONE);
        packageDetailsTransportName.setOnClickListener(this);
        packageDetailsTransportName.clearFocus();
        packageDetailsTransportName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (packageDetailsTransportName.getText().toString().trim().length() == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            openTransporterNameSearchDialog();
                            hideSoftKeyboard(CreatePackageActivity.this);
                        }
                    }
                }
            }
        });

        if (customerName != null) {
            packageDetailsName.setText(customerName);
        }
        if (customerEmail != null) {
            packageDetailsEmail.setText(customerEmail);
        }

        if (customerMobile != null) {
            packageDetailsMobile.setText(customerMobile);
        }


        if (customerBillingAddress != null) {
            packageDetailsBillingAddress.setText(customerBillingAddress);
        }

        if (customerDeliveryAddress != null) {
            packageDetailsDeliveryAddress.setText(customerDeliveryAddress);
        }

        //TODO - Package's Invoice Date is of current date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        packageDetailsInvoiceDate.setText(formattedDate);
        packageDetailsDate.setText(formattedDate);

        if (packageDetailsList.get(0).getInvoiceNumber() != null &&
                !packageDetailsList.get(0).getInvoiceNumber().isEmpty()) {
            packageDetailsTaxInvoiceSerialNo.setText(packageDetailsList.get(0).getInvoiceNumber());
        }

        if (packageDetailsList.get(0).getInvoiceNumberLabel() != null &&
                !packageDetailsList.get(0).getInvoiceNumberLabel().isEmpty()) {
            invoiceSerialNoTextInputLayout.setHint("Tax Invoice Serial No.: " +
                    packageDetailsList.get(0).getInvoiceNumberLabel());
        } else {
            invoiceSerialNoTextInputLayout.setHint("Tax Invoice Serial No.: ");
        }

        packageDetailsAdapter = new PackageDetailsAdapter(getApplicationContext(), packageDetailsList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        packageDetailsRecyclerView.setLayoutManager(mLayoutManager);
        packageDetailsRecyclerView.setHasFixedSize(true);
        packageDetailsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageDetailsRecyclerView.setNestedScrollingEnabled(false);
        packageDetailsRecyclerView.setAdapter(packageDetailsAdapter);


        dynamicChargesAdapter = new DynamicChargeAdapter(getApplicationContext(), dynamicChargesList, this);
        LinearLayoutManager mDynamicChargesLayoutManager = new LinearLayoutManager(getApplicationContext());
        dynamicChargesRecyclerView.setLayoutManager(mDynamicChargesLayoutManager);
        dynamicChargesRecyclerView.setHasFixedSize(true);
        dynamicChargesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dynamicChargesRecyclerView.setNestedScrollingEnabled(false);
        dynamicChargesRecyclerView.setAdapter(dynamicChargesAdapter);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        packageDetailsUploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogUploadDoc(CreatePackageActivity.this);
            }
        });

        packageDetailsInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dialog_from));
            }
        });


        packageDetailsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dialog_from));
                flagToShowTransportDate = true;
            }
        });


        if (transportTypeList.size() > 0) {
            transportTypeList.clear();
            transportTypeList = getIntent().getParcelableArrayListExtra("transportData");
        } else {
            transportTypeList = getIntent().getParcelableArrayListExtra("transportData");
        }


        invoiceTypeAdapter();
        tansportTypeAdapter();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayoutSuccess.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent("notifyOrderInfo");
                    // You can also include some extra data.
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
                finish();

            }
        });
        //dynamicFields();

        packageDetailsTransportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transportModeAdapter(position);
                dynamicFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


      /*  packageDetailsTransportType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageDetailsTransportType.showDropDown();
            }
        });
*/

      /*  packageDetailsTransportMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (transportMode != null && transportMode.size() == 0) {
                    Toast.makeText(CreatePackageActivity.this, "Please select transport type to select mode", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
*/

        packageDetailsInvoiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageDetailsInvoiceType.showDropDown();
            }
        });

        packageDetailsCreatePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packageDetailsList.size() > 0) {
                    showLoader();
                    createPackage();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_message_create_package, Toast.LENGTH_SHORT).show();
                }
            }
        });


        packageGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDrawerActivity = new Intent(CreatePackageActivity.this, DrawerActivity.class);
                intentDrawerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentDrawerActivity.putExtra("flagToManage", "true");
                intentDrawerActivity.putExtra("flagToRedirect", flagToCallApi);
                startActivity(intentDrawerActivity);
                finish();
            }
        });

        if (getIntent().getStringExtra("flagInterstate").equals("0") && !flagToCallApi.equals("runningOrder")) {
            cardViewTransporters.setVisibility(View.GONE);
            cardViewPackageDeliveryDetails.setVisibility(View.GONE);
        } else {
            cardViewTransporters.setVisibility(View.VISIBLE);
            cardViewPackageDeliveryDetails.setVisibility(View.VISIBLE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openTransporterNameSearchDialog() {

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_transporter_name_search);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        transporterNameSearchEditText = (CustomisedEdiText) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        RecyclerView customerRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);


        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        referredByTransporterNameAdapter = new ReferredByTransporterNameAdapter(this,
                transporterNameList, this);

        customerRecyclerView.setLayoutManager(mLayoutManager);
        customerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        customerRecyclerView.setAdapter(referredByTransporterNameAdapter);
        customerRecyclerView.setNestedScrollingEnabled(false);

        transporterNameSearchEditText.setHint("Search Transporter");
        transporterNameSearchEditText.setThreshold(1);
        transporterNameSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (transporterNameSearchEditText.isPerformingCompletion()) {

                } else {
                    String status = NetworkUtil.getConnectivityStatusString(CreatePackageActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchTransporter(s.toString().trim());
                    } else {
                        //Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                        if (transporterNameSearchEditText.getText().toString() != null && !transporterNameSearchEditText.getText().toString().isEmpty()) {
                            //Toast.makeText(getActivity(), getString(R.string.add_customer_error), Toast.LENGTH_SHORT).show();
                            //CustomisedToast.info(getActivity(), getString(R.string.add_customer_error)).show();
                            Toast.makeText(CreatePackageActivity.this, getString(R.string.add_transporter_name_error), Toast.LENGTH_SHORT).show();
                            hideSoftKeyboard(CreatePackageActivity.this);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        transporterNameSearchEditText.addListener(new CustomisedEdiTextListener() {
            @Override
            public void onUpdate() {
                if (transporterNameList != null && transporterNameList.size() > 0) {
                    transporterNameList.clear();
                }

                referredByTransporterNameAdapter.notifyDataSetChanged();
            }
        });

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();


    }

    private void searchTransporter(String s) {
        task = getString(R.string.task_transporter_name_search);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getTransporterName(version, key, task, userId, accessToken, chkid, s, "26");
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (transporterNameList != null && transporterNameList.size() > 0) {
                            transporterNameList.clear();
                        }

                        for (final TransporterNameSearchDatum searchRefferedDatum : apiResponse.getData().getLedgerSearchData()) {
                            if (searchRefferedDatum != null) {
                                transporterNameList.add(searchRefferedDatum);
                            }
                        }
                        refreshTransporterNameRecyclerView();
                    } else {
                       /* if (apiResponse.getSuccessCode().equals("10001")) {
                            //redirectToDomain(apiResponse);
                            // CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10002")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            //  redirectToDomain(apiResponse);
                        } else if (apiResponse.getSuccessCode().equals("10003")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else if (apiResponse.getSuccessCode().equals("10004")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else if (apiResponse.getSuccessCode().equals("10005")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            //redirectToDomain(apiResponse);
                            //  CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            getDataInvoiceDataFromDB();
                            if (NetworkUtil.getConnectivityStatusString(getActivity()).equals("Not connected to Internet")) {
                                CustomisedToast.error(getActivity(), getString(R.string.not_connected_to_internet)).show();
                            } else if (invoiceList.size() > 0) {
                                CustomisedToast.error(getActivity(), getString(R.string.customer_support_error)).show();
                            } else {
                                logout();
                            }
                        } else if (apiResponse.getSuccessCode().equals("10007")) {
                            //redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10008")) {
                            // CustomisedToast.error(getActivity(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10009")) {
                            // CustomisedToast.error(getActivity(), getString(R.string.error_login_mac), Toast.LENGTH_SHORT).show();
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10010")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("100011")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10012")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10013")) {
                            //  redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/

                        Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        hideSoftKeyboard(CreatePackageActivity.this);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideSoftKeyboard(CreatePackageActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getApplicationContext() != null) {
                    //Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    // Log.d("error message",getString(R.string.connect_server_failed));
                    hideSoftKeyboard(CreatePackageActivity.this);
                }
            }
        });
    }

    private void refreshTransporterNameRecyclerView() {
        //Refreshing data after getting input from openCustomerSearchDialog
        referredByTransporterNameAdapter.notifyDataSetChanged();

        transporterNameSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TransporterNameSearchDatum selected = (TransporterNameSearchDatum) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    transporterNameSearchEditText.setText(selected.getName().toString().trim());
                }

                packageDetailsTransportName.setVisibility(View.VISIBLE);

                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    packageDetailsTransportName.setText(selected.getName().toString().trim());
                    strTransportId = selected.getId();
                }

                //currentAddressLayout.setVisibility(View.VISIBLE);
                transporterNameSearchEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        transporterNameSearchEditText.setSelection(transporterNameSearchEditText.getText().toString().length());
                    }
                });


                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        hideSoftKeyboard(CreatePackageActivity.this);

    }

    private void dynamicFields() {
        if (packageDetailsTransportType.getSelectedItem().toString().equals("Air")
                || packageDetailsTransportType.getSelectedItem().toString().equals("Rail")
                || packageDetailsTransportType.getSelectedItem().toString().equals("Ship")) {
            packageDetailsDistanceLayout.setVisibility(View.VISIBLE);
            packageDetailsDateLayout.setVisibility(View.VISIBLE);
            packageDetailsTransportNumberLayout.setVisibility(View.VISIBLE);
            packageDetailsVehicleNumberLayout.setVisibility(View.GONE);
            packageDetailsTransportNameLayout.setVisibility(View.GONE);
        } else if (packageDetailsTransportType.getSelectedItem().toString().equals("Road")) {
            packageDetailsDistanceLayout.setVisibility(View.VISIBLE);
            packageDetailsDateLayout.setVisibility(View.VISIBLE);
            packageDetailsTransportNumberLayout.setVisibility(View.GONE);
            packageDetailsTransportNameLayout.setVisibility(View.VISIBLE);
            packageDetailsVehicleNumberLayout.setVisibility(View.VISIBLE);
        } else {
            packageDetailsDistanceLayout.setVisibility(View.GONE);
            packageDetailsDateLayout.setVisibility(View.GONE);
            packageDetailsTransportNumberLayout.setVisibility(View.GONE);
            packageDetailsTransportNameLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }

    @Override
    public void onExecute(int position) {
        packageDetailsList.remove(position);
        packageDetailsAdapter.notifyDataSetChanged();

        //Clear values of Serial Invoice Number
        if (packageDetailsList != null && packageDetailsList.size() == 0) {
            packageDetailsTaxInvoiceSerialNo.setText("");
            invoiceSerialNoTextInputLayout.setHint("Tax Invoice Serial No.: ");
        }
    }

    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (!flagToShowTransportDate) {
                stringDateOfInvoice = s;
                Date startDate = formatter.parse(stringDateOfInvoice);
                stringDateOfInvoice = targetFormat.format(startDate);
                packageDetailsInvoiceDate.setText(s);
            } else {
                strDateForTransport = s;
                Date startDate = formatter.parse(strDateForTransport);
                strDateForTransport = targetFormat.format(startDate);
                packageDetailsDate.setText(s);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }

    private void invoiceTypeAdapter() {
        invoiceTypeList.add("Retail");
        invoiceTypeList.add("Tax");
        ArrayAdapter arrayAdapterInvoiceType = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, invoiceTypeList);
        arrayAdapterInvoiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packageDetailsInvoiceType.setAdapter(arrayAdapterInvoiceType);
    }

    private void tansportTypeAdapter() {
        List<String> transportType = new ArrayList<>();
        for (int i = 0; i < transportTypeList.size(); i++) {
            transportType.add(transportTypeList.get(i).getType());
        }
      /*  if (transportTypeList != null && transportTypeList.size() > 0) {
            packageDetailsTransportType.setText(transportTypeList.get(0).getType());
        }*/
        ArrayAdapter arrayAdapterInvoiceType = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, transportType);
        arrayAdapterInvoiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packageDetailsTransportType.setAdapter(arrayAdapterInvoiceType);

        if (arrayAdapterTransportMode != null) {
            arrayAdapterTransportMode.notifyDataSetChanged();
        }
    }


    private void transportModeAdapter(int position) {
        try {
            transportMode = new ArrayList<>();
            if (packageDetailsTransportType.getSelectedItem().toString().equals(transportTypeList.get(position).getType())) {
                for (int y = 0; y < transportTypeList.get(position).getModes().size(); y++) {
                    transportMode.add(transportTypeList.get(position).getModes().get(y).getName());
                }

                if (transportTypeList != null && transportTypeList.size() > 0 && transportTypeList.get(position).getModes().get(0).getName() != null) {
                    packageDetailsTransportMode.setSelection(0);
                    strMode = transportTypeList.get(position).getModes().get(0).getPacdelgatpactid();
                }
            }
            arrayAdapterTransportMode = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, transportMode);
            arrayAdapterTransportMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            packageDetailsTransportMode.setAdapter(arrayAdapterTransportMode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Opening Dialog to Upload Documents
    private void openDialogUploadDoc(final Activity activity) {
        View dialogView = View.inflate(CreatePackageActivity.this, R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePackageActivity.this);
        builder.setView(dialogView)
                .setCancelable(false);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(false);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        btnAddImageView = (TextView) dialogView.findViewById(R.id.select_file_textView);
        btnUpload = (TextView) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        textViewEmpty = (TextView) dialogView.findViewById(R.id.dialog_upload_doc_empty_view);

        documentUploaderAdapter = new DocumentUploaderAdapter(getApplicationContext(), uploadDocumentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if(uploadDocumentList.size()>0){
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpload.setEnabled(false);
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                        showLoader();
                        uploadDocAPI();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getApplicationContext(), "document uploaded successfully", Toast.LENGTH_SHORT).show();
                    dialogUploadDoc.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Please upload atleast one doc.", Toast.LENGTH_SHORT).show();
                }
                //Enable Again
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnUpload.setEnabled(true);
                    }
                }, 4000);
            }
        });

        //Dismiss dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    uploadDocumentList.clear();
                }
                dialogUploadDoc.dismiss();
            }
        });

        //Call Intent to select file and add into List
        btnAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkPermissionWithRationale(CreatePackageActivity.this, new HomeFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
                        if (ActivityCompat.checkSelfPermission(CreatePackageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                } else {
                    selectFile();
                }
            }
        });

        dialogUploadDoc.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogUploadDoc.isShowing()) {
            dialogUploadDoc.show();
        }
    }

    private void createPackage() {
        try {
            task = getString(R.string.create_package_task);
            String chkid = null;
            strEmail = packageDetailsEmail.getText().toString();
            strMobile = packageDetailsMobile.getText().toString();
            strShippingAddress = packageDetailsDeliveryAddress.getText().toString();
            strBillingAddress = packageDetailsBillingAddress.getText().toString();
            strDistance = packageDetailsDistance.getText().toString();
            strVehicleNumber = packageDetailsVehicleNumber.getText().toString();
            strTransportNumber = packageDetailsTransportNumber.getText().toString();
            strDate = strDateForTransport;
            strEwayNumber = packageDetailsE_wayNumber.getText().toString();

            if (flagToCallApi.equals("runningOrder")) {
                strType = "1";
            } else {
                strType = "2";
            }

            strLocal = "1";
            strOrder = orderId;
            String strChkoid = getIntent().getExtras().getString("chkoid");

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                stringDateOfInvoice = packageDetailsInvoiceDate.getText().toString();
                Date startDate = formatter.parse(stringDateOfInvoice);
                stringDateOfInvoice = targetFormat.format(startDate);
                packageDetailsInvoiceDate.setText(packageDetailsInvoiceDate.getText().toString());

            } catch (ParseException e) {
                e.printStackTrace();
                hideLoader();
            }

            strInvoiceDate = stringDateOfInvoice;
            strInvoiceNumber = packageDetailsTaxInvoiceSerialNo.getText().toString();
            JSONArray jsonArrayPackageDetails = new JSONArray();

            try {
                Log.e("Pending Item List", String.valueOf(packageDetailsList.size()));
                if (packageDetailsList != null && packageDetailsList.size() > 0) {
                    for (int i = 0; i < packageDetailsList.size(); i++) {
                        JSONObject jsonObjectPackageDetails = new JSONObject();
                        Log.e("name ", packageDetailsList.toString());
                        jsonObjectPackageDetails.put(getString(R.string.isvid), packageDetailsList.get(i).getIsvid());
                        jsonObjectPackageDetails.put(getString(R.string.isid), packageDetailsList.get(i).getIsid());
                        jsonObjectPackageDetails.put(getString(R.string.iid), packageDetailsList.get(i).getIid());
                        if (packageDetailsList.get(i).getMeaid() != null && !packageDetailsList.get(i).getMeaid().isEmpty()) {

                            String qty = calculateQuantity(Double.valueOf(packageDetailsList.get(i).getUsedQuantity()),
                                    Double.parseDouble(packageDetailsList.get(i).getCurrentConversionRate()),
                                    packageDetailsList.get(i).getPreviousConversionRate());
                            Log.d("conversionRate", String.valueOf(packageDetailsList.get(i).getCurrentConversionRate()));
                            jsonObjectPackageDetails.put(getString(R.string.qty), qty);
                        } else {
                            jsonObjectPackageDetails.put(getString(R.string.qty),
                                    packageDetailsList.get(i).getUsedQuantity());
                        }
                        jsonObjectPackageDetails.put(getString(R.string.oiid), packageDetailsList.get(i).getOiid());
                        jsonObjectPackageDetails.put(getString(R.string.meaid), packageDetailsList.get(i).getMeaid());
                        jsonObjectPackageDetails.put(getString(R.string.name), packageDetailsList.get(i).getItemVariationName());
                        jsonObjectPackageDetails.put(getString(R.string.variation), packageDetailsList.get(i).getItemVariationName());
                        jsonObjectPackageDetails.put(getString(R.string.unit), packageDetailsList.get(i).getPreviousUnit());
                        jsonArrayPackageDetails.put(jsonObjectPackageDetails);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                hideLoader();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                hideLoader();
            }

            JSONArray jsonArrayDynamicCharges = new JSONArray();

            try {
                Log.e("Charge List", String.valueOf(dynamicChargesList.size()));
                if (dynamicChargesList != null && dynamicChargesList.size() > 0) {
                    for (int i = 0; i < dynamicChargesList.size(); i++) {
                        JSONObject jsonObjectDynamicChargeDetails = new JSONObject();
                        jsonObjectDynamicChargeDetails.put("id", dynamicChargesList.get(i).getChkoecid());
                        jsonObjectDynamicChargeDetails.put("value", dynamicChargesList.get(i).getValue());
                        jsonArrayDynamicCharges.put(jsonObjectDynamicChargeDetails);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                hideLoader();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                hideLoader();
            }

            JSONArray jsonArrayUploadDetails = new JSONArray();

            try {
                Log.e("packageUploadDocDetails", String.valueOf(packageUploadDocDetails.size()));
                if (packageUploadDocDetails != null && packageUploadDocDetails.size() > 0) {
                    for (int i = 0; i < packageUploadDocDetails.size(); i++) {
                        JSONObject jsonObjectUploadDocDetails = new JSONObject();
                        jsonObjectUploadDocDetails.put("name", packageUploadDocDetails.get(i).getName());
                        jsonObjectUploadDocDetails.put("type", packageUploadDocDetails.get(i).getType());
                        jsonObjectUploadDocDetails.put("tmp_name", packageUploadDocDetails.get(i).getTmpName());
                        jsonObjectUploadDocDetails.put("error", packageUploadDocDetails.get(i).getError());
                        jsonObjectUploadDocDetails.put("size", packageUploadDocDetails.get(i).getSize());
                        jsonObjectUploadDocDetails.put("filepath", packageUploadDocDetails.get(i).getFilepath());
                        jsonArrayUploadDetails.put(jsonObjectUploadDocDetails);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                hideLoader();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                hideLoader();
            }

            //   UploadDocument uploadDocument = new UploadDocument();
//        File propertyImageFile = new File(uploadDocument.getFilePath());
            //  RequestBody propertyImage = RequestBody.create(MediaType.parse("image*//*"), propertyImageFile);
            // MultipartBody.Part propertyImagePart = MultipartBody.Part.createFormData("files", propertyImageFile.getName(), propertyImage);
            //   MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[0];
            //   if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            //   surveyImagesParts = new MultipartBody.Part[uploadDocumentList.size()];
            //  for (int index = 0; index < uploadDocumentList.size(); index++) {
            //    Log.d("Package Details", "requestUploadSurvey: survey image " + index + "  " + uploadDocumentList.get(index).getFilePath());
            //    File file = new File(uploadDocumentList.get(index).getFilePath());
            //   RequestBody surveyBody = RequestBody.create(MediaType.parse("**/*//*"), file);
            //    surveyImagesParts[index] = MultipartBody.Part.createFormData("files[]", file.getPath(), surveyBody);
            //  }
            //  }


            if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
                chkid = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = null;

            //     if (surveyImagesParts != null && surveyImagesParts.length > 0) {
            // call = apiService.createPackageMultipart(version, key, task, userId, accessToken,
            //     strEmail, strMobile, strShippingAddress, strBillingAddress, jsonArrayPackageDetails, chkid, strOrder, strType, strLocal, strInvoiceDate
            //   , strInvoiceNumber, strESugam, surveyImagesParts, strChkoid, strDistance, strVehicleNumber, strTransportNumber, strDate, strMode, strTransportId, jsonArrayDynamicCharges, strEwayNumber);
            // } else {
            call = apiService.createPackageWithoutMultipart(version, key, task, userId, accessToken,
                    strEmail, strMobile, strShippingAddress, strBillingAddress, jsonArrayPackageDetails, chkid, strOrder, strType, strLocal, strInvoiceDate
                    , strInvoiceNumber, strESugam, strChkoid, strDistance, strVehicleNumber, strTransportNumber, strDate, strMode, strTransportId, jsonArrayDynamicCharges, strEwayNumber, jsonArrayUploadDetails);
            // }
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // enquiryList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();

                    if (apiResponse.getSuccess()) {
                        // Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        linearLayoutSuccess.setVisibility(View.VISIBLE);
                        //   Intent intent = new Intent();
                        // setResult(RESULT_OK, intent);
                        //
                        //   finish();
                   /*AlreadyCreatedPackages packages = new AlreadyCreatedPackages();
                        packages.setInvoiceDate(strInvoiceDate);
                        packages.setInvoiceNo(strInvoiceNumber);
                        packages.setCustomerName(packageDetailsName.getText().toString());
                        packages.setOrderID(strOrder);
                        packages.setPacid(apiResponse.getData().getPacid());

                        packedList.add(packages);*/

                        //     ((RunningOrdersExecuteActivity) getParentFragment()).sendPackageDetails();
                        Intent intent = new Intent("notifyOrderInfo");
                        // You can also include some extra data.
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("20004")) {

                        } else {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    hideLoader();

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (CreatePackageActivity.this != null) {
                        Toast.makeText(CreatePackageActivity.this, "Order Execution failed", Toast.LENGTH_LONG).show();
                        hideLoader();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }


    private void uploadDocAPI() {
        try {
            task = getString(R.string.create_package_upload_doc_api);
            String chkid = null;
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[0];
            if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[uploadDocumentList.size()];
                for (int index = 0; index < uploadDocumentList.size(); index++) {
                    Log.d("Package Details", "requestUploadSurvey: survey image " + index + "  " + uploadDocumentList.get(index).getFileUrl());
                    // File file = new File(uploadDocumentList.get(index).getFilePath());
                    File file = new File(decodeFile(uploadDocumentList.get(index).getFileUrl(), 400, 400));
                    RequestBody surveyBody = RequestBody.create(MediaType.parse("png"), file);
                    surveyImagesParts[index] = MultipartBody.Part.createFormData("files[]", file.getPath(), surveyBody);
                }
            }


            if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
                chkid = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = null;

            if (surveyImagesParts != null && surveyImagesParts.length > 0) {
                call = apiService.createPackageUploadMultipleDoc(version, key, task, userId, accessToken,
                        surveyImagesParts);
            }
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // enquiryList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        packageUploadDocDetails.addAll(apiResponse.getData().getPackageUploadData());
                        Toast.makeText(CreatePackageActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("20004")) {

                        } else {
                            Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    hideLoader();

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    if (CreatePackageActivity.this != null) {
                        Toast.makeText(CreatePackageActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                        hideLoader();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }


    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            uploadDocumentList.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } else {
                dialogUploadDocRecyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText("No file selected");
            }
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    @Override
    public void onClickUrlToDownload(int position) {

    }

    private String calculateQuantity(double allocatedQuantity, double selectedConversionRate, double previousConversionRate) {
        Log.d("allocated quantity", allocatedQuantity + "   " + selectedConversionRate + "  " + previousConversionRate);
        double allocatedQuantityValue = (allocatedQuantity / previousConversionRate) * selectedConversionRate;
        return String.valueOf(roundTwoDecimals(allocatedQuantityValue));
    }

    //Add Document into List
    public void addDocument(String selectedFilePath, String fileName) {
        PackageFile uploadDocument = new PackageFile();
        uploadDocument.setName(fileName);
        uploadDocument.setFileUrl(selectedFilePath);
        uploadDocument.setType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        uploadDocumentList.add(uploadDocument);
        documentUploaderAdapter.notifyDataSetChanged();

        if(uploadDocumentList.size()>0){
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    //Intent to select file
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, PICK_FILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG_REQUEST", " " + requestCode + " " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_RESULT_CODE) {

                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);

                selectedFilePath = FilePath.getPath(this, uri);

                String displayName = null;
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

                addDocument(selectedFilePath, displayName);
            }
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("PERMISSION", "Permission callback for  call action");
        switch (requestCode) {

            case REQUEST_CODE_ASK_STORAGE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("PERMISSION", "Storage permission granted");
                        selectFile();

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

    @Override
    public void onClick(View v) {
        packageDetailsTransportName.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openTransporterNameSearchDialog();
        }
        //Enable Again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                packageDetailsTransportName.setEnabled(true);
            }
        }, 4000);
    }

    @Override
    public void onCustomerClick(int position) {
        TransporterNameSearchDatum selected = transporterNameList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            packageDetailsTransportName.setText(selected.getName().toString().trim());
            strTransportId = selected.getId();
        }

        packageDetailsTransportName.setVisibility(View.VISIBLE);
        //currentAddressLayout.setVisibility(View.VISIBLE);
        packageDetailsTransportName.post(new Runnable() {
            @Override
            public void run() {
                packageDetailsTransportName.setSelection(packageDetailsTransportName.getText().toString().length());
            }
        });


        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (linearLayoutSuccess.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent("notifyOrderInfo");
            // You can also include some extra data.
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    private void hideLoader() {
        if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
            imageViewLoader.setVisibility(View.GONE);
        }
        //Enable Touch Back
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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


                });
            }
        });

        thread.start();
    }

    private void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}

