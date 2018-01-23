package com.accrete.warehouse.fragment.createpackage;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.ViewPackageGatePassActivity;
import com.accrete.warehouse.adapter.AlreadyCreatedPackagesAdapter;
import com.accrete.warehouse.adapter.PackedAdapter;
import com.accrete.warehouse.fragment.managegatepass.ManageGatePassFragment;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.Packed;
import com.accrete.warehouse.model.RunningOrder;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;
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

public class AlreadyCreatedPackagesFragment extends Fragment implements AlreadyCreatedPackagesAdapter.AlreadyCreatedPackagesAdapterListener {
    private SwipeRefreshLayout alreadyCreatedPackagesSwipeRefreshLayout;
    private RecyclerView alreadyCreatedPackagesRecyclerView;
    private TextView alreadyCreatedPackagesEmptyView;
    private AlreadyCreatedPackagesAdapter packedAdapter;
    private List<Packages> packedList = new ArrayList<>();
    private Packages packages = new Packages();
    private AlertDialog dialogSelectEvent;
    private String status;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private String strPacid;

    private void findViews(View rootView) {
        alreadyCreatedPackagesSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById( R.id.already_created_packages_swipe_refresh_layout );
        alreadyCreatedPackagesRecyclerView = (RecyclerView)rootView.findViewById( R.id.already_created_packages_recycler_view );
        alreadyCreatedPackagesEmptyView = (TextView)rootView.findViewById( R.id.already_created_packages_empty_view );

        packedAdapter = new AlreadyCreatedPackagesAdapter(getActivity(), packedList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        alreadyCreatedPackagesRecyclerView.setLayoutManager(mLayoutManager);
        alreadyCreatedPackagesRecyclerView.setHasFixedSize(true);
        alreadyCreatedPackagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alreadyCreatedPackagesRecyclerView.setNestedScrollingEnabled(false);
        alreadyCreatedPackagesRecyclerView.setAdapter(packedAdapter);
        alreadyCreatedPackagesSwipeRefreshLayout.setEnabled(false);
        ViewAfterUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_already_created_packages, container, false);
        Bundle bundle = this.getArguments();
    /*    if (bundle != null) {
            packedList = bundle.getParcelableArrayList("packagesList");
        }*/
        findViews(rootView);
        return rootView;
    }



    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(final int position) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_select_actions_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        imageBack = (ImageView) dialogView.findViewById(R.id.image_back);

        actionsViewPackage.setVisibility(View.GONE);
        actionsPrintPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPdfDialog(packedList.get(position).getInvid());
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
        View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                cancelPackage(dialogCancelGatepass, packedList.get(position).getPacid(), dialogSelectEvent);
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

    private void cancelPackage(final AlertDialog dialogCancelGatepass, String gatepassId, final AlertDialog dialogSelectEvent) {
        task = getString(R.string.task_cancel_package);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.cancelGatepass(version, key, task, userId, accessToken, gatepassId);
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
                        dialogCancelGatepass.dismiss();
                        dialogSelectEvent.dismiss();
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                           // getManageGatepassList();
                        } else {
                        /*    manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                            manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);*/
                        }

                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogCancelGatepass.dismiss();
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

    public void downloadPdfDialog(final String strInvid) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

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
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionWithRationale((Activity) getActivity(), new ManageGatePassFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                        }
                    } else {
                        downloadPdf(strInvid);
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    private void downloadPdf(final String strInvid) {
        task = getString(R.string.download_invoice_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.cancelPackage(version, key, task, userId, accessToken, strInvid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(strPacid + "_gatepass" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            strPacid + "_gatepass" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void sendPackageDetails(List<Packages> packages) {
        packedList = new ArrayList<>();
        packedList.addAll(packages);
        packedAdapter = new AlreadyCreatedPackagesAdapter(getActivity(), packedList, this);
        alreadyCreatedPackagesRecyclerView.setAdapter(packedAdapter);
        ViewAfterUpdate();
    }

    public void getPackageList(List<Packages> packagesList) {
        if(packedList.size()>0){
            packedList.clear();
        }
        packedList.addAll(packagesList);
        ViewAfterUpdate();
    }

    private void ViewAfterUpdate() {
        if(packedList.size()>0){
            alreadyCreatedPackagesRecyclerView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.GONE);
        }else{
            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_data_available));
        }
    }
}

