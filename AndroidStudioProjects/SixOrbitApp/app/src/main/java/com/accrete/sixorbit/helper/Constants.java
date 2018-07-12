package com.accrete.sixorbit.helper;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.domain.DomainActivity;
import com.accrete.sixorbit.service.ChatService;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by poonam on 12/4/17.
 */

public class Constants {

    public static String version = "1.0";
    /* public static String key = "123";
     public static String task = "";
     public static String userId = "1";
     public static String accessToken = "5385106138734428160";*/
    public static String key = "123";
    public static String task = "";
    public static String userId = "";
    public static String accessToken = "";
    public static DatabaseHandler databaseHandler;
    public static String WRONG_CREDENTIALS = "10005";
    public static String INVALID_ACCESSTOKEN = "10006";

    //public static List<FollowupCommunicationMode> communicationModesList = new ArrayList<>();
    public static String removeComma(String str) {
        if (!str.isEmpty()) {
            if (str.contains(",")) {
                str.replaceAll(",", "");
            }
        }
        return str;
    }

    public static boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static void logoutWrongCredentials(Activity activity, String message) {
        databaseHandler = new DatabaseHandler(activity);

        if (message != null && !message.isEmpty()) {
            CustomisedToast.error(activity, message).show();
        }

        AppPreferences.setModesList(activity, AppUtils.FOLLOWUPS_COMMUNICATION_MODE, null);
        AppPreferences.setIsLogin(activity, AppUtils.ISLOGIN, false);
        AppPreferences.setIsUserFirstTime(activity, AppUtils.USER_FIRST_TIME, false);
        AppPreferences.setNotificationRead(activity, AppUtils.NOTIFICATION_READ, null);
        AppPreferences.setNotificationReadTime(activity, AppUtils.NOTIFICATION_READ_TIME, null);
        AppPreferences.setSearchHistory(activity, AppUtils.SEARCH_HISTORY, null);
        AppPreferences.setUserId(activity, AppUtils.USER_ID, null);
        AppPreferences.setUserSessionId(activity, AppUtils.USER_SESSION_ID, null);
        AppPreferences.setUserName(activity, AppUtils.USER_NAME, null);

        AppPreferences.setEmail(activity, AppUtils.USER_EMAIL, null);
        AppPreferences.setPhoto(activity, AppUtils.USER_PHOTO, "null");
        AppPreferences.setLeadsCount(activity, AppUtils.LEADS_COUNT, null);
        AppPreferences.setFollowupsCount(activity, AppUtils.FOLLOWUPS_COUNT, null);

        AppPreferences.setUserRealName(activity, AppUtils.USER_REAL_NAME, null);
        // AppPreferences.setUserAddress(activity, AppUtils.USER_ADDRESS, null);
        AppPreferences.setUserLastseen(activity, AppUtils.USER_LAST_SEEN, null);
        AppPreferences.setUserJoined(activity, AppUtils.USER_JOINED, null);
        AppPreferences.setUserEmployeeId(activity, AppUtils.USER_EMP_ID, null);
        AppPreferences.setUserReportTo(activity, AppUtils.USER_REPORT_TO, null);
        AppPreferences.setUserDesignation(activity, AppUtils.USER_DESIGNATION, null);
        AppPreferences.setProfileRoles(activity, AppUtils.USER_ROLES, null);
        AppPreferences.setUserName(activity, AppUtils.USER_NAME, null);
        AppPreferences.setUserDateOfBirth(activity, AppUtils.USER_DOB, null);
        AppPreferences.setUserAddressLine1(activity, AppUtils.USER_ADDRESS_LINE_ONE, null);
        AppPreferences.setUserAddressLine2(activity, AppUtils.USER_ADDRESS_LINE_TWO, null);
        AppPreferences.setUserState(activity, AppUtils.USER_STATE, null);

        AppPreferences.setuserBio(activity, AppUtils.USER_BIO, null);
        AppPreferences.setUserLocality(activity, AppUtils.USER_LOCALITY, null);
        AppPreferences.setUserCity(activity, AppUtils.USER_CITY, null);
        AppPreferences.setUserCountry(activity, AppUtils.USER_COUNTRY, null);
        AppPreferences.setUserZipCode(activity, AppUtils.USER_ZIP_CODE, null);
        AppPreferences.setTwoStepVerificationStatus(activity, AppUtils.TWO_STEP_VERIFICATION_STATUS, null);
        AppPreferences.setBoolean(activity, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);
        AppPreferences.setUserCountryCode(activity, AppUtils.USER_COUNTRY_CODE, null);
        AppPreferences.setUserStateCode(activity, AppUtils.USER_STATE_CODE, null);
        AppPreferences.setUserCityCode(activity, AppUtils.USER_CITY_CODE, null);
        AppPreferences.setLastCallLogTime(activity, AppUtils.CALL_LOG_LAST_TIME, 0);
        AppPreferences.setString(activity, AppUtils.COMPANY_NAME, null);
        AppPreferences.setString(activity, AppUtils.COMPANY_ID, null);

        databaseHandler.deleteDatabase();

        Intent intent_receiver = new Intent(activity.getString(R.string.logout_user_broadcast_event));
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent_receiver);

        //Delete files from local folder
        File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
        final File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + activity.getString(R.string.app_name));
        Thread t = new Thread(new Runnable() {
            public void run() {
                deleteRecursive(dir);
            }
        });
        t.start();

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) activity.getSystemService(ns);
        nMgr.cancelAll();
        Intent intent_stop_service = new Intent(activity, ChatService.class);
        activity.stopService(intent_stop_service);
        if (message != null && !message.isEmpty()) {
            Intent intentDomain = new Intent(activity, DomainActivity.class);
            activity.startActivity(intentDomain);
            activity.finish();
        }
    }

    public static void deleteRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                deleteRecursive(temp);
                //Log.e("Deleted", temp + " file");
            }
        }
        if (dir.delete() == false) {
            //Log.d("DeleteRecursive", "DELETE FAIL");
        }
    }

    //To deal with empty string of amount
    public static double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static double removeDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat();
        twoDForm.setDecimalSeparatorAlwaysShown(false);
        return Double.valueOf(twoDForm.format(d));
    }
}
