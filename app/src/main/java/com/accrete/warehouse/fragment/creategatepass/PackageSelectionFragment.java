package com.accrete.warehouse.fragment.creategatepass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackedItemAdapter;
import com.accrete.warehouse.fragment.creategatepass.CreatePassMainTabFragment;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackedItem;
import com.accrete.warehouse.model.ShippingBy;
import com.accrete.warehouse.model.ShippingType;
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

import static com.accrete.warehouse.fragment.creategatepass.CreatePassMainTabFragment.createGatepassViewpager;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/21/17.
 */

public class PackageSelectionFragment extends Fragment implements PackedItemAdapter.PackedItemAdapterListener {


    SendDataListener dataListener;
    private EditText packageSelectionEditSearchView;
    private TextView textViewEmptyView;
    private RecyclerView packageSelectionRecyclerView;
    private LinearLayout packageSelectionAdd;
    private PackedItemAdapter packedItemAdapter;
    private List<PackedItem> packedList = new ArrayList<>();
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private ArrayList<String> packageIdListToAdd = new ArrayList<>();

    public void PackageSelectionFragment() {
    }

    // constructor
    public void sendData(SendDataListener dataListener) {
        this.dataListener = dataListener;
    }

    public void dataToSend(ArrayList<String> packageIdListToAdd, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy) {
        //   if(dataListener!=null) {
        dataListener.callback(packageIdListToAdd, shippingTypes, shippingBy);
        //  }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_package_selection, container,
                false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packageSelectionEditSearchView = (EditText) rootView.findViewById(R.id.package_selection_edit_search_view);
        packageSelectionRecyclerView = (RecyclerView) rootView.findViewById(R.id.package_selection_recycler_view);
        packageSelectionAdd = (LinearLayout) rootView.findViewById(R.id.package_selection_add);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.package_selection_empty_view);

        packedItemAdapter = new PackedItemAdapter(getActivity(), packedList, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packageSelectionRecyclerView.setLayoutManager(mLayoutManager);
        packageSelectionRecyclerView.setHasFixedSize(true);
        packageSelectionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageSelectionRecyclerView.setNestedScrollingEnabled(false);
        packageSelectionRecyclerView.setAdapter(packedItemAdapter);
        packageSelectionEditSearchView.setVisibility(View.GONE);

        packageSelectionEditSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  filter(packageSelectionEditSearchView.getText().toString());
            }
        });

        //Scroll Listener
     /*   packageSelectionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    } else {
                       *//* if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }*//*
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
*/
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            getPackageDetailsList(getString(R.string.last_updated_date), "1");

        } else {
                       /* if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }*/
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }


        packageSelectionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packageIdListToAdd.size() > 0) {
                    addPackagesInBucket();
                } else {
                    Toast.makeText(getActivity(), "Please add one or more than one package", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        List<PackedItem> filterdNames = new ArrayList<>();
        //looping through existing elements
        for (int i = 0; i < packedList.size(); i++) {
            if (text.contains(packedList.get(i).getPackageId())) {
                filterdNames.add(packedList.get(i));
            }
        }
        //calling a method of the adapter class and passing the filtered list
        packedItemAdapter.filterList(filterdNames);



        /*else{

            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                getPackageDetailsList(getString(R.string.last_updated_date), "1");

            } else {
                       *//* if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }*//*
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }*/

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute(ArrayList<String> packageIdList) {
        packageIdListToAdd = packageIdList;
        Log.d("size", String.valueOf(packageIdList.size()));
    }


    private void addPackagesInBucket() {
        task = getString(R.string.shipping_form_data);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getRunningOrderList(version, key, task, userId, accessToken, chkid);
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

                        ((CreatePassMainTabFragment) getParentFragment()).getResult(packageIdListToAdd,
                                apiResponse.getData().getShippingTypes(), apiResponse.getData().getShippingBy());
                        //((CreateGatepassActivity)getActivity()).getResult(packageIdListToAdd,apiResponse.getData().getShippingTypes(),apiResponse.getData().getShippingBy());
                        createGatepassViewpager.setCurrentItem(1);

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {

                        }
                    }
                  /*  if (sw != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("wh:packageDetails", t.getMessage());
            }
        });
    }

    private void getPackageDetailsList(String updatedDate, String traversalValue) {
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
                    if (apiResponse.getSuccess()) {
                        packageSelectionRecyclerView.setVisibility(View.VISIBLE);
                        textViewEmptyView.setVisibility(View.GONE);
                        for (PackedItem packedItem : apiResponse.getData().getPackedItems()) {
                            packedList.add(packedItem);
                        }
                        if (packedList.size() > 0) {
                        } else {
                            textViewEmptyView.setText(getString(R.string.no_data_available));
                            packageSelectionRecyclerView.setVisibility(View.GONE);
                            textViewEmptyView.setVisibility(View.VISIBLE);
                        }
                        packedItemAdapter.notifyDataSetChanged();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            textViewEmptyView.setText(getString(R.string.no_data_available));
                            packageSelectionRecyclerView.setVisibility(View.GONE);
                            textViewEmptyView.setVisibility(View.VISIBLE);
                        }else if(apiResponse.getSuccessCode().equals("20004")){
                            textViewEmptyView.setText(apiResponse.getMessage());
                            packageSelectionRecyclerView.setVisibility(View.GONE);
                            textViewEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                  /*  if (sw != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("wh:packageDetails", t.getMessage());
            }
        });
    }

    public interface SendDataListener {
        void callback(List<String> packageList, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy);
    }


}
