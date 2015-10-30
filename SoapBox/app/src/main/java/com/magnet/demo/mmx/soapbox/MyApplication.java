package com.magnet.demo.mmx.soapbox;

import android.app.Application;

import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.max.android.Max;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Extension of the android Application where the wakeup
 * listener can be registered (if used by the application).
 */
public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    //First thing to do is init the Max API.
    Max.init(this.getApplicationContext(),
            new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
    MMX.registerListener(new MMX.EventListener() {
      @Override
      public boolean onMessageReceived(MMXMessage mmxMessage) {
        return false;
      }
    });
  }
}
