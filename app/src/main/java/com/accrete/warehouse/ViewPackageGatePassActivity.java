package com.accrete.warehouse;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.adapter.ViewPackageGatePassAdapter;
import com.accrete.warehouse.fragment.manageConsignment.ConsignmentMainTabFragment;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ItemsInsidePackage;
import com.accrete.warehouse.model.ViewGatepassPackages;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_ASK_STORAGE_PERMISSIONS;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 1/21/18.
 */

public class ViewPackageGatePassActivity extends AppCompatActivity implements ViewPackageGatePassAdapter.PackedAdapterListener {
    private Toolbar toolbar;
    private RecyclerView packageGatepassRecyclerView;
    private TextView packageGatepassEmptyView;
    private ViewPackageGatePassAdapter viewPackageGatePassAdapter;
    private List<ViewGatepassPackages> packageList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;

    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private String strCuid, strInvid;
    private List<ItemsInsidePackage> itemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_gatepass);
        findViews();
    }


    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        packageGatepassRecyclerView = (RecyclerView) findViewById(R.id.package_gatepass_recycler_view);
        packageGatepassEmptyView = (TextView) findViewById(R.id.package_gatepass_empty_view);

        toolbar.setTitle(getString(R.string.view_packages));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });

        viewPackageGatePassAdapter = new ViewPackageGatePassAdapter(this, packageList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        packageGatepassRecyclerView.setLayoutManager(mLayoutManager);
        packageGatepassRecyclerView.setHasFixedSize(true);
        packageGatepassRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageGatepassRecyclerView.setNestedScrollingEnabled(false);
        packageGatepassRecyclerView.setAdapter(viewPackageGatePassAdapter);

        String pacdelgatid = getIntent().getStringExtra("id");
        getPackageGatepassList(pacdelgatid);
    }

    private void getPackageGatepassList(String pacdelgatid) {
        task = getString(R.string.view_gatepass_list_task);
        String chkid = null;
        if (packageList != null && packageList.size() > 0) {
            packageList.clear();
        }

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.viewGatepassPackages(version, key, task, userId, accessToken, chkid, pacdelgatid);
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
                        packageGatepassRecyclerView.setVisibility(View.VISIBLE);
                        packageGatepassEmptyView.setVisibility(View.GONE);

                        for (ViewGatepassPackages viewGatepassPackage : apiResponse.getData().getGatePassData()) {
                            packageList.add(viewGatepassPackage);

                        }
                        for (int i = 0; i <packageList.size() ; i++) {
                            itemList.addAll(packageList.get(i).getItemList());
                        }

                        viewPackageGatePassAdapter.notifyDataSetChanged();

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            packageGatepassEmptyView.setText(getString(R.string.no_data_available));
                            packageGatepassRecyclerView.setVisibility(View.GONE);
                            packageGatepassEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(ViewPackageGatePassActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("warehouse:viewGatePassPackages", t.getMessage());
            }
        });
    }


    @Override
    public void onRowClicked(int position, String cuid, String invid, String chkoid, String pacid) {
        dialogItemEvents(cuid, invid, chkoid,pacid);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(final String cuid, final String invid, final String chkoid,final String pacid) {
        View dialogView = View.inflate(this, R.layout.dialog_select_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectEvent = builder.create();
        dialogSelectEvent.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        LinearLayout actionsItemsInsidePackage;
        LinearLayout actionsPackageStatus;
        LinearLayout actionsPackageHistory;
        LinearLayout actionsCustomerDetails;
        LinearLayout actionsPrintInvoice;
        LinearLayout actionsPrintDeliveryChallan;
        LinearLayout actionsOtherDocuments;
        LinearLayout actionsPrintGatepass;
        LinearLayout actionsPrintLoadingSlip;
        Button btnOk;
        ProgressBar dialogSelectActionsProgressBar;
        Button btnCancel;
        ImageView imageViewBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsItemsInsidePackage = (LinearLayout) dialogView.findViewById(R.id.actions_items_inside_package);
        actionsPackageStatus = (LinearLayout) dialogView.findViewById(R.id.actions_package_status);
        actionsPackageHistory = (LinearLayout) dialogView.findViewById(R.id.actions_package_history);
        actionsCustomerDetails = (LinearLayout) dialogView.findViewById(R.id.actions_customer_details);
        actionsPrintInvoice = (LinearLayout) dialogView.findViewById(R.id.actions_print_invoice);
        actionsPrintDeliveryChallan = (LinearLayout) dialogView.findViewById(R.id.actions_print_delivery_challan);
        actionsOtherDocuments = (LinearLayout) dialogView.findViewById(R.id.actions_other_documents);
        actionsPrintGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_print_gatepass);
        actionsPrintLoadingSlip = (LinearLayout) dialogView.findViewById(R.id.actions_print_loading_slip);
        // btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        dialogSelectActionsProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_select_warehouse_progress_bar);
        //btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);


        actionsPackageStatus.setVisibility(View.GONE);
        actionsPrintDeliveryChallan.setVisibility(View.GONE);
        actionsOtherDocuments.setVisibility(View.GONE);
        actionsPrintGatepass.setVisibility(View.GONE);
        actionsPrintLoadingSlip.setVisibility(View.GONE);
        actionsPackageStatus.setVisibility(View.GONE);
        actionsPackageHistory.setBackgroundColor(Color.WHITE);
        actionsCustomerDetails.setBackgroundColor(getResources().getColor(R.color.home_page_gray));
        actionsPrintInvoice.setBackgroundColor(Color.WHITE);

        actionsItemsInsidePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.size()>0) {
                    Intent intentItems = new Intent(ViewPackageGatePassActivity.this, ItemsInsidePackageActivity.class);
                    intentItems.putParcelableArrayListExtra("itemsList", (ArrayList<? extends Parcelable>) itemList);
                    startActivity(intentItems);
                }else{
                    Toast.makeText(ViewPackageGatePassActivity.this, "No items available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        actionsCustomerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                    customerDetailApi(pacid);

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

        actionsPackageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackageHistory = new Intent(ViewPackageGatePassActivity.this, PackageHistoryActivity.class);
                intentPackageHistory.putExtra("packageid", pacid);
                startActivity(intentPackageHistory);
            }
        });

        actionsPrintInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                downloadPdfDialog(cuid, invid);
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });

        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }

    private void customerDetailApi(String pacid) {
        task = getString(R.string.customer_info_gatepass_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.customerInfoInGatepassPackage(version, key, task, userId, accessToken, pacid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Intent intentCustomerDetails = new Intent(ViewPackageGatePassActivity.this, CustomerDetailsActivity.class);
                        intentCustomerDetails.putExtra("customerInfo", apiResponse.getData().getCustomerInfo());
                        startActivity(intentCustomerDetails);
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

    public void downloadPdfDialog(final String cuid, final String invid) {
        final View dialogView = View.inflate(this, R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        strCuid = cuid;
        strInvid = invid;
        TextView textViewTitle = (TextView) dialogView.findViewById(R.id.title_textView);
        TextView downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        final TextView btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        textViewTitle.setText(getString(R.string.invoice_download_title));
        downloadConfirmMessage.setText(getString(R.string.download_gatepass_invoice_confirm_msg));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
                btnYes.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(ViewPackageGatePassActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        askStoragePermission();
                    } else {
                        downloadPdf(cuid, invid);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void downloadPdf(final String cuid, final String invid) {
        task = getString(R.string.download_gatepass_invoice_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadPackageGatepassInvoice(version, key, task, userId, accessToken, cuid, invid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(invid + "_invoice" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            invid + "_invoice" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

    public void askStoragePermission() {
        if (checkPermissionWithRationale(this, new ConsignmentMainTabFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_STORAGE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions

                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        downloadPdf(strCuid, strInvid);
                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
