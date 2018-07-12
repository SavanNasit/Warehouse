package com.accrete.warehouse.fragment.creategatepass;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackedItemAdapter;
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
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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
    private List<PackedItem> selectedPackedList = new ArrayList<>();
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private ArrayList<String> packageIdListToAdd = new ArrayList<>();
    private ImageView imageViewLoader;
    private String status;
    private String stringSearchText;
    private String dataChanged;


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
        imageViewLoader = (ImageView) rootView.findViewById(R.id.imageView_loader);

        packedItemAdapter = new PackedItemAdapter(getActivity(), packedList, this, "packageSelection");
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packageSelectionRecyclerView.setLayoutManager(mLayoutManager);
        packageSelectionRecyclerView.setHasFixedSize(true);
        packageSelectionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageSelectionRecyclerView.setNestedScrollingEnabled(false);
        packageSelectionRecyclerView.setAdapter(packedItemAdapter);
        packageSelectionEditSearchView.setVisibility(View.VISIBLE);



        doRefresh();
        packageSelectionEditSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAPI(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //filter(packageSelectionEditSearchView.getText().toString());
            }
        });

        //Scroll Listener
        //Scroll Listener
        packageSelectionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        if (getActivity() != null && isAdded()) {
                            // getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2");
                            getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");

                        }
                    } else {
                       /* if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }*/
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        packageSelectionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageSelectionAdd.setEnabled(false);
                if (packageIdListToAdd.size() > 0) {
                    addPackagesInBucket();
                } else {
                    Toast.makeText(getActivity(), "Please add one or more than one package", Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        packageSelectionAdd.setEnabled(true);
                    }
                }, 1000);
            }
        });
    }


    public void doRefresh() {
        if (packedList != null && packedList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && isAdded()) {
                            showLoader();
                            textViewEmptyView.setText(getString(R.string.no_data_available));
                            getPackageDetailsList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
                        }
                    }
                }, 200);
            } else {
                packageSelectionRecyclerView.setVisibility(View.GONE);
                textViewEmptyView.setVisibility(View.VISIBLE);
                textViewEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
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
    public void onExecute(ArrayList<String> packageIdList, List<PackedItem> packedList) {
        packageIdListToAdd = packageIdList;
        if (selectedPackedList != null && selectedPackedList.size() > 0) {
            selectedPackedList.clear();
        }
        selectedPackedList.addAll(packedList);
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
        Call<ApiResponse> call = apiService.getPackageSelectionList(version, key, task, userId, accessToken, chkid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        final String finalChkid = chkid;
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {

                        ((CreatePassMainTabFragment) getParentFragment()).getResult(packageIdListToAdd,
                                apiResponse.getData().getShippingTypes(), apiResponse.getData().getTransprotModes(), finalChkid, selectedPackedList);
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

    private void getPackageDetailsList(final String time, final String traversalValue,
                                       String searchValue, String startDate, String endDate){
        task = getString(R.string.packed_packages_list_task);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageLists(version, key, task, userId, accessToken, chkid,
                time, traversalValue, searchValue, startDate, endDate,"1");
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
                        for (final PackedItem packedItem : apiResponse.getData().getPackedItems()) {
                            if (packedItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(packedItem.getCreatedTs())) {
                                        packedList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                  /*  if (packedSwipeRefreshLayout != null &&
                                            packedSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedList.add(packedItem);
                                        }
                                    }*/
                                    packedList.add(packedItem);
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (packedList != null && packedList.size() == 0) {
                            textViewEmptyView.setVisibility(View.VISIBLE);
                            textViewEmptyView.setText("No data available");
                            packageSelectionRecyclerView.setVisibility(View.GONE);
                            packageSelectionRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            textViewEmptyView.setVisibility(View.GONE);
                            packageSelectionRecyclerView.setVisibility(View.VISIBLE);
                        }

                        if (traversalValue.equals("2")) {
                            packedItemAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedItemAdapter.notifyDataSetChanged();
                                packageSelectionRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        if (packedList != null && packedList.size() == 0) {
                            textViewEmptyView.setVisibility(View.VISIBLE);
                            textViewEmptyView.setText("No data available");
                            packageSelectionRecyclerView.setVisibility(View.VISIBLE);

                        } else {
                            textViewEmptyView.setVisibility(View.GONE);
                            packageSelectionRecyclerView.setVisibility(View.VISIBLE);
                        }

                        if (traversalValue.equals("2")) {
                            packedItemAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedItemAdapter.notifyDataSetChanged();
                                packageSelectionRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (getActivity() != null && isAdded()) {
                    hideLoader();
                }
            }
        });
    }

    private void hideLoader() {
        if (getActivity() != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (imageViewLoader.getVisibility() == View.GONE) {
                                imageViewLoader.setVisibility(View.VISIBLE);
                            }
                            //Disable Touch
                            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Ion.with(imageViewLoader)
                                    .animateGif(AnimateGifMode.ANIMATE)
                                    .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                    .withBitmapInfo();
                        }

                    }
                });
            }
        });

        thread.start();
    }

    public void onBackPressed() {
        getChildFragmentManager().popBackStack();
    }

    public interface SendDataListener {
        void callback(List<String> packageList, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy);
    }

    public void searchAPI(final String searchText) {

        stringSearchText = searchText;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {

                    if (packedList != null) {
                        if (packedList.size() > 0) {
                            packedList.clear();
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    packedItemAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            //  loading = true;
                            showLoader();
                            getPackageDetailsList(getString(R.string.last_updated_date), "1", searchText, "", "");
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
        thread.start();

    }

}
