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

    public String downloadFile(String reference, String pathOnSd, String fileName) throws IOException {
        if (isAnyConnectionAvailable()) {
            System.setProperty("http.keepAlive", "false");
            URL url = new URL(reference);
            try {
                BufferedInputStream reader = new BufferedInputStream(url.openStream());
                File file = new File(pathOnSd, url.getFile());
                if (!file.exists()) {
                    Logger.debug("downloadFile", file);
                    FileOutputStream writer = new FileOutputStream(file);
                    final byte data[] = new byte[1024];
                    int count;
                    while ((count = reader.read(data, 0, 1024)) != -1) {
                        writer.write(data, 0, count);
                    }
                    reader.close();
                    writer.close();
                }
                return file.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void downloadFileInThread(final String reference, final String pathOnSd, final String fileName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadFile(reference, pathOnSd, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
