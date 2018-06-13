package com.accrete.warehouse.fragment.managegatepass;

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
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.GatepassList;
import com.accrete.warehouse.model.ManageGatepass;
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
 * Created by poonam on 12/5/17.
 */

public class ManageGatePassFragment extends Fragment implements ManageGatepassAdapter.ManageGatepassAdapterrListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_TITLE = "ManageGatePass";
    String strPacdelgatid;
    private SwipeRefreshLayout manageGatepassSwipeRefreshLayout;
    private RecyclerView manageGatepassRecyclerView;
    private TextView manageGatepassEmptyView;
    private ManageGatepassAdapter manageGatePassAdapter;
    private List<GatepassList> gatepassList = new ArrayList<>();
    private ManageGatepass manageGatepass = new ManageGatepass();
    private AlertDialog dialogSelectEvent;
    private String status;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private String dataChanged;

    public static ManageGatePassFragment newInstance(String title) {
        ManageGatePassFragment f = new ManageGatePassFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_gatepass, container, false);
        findViews(rootView);
        getActivity().setTitle(R.string.manage_gatepass_fragment);
        return rootView;
    }

    private void findViews(View rootView) {
        manageGatepassSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_gatepass_swipe_refresh_layout);
        manageGatepassRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_gatepass_recycler_view);
        manageGatepassEmptyView = (TextView) rootView.findViewById(R.id.manage_gatepass__empty_view);

        manageGatePassAdapter = new ManageGatepassAdapter(getActivity(), gatepassList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        manageGatepassRecyclerView.setLayoutManager(mLayoutManager);
        manageGatepassRecyclerView.setHasFixedSize(true);
        manageGatepassRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageGatepassRecyclerView.setNestedScrollingEnabled(false);
        manageGatepassRecyclerView.setAdapter(manageGatePassAdapter);


        manageGatepassRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && gatepassList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getManageGatepassList(gatepassList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (manageGatepassSwipeRefreshLayout.isRefreshing()) {
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        manageGatepassSwipeRefreshLayout.setOnRefreshListener(this);
        doRefresh();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_gatepass_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_gatepass_fragment));
        }
    }


    @Override
    public void onMessageRowClicked(int position, String status) {
        dialogItemEvents(position, status);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(final int position, String status) {
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

        if (status.equals("Cancelled")) {
            actionsCancelGatepass.setVisibility(View.GONE);
        }

        actionsViewPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewPackage = new Intent(getActivity(), ViewPackageGatePassActivity.class);
                intentViewPackage.putExtra("id", gatepassList.get(position).getPacdelgatid());
                startActivity(intentViewPackage);
            }
        });

        actionsPrintPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPdfDialog(gatepassList.get(position).getPacdelgatid());
            }
        });

        actionsCancelGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass(position, dialogSelectEvent);
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

    private void dialogCancelGatepass(final int position, final AlertDialog dialogSelectEvent) {
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
                cancelGatepass(position,dialogCancelGatepass, gatepassList.get(position).getPacdelgatid(), dialogSelectEvent);
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

    private void getManageGatepassList(final String createdTs, final String traversal) {
        task = getString(R.string.task_manage_gatepass);
        String chkid = null;
      /*  if (gatepassList != null && gatepassList.size() > 0) {
            gatepassList.clear();
        }*/

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getGatepassList(version, key, task, userId, accessToken, chkid, createdTs, traversal);
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
                        manageGatepassRecyclerView.setVisibility(View.VISIBLE);
                        manageGatepassEmptyView.setVisibility(View.GONE);

                        for (GatepassList gatepassLists : apiResponse.getData().getGatepassList()) {
                            if (gatepassLists != null) {
                                if (traversal.equals("2")) {
                                    if (!createdTs.equals(gatepassLists.getCreatedTs())) {
                                        gatepassList.add(gatepassLists);
                                    }
                                    dataChanged = "yes";
                                } else if (traversal.equals("1")) {
                                    if (manageGatepassSwipeRefreshLayout != null &&
                                            manageGatepassSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!createdTs.equals(gatepassLists.getCreatedTs())) {
                                            gatepassList.add(0, gatepassLists);
                                        }
                                    } else {
                                        if (!createdTs.equals(gatepassLists.getCreatedTs())) {
                                            gatepassList.add(gatepassLists);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }

                        }
                        loading = false;
                        if (gatepassList != null && gatepassList.size() == 0) {

                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            manageGatepassEmptyView.setVisibility(View.GONE);
                            manageGatepassRecyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (manageGatepassSwipeRefreshLayout != null &&
                                manageGatepassSwipeRefreshLayout.isRefreshing()) {
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversal.equals("2")) {
                            manageGatePassAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversal.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                manageGatePassAdapter.notifyDataSetChanged();
                                manageGatepassRecyclerView.smoothScrollToPosition(0);
                            }
                        }

                    } else {
                       /* if (apiResponse.getSuccessCode().equals("10001")) {
                            manageGatepassEmptyView.setText(getString(R.string.no_data_available));
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);

                        } else if (apiResponse.getSuccessCode().equals("20004")) {
                            manageGatepassEmptyView.setText(getString(R.string.no_data_available));
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/


                        loading = false;
                        if (gatepassList != null && gatepassList.size() == 0) {
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            //       customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            manageGatepassEmptyView.setVisibility(View.GONE);
                            manageGatepassRecyclerView.setVisibility(View.VISIBLE);
                            //       customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (manageGatepassSwipeRefreshLayout != null && manageGatepassSwipeRefreshLayout.isRefreshing()) {
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversal.equals("2")) {
                            manageGatePassAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversal.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                manageGatePassAdapter.notifyDataSetChanged();
                                manageGatepassRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (manageGatepassSwipeRefreshLayout != null && manageGatepassSwipeRefreshLayout.isRefreshing()) {
                        manageGatepassSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                manageGatepassSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });


    }


    private void cancelGatepass(final int position, final AlertDialog dialogCancelGatepass, String gatepassId, final AlertDialog dialogSelectEvent) {
        task = getString(R.string.task_cancel_gatepass);
        if (gatepassList != null && gatepassList.size() > 0) {
            gatepassList.clear();
        }
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
                            gatepassList.remove(position);
                            manageGatePassAdapter.notifyDataSetChanged();
                           // getManageGatepassList(gatepassList.get(0).getCreatedTs(), "1");
                        } else {
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                            manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);
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
                manageGatepassSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });
    }

    public void downloadPdfDialog(final String pacdelgatid) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        strPacdelgatid = pacdelgatid;

        TextView textViewTitle = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        textViewTitle.setText(getString(R.string.gatepass_download_title));
        downloadConfirmMessage.setText(getString(R.string.download_gatepass_confirm_msg));

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
                        downloadPdf(pacdelgatid);
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

    private void downloadPdf(final String pacdelgatid) {
        task = getString(R.string.download_gatepass);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.cancelGatepass(version, key, task, userId, accessToken, pacdelgatid);
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
                                    .setTitle(pacdelgatid + "_gatepass" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            pacdelgatid + "_gatepass" + ".pdf")
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


    public void doRefresh() {
        if (gatepassList != null && gatepassList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if(!status.equals(getString(R.string.not_connected_to_internet))){
              /*  if (getActivity()!=null && !loading && isAdded()
                    && isVisible()) {*/

                    loading = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            manageGatepassEmptyView.setText(getString(R.string.no_data_available));
                            getManageGatepassList(getString(R.string.last_updated_date), "1");
                        }
                    }, 200);
               // }
            } else {
                manageGatepassRecyclerView.setVisibility(View.GONE);
                manageGatepassEmptyView.setVisibility(View.VISIBLE);
                manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
                manageGatepassSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }


    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (gatepassList != null && gatepassList.size() > 0) {
                getManageGatepassList(gatepassList.get(0).getCreatedTs(), "1");
            } else {
                getManageGatepassList(getString(R.string.last_updated_date), "1");
            }
            manageGatepassRecyclerView.setVisibility(View.VISIBLE);
            manageGatepassEmptyView.setVisibility(View.GONE);
            manageGatepassSwipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            manageGatepassRecyclerView.setVisibility(View.GONE);
            manageGatepassEmptyView.setVisibility(View.VISIBLE);
            manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
            manageGatepassSwipeRefreshLayout.setRefreshing(false);
        }

    }

    public void downloadPdfCall() {
        downloadPdf(strPacdelgatid);
    }
}
