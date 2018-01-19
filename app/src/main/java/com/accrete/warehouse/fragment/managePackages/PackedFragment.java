package com.accrete.warehouse.fragment.managePackages;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackedItemWithoutCheckboxAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PackedItem;
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

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 11/30/17.
 */

public class PackedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PackedItemWithoutCheckboxAdapter.PackedItemAdapterListener {

    String status = "";
    private SwipeRefreshLayout packedSwipeRefreshLayout;
    private RecyclerView packedRecyclerView;
    private TextView packedEmptyView, packedAdd, packedDeliver;
    private PackedItemWithoutCheckboxAdapter packedItemAdapter;
    private List<PackedItem> packedList = new ArrayList<>();
    private Packages packages = new Packages();
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private String dataChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_packed, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packedSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.packed_swipe_refresh_layout);
        packedRecyclerView = (RecyclerView) rootView.findViewById(R.id.packed_recycler_view);
        packedEmptyView = (TextView) rootView.findViewById(R.id.packed_empty_view);
     /*   packedAdd = (TextView) rootView.findViewById(R.id.packed_text_add);
        packedDeliver = (TextView) rootView.findViewById(R.id.packed_text_deliver);*/

        packedItemAdapter = new PackedItemWithoutCheckboxAdapter(getActivity(), packedList, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packedRecyclerView.setLayoutManager(mLayoutManager);
        packedRecyclerView.setHasFixedSize(true);
        packedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedRecyclerView.setNestedScrollingEnabled(false);
        packedRecyclerView.setAdapter(packedItemAdapter);

       /* packedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedAdd.setBackgroundColor(getResources().getColor(R.color.add_dark_blue));
               //dialogAddPackages();
            }
        });
        packedDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedDeliver.setBackgroundColor(getResources().getColor(R.color.add_dark_red));
                dialogDeliverPackages();
            }
        });
*/
        doRefresh();

        //Scroll Listener
        packedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && packedList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        packedSwipeRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        packedEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packedEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });
    }

 /*   private void dialogAddPackages() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_add_packages, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogDeliver = builder.create();
        dialogDeliver.setCanceledOnTouchOutside(true);
        AddedItemsAdapter addedItemsAdapter;
        TextView addPackagesCancel;
        AutoCompleteTextView addPackagesSearchWithPackageId;
        RecyclerView dialogAddPackagesRecyclerView;
        TextView dialogAddPackagesSelectShippingCompany;
        List<AlreadyAddedItem> alreadyAddedItemList = new ArrayList<>();
        AlreadyAddedItem alreadyAddedItem = new AlreadyAddedItem();

        addPackagesCancel = (TextView) dialogView.findViewById(R.id.add_packages_cancel);
        addPackagesSearchWithPackageId = (AutoCompleteTextView) dialogView.findViewById(R.id.add_packages_search_with_package_id);
        dialogAddPackagesRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_add_packages_recycler_view);
        dialogAddPackagesSelectShippingCompany = (TextView) dialogView.findViewById(R.id.dialog_add_packages_select_shipping_company);
        addedItemsAdapter = new AddedItemsAdapter(getActivity(), alreadyAddedItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        dialogAddPackagesRecyclerView.setLayoutManager(mLayoutManager);
        dialogAddPackagesRecyclerView.setHasFixedSize(true);
        dialogAddPackagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dialogAddPackagesRecyclerView.setNestedScrollingEnabled(false);
        dialogAddPackagesRecyclerView.setAdapter(addedItemsAdapter);

        dialogAddPackagesSelectShippingCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateGatepass();
            }
        });

        addPackagesCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeliver.dismiss();
            }
        });


        alreadyAddedItem.setPackageID("PAK1002890001");
        alreadyAddedItemList.add(alreadyAddedItem);
        alreadyAddedItemList.add(alreadyAddedItem);
        alreadyAddedItemList.add(alreadyAddedItem);
        alreadyAddedItemList.add(alreadyAddedItem);
        alreadyAddedItemList.add(alreadyAddedItem);
        alreadyAddedItemList.add(alreadyAddedItem);

        dialogDeliver.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogDeliver.isShowing()) {
            dialogDeliver.show();
        }
    }*/

    public void doRefresh() {
        if (packedList != null && packedList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        packedEmptyView.setText(getString(R.string.no_data_available));
                        getPackageDetailsList(getString(R.string.last_updated_date), "1");
                    }
                }, 200);
            } else {
                packedRecyclerView.setVisibility(View.GONE);
                packedEmptyView.setVisibility(View.VISIBLE);
                packedEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void dialogCreateGatepass() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_create_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogCreateGatepass = builder.create();
        dialogCreateGatepass.setCanceledOnTouchOutside(true);

        LinearLayout linearLayout;
        TextView cancelGatepassTitle;
        AutoCompleteTextView dialogCreateGatepassShippingBy;
        AutoCompleteTextView dialogCreateGatepassVehicleNumber;
        AutoCompleteTextView dialogCreateGatepassShippingType;
        AutoCompleteTextView dialogCreateGatepassShippingCompany;
        Button btnOk;
        ProgressBar cancelGatepassProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        cancelGatepassTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_title);
        dialogCreateGatepassShippingBy = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_by);
        dialogCreateGatepassVehicleNumber = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_vehicle_number);
        dialogCreateGatepassShippingType = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_type);
        dialogCreateGatepassShippingCompany = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_company);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateGatepass.dismiss();
            }
        });

        dialogCreateGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogCreateGatepass.isShowing()) {
            dialogCreateGatepass.show();
        }
    }

    private void dialogConfirmGatepass() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_gatepass_authentication, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogConfirmGatepass = builder.create();
        dialogConfirmGatepass.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        TextView dialogGatepassAuthenticationTitle;
        AutoCompleteTextView dialogGatepassAuthenticationDeliveryUser;
        Button dialogGatepassAuthenticationConfirm;
        ProgressBar cancelGatepassProgressBar;
        Button dialogGatepassAuthenticationCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogGatepassAuthenticationTitle = (TextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_title);
        dialogGatepassAuthenticationDeliveryUser = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_delivery_user);
        dialogGatepassAuthenticationConfirm = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_confirm);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        dialogGatepassAuthenticationCancel = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_cancel);

        dialogGatepassAuthenticationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass.dismiss();
            }
        });

        dialogGatepassAuthenticationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass.dismiss();
            }
        });

        dialogConfirmGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogConfirmGatepass.isShowing()) {
            dialogConfirmGatepass.show();
        }
    }

    private void dialogDeliverPackages() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogDeliver = builder.create();
        dialogDeliver.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        Button btnOk;
        ProgressBar cancelGatepassProgressBar;
        TextView textViewMessage, textViewTitle;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        textViewMessage = (TextView) dialogView.findViewById(R.id.cancel_gatepass_message);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        textViewTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_title);
        textViewTitle.setText("Deliver");
        textViewMessage.setText("Do you want to deliver selected packages without selecting Shipping company and delivery user?");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeliver.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeliver.dismiss();
            }
        });

        dialogDeliver.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogDeliver.isShowing()) {
            dialogDeliver.show();
        }
    }

    private void apiCall() {
        String status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            loading = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPackageDetailsList(getString(R.string.last_updated_date), "1");
                    packedRecyclerView.setVisibility(View.VISIBLE);
                    packedEmptyView.setVisibility(View.GONE);
                    packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }, 00);
        } else {
            packedRecyclerView.setVisibility(View.GONE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setVisibility(View.GONE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute(ArrayList<String> packageIdList) {

    }

    private void getPackageDetailsList(final String updatedDate, final String traversalValue) {
        task = getString(R.string.packed_packages_list_task);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageDetails(version, key, task, userId, accessToken, chkid, "1", updatedDate,
                traversalValue);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess())

                    {
                        for (final PackedItem packedItem : apiResponse.getData().getPackedItems()) {
                            if (packedItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                        packedList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (packedSwipeRefreshLayout != null &&
                                            packedSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                            packedList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                            packedList.add(packedItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (packedList != null && packedList.size() == 0) {
                            packedEmptyView.setVisibility(View.VISIBLE);
                            packedRecyclerView.setVisibility(View.GONE);
                            packedSwipeRefreshLayout.setVisibility(View.GONE);
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            packedEmptyView.setVisibility(View.GONE);
                            packedRecyclerView.setVisibility(View.VISIBLE);
                            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (packedSwipeRefreshLayout != null &&
                                packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            packedItemAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedItemAdapter.notifyDataSetChanged();
                                packedRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            packedEmptyView.setText(getString(R.string.no_data_available));
                            packedRecyclerView.setVisibility(View.GONE);
                            packedEmptyView.setVisibility(View.VISIBLE);
                            packedSwipeRefreshLayout.setVisibility(View.GONE);
                        }
                    }
                    if (packedSwipeRefreshLayout != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (packedList != null && packedList.size() > 0) {
                getPackageDetailsList(packedList.get(0).getCreatedTs(), "1");
            } else {
                getPackageDetailsList(getString(R.string.last_updated_date), "1");
            }
            packedRecyclerView.setVisibility(View.VISIBLE);
            packedEmptyView.setVisibility(View.GONE);
            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            packedRecyclerView.setVisibility(View.GONE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setVisibility(View.GONE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
            packedSwipeRefreshLayout.setRefreshing(false);
        }

    }
}
