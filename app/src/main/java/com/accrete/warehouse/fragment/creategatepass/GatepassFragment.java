package com.accrete.warehouse.fragment.creategatepass;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ReferredByTransporterNameAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ShippingCompany;
import com.accrete.warehouse.model.ShippingType;
import com.accrete.warehouse.model.TransporterNameSearchDatum;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.CustomisedEdiText;
import com.accrete.warehouse.utils.CustomisedEdiTextListener;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
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
 * Created by poonam on 12/21/17.
 */

public class GatepassFragment extends Fragment implements ReferredByTransporterNameAdapter.ReferredByTransporterNameAdapterListener, View.OnClickListener {
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
    private List<TransportMode> shippingByList = new ArrayList<>();
    private ArrayList<String> arrayListShippingType = new ArrayList<>();
    private ArrayList<String> arrayListShippingBy = new ArrayList<>();
    private ArrayAdapter arrayAdapterShippingType, arrayAdapterShippingBy, arrayAdapterShippingCompany;

    private String strShippingType;
    private String strShippingBy;
    private TextView dialogCreateGatepass;
    private String pacid;
    private ArrayList<TransportMode> transportTypeList = new ArrayList<>();
    private ArrayList<String> transportMode = new ArrayList<>();
    private Spinner dialogCreateGatepassTransportMode, dialogCreateGatepassTransportType;
    private ArrayAdapter arrayAdapterTransportMode;
    private String strMode;
    private EditText dialogCreateGatepassTransporterName;
    private Dialog dialog;
    private List<TransporterNameSearchDatum> transporterNameList = new ArrayList<>();
    private CustomisedEdiText transporterNameSearchEditText;
    private ReferredByTransporterNameAdapter referredByTransporterNameAdapter;
    private String strTransportId, sChkid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatepass, container,
                false);
        findViews(rootView);
        return rootView;
    }

    public void setShippingData(List<String> packageIdList, List<ShippingType> shippingTypes, List<TransportMode> shippingBy, String chkid) {
        // Toast.makeText(getActivity(), "hello PK", Toast.LENGTH_SHORT).show();
        packageIdAddList = packageIdList;
        shippingTypesList = shippingTypes;
        shippingByList = shippingBy;
        sChkid = chkid;
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
        dialogCreateGatepass = (TextView) rootView.findViewById(R.id.dialog_create_gatepass);
        dialogCreateGatepassTransportType = (Spinner) rootView.findViewById(R.id.dialog_create_gatepass_transport_type);
        dialogCreateGatepassTransportMode = (Spinner) rootView.findViewById(R.id.dialog_create_gatepass_transport_mode);
        dialogCreateGatepassTransporterName = (EditText) rootView.findViewById(R.id.dialog_create_gatepass_transporter);
        dialogCreateGatepassShippingCompany.setVisibility(View.GONE);
        dialogCreateGatepassTransporterName.setOnClickListener(this);

        dialogCreateGatepassTransporterName.clearFocus();
        dialogCreateGatepassTransporterName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (dialogCreateGatepassTransporterName.getText().toString().trim().length() == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hideSoftKeyboard(getActivity());
                            openTransporterNameSearchDialog();
                        }
                    }
                }
            }
        });


        if (transportTypeList.size() > 0) {
            transportTypeList.clear();
            transportTypeList.addAll(shippingByList);
        } else {
            transportTypeList.addAll(shippingByList);
        }

        tansportTypeAdapter();
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

                try {
                    dialogCreateGatepass.setEnabled(false);

                    ((CreatePassMainTabFragment) getParentFragment())
                            .sendData(pacid, strMode,
                                    shippingTypesList.get(dialogCreateGatepassShippingType.getSelectedItemPosition()).getPacshtid(),
                                    arrayListShippingCompanyID.get(dialogCreateGatepassShippingCompany.getSelectedItemPosition()),
                                    dialogCreateGatepassVehicleNumber.getText().toString(), strTransportId);
                    CreatePassMainTabFragment.createGatepassViewpager.setCurrentItem(2);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogCreateGatepass.setEnabled(true);
                        }
                    }, 1000);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Please select shipping company", Toast.LENGTH_SHORT).show();
                    dialogCreateGatepass.setEnabled(true);
                }
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

      /*  for (int i = 0; i < shippingByList.size(); i++) {
            Log.d("called", "happy" + shippingByList.get(i).getName());
            arrayListShippingBy.add(shippingByList.get(i).getName());
        }*/


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
                CreatePassMainTabFragment.createGatepassViewpager.setCurrentItem(0);
            }
        });

        dialogCreateGatepassShippingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
              //  hideSpinnerDropDown(dialogCreateGatepassShippingType);
               // dialogCreateGatepassShippingCompany.setVisibility(View.VISIBLE);
                getCompany(shippingTypesList.get(dialogCreateGatepassShippingType.getSelectedItemPosition()).getPacshtid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        dialogCreateGatepassTransportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transportModeAdapter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openTransporterNameSearchDialog() {

        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_transporter_name_search);
      //  getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        transporterNameSearchEditText = (CustomisedEdiText) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        RecyclerView customerRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);


        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        referredByTransporterNameAdapter = new ReferredByTransporterNameAdapter(getActivity(),
                transporterNameList, this);

        customerRecyclerView.setLayoutManager(mLayoutManager);
        customerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        customerRecyclerView.setAdapter(referredByTransporterNameAdapter);
        customerRecyclerView.setNestedScrollingEnabled(false);

        transporterNameSearchEditText.setHint("Search Transporter");
        transporterNameSearchEditText.setThreshold(1);
        transporterNameSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (transporterNameSearchEditText.isPerformingCompletion()) {

                } else {
                    String status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchTransporter(s.toString().trim(), sChkid);
                    } else {
                        //Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                        if (transporterNameSearchEditText.getText().toString() != null && !transporterNameSearchEditText.getText().toString().isEmpty()) {
                            //Toast.makeText(getActivity(), getString(R.string.add_customer_error), Toast.LENGTH_SHORT).show();
                            //CustomisedToast.info(getActivity(), getString(R.string.add_customer_error)).show();
                            Toast.makeText(getActivity(), getString(R.string.add_transporter_name_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        transporterNameSearchEditText.addListener(new CustomisedEdiTextListener() {
            @Override
            public void onUpdate() {
                if (transporterNameList != null && transporterNameList.size() > 0) {
                    transporterNameList.clear();
                }

                referredByTransporterNameAdapter.notifyDataSetChanged();
            }
        });

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();


    }

    private void searchTransporter(String s, String chkid) {
        task = getString(R.string.task_transporter_name_search);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getTransporterName(version, key, task, userId, accessToken, chkid, s, "26");
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (transporterNameList != null && transporterNameList.size() > 0) {
                            transporterNameList.clear();
                        }

                        for (final TransporterNameSearchDatum searchRefferedDatum : apiResponse.getData().getLedgerSearchData()) {
                            if (searchRefferedDatum != null) {
                                transporterNameList.add(searchRefferedDatum);
                            }
                        }
                        refreshTransporterNameRecyclerView();
                    } else {
                       /* if (apiResponse.getSuccessCode().equals("10001")) {
                            //redirectToDomain(apiResponse);
                            // CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10002")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            //  redirectToDomain(apiResponse);
                        } else if (apiResponse.getSuccessCode().equals("10003")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else if (apiResponse.getSuccessCode().equals("10004")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else if (apiResponse.getSuccessCode().equals("10005")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            //redirectToDomain(apiResponse);
                            //  CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            getDataInvoiceDataFromDB();
                            if (NetworkUtil.getConnectivityStatusString(getActivity()).equals("Not connected to Internet")) {
                                CustomisedToast.error(getActivity(), getString(R.string.not_connected_to_internet)).show();
                            } else if (invoiceList.size() > 0) {
                                CustomisedToast.error(getActivity(), getString(R.string.customer_support_error)).show();
                            } else {
                                logout();
                            }
                        } else if (apiResponse.getSuccessCode().equals("10007")) {
                            //redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10008")) {
                            // CustomisedToast.error(getActivity(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10009")) {
                            // CustomisedToast.error(getActivity(), getString(R.string.error_login_mac), Toast.LENGTH_SHORT).show();
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10010")) {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("100011")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10012")) {
                            // redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10013")) {
                            //  redirectToDomain(apiResponse);
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            CustomisedToast.error(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/

                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideSoftKeyboard(getActivity());

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null) {
                    //Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    // Log.d("error message",getString(R.string.connect_server_failed));
                    hideSoftKeyboard(getActivity());
                }
            }
        });
    }

    private void refreshTransporterNameRecyclerView() {
        //Refreshing data after getting input from openCustomerSearchDialog
        referredByTransporterNameAdapter.notifyDataSetChanged();

        transporterNameSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TransporterNameSearchDatum selected = (TransporterNameSearchDatum) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    transporterNameSearchEditText.setText(selected.getName().toString().trim());
                }

                transporterNameSearchEditText.setVisibility(View.VISIBLE);

                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    transporterNameSearchEditText.setText(selected.getName().toString().trim());
                    strTransportId = selected.getId();
                }

                //currentAddressLayout.setVisibility(View.VISIBLE);
                transporterNameSearchEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        transporterNameSearchEditText.setSelection(dialogCreateGatepassTransporterName.getText().toString().length());
                    }
                });


                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });


    }

    private void transportModeAdapter(int position) {
        try {
            transportMode = new ArrayList<>();
            if (dialogCreateGatepassTransportType.getSelectedItem().toString().equals(transportTypeList.get(position).getType())) {
                for (int y = 0; y < transportTypeList.get(position).getModes().size(); y++) {
                    transportMode.add(transportTypeList.get(position).getModes().get(y).getName());
                }

                if (transportTypeList != null && transportTypeList.size() > 0 && transportTypeList.get(position).getModes().get(0).getName() != null) {
                    dialogCreateGatepassTransportMode.setSelection(0);
                    strMode = transportTypeList.get(position).getModes().get(0).getPacdelgatpactid();
                }
            }
            arrayAdapterTransportMode = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, transportMode);
            arrayAdapterTransportMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogCreateGatepassTransportMode.setAdapter(arrayAdapterTransportMode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void tansportTypeAdapter() {
        List<String> transportType = new ArrayList<>();
        for (int i = 0; i < transportTypeList.size(); i++) {
            transportType.add(transportTypeList.get(i).getType());
        }
        if (transportTypeList != null && transportTypeList.size() > 0) {
            dialogCreateGatepassTransportType.setSelection(0);
        }
        ArrayAdapter arrayAdapterInvoiceType = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, transportType);
        arrayAdapterInvoiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCreateGatepassTransportType.setAdapter(arrayAdapterInvoiceType);

        if (arrayAdapterTransportMode != null) {
            arrayAdapterTransportMode.notifyDataSetChanged();
        }


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
                        hideSpinnerDropDown(dialogCreateGatepassShippingCompany);
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
                        hideSpinnerDropDown(dialogCreateGatepassShippingCompany);
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

    /**
     * Hides a spinner's drop down.
     */
    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCustomerClick(int position) {
        TransporterNameSearchDatum selected = transporterNameList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            dialogCreateGatepassTransporterName.setText(selected.getName().toString().trim());
            strTransportId = selected.getId();
        }

        dialogCreateGatepassTransporterName.setVisibility(View.VISIBLE);
        //currentAddressLayout.setVisibility(View.VISIBLE);
        dialogCreateGatepassTransporterName.post(new Runnable() {
            @Override
            public void run() {
                dialogCreateGatepassTransporterName.setSelection(dialogCreateGatepassTransporterName.getText().toString().length());
            }
        });


        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        dialogCreateGatepassTransporterName.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openTransporterNameSearchDialog();
        }
        //Enable Again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogCreateGatepassTransporterName.setEnabled(true);
            }
        }, 4000);
    }

    private void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onBackPressed() {
        getChildFragmentManager().popBackStack();
    }
}
