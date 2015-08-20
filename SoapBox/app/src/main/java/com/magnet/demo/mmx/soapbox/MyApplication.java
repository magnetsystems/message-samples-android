package com.magnet.demo.mmx.soapbox;

import android.app.Application;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Extension of the android Application where the wakeup
 * listener can be registered (if used by the application).
 */
public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    MMX.init(this, R.raw.soapbox);
    MMX.registerListener(new MMX.EventListener() {
      @Override
      public boolean onMessageReceived(MMXMessage mmxMessage) {
        return false;
      }
    });
  }
}
