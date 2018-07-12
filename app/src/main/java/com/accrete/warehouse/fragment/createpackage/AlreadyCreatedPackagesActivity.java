package com.accrete.warehouse.fragment.createpackage;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.AlreadyCreatedPackagesAdapter;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersTabFragment;
import com.accrete.warehouse.model.AlreadyCreatedPackages;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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
 * Created by poonam on 12/12/17.
 */

public class AlreadyCreatedPackagesActivity extends AppCompatActivity implements AlreadyCreatedPackagesAdapter.AlreadyCreatedPackagesAdapterListener, SwipeRefreshLayout.OnRefreshListener {
    public String strPacid, strInvid, strCuid;
    private SwipeRefreshLayout alreadyCreatedPackagesSwipeRefreshLayout;
    private RecyclerView alreadyCreatedPackagesRecyclerView;
    private TextView alreadyCreatedPackagesEmptyView;
    private AlreadyCreatedPackagesAdapter packedAdapter;
    private List<AlreadyCreatedPackages> alreadyCreatedPackagesList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;
    private String status;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private String chkoid;
    private String flagToCallApi;
    private ImageView imageViewLoader;

    private void findViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_already_created_packages));
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

        alreadyCreatedPackagesSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.already_created_packages_swipe_refresh_layout);
        alreadyCreatedPackagesRecyclerView = (RecyclerView) findViewById(R.id.already_created_packages_recycler_view);
        alreadyCreatedPackagesEmptyView = (TextView) findViewById(R.id.already_created_packages_empty_view);
        imageViewLoader = (ImageView) findViewById(R.id.imageView_loader);


        if (getIntent().getStringExtra("chkoid") != null) {
            chkoid = getIntent().getStringExtra("chkoid");
            flagToCallApi = getIntent().getStringExtra("flag");
        }

        packedAdapter = new AlreadyCreatedPackagesAdapter(getApplicationContext(), alreadyCreatedPackagesList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        alreadyCreatedPackagesRecyclerView.setLayoutManager(mLayoutManager);
        alreadyCreatedPackagesRecyclerView.setHasFixedSize(true);
        alreadyCreatedPackagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alreadyCreatedPackagesRecyclerView.setNestedScrollingEnabled(false);
        alreadyCreatedPackagesRecyclerView.setAdapter(packedAdapter);
        alreadyCreatedPackagesSwipeRefreshLayout.setOnRefreshListener(this);
        alreadyCreatedPackagesSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                showLoader();
                getAlreadyCreatedPackages();
            }
        });
        ViewAfterUpdate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_created_packages);
        findViews();
    }


    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(final int position) {
        View dialogView = View.inflate(this, R.layout.dialog_select_actions_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectEvent = builder.create();
        dialogSelectEvent.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        LinearLayout actionsFilter;
        LinearLayout actionsViewPackage;
        LinearLayout actionsPrintPackage;
        LinearLayout actionsCancelGatepass;
        ImageView imageBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsViewPackage = (LinearLayout) dialogView.findViewById(R.id.actions_view_package);
        actionsPrintPackage = (LinearLayout) dialogView.findViewById(R.id.actions_print_package);
        actionsCancelGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_cancel_gatepass);
        TextView textViewCancelTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_textview_title);
        imageBack = (ImageView) dialogView.findViewById(R.id.image_back);
        textViewCancelTitle.setText("Cancel Package");

        if (flagToCallApi.equals("runningOrders")) {
            actionsPrintPackage.setVisibility(View.VISIBLE);
        } else {
            if (alreadyCreatedPackagesList.get(position).getPrintFlag()) {
                actionsPrintPackage.setVisibility(View.VISIBLE);

            } else {
                actionsPrintPackage.setVisibility(View.GONE);

            }
        }

        actionsViewPackage.setVisibility(View.GONE);
        actionsPrintPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                downloadPdfDialog(alreadyCreatedPackagesList.get(position).getInvid(), alreadyCreatedPackagesList.get(position).getCuid());
                strPacid = alreadyCreatedPackagesList.get(position).getPackageId();
            }
        });

        actionsCancelGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancel(position, dialogSelectEvent);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
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


    private void dialogCancel(final int position, final AlertDialog dialogSelectEvent) {
        View dialogView = View.inflate(this, R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogCancelGatepass = builder.create();
        dialogCancelGatepass.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        Button btnOk;
        final ProgressBar cancelGatepassProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPackage(position, dialogCancelGatepass, alreadyCreatedPackagesList.get(position).getPacid(), dialogSelectEvent);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass.dismiss();
            }
        });


        dialogCancelGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogCancelGatepass.isShowing()) {
            dialogCancelGatepass.show();
        }

    }

    private void cancelPackage(final int position, final AlertDialog dialogCancel, final String pacid, final AlertDialog dialogSelectEvent) {
        task = getString(R.string.task_cancel_package);
        String chkid = null;
        if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.cancelPackage(version, key, task, userId, accessToken, chkid, pacid);
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
                        if (pacid.equals(apiResponse.getData().getPacid())) {
                            alreadyCreatedPackagesList.remove(position);
                        }
                        dialogCancel.dismiss();
                        dialogSelectEvent.dismiss();
                        packedAdapter.notifyDataSetChanged();
                        Intent intent = new Intent("notifyOrderInfo");
                        // You can also include some extra data.
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


                        status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            showLoader();
                            getAlreadyCreatedPackages();
                        } else {
                            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
                            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);
                            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_internet_try_later));
                            alreadyCreatedPackagesSwipeRefreshLayout.setRefreshing(false);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogCancel.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                // manageGatepassSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });
    }

    public void downloadPdfDialog(final String invid, final String cuid) {
        final View dialogView = View.inflate(this, R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        strInvid = invid;
        strCuid = cuid;
        TextView textViewTitle = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        textViewTitle.setText(getString(R.string.invoice_download_title));
        downloadConfirmMessage.setText(getString(R.string.download_invoice_confirm_msg));

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
                if (!NetworkUtil.getConnectivityStatusString(AlreadyCreatedPackagesActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionWithRationale((Activity) AlreadyCreatedPackagesActivity.this, new RunningOrdersTabFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
                            if (ActivityCompat.checkSelfPermission(AlreadyCreatedPackagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                        }
                    } else {
                        downloadPdf(invid, cuid);
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

    private void downloadPdf(final String strInvid, String strCuid) {
        task = getString(R.string.download_invoice_task);
        if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadInvoicePDF(version, key, task, userId, accessToken, strCuid, strInvid);
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
                            downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(strPacid + "_package" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            strPacid + "_package" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
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


    public void sendPackageDetails() {
        //alreadyCreatedPackagesList = new ArrayList<>();
        //  alreadyCreatedPackagesList.addAll(packages);
        //  packedAdapter = new AlreadyCreatedPackagesAdapter(getApplicationContext(), alreadyCreatedPackagesList, this);
        //  alreadyCreatedPackagesRecyclerView.setAdapter(packedAdapter);
        getAlreadyCreatedPackages();
        //  ViewAfterUpdate();
    }

    public void getPackageList(List<AlreadyCreatedPackages> packagesList) {
        if (alreadyCreatedPackagesList.size() > 0) {
            alreadyCreatedPackagesList.clear();
        }
        alreadyCreatedPackagesList.addAll(packagesList);
        ViewAfterUpdate();
    }

    private void ViewAfterUpdate() {
        if (alreadyCreatedPackagesList.size() > 0) {
            alreadyCreatedPackagesRecyclerView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.GONE);
        } else {
            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_data_available));
        }
    }

    public void checkFragmentAndDownloadPDF() {
        downloadPdf(strInvid, strCuid);
    }


    private void getAlreadyCreatedPackages() {
        task = getString(R.string.task_already_created_packages);
        String chkid = null;
        if (alreadyCreatedPackagesList != null && alreadyCreatedPackagesList.size() > 0) {
            alreadyCreatedPackagesList.clear();
        }
        if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call;
        if (flagToCallApi.equals("runningOrder")) {
            call = apiService.getOrderPackages(version, key, task, userId, accessToken, chkid, chkoid);
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
                        alreadyCreatedPackagesRecyclerView.setVisibility(View.VISIBLE);
                        alreadyCreatedPackagesEmptyView.setVisibility(View.GONE);

                        for (AlreadyCreatedPackages alreadyCreatedPackages : apiResponse.getData().getPackages()) {
                            alreadyCreatedPackagesList.add(alreadyCreatedPackages);
                        }

                        packedAdapter.notifyDataSetChanged();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_data_available));
                            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
                            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);

                        } else if (apiResponse.getSuccessCode().equals("20004")) {
                            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_data_available));
                            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
                            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (alreadyCreatedPackagesSwipeRefreshLayout != null && alreadyCreatedPackagesSwipeRefreshLayout.isRefreshing()) {
                        alreadyCreatedPackagesSwipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alreadyCreatedPackagesSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
                hideLoader();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        showLoader();
        getAlreadyCreatedPackages();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG_REQUEST", " " + requestCode + " " + resultCode);
        if (resultCode == REQUEST_CODE_ASK_STORAGE_PERMISSIONS) {
            checkFragmentAndDownloadPDF();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                        checkFragmentAndDownloadPDF();
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
                        if (alreadyCreatedPackagesList != null && alreadyCreatedPackagesList.size() == 0) {
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
        });

        thread.start();
    }
}

