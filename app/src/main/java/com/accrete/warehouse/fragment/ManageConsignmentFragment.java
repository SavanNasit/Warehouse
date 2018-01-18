package com.accrete.warehouse.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.ViewConsignmentActivity;
import com.accrete.warehouse.adapter.ManageConsignmentAdapter;
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.ManageConsignment;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentFragment extends Fragment implements ManageConsignmentAdapter.ManageConsignmentAdapterListener {

    private static final String KEY_TITLE = "ManageConsignment";

    private SwipeRefreshLayout manageConsignmentSwipeRefreshLayout;
    private RecyclerView manageConsignmentRecyclerView;
    private TextView manageConsignmentEmptyView;
    private ManageConsignmentAdapter manageConsignmentAdapter;
    private List<ManageConsignment> manageConsignmentList = new ArrayList<>();
    private ManageConsignment manageConsignment = new ManageConsignment();
    private FloatingActionButton floatingActionButtonPrint;

    public static ManageConsignmentFragment newInstance(String title) {
        ManageConsignmentFragment f = new ManageConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        manageConsignmentSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_consignment_swipe_refresh_layout);
        manageConsignmentRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_consignment_recycler_view);
        manageConsignmentEmptyView = (TextView) rootView.findViewById(R.id.manage_consignment_empty_view);
        floatingActionButtonPrint = (FloatingActionButton)rootView.findViewById(R.id.manage_consignment_goods_receipt_print);

        manageConsignmentAdapter = new ManageConsignmentAdapter(getActivity(), manageConsignmentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        manageConsignmentRecyclerView.setLayoutManager(mLayoutManager);
        manageConsignmentRecyclerView.setHasFixedSize(true);
        manageConsignmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageConsignmentRecyclerView.setNestedScrollingEnabled(false);
        manageConsignmentRecyclerView.setAdapter(manageConsignmentAdapter);

        manageConsignment.setConsignmentID("RPDCNS000295");
        manageConsignment.setPurchaseOrder("NA");
        manageConsignment.setInvoiceNumber("Ms Poonam Kukreti");
        manageConsignment.setInvoiceDate("18 Sep, 2017");
        manageConsignment.setPurchaseOrderDate("04 Dec, 2017");
        manageConsignment.setVendor("rahul goyal");
        manageConsignment.setWarehouse("BTM WAREHOUSE");
        manageConsignment.setReceivedOn("04 Dec, 2017");
        manageConsignment.setStatus("Freezed");

        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);

        floatingActionButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
            }
        });


    }


   /* private void downloadPdf(final AlertDialog alertDialog, final String cuId, final String avid, final String date,
                            final String fileName) {
        task = mContext.getString(R.string.customer_wallet_download_voucher);
        if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadCustomerWalletVoucher(version, key, task, userId, accessToken, cuId, avid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_purchase" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_purchase" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(context, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }
*/




   /* public void downloadVoucherDialog(final String cuId, final String avid, final String date, final String fileName) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
      final AlertDialog  alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
              //  btnYes.setEnabled(false);
              //  progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, cuId, avid, date, fileName);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_consignment_fragment));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intentView = new Intent(getActivity(),ViewConsignmentActivity.class);
        startActivity(intentView);
    }

    @Override
    public void onExecute() {

    }
}
