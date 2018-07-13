package com.accrete.sixorbit.service;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ContactPerson;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 5/7/18.
 */

public class ContactPersonsAPI {
    private Activity activity;
    private DatabaseHandler databaseHandler;

    public ContactPersonsAPI(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            databaseHandler = new DatabaseHandler(activity);
        }
    }

    public void getCustomersContactPersons(Activity activity, String qoId, String leaId, String enId,
                                           String chkOId, String cuId, String jocaId, String purOrId) {
        try {
            task = activity.getString(R.string.followups_contact_person);
            if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.getCustomersContactPersons(version, key, task,
                    userId, accessToken, qoId, leaId, enId, chkOId, cuId, jocaId, purOrId);
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {

                        if (apiResponse.getSuccess()) {
                            if (apiResponse.getData().getContactPersonArr() != null) {
                                for (ContactPerson contactPerson : apiResponse.getData().getContactPersonArr()) {
                                    if (contactPerson != null) {
                                        if (contactPerson.getCuid() != null && !contactPerson.getCuid().isEmpty()) {
                                            if (databaseHandler != null) {
                                                databaseHandler.deleteCustomersContactPersons(contactPerson.getCuid());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (apiResponse.getData().getContactPersonArr() != null) {
                                for (ContactPerson contactPerson : apiResponse.getData().getContactPersonArr()) {
                                    if (contactPerson != null) {
                                        if (databaseHandler != null &&
                                                !databaseHandler.checkCustomersContactPersonResult(contactPerson.getCodeid())) {
                                            Contacts contacts = new Contacts();
                                            contacts.setCodeid(contactPerson.getCodeid());
                                            contacts.setName(contactPerson.getName());
                                            contacts.setCuId(contactPerson.getCuid());
                                            databaseHandler.insertCustomersContactPersons(contacts);
                                        } else if (databaseHandler != null &&
                                                databaseHandler.checkCustomersContactPersonResult(contactPerson.getCodeid())) {
                                            Contacts contacts = new Contacts();
                                            contacts.setCodeid(contactPerson.getCodeid());
                                            contacts.setName(contactPerson.getName());
                                            contacts.setCuId(contactPerson.getCuid());
                                            databaseHandler.updateCustomersContactPersons(contacts);
                                        }
                                    }
                                }
                            }

                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(activity, apiResponse.getMessage());
                        } else {
                            //Toast.makeText(activity, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (activity != null) {
                        Toast.makeText(activity, activity.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
