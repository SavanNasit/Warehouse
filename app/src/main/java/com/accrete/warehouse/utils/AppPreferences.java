package com.accrete.warehouse.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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


    public static String getWarehouseDefaultName(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setWarehouseDefaultName(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getWarehouseDefaultCheckId(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    public static void setWarehouseDefaultCheckId(Context ctx, String key, String value) {
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

    public static String getWarehouseOrderCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setWarehouseOrderCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static String getWarehousePackageCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setWarehousePackageCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static String getWarehouseGatepassCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setWarehouseGatepassCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getWarehouseConsignmentCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setWarehouseConsignmentCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getWarehouseReceiveConsignmentCount(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, " ");
    }


    public static void setWarehouseReceiveConsignmentCount(Context ctx, String key, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static double roundTwoDecimals(double totalQty) {
      /*  BigDecimal totalBigDecimal = new BigDecimal(totalQty);
        totalBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return  totalBigDecimal.doubleValue();*/

        DecimalFormat df = new DecimalFormat("#.####");
        totalQty = Double.parseDouble(df.format(totalQty));
        return totalQty;
    }
}