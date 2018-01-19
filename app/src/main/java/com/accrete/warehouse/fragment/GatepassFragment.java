package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ShippingBy;
import com.accrete.warehouse.model.ShippingCompany;
import com.accrete.warehouse.model.ShippingType;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.fragment.CreatePassMainTabFragment.createGatepassViewpager;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/21/17.
 */

public class GatepassFragment extends Fragment {
    List<String> arrayListShippingCompany = new ArrayList<>();
    List<String> arrayListShippingCompanyID = new ArrayList<>();
    private Spinner dialogCreateGatepassShippingBy;
    private EditText dialogCreateGatepassVehicleNumber;
    private Spinner dialogCreateGatepassShippingType;
    private Spinner dialogCreateGatepassShippingCompany;
    private TextView dialogCreateGatepassBack;
    private List<String> packageIdAddList = new ArrayList<>();
    private List<ShippingCompany> shippingCompany = new ArrayList<>();
    private List<ShippingType> shippingTypesList = new ArrayList<>();
    private List<ShippingBy> shippingByList = new ArrayList<>();
    private ArrayList<String> arrayListShippingType = new ArrayList<>();
    private ArrayList<String> arrayListShippingBy = new ArrayList<>();
    private ArrayAdapter arrayAdapterShippingType, arrayAdapterShippingBy, arrayAdapterShippingCompany;

    private String strShippingType;
    private String strShippingBy;
    private LinearLayout dialogCreateGatepass;
    private String pacid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatepass, container,
                false);
        findViews(rootView);
        return rootView;
    }

    public void setShippingData(List<String> packageIdList, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy) {
        // Toast.makeText(getActivity(), "hello PK", Toast.LENGTH_SHORT).show();
        packageIdAddList = packageIdList;
        shippingTypesList = shippingTypes;
        shippingByList = shippingBy;
        Log.d("hello ppp", packageIdAddList.size() + " " + shippingTypesList.size() + " " + shippingByList.size());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void findViews(View rootView) {
        dialogCreateGatepassShippingBy = (Spinner) rootView.findViewById(R.id.dialog_create_gatepass_shipping_by);
        dialogCreateGatepassVehicleNumber = (EditText) rootView.findViewById(R.id.dialog_create_gatepass_vehicle_number);
        dialogCreateGatepassShippingType = (Spinner) rootView.findViewById(R.id.dialog_create_gatepass_shipping_type);
        dialogCreateGatepassShippingCompany = (Spinner) rootView.findViewById(R.id.dialog_create_gatepass_shipping_company);
        dialogCreateGatepassBack = (TextView) rootView.findViewById(R.id.dialog_create_gatepass_back);
        dialogCreateGatepass = (LinearLayout) rootView.findViewById(R.id.dialog_create_gatepass);
        dialogCreateGatepassShippingCompany.setVisibility(View.GONE);

     /*   for (int i = 0; i <packageIdAddList.size() ; i++) {
            pacid = packageIdAddList.get(i);

        }*/

        StringBuilder listString = new StringBuilder();

        for (String s : packageIdAddList) {
            listString.append(s + ",");
        }
        pacid = TextUtils.join(",", packageIdAddList);

        dialogCreateGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreatePassMainTabFragment) getParentFragment())
                        .sendData(pacid,shippingByList.get(dialogCreateGatepassShippingBy.getSelectedItemPosition()).getPacdelgatpactid(),
                                shippingTypesList.get(dialogCreateGatepassShippingType.getSelectedItemPosition()).getPacshtid(),
                                arrayListShippingCompanyID.get(dialogCreateGatepassShippingCompany.getSelectedItemPosition()),
                                dialogCreateGatepassVehicleNumber.getText().toString());
                createGatepassViewpager.setCurrentItem(2);
            }
        });

    /*    if(packageIdAddList.size()>0){
            packageIdAddList.clear();
        }*/
        if (arrayListShippingBy.size() > 0) {
            arrayListShippingBy.clear();
        }
        if (arrayListShippingType.size() > 0) {
            arrayListShippingType.clear();
        }

        for (int i = 0; i < shippingByList.size(); i++) {
            Log.d("called", "happy" + shippingByList.get(i).getName());
            arrayListShippingBy.add(shippingByList.get(i).getName());
        }


        for (int i = 0; i < shippingTypesList.size(); i++) {
            Log.d("called", "happy" + shippingTypesList.get(i).getName());
            arrayListShippingType.add(shippingTypesList.get(i).getName());
        }


        arrayAdapterShippingType = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingType);
        arrayAdapterShippingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCreateGatepassShippingType.setAdapter(arrayAdapterShippingType);
        //  arrayAdapterShippingType.notifyDataSetChanged();

        arrayAdapterShippingBy = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingBy);
        arrayAdapterShippingBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCreateGatepassShippingBy.setAdapter(arrayAdapterShippingBy);
        // arrayAdapterShippingBy.notifyDataSetChanged();

        dialogCreateGatepassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGatepassViewpager.setCurrentItem(0);
            }
        });

        dialogCreateGatepassShippingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                getCompany(shippingTypesList.get(dialogCreateGatepassShippingType.getSelectedItemPosition()).getPacshtid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    private void getCompany(String pacshtid) {
        task = getString(R.string.gatepass_shipping_company_info);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getGatepassShippingInfo(version, key, task, userId, accessToken, pacshtid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {

                    if (arrayListShippingCompany.size() > 0) {
                        arrayListShippingCompany.clear();
                    }
                    if (apiResponse.getSuccess()) {
                        dialogCreateGatepassShippingCompany.setVisibility(View.VISIBLE);
                        for (ShippingCompany shippingCompany : apiResponse.getData().getShippingCompany()) {
                            arrayListShippingCompany.add(shippingCompany.getName());
                            arrayListShippingCompanyID.add(shippingCompany.getScompid());
                        }

                        arrayAdapterShippingCompany = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingCompany);
                        arrayAdapterShippingCompany.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dialogCreateGatepassShippingCompany.setAdapter(arrayAdapterShippingCompany);
                    } else {
                    /*    if (apiResponse.getSuccessCode().equals("10001")) {

                        }
*/

                        // arrayListShippingCompany.add("Please select other shipping type");
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        arrayAdapterShippingCompany = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingCompany);
                        arrayAdapterShippingCompany.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dialogCreateGatepassShippingCompany.setAdapter(arrayAdapterShippingCompany);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("wh:gatepass", t.getMessage());
            }
        });
    }

}
