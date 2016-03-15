package com.magnet.magntetchatapp.core;

import android.support.multidex.MultiDexApplication;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magntetchatapp.R;
import com.magnet.max.android.Max;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class CurrentApplication extends MultiDexApplication {

    private static CurrentApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        onInitMagnetMax();
    }

    /**
     * Method which provide initialization of the magnet chat
     */
    private void onInitMagnetMax() {
        Max.init(this, new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        ChatSDK.init(this);
    }

    public static CurrentApplication getInstance() {
        return instance;
    }
}
