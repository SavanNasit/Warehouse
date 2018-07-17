package com.accrete.sixorbit.fragment.Drawer.collections;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.collections.CollectPaymentCustomerActivity;
import com.accrete.sixorbit.activity.collections.CollectionOrderTransactionActivity;
import com.accrete.sixorbit.activity.collections.CollectionsViewInvoiceActivity;
import com.accrete.sixorbit.adapter.LongClickActionsAdapter;
import com.accrete.sixorbit.adapter.MyCollectionsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CollectionData;
import com.accrete.sixorbit.model.Outlet;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 29/5/18.
 */

public class OpenInvoiceCollectionFragment extends Fragment implements
        MyCollectionsAdapter.MyCollectionsListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        LongClickActionsAdapter.LongClickActionsAdapterListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private String dataChanged, invoiceStatus, outletchkid = "0", outletName, internetStatus;
    private TextView textViewEmpty;
    private boolean loading;
    private MyCollectionsAdapter adapter;
    private LinearLayout topLayout;
    private TextView outletFilterTextView, statusFilterTextView;
    private LinearLayoutManager mLayoutManager, mActionsLayoutManager;
    private List<CollectionData> collectionInvoiceList = new ArrayList<CollectionData>();
    private FloatingActionButton filterFab;
    private View dialogView;
    private AlertDialog alertDialog;
    private String[] statusArray = {"All", "Collection Pending", "Partially Paid",
            "Collected"};
    private ArrayAdapter arrayAdapterStatuses;
    private ArrayList<Outlet> outletArrayList = new ArrayList<>();
    private boolean isOutletEnable;
    private LongClickActionsAdapter actionsAdapter;
    private AlertDialog dialogActions;
    private String searchText;
    private LinearLayout bottomLayout;
    private TextView balanceTextView;
    private DecimalFormat amountFormatter = new DecimalFormat("#,##,##,##,##,##,##,##,##,###");

    public OpenInvoiceCollectionFragment() {
        // Required empty public constructor
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (imageView.getVisibility() == View.GONE) {
                                    if (swipeRefreshLayout != null &&
                                            !swipeRefreshLayout.isRefreshing()) {
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections_invoice, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        topLayout = (LinearLayout) rootView.findViewById(R.id.top_layout);
        statusFilterTextView = (TextView) rootView.findViewById(R.id.status_filter_textView);
        outletFilterTextView = (TextView) rootView.findViewById(R.id.outlet_filter_textView);
        filterFab = (FloatingActionButton) rootView.findViewById(R.id.main_fab);
        bottomLayout = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
        balanceTextView = (TextView) rootView.findViewById(R.id.balance_textView);

        imageView.setVisibility(View.GONE);
        adapter = new MyCollectionsAdapter(getActivity(), collectionInvoiceList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) &&
                        collectionInvoiceList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getCollectionsInvoices(collectionInvoiceList.get(totalItemCount - 1).getCreatedTs(), "2",
                                invoiceStatus, outletchkid);
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Listeners
        swipeRefreshLayout.setOnRefreshListener(this);
        filterFab.setOnClickListener(this);
        doRefresh("");


        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            getOutlets();
        }

    }

    private void getCollectionsInvoices(String time, String traversalValue, String status,
                                        String chkId) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.my_collections_open_invoices_task);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getCollectionsInvoicesList(version, key, task, userId,
                        accessToken, time, traversalValue, status, chkId, searchText);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));


                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (CollectionData collectionData : apiResponse.getData().getCollectionData()) {
                                    if (collectionData != null) {
                                        if (traversalValue.equals("2")) {
                                            if (!time.equals(collectionData.getCreatedTs())) {
                                                collectionInvoiceList.add(collectionData);
                                            }
                                            dataChanged = "yes";
                                        } else if (traversalValue.equals("1")) {
                                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                                // To remove existing item
                                                if (!time.equals(collectionData.getCreatedTs())) {
                                                    collectionInvoiceList.add(0, collectionData);
                                                }
                                            } else {
                                                collectionInvoiceList.add(collectionData);
                                            }
                                            dataChanged = "yes";
                                        }
                                    }
                                }
                                loading = false;
                            }
                            //Deleted User
                            else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            } else {
                                //  Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (collectionInvoiceList != null && collectionInvoiceList.size() == 0) {
                                textViewEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                textViewEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            if (traversalValue.equals("2")) {
                                adapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    adapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }
                            //TODO Total amount value in bottom added on 14th June 2k18
                            if (apiResponse != null && apiResponse.getData() != null &&
                                    apiResponse.getData().getTotalPendingAmount() != null &&
                                    !apiResponse.getData().getTotalPendingAmount().isEmpty()) {
                                balanceTextView.setText("Total Pending Amount: " +
                                        amountFormatter.format(roundTwoDecimals(ParseDouble(apiResponse.getData().getTotalPendingAmount()))
                                        ));
                                bottomLayout.setVisibility(View.VISIBLE);
                            }

                            if (collectionInvoiceList == null || collectionInvoiceList.size() == 0) {
                                bottomLayout.setVisibility(View.GONE);
                            }

                            hideLoader();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            hideLoader();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (getActivity() != null && isAdded()) {
                            Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            hideLoader();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onLongClicked(int position) {
        if (dialogActions == null || !dialogActions.isShowing()) {
            dialogActionsItems(position);
        }
    }

    private void dialogActionsItems(final int itemsPosition) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_orders_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogActions = builder.create();
        dialogActions.setCanceledOnTouchOutside(false);
        ImageView imageViewBack;
        RecyclerView recyclerView;
        String[] actionsArr = null;

        if (invoiceStatus != null && invoiceStatus.equals("3")) {
            actionsArr = new String[]{"View Invoice", "Invoice Transaction"};
        } else if (invoiceStatus == null || invoiceStatus.isEmpty()) {
            if (collectionInvoiceList.get(itemsPosition).getaStatus().equals("1")) {
                actionsArr = new String[]{"View Invoice", "Invoice Transaction", "Collect Payment"};
            } else if (collectionInvoiceList.get(itemsPosition).getaStatus().equals("2")) {
                actionsArr = new String[]{"View Invoice", "Invoice Transaction", "Collect Payment"};
            } else if (collectionInvoiceList.get(itemsPosition).getaStatus().equals("3")) {
                actionsArr = new String[]{"View Invoice", "Invoice Transaction"};
            }
        } else {
            actionsArr = new String[]{"View Invoice", "Invoice Transaction", "Collect Payment"};
        }

        recyclerView = (RecyclerView) dialogView.findViewById(R.id.recyclerView);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);

        //Adapter
        actionsAdapter = new LongClickActionsAdapter(getActivity(), actionsArr, this, itemsPosition);
        mActionsLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mActionsLayoutManager);
        recyclerView.setAdapter(actionsAdapter);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogActions.dismiss();
            }
        });

        dialogActions.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogActions.isShowing()) {
            dialogActions.show();
        }
    }

    @Override
    public void onRefresh() {
        loading = true;

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (collectionInvoiceList != null && collectionInvoiceList.size() > 0) {
                showLoader();
                getCollectionsInvoices(collectionInvoiceList.get(0).getCreatedTs(), "1",
                        invoiceStatus, outletchkid);
            } else {
                showLoader();
                getCollectionsInvoices(getString(R.string.last_updated_date), "1",
                        invoiceStatus, outletchkid);
            }
        } else {
            if (getActivity() != null && isAdded()) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void doRefresh(String searchQuery) {
        if (collectionInvoiceList != null) {
            loading = true;
            if (getActivity() != null && isAdded()) {
                searchText = searchQuery;
                //calling API
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    if (collectionInvoiceList != null && collectionInvoiceList.size() > 0) {
                        collectionInvoiceList.clear();
                        getCollectionsInvoices(getString(R.string.last_updated_date),
                                "1", invoiceStatus, outletchkid);
                    } else {
                        getCollectionsInvoices(getString(R.string.last_updated_date),
                                "1", invoiceStatus, outletchkid);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == filterFab) {
            //Open Dialog to filter
            if (alertDialog == null || !alertDialog.isShowing()) {
                filterDialog();
            }
        }
    }

    public void filterDialog() {
        try {
            dialogView = View.inflate(getActivity(), R.layout.dialog_collections_invoice_filter, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialogView)
                    .setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCancelable(true);

            LinearLayout linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
            RelativeLayout outletLayout = (RelativeLayout) dialogView.findViewById(R.id.outlet_layout);
            Spinner outletSpinner = (Spinner) dialogView.findViewById(R.id.outlet_spinner);
            RelativeLayout statusLayout = (RelativeLayout) dialogView.findViewById(R.id.status_layout);
            Spinner statusSpinner = (Spinner) dialogView.findViewById(R.id.status_spinner);
            TextView okTextView = (TextView) dialogView.findViewById(R.id.ok_textView);
            TextView outletTitleTextView = (TextView) dialogView.findViewById(R.id.outlet_title_textView);
            ImageView clearImageView = (ImageView) dialogView.findViewById(R.id.dialog_filter_clear_all);

            //Adapter
            arrayAdapterStatuses = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, statusArray);
            arrayAdapterStatuses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(arrayAdapterStatuses);

            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }

                    if (statusSpinner.getSelectedItemPosition() == 0) {
                        invoiceStatus = "";
                    } else if (statusSpinner.getSelectedItemPosition() == 1) {
                        invoiceStatus = "1";
                    } else if (statusSpinner.getSelectedItemPosition() == 2) {
                        invoiceStatus = "2";
                    } else if (statusSpinner.getSelectedItemPosition() == 3) {
                        invoiceStatus = "3";
                    }

                    if (outletSpinner.getSelectedItemPosition() == 0) {
                        outletchkid = "";
                        outletName = "";
                    } else {
                        if (isOutletEnable && outletArrayList != null && outletArrayList.size() > 0) {
                            outletchkid = outletArrayList.get(outletSpinner.getSelectedItemPosition()).getChkid();
                            outletName = outletArrayList.get(outletSpinner.getSelectedItemPosition()).getName();
                        }
                    }

                    if (collectionInvoiceList != null && collectionInvoiceList.size() > 0) {
                        collectionInvoiceList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    doRefresh(searchText);
                    updateFitlersTextViews();
                }
            });

            clearImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOutletEnable) {
                        outletSpinner.setSelection(0);
                        statusSpinner.setSelection(0);
                    }
                }
            });

            if (invoiceStatus != null && !invoiceStatus.isEmpty()) {
                if (invoiceStatus.equals("1")) {
                    statusSpinner.setSelection(1);
                } else if (invoiceStatus.equals("3")) {
                    statusSpinner.setSelection(3);
                } else if (invoiceStatus.equals("2")) {
                    statusSpinner.setSelection(2);
                } else {
                    statusSpinner.setSelection(0);
                }
            }

            if (isOutletEnable) {

                //Outlet Adapter
                ArrayAdapter<Outlet> outletArrayAdapter =
                        new ArrayAdapter<Outlet>(getActivity(), R.layout.simple_spinner_item, outletArrayList);
                outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                outletSpinner.setAdapter(outletArrayAdapter);
                outletSpinner.setSelection(0);

                outletLayout.setVisibility(View.VISIBLE);
                outletTitleTextView.setVisibility(View.VISIBLE);
            } else {
                outletLayout.setVisibility(View.GONE);
                outletTitleTextView.setVisibility(View.GONE);
            }

            for (int i = 0; i < outletArrayList.size(); i++) {
                if (outletArrayList.get(i).getChkid().equals(outletchkid)) {
                    outletSpinner.setSelection(i);
                }
            }


            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFitlersTextViews() {
        //Update TextViews of filter
        if (invoiceStatus != null && !invoiceStatus.isEmpty()) {
            if (invoiceStatus.equals("1")) {
                statusFilterTextView.setText("Status - " + statusArray[1]);
                topLayout.setVisibility(View.VISIBLE);

            } else if (invoiceStatus.equals("2")) {
                statusFilterTextView.setText("Status - " + statusArray[2]);
                topLayout.setVisibility(View.VISIBLE);

            } else if (invoiceStatus.equals("3")) {
                statusFilterTextView.setText("Status - " + statusArray[3]);
                topLayout.setVisibility(View.VISIBLE);
            } else {
                statusFilterTextView.setText("Status - " + "All");
                topLayout.setVisibility(View.GONE);
            }
        } else {
            statusFilterTextView.setText("Status - " + "All");
            topLayout.setVisibility(View.GONE);
        }


        if (isOutletEnable && outletArrayList != null && outletArrayList.size() > 0) {
            if (outletchkid == null || outletchkid.isEmpty() || outletchkid.equals("0")) {
                outletFilterTextView.setText(" Outlet - All");
            } else {
                topLayout.setVisibility(View.VISIBLE);
                if (invoiceStatus == null || invoiceStatus.isEmpty() ||
                        invoiceStatus.equals("0")) {
                    statusFilterTextView.setText("Status - All");
                }
                outletFilterTextView.setText(" Outlet - " + outletName);
            }
        } else {
            outletFilterTextView.setText("");
        }
    }

    public void getOutlets() {
        task = getString(R.string.collections_fetch_outlets);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {

                    if (apiResponse.getSuccess()) {

                        //TODO Outlet
                        if (outletArrayList != null && outletArrayList.size() > 0) {
                            outletArrayList.clear();
                        }

                        //One Static
                        Outlet outletStatic = new Outlet();
                        outletStatic.setName("All");
                        outletStatic.setChkid("0");
                        outletArrayList.add(outletStatic);
                        if (apiResponse.getData().getOutlet() != null) {
                            for (final Outlet outlet : apiResponse.getData().getOutlet()) {
                                if (outlet != null) {
                                    outletArrayList.add(outlet);
                                }
                            }
                        }
                        isOutletEnable = apiResponse.getData().isOutletEnable();
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActionsRowClicked(int rowPosition, int itemsPosition) {
        if (rowPosition == 0) {
            internetStatus = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!internetStatus.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                navigateToViewInvoiceScreen(itemsPosition);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (rowPosition == 1) {
            internetStatus = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!internetStatus.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                navigateToTransactionScreen(itemsPosition);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (rowPosition == 2) {
            internetStatus = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!internetStatus.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                navigateCollectPaymentScreen(collectionInvoiceList.get(itemsPosition).getCuid(),
                        collectionInvoiceList.get(itemsPosition).getName(),
                        collectionInvoiceList.get(itemsPosition).getAlid(),
                        new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                                collectionInvoiceList.get(itemsPosition).getBalance())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString(),
                        collectionInvoiceList.get(itemsPosition).getInvid());
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToTransactionScreen(int position) {
        Intent intent = new Intent(getActivity(), CollectionOrderTransactionActivity.class);
        intent.putExtra(getString(R.string.invid), collectionInvoiceList.get(position).getInvid());
        intent.putExtra(getString(R.string.invoice_id_text),
                collectionInvoiceList.get(position).getInvoiceID());
        startActivity(intent);
    }

    private void navigateToViewInvoiceScreen(int position) {
        Intent intent = new Intent(getActivity(), CollectionsViewInvoiceActivity.class);
        intent.putExtra(getString(R.string.invid), collectionInvoiceList.get(position).getInvid());
        intent.putExtra("text", collectionInvoiceList.get(position).getInvoiceID());
        startActivity(intent);
    }

    private void navigateCollectPaymentScreen(String cuId, String customerName, String alId,
                                              String pendingAmount, String invId) {
        Intent intent = new Intent(getActivity(), CollectPaymentCustomerActivity.class);
        intent.putExtra(getString(R.string.cuid), cuId);
        intent.putExtra(getString(R.string.customer), customerName);
        intent.putExtra(getString(R.string.alid), alId);
        intent.putExtra(getString(R.string.pending_amount), pendingAmount);
        intent.putExtra(getString(R.string.invid), invId);
        startActivity(intent);
    }
}