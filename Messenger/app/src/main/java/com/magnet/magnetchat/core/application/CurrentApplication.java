package com.magnet.magnetchat.core.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.core.managers.MMXManager;
import com.magnet.magnetchat.core.managers.SharedPreferenceManager;
import com.magnet.magnetchat.core.managers.TypeFaceManager;

import io.fabric.sdk.android.Fabric;

public class CurrentApplication extends MultiDexApplication {

    private static CurrentApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        onLibrariesInitialization();
        onManagersInitialization();
    }

    /**
     * Method which provide the libraries initialization
     */
    private void onLibrariesInitialization() {
        Fabric.with(this, new Crashlytics());
    }

    /**
     * Method which provide the Managers initialization
     */
    private void onManagersInitialization() {
        MMXManager.getInstance(this);
        SharedPreferenceManager.getInstance(this);
        InternetConnectionManager.getInstance(this);
        TypeFaceManager.getInstance(this);
        ChannelCacheManager.getInstance();
    }

    /**
     * Method which provide the enabling of the Multidex
     *
     * @param base
     */
    public void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    public static CurrentApplication getInstance() {
        return instance;
    }


    /**
     * Method which provide the getting of the resource string
     *
     * @param resID resource string ID
     * @return string value from resource
     */
    public String getResourceString(int resID) {
        return getResources().getString(resID);
    }


}
