package com.accrete.warehouse.fragment.creategatepass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackedItemAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.DeliveryUserList;
import com.accrete.warehouse.model.PackedItem;
import com.accrete.warehouse.navigationView.DrawerActivity;
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

public class ConfirmGatepassFragment extends Fragment implements PackedItemAdapter.PackedItemAdapterListener {
    List<DeliveryUserList> deliveryUserLists = new ArrayList<>();
    List<String> usernameList = new ArrayList<>();
    String uid, pacid, pacshtid, chkid, scompid, vehicle;
    List<PackedItem> selectedPackedList = new ArrayList<>();
    private AutoCompleteTextView dialogGatepassAuthenticationDeliveryUser;
    private TextView dialogGatepassBack;
    private TextView dialogGatepassConfirm;
    private String strDeliveryUser;
    private ArrayAdapter arrayAdapterDeliveryUser;
    private String shippingBy;
    private String transporterId;
    private TextView textViewEmptyView;
    private RecyclerView packageSelectionRecyclerView;
    private PackedItemAdapter packedItemAdapter;
    private ImageView imageViewLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_gatepass, container,
                false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialogGatepassAuthenticationDeliveryUser = (AutoCompleteTextView) rootView.findViewById(R.id.dialog_gatepass_authentication_delivery_user);
        dialogGatepassBack = (TextView) rootView.findViewById(R.id.dialog_gatepass_back);
        dialogGatepassConfirm = (TextView) rootView.findViewById(R.id.dialog_gatepass_confirm);

        packageSelectionRecyclerView = (RecyclerView) rootView.findViewById(R.id.package_selection_recycler_view);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.package_selection_empty_view);
        imageViewLoader = (ImageView) rootView.findViewById(R.id.imageView_loader);

        packedItemAdapter = new PackedItemAdapter(getActivity(), selectedPackedList, this, "confirmGatepass");
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packageSelectionRecyclerView.setLayoutManager(mLayoutManager);
        packageSelectionRecyclerView.setHasFixedSize(true);
        packageSelectionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageSelectionRecyclerView.setNestedScrollingEnabled(false);
        packageSelectionRecyclerView.setAdapter(packedItemAdapter);


        arrayAdapterDeliveryUser = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, usernameList);
        arrayAdapterDeliveryUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogGatepassAuthenticationDeliveryUser.setAdapter(arrayAdapterDeliveryUser);


        dialogGatepassAuthenticationDeliveryUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameList.size() > 0) {
                    dialogGatepassAuthenticationDeliveryUser.showDropDown();
                }
            }
        });

/*         dialogGatepassAuthenticationDeliveryUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strDeliveryUser = dialogGatepassAuthenticationDeliveryUser.getText().toString();
                        for (int i = 0; i < deliveryUserLists.size(); i++) {
                            String temp = deliveryUserLists.get(i).getName();
                            if (strDeliveryUser.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        dialogGatepassAuthenticationDeliveryUser.setText("");
                    }
                }

            });*/


        dialogGatepassConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGatepassConfirm.setEnabled(false);
                if (uid != null && !uid.isEmpty()) {
                    if(getActivity()!=null && isAdded()) {
                        showLoader();
                        createGatePass(uid, pacid, pacshtid, scompid, vehicle, shippingBy);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select the delivery user", Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogGatepassConfirm.setEnabled(true);
                    }
                }, 1000);
            }
        });

        dialogGatepassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGatepassViewpager.setCurrentItem(1);
            }
        });

        dialogGatepassAuthenticationDeliveryUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {

                    //Code to be executed after desired time
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        getDeliveryUser(dialogGatepassAuthenticationDeliveryUser.getText().toString().trim());
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void createGatePass(String uid, String pacid, String pacshtid, String scompid, String vehicle, String shippingBy) {
        try {
            task = getString(R.string.create_gatepass_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.createGatePass(version, key, task, userId, accessToken, uid,
                pacid, pacshtid, scompid, chkid, "0", "0", shippingBy, vehicle, transporterId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Toast.makeText(getActivity(), "Gatepass created successfully", Toast.LENGTH_SHORT).show();
                      /*  Fragment manageGatePassFragment = ManageGatePassFragment.newInstance(getString(R.string.manage_gatepass_fragment));
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.confirm_gatepass_container, manageGatePassFragment).addToBackStack(null).commit();*/

                   //     ((CreatePackageActivity) getActivity()).getSupportActionBar().setTitle("Manage GatePass");

                        Intent intentDrawerActivity = new Intent(getActivity(), DrawerActivity.class);
                        intentDrawerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentDrawerActivity.putExtra("flagToManageGatepass", "true");
                        startActivity(intentDrawerActivity);
                        getActivity().finish();

                        // Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if(getActivity()!=null && isAdded()) {
                        hideLoader();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if(getActivity()!=null && isAdded()) {
                        hideLoader();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });

        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }


    private void getDeliveryUser(String searchText) {
        task = getString(R.string.task_delivery_user);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getDeliveryUserList(version, key, task, userId, accessToken, searchText);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (DeliveryUserList deliveryUserList : apiResponse.getData().getDeliveryUserList()) {
                            if (deliveryUserList.getName() != null && !deliveryUserList.getName().isEmpty()) {
                                if (deliveryUserLists.size() > 0) {
                                    deliveryUserLists.clear();
                                    usernameList.clear();
                                }
                                deliveryUserLists.add(deliveryUserList);
                                usernameList.add(deliveryUserList.getName());
                                Log.d("username", deliveryUserList.getName());
                            }
                        }

                        arrayAdapterDeliveryUser = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, usernameList);
                        arrayAdapterDeliveryUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dialogGatepassAuthenticationDeliveryUser.setAdapter(arrayAdapterDeliveryUser);
                        arrayAdapterDeliveryUser.notifyDataSetChanged();

                        dialogGatepassAuthenticationDeliveryUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (deliveryUserLists.size() > 0) {
                                    uid = deliveryUserLists.get(position).getId();
                                }
                            }
                        });



                    /*    if(usernameList.size()>0) {
                          usernameList.clear();
                        }*/
                       /* for (int i = 0; i < deliveryUserLists.size(); i++) {
                            usernameList.add(deliveryUserLists.get(i).getName());
                        }*/

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }

    public void getData(String strPacid, String strPacdelgatpactid, String strPacshtid, String strShippingCompanyId, String sirVehicleNumber, String tarnsporterId, List<PackedItem> selectedPackedLists) {
        pacid = strPacid;
        shippingBy = strPacdelgatpactid;
        pacshtid = strPacshtid;
        scompid = strShippingCompanyId;
        vehicle = sirVehicleNumber;
        transporterId = tarnsporterId;
        selectedPackedList = selectedPackedLists;
        if (getActivity() != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute(ArrayList<String> packageIdList, List<PackedItem> packedList) {

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
}
