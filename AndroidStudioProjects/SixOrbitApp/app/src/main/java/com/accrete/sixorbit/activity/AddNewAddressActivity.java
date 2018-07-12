package com.accrete.sixorbit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.AddressList;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CityName;
import com.accrete.sixorbit.model.CountryList;
import com.accrete.sixorbit.model.StateList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 12/26/17.
 */

public class AddNewAddressActivity extends AppCompatActivity {
    public String status, strCuid, strLineOne, strLineTwo, strCountryId,
            strStateId, strCityId, strPincode, strFirstName, strLastName, strMobile, strOfficeName, strType;
    private Toolbar addAddressToolbar;
    private LinearLayout addAddressMain;
    private TextInputEditText addAddressOfficeName;
    private EditText addAddressFirstName;
    private EditText addAddressLastName;
    private TextView addAddressContactNumber;
    private EditText addAddressPhoneNumber;
    private TextInputEditText addAddressLineOne;
    private TextInputEditText addAddressLineTwo;
    private LinearLayout addAddressCountryMainLayout;
    private TextView addAddressCountryTitle;
    private TextView addAddressStateTitle;
    private AutoCompleteTextView addAddressCountry;
    private AutoCompleteTextView addAddressState;
    private AutoCompleteTextView addAddressCity;
    private TextInputEditText addAddressPincode;
    private TextView addAddressType;
    private Spinner addAddressTypeSpinner;
    private TextView addAddressSave;
    private String addressCountryCode, addressStateCode, defaultAddressCountryCode, defaultAddressStateCode, addressType;
    private List<StateList> stateListArrayList = new ArrayList<>();
    private List<CountryList> countryListArrayList = new ArrayList<>();
    private String[] addressTypeArray = new String[]{"Current Address", "Delivery Address", "Both"};
    private ArrayList<AddressList> addressListArrayList = new ArrayList<>();
    private AddressList addressList = new AddressList();
    private List<CityName> cityNameArrayList = new ArrayList<>();
    private ArrayAdapter<CityName> cityNameArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        strCuid = getIntent().getStringExtra("cuid");
        addressType = getIntent().getStringExtra("addressType");
        findViews();
    }


    private void findViews() {
        addAddressToolbar = (Toolbar) findViewById(R.id.add_address_toolbar);
        addAddressToolbar.setTitle(getString(R.string.add_new_address));
        addAddressToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(addAddressToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addAddressMain = (LinearLayout) findViewById(R.id.add_address_main);
        addAddressOfficeName = (TextInputEditText) findViewById(R.id.add_address_office_name);
        addAddressFirstName = (EditText) findViewById(R.id.add_address_first_name);
        addAddressLastName = (EditText) findViewById(R.id.add_address_last_name);
        addAddressContactNumber = (TextView) findViewById(R.id.add_address_contact_number);
        addAddressPhoneNumber = (EditText) findViewById(R.id.add_address_phone_number);
        addAddressLineOne = (TextInputEditText) findViewById(R.id.add_address_line_one);
        addAddressLineTwo = (TextInputEditText) findViewById(R.id.add_address_line_two);
        addAddressCountryMainLayout = (LinearLayout) findViewById(R.id.add_address_country_main_layout);
        addAddressCountryTitle = (TextView) findViewById(R.id.add_address_country_title);
        addAddressStateTitle = (TextView) findViewById(R.id.add_address_state_title);
        addAddressCountry = (AutoCompleteTextView) findViewById(R.id.add_address_country);
        addAddressState = (AutoCompleteTextView) findViewById(R.id.add_address_state);
        addAddressCity = (AutoCompleteTextView) findViewById(R.id.add_address_city);
        addAddressPincode = (TextInputEditText) findViewById(R.id.add_address_pincode);
        addAddressType = (TextView) findViewById(R.id.add_address_type);
        addAddressTypeSpinner = (Spinner) findViewById(R.id.add_address_type_spinner);
        addAddressSave = (TextView) findViewById(R.id.add_address_save);
        addAddressToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        addAddressToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String colored = " *";

        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Country
        addAddressCountryTitle.setHint(TextUtils.concat("Country", spannableStringBuilder));
        addAddressCountry.setHint(TextUtils.concat("Country", spannableStringBuilder));
        addAddressCountry.setHint(TextUtils.concat("Country"));

        //State
        addAddressStateTitle.setHint(TextUtils.concat("State", spannableStringBuilder));
        addAddressState.setHint(TextUtils.concat("State"));
        addAddressState.setHint(TextUtils.concat("State"));
        getCommonData();

        addAddressSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInList();
/*
                Intent resultIntent = new Intent();
                resultIntent.putExtra("addressResult", String.valueOf(addressList));
                setResult(300, resultIntent);*/

            }
        });
    }

    private void addInList() {
        addressList.setSiteName(addAddressOfficeName.getText().toString());
        addressList.setCity(addAddressCity.getText().toString());
        addressList.setCountry(addressCountryCode);
        addressList.setLine1(addAddressLineOne.getText().toString());
        addressList.setLine2(addAddressLineTwo.getText().toString());
        addressList.setState(addressStateCode);
        addressList.setZipCode(addAddressPincode.getText().toString());
        addressList.setContactFirstName(addAddressFirstName.getText().toString());
        addressList.setContactLastName(addAddressLastName.getText().toString());
        addressList.setContactMobileNumber(addAddressPhoneNumber.getText().toString());
        addressListArrayList.add(addressList);

        if (addAddressPhoneNumber.getText().toString().trim() != null &&
                addAddressPhoneNumber.getText().toString().length() > 0 && addAddressPhoneNumber.getText().toString().length() != 10) {
            Toast.makeText(this, "Phone number should be of 10 digits", Toast.LENGTH_SHORT).show();
        } else if (addressCountryCode == null || addressCountryCode.isEmpty()) {
            Toast.makeText(this, "Please select country", Toast.LENGTH_SHORT).show();
        } else if (addressStateCode == null || addressStateCode.isEmpty()) {
            Toast.makeText(this, "Please select state", Toast.LENGTH_SHORT).show();
        } else if (addAddressPincode.getText().toString().trim() != null &&
                addAddressPincode.getText().toString().length() > 0 && addAddressPincode.getText().toString().length() != 6) {
            Toast.makeText(this, "Pin code should be of 6 digits", Toast.LENGTH_SHORT).show();
        } else {
            saveAddAddress(addressList);
        }
    }


    public void getCommonData() {
        task = getString(R.string.add_customer_dynamic_fields);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {

                        for (final CountryList countryList : apiResponse.getData().getCountryList()) {
                            if (countryList != null) {
                                countryListArrayList.add(countryList);
                            }
                        }

                        for (final StateList stateList : apiResponse.getData().getStateList()) {
                            if (stateList != null) {
                                stateListArrayList.add(stateList);
                            }
                        }

                        //defaultGroupId = apiResponse.getData().getDefaultAccountGroupId();
                        defaultAddressCountryCode = apiResponse.getData().getDefaultCountry();
                        defaultAddressStateCode = apiResponse.getData().getDefaultState();
                        //Spinners
                        setSpinnerAdapter();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(AddNewAddressActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(AddNewAddressActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddNewAddressActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSpinnerAdapter() {


        final ArrayAdapter<CountryList> countryArrayAdapter = new ArrayAdapter<CountryList>
                (AddNewAddressActivity.this, R.layout.simple_spinner_item, countryListArrayList);
        countryArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        addAddressCountry.setAdapter(countryArrayAdapter);

        //when autocomplete is clicked
        addAddressCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = countryArrayAdapter.getItem(position).toString();
                addAddressCountry.setText(selectedItem);
                addAddressCountry.setSelection(selectedItem.length());
                int pos = -1;
                for (int i = 0; i < countryListArrayList.size(); i++) {
                    if (countryListArrayList.get(i).getCountryName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressCountryCode = countryListArrayList.get(pos).getCountryId();
            }
        });


        addAddressCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressCountry.showDropDown();
            }
        });


        addAddressCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strCountryId = addAddressCountry.getText().toString();
                    for (int i = 0; i < countryListArrayList.size(); i++) {
                        String temp = countryListArrayList.get(i).getCountryName();
                        if (strCountryId.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    addAddressCountry.setText("");
                    addressCountryCode = "";
                }
            }

        });


        //State Address

        addAddressState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressState.showDropDown();
            }
        });


        addAddressState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strStateId = addAddressState.getText().toString();
                    for (int i = 0; i < stateListArrayList.size(); i++) {
                        String temp = stateListArrayList.get(i).getStateName();
                        if (strStateId.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    addAddressState.setText("");
                    strStateId = "";
                }
            }

        });
        final ArrayAdapter<StateList> stateListArrayAdapter =
                new ArrayAdapter<StateList>(this, R.layout.simple_spinner_item, stateListArrayList);
        stateListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        addAddressState.setAdapter(stateListArrayAdapter);

        addAddressState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = stateListArrayAdapter.getItem(position).toString();
                addAddressState.setText(selectedItem);
                addAddressState.setSelection(selectedItem.length());
                int pos = -1;
                for (int i = 0; i < stateListArrayList.size(); i++) {
                    if (stateListArrayList.get(i).getStateName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressStateCode = stateListArrayList.get(pos).getStateId();
            }
        });


        //Address Type Adapter
        ArrayAdapter<String> addressArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.simple_spinner_item, addressTypeArray);
        addressArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        addAddressTypeSpinner.setAdapter(addressArrayAdapter);
        addAddressTypeSpinner.setSelection(addressTypeArray.length - 1);


        //Country
        addAddressCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressCountry.showDropDown();
            }
        });

        //State
        addAddressState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressState.showDropDown();
            }
        });


        //Set City

        //City
        addAddressCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressCity.showDropDown();
            }
        });

        //when autocomplete is clicked
        addAddressCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = cityNameArrayAdapter.getItem(position).toString();
                addAddressCity.setText(selectedItem);
                addAddressCity.setSelection(selectedItem.length());
                int pos = -1;
                for (int i = 0; i < cityNameArrayList.size(); i++) {
                    if (cityNameArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strCityId = cityNameArrayList.get(pos).getId();
            }
        });

        addAddressCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (int i = 0; i < cityNameArrayList.size(); i++) {
                        String temp = cityNameArrayList.get(i).getName();
                        if (addAddressCity.getText().toString().compareTo(temp) == 0) {
                            return;
                        }
                    }
                    addAddressCity.setText("");
                    strCityId = "";
                }
            }

        });
        addAddressCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status = NetworkUtil.getConnectivityStatusString(AddNewAddressActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    if (addressCountryCode != null && !addressCountryCode.isEmpty() &&
                            addressStateCode != null && !addressStateCode.isEmpty()) {
                        getAddressCity(addressCountryCode, addressStateCode, s.toString());
                    } else {
                        getAddressCity(defaultAddressCountryCode, defaultAddressStateCode, s.toString());
                    }
                } else {
                    Toast.makeText(AddNewAddressActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void getAddressCity(String countryId, String stateId, String searchText) {
        task = getString(R.string.add_customer_get_cities);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCities(version, key, task, userId, accessToken, countryId,
                stateId, searchText);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (cityNameArrayList != null && cityNameArrayList.size() > 0) {
                            cityNameArrayList.clear();
                        }
                        for (final CityName cityName : apiResponse.getData().getCityNames()) {
                            if (cityName != null) {
                                cityNameArrayList.add(cityName);
                            }
                        }

                        cityNameArrayAdapter =
                                new ArrayAdapter<CityName>(AddNewAddressActivity.this,
                                        R.layout.simple_spinner_item, cityNameArrayList);
                        cityNameArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        addAddressCity.setAdapter(cityNameArrayAdapter);
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(AddNewAddressActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(AddNewAddressActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddNewAddressActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void saveAddAddress(AddressList addressList) {
        task = getString(R.string.add_address_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        strLineOne = addressList.getLine1();
        strLineTwo = addressList.getLine2();
        strCountryId = addressList.getCountry();
        strStateId = addressList.getState();
        strPincode = addressList.getZipCode();
        strFirstName = addressList.getContactFirstName();
        strLastName = addressList.getContactLastName();
        strMobile = addressList.getContactMobileNumber();
        strOfficeName = addressList.getSiteName();
        strType = String.valueOf((addAddressTypeSpinner.getSelectedItemPosition() + 1));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.requestAddNewAddress(version, key, task, userId, accessToken, strCuid, strLineOne, strLineTwo, strCountryId,
                strStateId, strCityId, strPincode, strFirstName, strLastName, strMobile, strOfficeName, strType);

        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Toast.makeText(AddNewAddressActivity.this, "Address successfully added", Toast.LENGTH_SHORT).show();
                        String newAddress = apiResponse.getData().getSaid();
                        String cuid = apiResponse.getData().getCuid();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("said", newAddress);
                        resultIntent.putExtra("cuid", cuid);
                        resultIntent.putExtra("addressType", addressType);
                        setResult(300, resultIntent);
                        finish();
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(AddNewAddressActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(AddNewAddressActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddNewAddressActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
