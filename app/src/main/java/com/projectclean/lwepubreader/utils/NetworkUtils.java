package com.projectclean.lwepubreader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Carlos Albaladejo Pérez on 08/03/2016.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(Context pcontext) {
        ConnectivityManager connectivityManager  = (ConnectivityManager) pcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
