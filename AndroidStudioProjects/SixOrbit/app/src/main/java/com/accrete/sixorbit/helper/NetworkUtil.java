package com.accrete.sixorbit.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.accrete.sixorbit.R;

/**
 * Created by poonam on 27/6/17.
 */

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = "";
        if (context != null) {
            if (conn == NetworkUtil.TYPE_WIFI) {
                status = context.getString(R.string.wifi_enabled);
            } else if (conn == NetworkUtil.TYPE_MOBILE) {
                status = context.getString(R.string.mobile_enabled);
            } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
                status = context.getString(R.string.not_connected_to_internet);
                //  CustomisedToast.error(context,status).show();

            } else {
                // CustomisedToast.error(context, context.getString(R.string.not_connected_to_internet)).show();
                Toast.makeText(context, context.getString(R.string.not_connected_to_internet), Toast.LENGTH_SHORT).show();
            }
        }
        return status;
    }
}