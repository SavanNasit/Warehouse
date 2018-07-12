package com.accrete.warehouse.utils;


import com.accrete.warehouse.BuildConfig;

/**
 * Created by poonam on 12/4/17.
 */

public class Constants {

    public static String version = BuildConfig.VERSION_NAME;
    /* public static String key = "123";
     public static String task = "";
     public static String userId = "1";
     public static String accessToken = "5385106138734428160";*/
    public static String key = "123";
    public static String task = "";
    public static String userId = "";
    public static String accessToken = "";

    public static boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

}
