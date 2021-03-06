package com.accrete.warehouse.fragment.manageConsignment;

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
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
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
 * Created by poonam on 12/5/17.
 */

public class ViewConsignmentActivity extends AppCompatActivity implements View.OnClickListener {
    private String iscId;
    private FloatingActionButton floatingActionButton;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    // private LinearLayout layoutActivityViewConsignmentGoodsReceiptFab;
    private FloatingActionButton manageConsignmentGoodsReceiptPrint;
    /* private LinearLayout layoutActivityViewConsignmentEditFab;
     private FloatingActionButton activityViewConsignmentEditFab;
     private LinearLayout layoutActivityViewConsignmentAllocationFab;
     private FloatingActionButton activityViewConsignmentAllocationFab;
     private LinearLayout activityViewConsignmentConsumptionFabLayout;
     private FloatingActionButton activityViewConsignmentConsumptionFab;
     private LinearLayout layoutActivityViewConsignmentFab;
     private FloatingActionButton activityViewConsignmentFab;*/
    private Boolean isFabOpen = false;
    //   private Animation fab_open, fab_close, rotate_forward, rotate_backward;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consignment);
        if (getIntent() != null && getIntent().hasExtra("iscid")) {
            iscId = getIntent().getStringExtra("iscid");
        }

        findViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // layoutActivityViewConsignmentGoodsReceiptFab = (LinearLayout) findViewById(R.id.layout_activity_view_consignment_goods_receipt_fab);
        manageConsignmentGoodsReceiptPrint = (FloatingActionButton) findViewById(R.id.manage_consignment_goods_receipt_print);
       /* layoutActivityViewConsignmentEditFab = (LinearLayout) findViewById(R.id.layout_activity_view_consignment_edit_fab);
        activityViewConsignmentEditFab = (FloatingActionButton) findViewById(R.id.activity_view_consignment_edit_fab);
        layoutActivityViewConsignmentAllocationFab = (LinearLayout) findViewById(R.id.layout_activity_view_consignment_allocation_fab);
        activityViewConsignmentAllocationFab = (FloatingActionButton) findViewById(R.id.activity_view_consignment_allocation_fab);
        activityViewConsignmentConsumptionFabLayout = (LinearLayout) findViewById(R.id.activity_view_consignment_consumption_fab_layout);
        activityViewConsignmentConsumptionFab = (FloatingActionButton) findViewById(R.id.activity_view_consignment_consumption_fab);
        layoutActivityViewConsignmentFab = (LinearLayout) findViewById(R.id.layout_activity_view_consignment_fab);
        activityViewConsignmentFab = (FloatingActionButton) findViewById(R.id.activity_view_consignment_fab);*/

       /* fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);*/

        manageConsignmentGoodsReceiptPrint.setOnClickListener(this);
       /* activityViewConsignmentEditFab.setOnClickListener(this);
        activityViewConsignmentAllocationFab.setOnClickListener(this);
        activityViewConsignmentConsumptionFab.setOnClickListener(this);
        activityViewConsignmentFab.setOnClickListener(this);*/


        toolbar.setTitle(getString(R.string.view_consignment));
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        ConsignmentMainTabFragment consignmentMainTabFragment = new ConsignmentMainTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.iscid), iscId);
        consignmentMainTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, consignmentMainTabFragment);
        fragmentTransaction.commit();

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
                        downloadReceiptDialog(iscId);
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

    public void downloadReceiptDialog(final String fileName) {
        final View dialogView = View.inflate(this, R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

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
                if (!NetworkUtil.getConnectivityStatusString(ViewConsignmentActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, fileName);
                } else {
                    Toast.makeText(ViewConsignmentActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    private void downloadPdf(final AlertDialog alertDialog, final String fileName) {
        task = getString(R.string.download_consignment_receipt);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadConsignmentPDF(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID), iscId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(ViewConsignmentActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_goods_receipt" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_goods_receipt" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(ViewConsignmentActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(ViewConsignmentActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == manageConsignmentGoodsReceiptPrint) {
            // Handle clicks for manageConsignmentGoodsReceiptPrint

            if (android.os.Build.VERSION.SDK_INT >= 23) {
                askStoragePermission();
            } else {
                downloadReceiptDialog(iscId);
            }

            //  isFabOpen = true;
            //  animateFAB();
        }/* else if ( v == activityViewConsignmentEditFab ) {
            // Handle clicks for activityViewConsignmentEditFab
            isFabOpen = true;
            animateFAB();
        } else if ( v == activityViewConsignmentAllocationFab ) {
            // Handle clicks for activityViewConsignmentAllocationFab
            Intent intentAllocation = new Intent(ViewConsignmentActivity.this,AllocationActivity.class);
            intentAllocation.putExtra("iscId",iscId);
            startActivity(intentAllocation);

            isFabOpen = true;
            animateFAB();
        } else if ( v == activityViewConsignmentConsumptionFab ) {
            // Handle clicks for activityViewConsignmentConsumptionFab
            isFabOpen = true;
            animateFAB();
        } else if ( v == activityViewConsignmentFab ) {
            // Handle clicks for activityViewConsignmentFab
            animateFAB();
        }*/
    }

  /*  public void animateFAB() {
        if (isFabOpen) {
            activityViewConsignmentFab.startAnimation(rotate_backward);
            activityViewConsignmentEditFab.startAnimation(fab_close);
            activityViewConsignmentAllocationFab.startAnimation(fab_close);
            activityViewConsignmentConsumptionFab.startAnimation(fab_close);
            manageConsignmentGoodsReceiptPrint.startAnimation(fab_close);

            layoutActivityViewConsignmentEditFab.startAnimation(fab_close);
            layoutActivityViewConsignmentAllocationFab.startAnimation(fab_close);
            activityViewConsignmentConsumptionFabLayout.startAnimation(fab_close);
            layoutActivityViewConsignmentGoodsReceiptFab.startAnimation(fab_close);

            activityViewConsignmentEditFab.setClickable(false);
            activityViewConsignmentAllocationFab.setClickable(false);
            activityViewConsignmentConsumptionFab.setClickable(false);
            manageConsignmentGoodsReceiptPrint.setClickable(false);

            layoutActivityViewConsignmentEditFab.setVisibility(View.GONE);
            layoutActivityViewConsignmentAllocationFab.setVisibility(View.GONE);
            activityViewConsignmentConsumptionFabLayout.setVisibility(View.GONE);
            layoutActivityViewConsignmentGoodsReceiptFab.setVisibility(View.GONE);

            isFabOpen = false;
            Log.d("Lead", "close");
        } else {
            activityViewConsignmentFab.startAnimation(rotate_forward);
            activityViewConsignmentEditFab.startAnimation(fab_open);
            activityViewConsignmentAllocationFab.startAnimation(fab_open);
            activityViewConsignmentConsumptionFab.startAnimation(fab_open);
            manageConsignmentGoodsReceiptPrint.startAnimation(fab_open);

            layoutActivityViewConsignmentEditFab.startAnimation(fab_open);
            layoutActivityViewConsignmentAllocationFab.startAnimation(fab_open);
            activityViewConsignmentConsumptionFabLayout.startAnimation(fab_open);
            layoutActivityViewConsignmentGoodsReceiptFab.startAnimation(fab_open);

            layoutActivityViewConsignmentEditFab.setVisibility(View.VISIBLE);
            layoutActivityViewConsignmentAllocationFab.setVisibility(View.VISIBLE);
            activityViewConsignmentConsumptionFabLayout.setVisibility(View.VISIBLE);
            layoutActivityViewConsignmentGoodsReceiptFab.setVisibility(View.VISIBLE);

            activityViewConsignmentEditFab.setClickable(true);
            activityViewConsignmentAllocationFab.setClickable(true);
            activityViewConsignmentConsumptionFab.setClickable(true);
            manageConsignmentGoodsReceiptPrint.setClickable(true);

            isFabOpen = true;
            Log.d("Lead", "open");
        }
    }*/

}
