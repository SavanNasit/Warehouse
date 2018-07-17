package com.accrete.sixorbit.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.CallLogDetails;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by poonam on 2/20/18.
 */

public class CallLogBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CallLogBroadcastReceiver";
    int count = 0;
    Context mContext;
    ArrayList<CallLogDetails> callLogDetailsArrayList = new ArrayList<CallLogDetails>();
    private String permission;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mContext = context;

            if (callLogDetailsArrayList != null && callLogDetailsArrayList.size() > 0) {
                callLogDetailsArrayList.clear();
            }
            //StringBuffer sb = new StringBuffer();

            getCallDetails();
            new NetworkAccess().execute();

            if (callLogDetailsArrayList != null && callLogDetailsArrayList.size() > 0) {
                AppPreferences.setLastCallLogTime(mContext,
                        AppUtils.CALL_LOG_LAST_TIME,
                        callLogDetailsArrayList.get(callLogDetailsArrayList.size() - 1).getCallDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCallDetails() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                permission = "2";
                return;
            } else {
                permission = "1";
            }
            Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = 0;
            managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {

                SimpleDateFormat inputFormat = new SimpleDateFormat
                        ("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                // Adjust locale and zone appropriately


                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);

                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String outputText = outputFormat.format(callDayTime);
                System.out.println(outputText);

                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int callTypeCode = Integer.parseInt(callType);

                switch (callTypeCode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "Outgoing";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "Incoming";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "Missed";
                        break;
                }

                if (AppPreferences.getLastCallLogTime(mContext, AppUtils.CALL_LOG_LAST_TIME) < Long.valueOf(callDate)) {
                    CallLogDetails callLogDetails = new CallLogDetails();
                    callLogDetails.setPhNumber(phNumber);
                    callLogDetails.setCallType(callType);
                    callLogDetails.setCallDate(Long.valueOf(callDate));
                    callLogDetails.setCallDayTime(outputText);
                    callLogDetails.setCallDuration(callDuration);
                    callLogDetails.setDir(dir);
                    callLogDetails.setCallTypeCode(callTypeCode);
                    callLogDetailsArrayList.add(callLogDetails);
                }

            }
            managedCursor.close();
            // callLogsTextView.setText(Html.fromHtml(sb.toString()));
            for (int i = 0; i < callLogDetailsArrayList.size(); i++) {
                Log.d("list", String.valueOf(callLogDetailsArrayList.get(i).getCallDate()));
            }
            // Toast.makeText(mContext, "Sending Call Data ..", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getString() {

        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArrayCallLog = new JSONArray();
            for (int i = 0; i < callLogDetailsArrayList.size(); i++) {
                JSONObject jsonCallLog = new JSONObject();
                jsonCallLog.put("phno", callLogDetailsArrayList.get(i).getPhNumber());
                jsonCallLog.put("callDuration", callLogDetailsArrayList.get(i).getCallDuration());
                jsonCallLog.put("callDateTime", callLogDetailsArrayList.get(i).getCallDayTime());
                jsonCallLog.put("callType", callLogDetailsArrayList.get(i).getCallTypeCode());
                jsonArrayCallLog.put(jsonCallLog);
            }

            jsonObject.put("calls", jsonArrayCallLog);
            jsonObject.put("permission", permission);
            String postParams = "data=" + jsonObject.toString();
            Log.d("POST_UPDATE", postParams);
            URL obj = null;
            HttpURLConnection con = null;
            try {
                obj = new URL(AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=1.0&key=123&task=" + mContext.getString(R.string.send_call_log)
                        + "&user_id=" + AppPreferences.getUserId(mContext, AppUtils.USER_ID)
                        + "&access_token=" + AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN)
                );
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                // For POST only - BEGIN
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                // For POST only - END
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("REQUEST", response.toString());
                    Log.e("RESPONSE", response.toString());

                    return response.toString();

                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class NetworkAccess extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // call some loader
        }

        @Override
        protected String doInBackground(Void... params) {
            // Do background task
            return getString();
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (result != null) {
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("result_code");
                    if (status) {
                        Log.d("call Log", "success" + message);
                    } else {
                        Log.d("call Log", "false" + message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}