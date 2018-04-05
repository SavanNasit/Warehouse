package com.accrete.warehouse;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.PackageDetailsAdapter;
import com.accrete.warehouse.adapter.SelectOrderItemAdapter;
import com.accrete.warehouse.fragment.HomeFragment;
import com.accrete.warehouse.model.AlreadyCreatedPackages;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.CustomerInfo;
import com.accrete.warehouse.model.OrderData;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.model.UploadDocument;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.FilePath;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

public class CreatePackageActivity extends AppCompatActivity implements PackageDetailsAdapter.PackageDetailsAdapterListener, DocumentUploaderAdapter.DocAdapterListener, PassDateToCounsellor {
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
    private TextView packageDetailsEmptyView, packageDetailsCreatePackage;
    private TextInputEditText packageDetailsName;
    private TextInputEditText packageDetailsMobile;
    private TextInputEditText packageDetailsEmail;
    private TextInputEditText packageDetailsBillingAddress;
    private TextInputEditText packageDetailsDeliveryAddress;
    private TextInputEditText packageDetailsTaxInvoiceSerialNo;
    private TextInputEditText packageDetailsInvoiceDate;
    private AutoCompleteTextView packageDetailsInvoiceType;
    private TextView packageDetailsUploadDoc;
    private PackageDetailsAdapter packageDetailsAdapter;
    private List<OrderData> packageDetailsList = new ArrayList<>();
    private AllDatePickerFragment datePickerFragment;
    private String stringDateOfInvoice;
    private ArrayList<String> invoiceTypeList = new ArrayList<>();
    private String strEmail, strMobile, strShippingAddress, strBillingAddress, strOrder, strType, strLocal, strInvoiceDate, strInvoiceNumber, strESugam;
    private String chkid;
    private String orderId;
    private List<PendingItems> pendingItemList = new ArrayList<>();
    private List<AlreadyCreatedPackages> packedList = new ArrayList<>();
    private AlertDialog dialogUploadDoc;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<UploadDocument> uploadDocumentList = new ArrayList<>();
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private RecyclerView dialogUploadDocRecyclerView;
    private ImageView btnAddImageView;
    private Button btnUpload;
    private ProgressBar dialogUploadProgressBar;
    private String selectedFilePath;
    private TextInputLayout invoiceSerialNoTextInputLayout;
    private LinearLayout linearLayoutSuccess;
    private CustomerInfo customerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);
        findViews();
    }

    public void doRefresh() {
    }

    private void findViews() {
        packageDetailsList = getIntent().getParcelableArrayListExtra("packageDetails");
        customerInfo = getIntent().getParcelableExtra("customerInfo");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.create_package));
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
        packageDetailsName = (TextInputEditText) findViewById(R.id.package_details_name);
        packageDetailsMobile = (TextInputEditText) findViewById(R.id.package_details_mobile);
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
        packageDetailsInvoiceType.setVisibility(View.GONE);

        if(customerInfo!=null){
            packageDetailsName.setText(customerInfo.getName());
        }

        //TODO - Package's Invoice Date is of current date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        packageDetailsInvoiceDate.setText(formattedDate);

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

        invoiceTypeAdapter();
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
                    createPackage();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_message_create_package, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMessageRowClicked(int position) {

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
            stringDateOfInvoice = s;
            Date startDate = formatter.parse(stringDateOfInvoice);
            stringDateOfInvoice = targetFormat.format(startDate);
            packageDetailsInvoiceDate.setText(s);

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


    //Opening Dialog to Upload Documents
    private void openDialogUploadDoc(final Activity activity) {
        View dialogView = View.inflate(CreatePackageActivity.this, R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePackageActivity.this);
        builder.setView(dialogView)
                .setCancelable(true);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(true);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        btnAddImageView = (ImageView) dialogView.findViewById(R.id.add_imageView);
        btnUpload = (Button) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        documentUploaderAdapter = new DocumentUploaderAdapter(getApplicationContext(), uploadDocumentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if (uploadDocumentList.size() > 0) {
            uploadDocumentList.clear();
        }

        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpload.setEnabled(false);
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    /*if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                       //FilesUploadingAsyncTask filesUploadingAsyncTask = new FilesUploadingAsyncTask(activity, uploadDocumentList, pacId, dialogUploadDoc);
                     //  filesUploadingAsyncTask.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }*/
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
            strType = "1";
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
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            UploadDocument uploadDocument = new UploadDocument();
//        File propertyImageFile = new File(uploadDocument.getFilePath());
            //  RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), propertyImageFile);
            // MultipartBody.Part propertyImagePart = MultipartBody.Part.createFormData("files", propertyImageFile.getName(), propertyImage);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[0];
            if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[uploadDocumentList.size()];
                for (int index = 0; index < uploadDocumentList.size(); index++) {
                    Log.d("Package Details", "requestUploadSurvey: survey image " + index + "  " + uploadDocumentList.get(index).getFilePath());
                    File file = new File(uploadDocumentList.get(index).getFilePath());
                    RequestBody surveyBody = RequestBody.create(MediaType.parse("*/*"), file);
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
                call = apiService.createPackageMultipart(version, key, task, userId, accessToken,
                        strEmail, strMobile, strShippingAddress, strBillingAddress, jsonArrayPackageDetails, chkid, strOrder, strType, strLocal, strInvoiceDate
                        , strInvoiceNumber, strESugam, surveyImagesParts, strChkoid);
            } else {
                call = apiService.createPackageWithoutMultipart(version, key, task, userId, accessToken,
                        strEmail, strMobile, strShippingAddress, strBillingAddress, jsonArrayPackageDetails, chkid, strOrder, strType, strLocal, strInvoiceDate
                        , strInvoiceNumber, strESugam, strChkoid);
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
                        // Toast.makeText(CreatePackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        linearLayoutSuccess.setVisibility(View.VISIBLE);
                     //   Intent intent = new Intent();
                       // setResult(RESULT_OK, intent);
                     //
                     //   finish();
                   /*     AlreadyCreatedPackages packages = new AlreadyCreatedPackages();
                        packages.setInvoiceDate(strInvoiceDate);
                        packages.setInvoiceNo(strInvoiceNumber);
                        packages.setCustomerName(packageDetailsName.getText().toString());
                        packages.setOrderID(strOrder);
                        packages.setPacid(apiResponse.getData().getPacid());

                        packedList.add(packages);*/

                        //     ((RunningOrdersExecuteFragment) getParentFragment()).sendPackageDetails();

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

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (CreatePackageActivity.this != null) {
                        Toast.makeText(CreatePackageActivity.this, "Order Execution failed", Toast.LENGTH_LONG).show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            uploadDocumentList.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
        }
    }

    private String calculateQuantity(double allocatedQuantity, double selectedConversionRate, double previousConversionRate) {
        Log.d("allocated quantity", allocatedQuantity + "   " + selectedConversionRate + "  " + previousConversionRate);
        double allocatedQuantityValue = (allocatedQuantity / previousConversionRate) * selectedConversionRate;
        return String.valueOf(roundTwoDecimals(allocatedQuantityValue));
    }

    //Add Document into List
    public void addDocument(String selectedFilePath, String fileName) {
        UploadDocument uploadDocument = new UploadDocument();
        uploadDocument.setFileName(fileName);
        uploadDocument.setFilePath(selectedFilePath);
        uploadDocument.setFileType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        uploadDocumentList.add(uploadDocument);
        documentUploaderAdapter.notifyDataSetChanged();
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

}
