package com.accrete.warehouse.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.DeliveryUserList;
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

import static com.accrete.warehouse.CreateGatepassActivity.createGatepassViewpager;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/21/17.
 */

public class ConfirmGatepassFragment extends Fragment {
    List<DeliveryUserList> deliveryUserLists = new ArrayList<>();
    ArrayList<String> usernameList = new ArrayList<>();
    private AutoCompleteTextView dialogGatepassAuthenticationDeliveryUser;
    private TextView dialogGatepassBack;
    private LinearLayout dialogGatepassConfirm;
    private String strDeliveryUser;
    private ArrayAdapter arrayAdapterDeliveryUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_gatepass, container,
                false);

        dialogGatepassAuthenticationDeliveryUser = (AutoCompleteTextView) rootView.findViewById(R.id.dialog_gatepass_authentication_delivery_user);
        dialogGatepassBack = (TextView) rootView.findViewById(R.id.dialog_gatepass_back);
        dialogGatepassConfirm = (LinearLayout) rootView.findViewById(R.id.dialog_gatepass_confirm);

        arrayAdapterDeliveryUser = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, usernameList);
        arrayAdapterDeliveryUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogGatepassAuthenticationDeliveryUser.setAdapter(arrayAdapterDeliveryUser);


           /* dialogGatepassAuthenticationDeliveryUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogGatepassAuthenticationDeliveryUser.showDropDown();
                }
            });
*/
    /*        dialogGatepassAuthenticationDeliveryUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

            });
*/


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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            Activity activity = getActivity();
                            if (activity != null && isAdded()) {
                                getDeliveryUser(dialogGatepassAuthenticationDeliveryUser.getText().toString().trim());
                                if (usernameList.size() > 0) {
                                    dialogGatepassAuthenticationDeliveryUser.showDropDown();
                                }
                            }
                        }
                    }, 200);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


    private void getDeliveryUser(String searchText) {
        task = getString(R.string.task_delivery_user);
    /*    if (deliveryUserLists.size() > 0) {
            deliveryUserLists.clear();
        }*/

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
                            deliveryUserLists.add(deliveryUserList);

                        }

                        if(usernameList.size()>0) {
                          usernameList.clear();
                        }
                        for (int i = 0; i < deliveryUserLists.size(); i++) {
                            usernameList.add(deliveryUserLists.get(i).getName());
                        }

                        arrayAdapterDeliveryUser.notifyDataSetChanged();

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
}
