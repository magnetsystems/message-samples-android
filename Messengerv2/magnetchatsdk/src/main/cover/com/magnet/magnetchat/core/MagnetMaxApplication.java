package com.magnet.magnetchat.core;

import android.support.multidex.MultiDexApplication;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.core.factories.MyMMXListItemFactory;
import com.magnet.magnetchat.core.factories.MyMMXViewFactory;
import com.magnet.max.android.Max;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;

/**
 * Created by Artli_000 on 28.03.2016.
 */
public abstract class MagnetMaxApplication extends MultiDexApplication {
    private static MagnetMaxApplication instance;

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
        Max.init(this, new MaxAndroidPropertiesConfig(this, getPropertyFile()));

        new ChatSDK.Builder()
//                .setDefaultMMXListItemFactory(new MyMMXListItemFactory())
                .setDefaultMMXViewFactory(new MyMMXViewFactory())
                .registerNamedFactory("custom", new MyMMXListItemFactory())
                .init(this);

//        ChatSDK.init(this);
    }

    /**
     * Method which provide the getting of the property file ID
     *
     * @return property file ID
     */
    protected abstract int getPropertyFile();

    public static MagnetMaxApplication getInstance() {
        return instance;
    }
}

//SAMPLE TO USE:
//CODE:
//public class CurrentApplication extends MagnetMaxApplication {
//
//    @Override
//    protected int getPropertyFile() {
//        return R.raw.magnetmax;
//    }
//}
//MANIFETS.XML
//<application
//android:name=".core.CurrentApplication"
//        android:allowBackup="true"
