package com.accrete.sixorbit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by agt on 3/10/17.
 */

public class AppPreferences {
    private static String SIX_ORBIT_APP = "six_orbit_app";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SIX_ORBIT_APP,
                Context.MODE_PRIVATE);
    }

    public static Boolean getIsLogin(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void setIsLogin(Context ctx, String key, Boolean value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static Boolean getBoolean(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void setBoolean(Context ctx, String key, Boolean value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static String getUserId(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setUserId(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getAccessToken(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setAccessToken(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getUserSessionId(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setUserSessionId(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getEmail(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setEmail(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getUserName(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setUserName(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getPhoto(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");

    }

    public static void setPhoto(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getLeadsCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setLeadsCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getFollowupsCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setFollowupsCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getDomain(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setDomain(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getLastDomain(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setLastDomain(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static Set<String> getSearchHistory(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getStringSet(key, null);
    }

    public static void setSearchHistory(Context ctx, String key, Set<String> value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putStringSet(key, value);
        edit.commit();
    }

    public static Boolean getIsUserFirstTime(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void setIsUserFirstTime(Context ctx, String key, Boolean value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static long getLastCallLogTime(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getLong(key, 0);
    }

    public static void setLastCallLogTime(Context ctx, String key, long value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putLong(key, value);
        edit.commit();
    }



    public static String getNotificationRead(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setNotificationRead(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getNotificationReadTime(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setNotificationReadTime(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setCompanyCode(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setuserBio(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserZipCode(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserCity(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserState(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserCountry(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserLocality(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getCompanyCode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }

    public static void setUserMobile(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setProfileUserStatusId(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserDateOfBirth(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserEmployeeId(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserReportTo(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserDesignation(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserJoined(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserLastseen(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserRealName(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setProfileRoles(Context ctx, String key, List<String> value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, String.valueOf(value));
        edit.commit();
    }

    public static void setUserCountryCode(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, String.valueOf(value));
        edit.commit();
    }

    public static void setUserStateCode(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, String.valueOf(value));
        edit.commit();
    }


    public static void setUserCityCode(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, String.valueOf(value));
        edit.commit();
    }


  /*  public static void setUserAddress(Context ctx, String key, List<String> value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, String.valueOf(value));
        edit.commit();
    }*/

    public static String getUserMobile(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }

    public static String getProfileUserstatusId(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }

    public static String getUserDateOfBirth(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }
/*
    public static String getUserAddress(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");

    }*/

    public static String getUserEmployeeID(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }

    public static String getUserReportTo(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserDesignation(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserJoined(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserLastSeen(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserRealName(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserRoles(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserBio(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserCountry(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserCity(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserState(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserLocality(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserZipcode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static int getNotificationNumber(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt(key, 0);
    }

    public static void setNotificationNumber(Context ctx, String key, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getFeedNotificationNumber(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt(key, 0);
    }

    public static void setFeedNotificationNumber(Context ctx, String key, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getChatNotificationNumber(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt(key, 0);
    }

    public static void setChatNotificationNumber(Context ctx, String key, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void setUserAddressLine1(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setUserAddressLine2(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getUserStateCode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserCountryCode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserCityCode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserAddressLine1(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static String getUserAddressLine2(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static void setTwoStepVerificationStatus(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getTwoStepVerificationStatus(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "false");
    }

    public static List<FollowupCommunicationMode> getModes(Context ctx, String key) {
        Gson gson = new Gson();
        List<FollowupCommunicationMode> followupCommunicationModes = new ArrayList<>();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        String jsonPreferences = preference.getString(key, null);

        Type type = new TypeToken<List<FollowupCommunicationMode>>() {
        }.getType();
        followupCommunicationModes = gson.fromJson(jsonPreferences, type);
        return followupCommunicationModes;
    }

    public static void setModesList(Context ctx, String key, List<FollowupCommunicationMode> communicationMode) {
        Gson gson = new Gson();
        String json = gson.toJson(communicationMode);

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, json);
        edit.commit();
    }

    public static String getString(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setString(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }
}