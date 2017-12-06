package com.accrete.warehouse.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.Set;

/**
 * Created by agt on 3/10/17.
 */

public class AppPreferences {
    private static String WAREHOUSE_APP = "warehouse_app";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(WAREHOUSE_APP,
                Context.MODE_PRIVATE);
    }

    public static String getWarehouseName(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setWarehouseName(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
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

    public static String getCompanyCode(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setCompanyCode(Context ctx, String key, String value) {
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
}