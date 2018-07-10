package com.accrete.sixorbit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.LeadShippingAddress;
import com.accrete.sixorbit.model.RecordFollowUp;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public List<Lead> leadList = new ArrayList<>();
    public List<Contacts> contactsList = new ArrayList<>();
    public List<LeadShippingAddress> leadShippingAddresses = new ArrayList<>();
    public List<FollowUp> leadFollowUps = new ArrayList<>();
    String strSchedule, strLineOne, strLineTwo, strCity, strZipcode, strContactedPerson, strSAddressId,
            strCommId, strCountry, strState, strAssignee, strAlertMode, strScheduleDate, strAlertDate, strSiteName;
    List<RecordFollowUp> recordFollowUps = new ArrayList<RecordFollowUp>();
    List<Comment> comments = new ArrayList<Comment>();
    private Context mContext;
    private DatabaseHandler db;
    private Lead lead = new Lead();
    private List<FollowUp> FollowUps = new ArrayList<>();

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        db = new DatabaseHandler(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            getAllLeadDataFromDB();
            //  getPostCommentResponse();
            getFollowUpDataFromDB();
            addRecordFollowUp();
        }
    }

    private void getFollowUpDataFromDB() {
        if (db != null) {
            FollowUps.clear();
            FollowUps.addAll(db.getAllfollowUp());

            for (int i = 0; i < FollowUps.size(); i++) {
                if (FollowUps.get(i).getSyncStatus() != null && FollowUps.get(i).getSyncStatus().equals("false") &&
                        FollowUps.get(i).getLeaid() != null && !FollowUps.get(i).getLeaid().isEmpty()
                        && !FollowUps.get(i).getLeaid().equals("null")) {
                    addFollowUp(FollowUps.get(i));
                }
            }
        }
    }

    private void getAllLeadDataFromDB() {
        if (db != null) {
            leadList.clear();
            leadList = db.getAllNonSyncLeads("false");
            Log.d("lead size", String.valueOf(leadList.size()));
        }

        if (leadList != null && leadList.size() > 0) {
            for (int i = 0; i < leadList.size(); i++) {
                Log.d("Lead network receiver", leadList.get(i).getLeadSync() + " "
                        + leadList.get(i).getLeaid() + " " + leadList.get(i).getSyncId());

                final int finalVariable = i;
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (leadList.get(finalVariable).getLeadSync() != null &&
                                        leadList.get(finalVariable).getLeaid() != null &&
                                        !leadList.get(finalVariable).getLeaid().isEmpty()
                                        && !leadList.get(finalVariable).getLeaid().equals("null")) {
                                    getSingleLeadData(leadList.get(finalVariable).getLeaid(), mContext.getString(R.string.leaid));
                                    sendEditLeadData();
                                } else if (leadList.get(finalVariable).getLeadSync() != null &&
                                        (leadList.get(finalVariable).getLeaid() == null ||
                                                leadList.get(finalVariable).getLeaid().isEmpty()
                                                || leadList.get(finalVariable).getLeaid().equals("null"))) {
                                    getSingleLeadData(leadList.get(finalVariable).getSyncId(), mContext.getString(R.string.syncID));
                                    sendAddLeadData();
                                }
                            }
                        },
                        15000 * i);
            }
        }
    }

    private void addFollowUp(final FollowUp followUp) {
        try {
            task = mContext.getString(R.string.follow_up_add);
            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.addFollowUp(version, key, task, userId, accessToken,
                    followUp.getLeadId(), followUp.getContactPerson(), followUp.getFollowupCommunicationMode(),
                    followUp.getAssignedUid(), followUp.getScheduledDate(),
                    followUp.getAlertOn(), followUp.getAlertMode(), followUp.getSyncId(), "0");
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse != null && apiResponse.getSuccess() != null) {
                        if (apiResponse.getSuccess()) {
                            followUp.setFoid(apiResponse.getData().getCurrentFollowUpId());
                            followUp.setSyncStatus(String.valueOf(true));
                            db.updateFollowUp(followUp);

                            //TODO Added on 12th June to refresh
                            getFollowUps();
                        }
                        //10006 is the error code of wrong access token
                    /*else if (apiResponse.getSuccessCode().equals("10006")) {
                        if (apiResponse.getMessage().equals("Schedule and/or alert times can't be empty")) {

                        } else {

                        }
                    }*/
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    //   CustomisedToast.error(mContext, mContext.getString(R.string.something_wrong)).show();
                    Log.d("NCR:errorInAddFollowUp", t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRecordFollowUp() {
        try {
            task = mContext.getString(R.string.record_follow_up_task);
            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
            }
            recordFollowUps = db.getFalseRecordFollowUp(false);
            for (int i = 0; i < recordFollowUps.size(); i++) {
                final RecordFollowUp recordFollowUp = recordFollowUps.get(i);
                if (recordFollowUp.getSync() == 0) {
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<ApiResponse> call = apiService.recordFollowUp(version, key, task, userId, accessToken, recordFollowUp.getFollowup_leadId(),
                            recordFollowUp.getEnId(), recordFollowUp.getQoId(), recordFollowUp.getChkId(), recordFollowUp.getCuId(),
                            recordFollowUp.getJocaId(), recordFollowUp.getSchdeuled_radio(), recordFollowUp.getScheduled_time(),
                            recordFollowUp.getAlert_time(), recordFollowUp.getCommunicated_mode(), recordFollowUp.getOutcome(),
                            recordFollowUp.getDescription(), recordFollowUp.getReason(), recordFollowUp.getAssignee_name(),
                            recordFollowUp.getContact_person(), recordFollowUp.getAlert_mode(), recordFollowUp.getComment(),
                            recordFollowUp.getContacted_person(), recordFollowUp.getCommunication_mode(), recordFollowUp.getSync_id(),
                            recordFollowUp.getFoId(), recordFollowUp.getNext_followup());
                    //strFromTime,strToTime,
                    Log.d("Request", String.valueOf(call));
                    Log.d("url", String.valueOf(call.request().url()));
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                            ApiResponse apiResponse = (ApiResponse) response.body();
                            if (apiResponse != null && apiResponse.getSuccess() != null) {
                                if (apiResponse.getSuccess()) {
                                    db.updateRecordFollowUps(recordFollowUp.getFoId(), recordFollowUp.getFollowup_leadId(), recordFollowUp.getEnId(),
                                            recordFollowUp.getQoId(), recordFollowUp.getChkId(), recordFollowUp.getCuId(), recordFollowUp.getJocaId(),
                                            recordFollowUp.getSchdeuled_radio(), recordFollowUp.getScheduled_time(), recordFollowUp.getAlert_time(),
                                            recordFollowUp.getCommunicated_mode(), recordFollowUp.getOutcome(), recordFollowUp.getDescription(),
                                            recordFollowUp.getReason(), recordFollowUp.getAssignee_name(), recordFollowUp.getContact_person(),
                                            recordFollowUp.getAlert_mode(), recordFollowUp.getComment(), recordFollowUp.getContacted_person(),
                                            recordFollowUp.getCommunication_mode(), true, recordFollowUp.getSync_id());

                                    //updateCount lead status
                               /* if (recordFollowUp.getOutcome().equals("1") || recordFollowUp.getOutcome().equals("4") || recordFollowUp.getOutcome().equals("5")) {
                                    if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                    db.updateLeadsReason("4", recordFollowUp.getFollowup_leadId());}
                                }*/

                                    if (recordFollowUp.getOutcome().equals("1") || recordFollowUp.getOutcome().equals("4") ||
                                            recordFollowUp.getOutcome().equals("5")) {
                                        if (recordFollowUp.getFollowup_leadId() != null &&
                                                db.getFollowupsCount(recordFollowUp.getFollowup_leadId()) == 0) {
                                            db.updateLeadsReason("4", recordFollowUp.getFollowup_leadId(), "true");
                                        } else {
                                            db.updateLeadsReason("2", recordFollowUp.getFollowup_leadId(), "true");
                                        }
                                    } else {
                                        db.updateLeadsReason("2", recordFollowUp.getFollowup_leadId(), "true");
                                    }

                                    //TODO Added on 12th June to refresh
                                    getFollowUps();

                                } else {
                                    Log.e("ERROR", apiResponse.getMessage());
                                    //  CustomisedToast.error(getActivity(), apiResponse.getMessage()).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            // Toast.makeText(mContext, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("errorInRecordFollowUP", t.getMessage());
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method to get the data
    private void getSingleLeadData(String id, String idCheck) {
        if (contactsList != null && contactsList.size() > 0) {
            contactsList.clear();
        }
        if (idCheck.equals(mContext.getString(R.string.syncID))) {
            lead = db.getLead(id, mContext.getString(R.string.syncID));
            contactsList.addAll(lead.getContacts());
            leadShippingAddresses.addAll(db.getLeadAddress(id, mContext.getString(R.string.syncID)));
            leadFollowUps.addAll(db.getLeadFollowUp(id, mContext.getString(R.string.syncID)));
        } else {
            lead = db.getLead(id, mContext.getString(R.string.leaid));
            contactsList.addAll(lead.getContacts());
            leadShippingAddresses.addAll(db.getLeadAddress(id, mContext.getString(R.string.leaid)));
            leadFollowUps.addAll(db.getLeadFollowUp(id, mContext.getString(R.string.leaid)));
        }


        if (leadShippingAddresses.size() > 0) {
            strSiteName = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getSiteName();
            strLineOne = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getLine1();
            strLineTwo = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getLine2();
            strCountry = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getCountry();
            strCity = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getCity();
            strState = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getState();
            strZipcode = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getZipCode();
            strSAddressId = leadShippingAddresses.get(leadShippingAddresses.size() - 1).getSaid();

        }
        if (leadFollowUps.size() > 0) {
            if (leadFollowUps.get(leadFollowUps.size() - 1).getScheduledDate() != null) {
                strAlertDate = leadFollowUps.get(leadFollowUps.size() - 1).getAlertOn();
                strScheduleDate = leadFollowUps.get(leadFollowUps.size() - 1).getScheduledDate();
                strContactedPerson = leadFollowUps.get(leadFollowUps.size() - 1).getContactedPerson();
                strCommId = leadFollowUps.get(leadFollowUps.size() - 1).getCommid();
                strAssignee = leadFollowUps.get(leadFollowUps.size() - 1).getAssignedUid();
                strAlertMode = leadFollowUps.get(leadFollowUps.size() - 1).getAlertMode();
                strSchedule = "1";
            } else {
                strSchedule = "2";
            }
        }
    }


    private void sendAddLeadData() {
        try {
            task = mContext.getString(R.string.lead_add);
            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
            }


            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            JSONArray jsonArrayContactList = new JSONArray();
            try {
                Log.e("Lead Contact List", contactsList.toString());
                if (contactsList != null && contactsList.size() > 0) {
                    for (int i = 0; i < contactsList.size(); i++) {
                        JSONObject contactList = new JSONObject();
                        contactList.put(mContext.getString(R.string.codeid), contactsList.get(i).getCodeid());
                        contactList.put(mContext.getString(R.string.name), contactsList.get(i).getName());
                        contactList.put(mContext.getString(R.string.email), contactsList.get(i).getEmail());
                        contactList.put(mContext.getString(R.string.phone), contactsList.get(i).getPhoneNo());
                        contactList.put(mContext.getString(R.string.designation), contactsList.get(i).getDesignation());
                        contactList.put("is_owner", contactsList.get(i).getIsOwner());
                        Log.e("name ", contactsList.toString());
                        jsonArrayContactList.put(contactList);
                    }
                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Call<ApiResponse> call = apiService.addLead(version, key, task, userId, accessToken, lead.getFname(), lead.getLname(), lead.getName(),
                    lead.getMobile(), lead.getEmail(), lead.getWebsite(), lead.getGenderid(), strSiteName, strLineOne, strLineTwo, strCountry
                    , strState, strCity, strZipcode, lead.getAssignedUid(),
                    lead.getSpecialInstructions(), jsonArrayContactList, strSchedule, strContactedPerson, strCommId
                    , strAssignee, strScheduleDate, strAlertDate, strAlertMode, lead.getSyncId(), lead.getLeadPersonType());
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse != null && apiResponse.getSuccess() != null) {
                        if (apiResponse.getSuccess()) {
                            lead.setLeaid(String.valueOf(apiResponse.getData().getLeaid()));
                            lead.setLeadId(apiResponse.getData().getLeadId());
                            lead.setLeadNumber(apiResponse.getData().getLeadNumber());
                            lead.setLeadSync("true");
                            if (leadFollowUps.size() > 0) {
                                leadFollowUps.get(0).setSyncId(lead.getSyncId());
                                leadFollowUps.get(0).setLeadId(lead.getLeaid());
                                leadFollowUps.get(0).setLeadIdR(lead.getLeadId());
                                leadFollowUps.add(leadFollowUps.get(0));
                                lead.setFollowups(leadFollowUps);
                                lead.setLeasid("2");
                            } else {
                                lead.setLeasid("1");
                            }


                            Log.d("leadid", lead.getLeaid());
                            db.updateLead(lead, mContext.getString(R.string.syncID));
                            db.deleteSyncLeadContacts(lead.getSyncId());
                            db.deleteSyncLeadAddress(lead.getSyncId());
                            db.deleteSyncLeadFollowUp(lead.getSyncId());


                            if (apiResponse.getData().getContacts() != null) {
                                for (Contacts contacts : apiResponse.getData().getContacts()) {
                                    if (lead.getContacts() != null) {

                                        //TODO - Added on 2nd May
                                        if (contacts.getCodeid() != null &&
                                                !contacts.getCodeid().isEmpty()) {
                                            List<FollowUp> upList = new ArrayList<>();
                                            upList.addAll(db.getSameContactPersonsFollowUps(contacts.getCodeid()));
                                            if (upList != null && upList.size() > 0) {
                                                for (int i = 0; i < upList.size(); i++) {
                                                    if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                        db.updateFollowUpContacts(contacts.getName(),
                                                                contacts.getPhoneNo(),
                                                                contacts.getEmail(),
                                                                contacts.getCodeid());
                                                    }
                                                }
                                            }
                                        }
                                        if (contacts != null) {
                                            db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId());
                                        }
                                    }
                                }
                            }
                            if (leadShippingAddresses.size() > 0) {
                                db.addLeadAddress(leadShippingAddresses.get(leadShippingAddresses.size() - 1), lead.getLeaid());
                            }
                            if (leadFollowUps.size() > 0) {
                                if (leadFollowUps.get(leadFollowUps.size() - 1).getFoid() != null &&
                                        !leadFollowUps.get(leadFollowUps.size() - 1).getFoid().isEmpty() &&
                                        db.checkFollowUpResult(leadFollowUps.get(leadFollowUps.size() - 1).getFoid())) {
                                    db.updateFollowUp(leadFollowUps.get(leadFollowUps.size() - 1));
                                } else {
                                    db.insertFollowUpData(leadFollowUps.get(leadFollowUps.size() - 1));
                                }
                            }

                            if (apiResponse.getData().getFollowupCodeid() != null) {
                                if (db.checkFollowUpLeadResult(lead.getSyncId())) {
                                    db.updateFollowUpContactsCodeId(apiResponse.getData().getFollowupCodeid(),
                                            lead.getSyncId());
                                }
                            }

                            //Refresh Follow Ups
                            getFollowUps();

                        }//10006 is the error code of wrong access token
                     /*else if (apiResponse.getSuccessCode().equals("10006")) {
                        if (apiResponse.getMessage().equals("Schedule and/or alert times can't be empty")) {
                            CustomisedToast.error(mContext, mContext.getString(R.string.uncheck_followup_details)).show();
                        } else {
                            CustomisedToast.error(mContext, apiResponse.getMessage()).show();
                        }
                    }*/
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d("errorInLead", t.getMessage() + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getFollowUps() {
        task = mContext.getString(R.string.follow_up_fetch);
        String lastUpdated;
        try {
            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getDomain(mContext, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }
            Call<ApiResponse> call = apiService.getFollowUp(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", "" + String.valueOf(call));
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", "" + String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    // bundle.putParcelable("followUp",apiResponse);
                    AsyncTask<Object, Object, Object> saveFollowUpsAsyncTask = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            if (apiResponse != null && apiResponse.getData().getFollowups() != null) {
                                for (final FollowUp followUp : apiResponse.getData().getFollowups()) {

                                    Log.d("Insert: ", "Inserting Follow Ups .." + followUp.getName());
                                    Log.d("Inside API Call Service", followUp.getContactPersonEmail() + " " + followUp.getContactPersonMobile());

                                    if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                        if (db.checkFollowUpResult(followUp.getFoid())) {
                                            db.updateFollowUp(followUp);
                                        } else {
                                            if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && db.checkSyncIdFollowUp(followUp.getSyncId())) {
                                                db.updateFollowUpDataSyncId(followUp);
                                            } else {

                                                //  followUp.setLeadLocalId(String.valueOf(db.getLead(followUp.getLeaid(),"leaid").getID()));
                                                if (db.getLead(followUp.getLeadId(), mContext.getString(R.string.leaid)).getContacts() != null
                                                        && db.getLead(followUp.getLeadId(),
                                                        mContext.getString(R.string.leaid)).getContacts().size() > 0) {
                                                    for (int i = 0; i < db.getLead(followUp.getLeadId(),
                                                            mContext.getString(R.string.leaid)).getContacts().size(); i++) {
                                                        if (db.getLead(followUp.getLeadId(),
                                                                mContext.getString(R.string.leaid)).getContacts().get(i).getName()
                                                                .equals(followUp.getContactedPerson())) {
                                                            followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(),
                                                                    mContext.getString(R.string.leaid)).getContacts()));

                                                        }
                                                    }
                                                }
                                                db.insertFollowUpData(followUp);


                                            }
                                        }
                                    } else {
                                        //  followUp.setLeadLocalId(String.valueOf(db.getLead(followUp.getLeaid(),"leaid").getID()));
                                        if (db.getLead(followUp.getLeadId(), mContext.getString(R.string.leaid)).getContacts() != null
                                                && db.getLead(followUp.getLeadId(), mContext.getString(R.string.leaid)).getContacts().size() > 0
                                                ) {
                                            for (int i = 0; i < db.getLead(followUp.getLeadId(),
                                                    mContext.getString(R.string.leaid)).getContacts().size(); i++) {
                                                if (db.getLead(followUp.getLeadId(),
                                                        mContext.getString(R.string.leaid)).getContacts().get(i).getName()
                                                        .equals(followUp.getContactedPerson())) {
                                                    followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(),
                                                            mContext.getString(R.string.leaid)).getContacts()));

                                                }
                                            }
                                        }
                                        Log.d("Follow up ID 4", "" + String.valueOf(db.getLead(followUp.getLeadId(),
                                                mContext.getString(R.string.leaid)).getID()));
                                        db.insertFollowUpData(followUp);
                                    }

                                }

                                SyncCheck syncCheck = new SyncCheck();
                                syncCheck.setService(mContext.getString(R.string.follow_up_fetch));
                                if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                    syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                                } else {
                                    syncCheck.setCallTime(db.getFollowUpUpdatedTs());
                                }

                                if (db != null) {
                                    db.addSyncCheck(syncCheck);
                                }

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                        }
                    };

                    saveFollowUpsAsyncTask.execute((Object[]) null);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendEditLeadData() {
        try {
            task = mContext.getString(R.string.lead_edit);

            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            JSONArray jsonArrayContactList = new JSONArray();

            try {
                Log.e("Lead Contact List", contactsList.toString());
                if (contactsList != null && contactsList.size() > 0) {
                    for (int i = 0; i < contactsList.size(); i++) {
                        JSONObject contactList = new JSONObject();
                        contactList.put(mContext.getString(R.string.codeid), contactsList.get(i).getCodeid());
                        contactList.put(mContext.getString(R.string.name), contactsList.get(i).getName());
                        contactList.put(mContext.getString(R.string.email), contactsList.get(i).getEmail());
                        contactList.put(mContext.getString(R.string.phone), contactsList.get(i).getPhoneNo());
                        contactList.put(mContext.getString(R.string.designation), contactsList.get(i).getDesignation());
                        contactList.put("is_owner", contactsList.get(i).getIsOwner());
                        Log.e("name ", contactsList.toString());
                        jsonArrayContactList.put(contactList);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Call<ApiResponse> call = apiService.editLead(version, key, task, userId, accessToken, lead.getFname(), lead.getLname(),
                    lead.getName(), lead.getMobile(), lead.getEmail(), lead.getWebsite(), lead.getGenderid(), strSiteName,
                    strLineOne, strLineTwo, strCountry, strState, strCity, strZipcode, lead.getAssignedUid(),
                    lead.getSpecialInstructions(), jsonArrayContactList, strSchedule, strContactedPerson, strCommId, strAssignee,
                    strScheduleDate, strAlertDate, strAlertMode, lead.getLeaid(), lead.getSyncId(), lead.getLeadPersonType(),
                    strSAddressId);


            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse != null && apiResponse.getSuccess() != null) {
                        if (apiResponse.getSuccess()) {
                            lead.setLeadSync("true");
                            if (leadFollowUps.size() > 0) {
                                leadFollowUps.get(0).setSyncId(lead.getSyncId());
                                leadFollowUps.get(0).setLeadId(lead.getLeaid());
                                leadFollowUps.add(leadFollowUps.get(0));
                                db.deleteLeadFollowUp(lead.getLeaid());
                            }
                            db.updateLead(lead, mContext.getString(R.string.leaid));
                            db.deleteLeadContacts(lead.getLeaid());
                            db.deleteLeadAddress(lead.getLeaid());

                            for (Contacts contacts : apiResponse.getData().getContacts()) {
                                if (lead.getContacts() != null) {

                                    //TODO - Added on 2nd May
                                    if (contacts.getCodeid() != null &&
                                            !contacts.getCodeid().isEmpty()) {
                                        List<FollowUp> upList = new ArrayList<>();
                                        upList.addAll(db.getSameContactPersonsFollowUps(contacts.getCodeid()));
                                        if (upList != null && upList.size() > 0) {
                                            for (int i = 0; i < upList.size(); i++) {
                                                if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                    db.updateFollowUpContacts(contacts.getName(),
                                                            contacts.getPhoneNo(),
                                                            contacts.getEmail(),
                                                            contacts.getCodeid());
                                                }
                                            }
                                        }
                                    }
                                    if (contacts != null) {
                                        db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId());
                                    }
                                }
                            }
                            if ((lead.getLname() == null || lead.getLname().isEmpty()) &&
                                    (lead.getFname() != null && !lead.getFname().isEmpty())) {
                                db.updateFollowUpName(lead.getFname() + "", lead.getLeaid());
                            } else if ((lead.getFname() == null || lead.getFname().isEmpty()) &&
                                    (lead.getLname() != null && !lead.getLname().isEmpty())) {
                                db.updateFollowUpName(lead.getLname() + "", lead.getLeaid());
                            } else if ((lead.getLname() != null && !lead.getLname().isEmpty()) &&
                                    (lead.getFname() != null && !lead.getFname().isEmpty())) {
                                db.updateFollowUpName(lead.getFname() + " " + lead.getLname(), lead.getLeaid());
                            }
                            if (leadShippingAddresses.size() > 0) {
                                db.addLeadAddress(leadShippingAddresses.get(leadShippingAddresses.size() - 1), lead.getLeaid());
                            }
                            if (leadFollowUps.size() > 0) {
                                if (leadFollowUps.get(0).getFoid() != null &&
                                        !leadFollowUps.get(0).getFoid().isEmpty() &&
                                        db.checkFollowUpResult(leadFollowUps.get(0).getFoid())) {
                                    db.updateFollowUp(leadFollowUps.get(0));
                                } else {
                                    db.insertFollowUpData(leadFollowUps.get(0));
                                }
                            }
                        }
                        //10006 is the error code of wrong access token
                    /*else if (apiResponse.getSuccessCode().equals("10006")) {
                        if (apiResponse.getMessage().equals("Schedule and/or alert times can't be empty")) {
                            CustomisedToast.error(mContext, mContext.getString(R.string.uncheck_followup_details)).show();
                        } else {
                            CustomisedToast.error(mContext, apiResponse.getMessage()).show();
                        }

                    }*/
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d("errorInLead", t.getMessage() + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //   Post Pending Comment API
    public void getPostCommentResponse() {
        task = mContext.getString(R.string.post_comment);
        if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
        }

        comments = db.getPendingComments();
        for (int i = 0; i < comments.size(); i++) {
            final Comment comment = comments.get(i);
            if (comment.getSync() == 0) {
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                String type = "";
                Call<ApiResponse> call = apiService.postFeedsComments(version, key, task, userId, accessToken, userId, comment.getUaid(),
                        comment.getComment(), "", comment.getSync_id());
                Log.d("Request", String.valueOf(call));
                Log.d("url", String.valueOf(call.request().url()));
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        // clear the inbox
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            Comment comment = new Comment();
                            comment.setSync_id(comment.getSync_id());
                            comment.setUaid(comment.getUaid());
                            comment.setSync(1);
                            db.updateActivityFeedsComments(comment);
                        } else {
                            Log.e("ERROR", apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                    }
                });
            }
        }
    }

}