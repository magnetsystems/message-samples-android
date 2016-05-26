package com.magnet.magnetchat.core.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.magnet.magnetchat.util.Logger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class InternetConnectionManager {

    private static InternetConnectionManager instance;

    private ConnectivityManager connectivityManager;

    private InternetConnectionManager(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static InternetConnectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new InternetConnectionManager(context);
        }
        return instance;
    }

    public static InternetConnectionManager getInstance() {
        if (instance == null) {
            throw new Error("No context for instance");
        }
        return instance;
    }

    public boolean isAnyConnectionAvailable() {
        return (isWiFiAvailable() || isMobileInternetAvailable());
    }

    public boolean isMobileInternetAvailable() {
        return isSomeConnectionAvailable(ConnectivityManager.TYPE_MOBILE);
    }

    public boolean isWiFiAvailable() {
        return isSomeConnectionAvailable(ConnectivityManager.TYPE_WIFI);
    }

    private boolean isSomeConnectionAvailable(int type) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == type;
    }
}
