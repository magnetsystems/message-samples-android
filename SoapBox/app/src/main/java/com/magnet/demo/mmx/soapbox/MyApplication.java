package com.magnet.demo.mmx.soapbox;

import android.app.Application;

/**
 * Extension of the android Application where the wakeup
 * listener can be registered (if used by the application).
 */
public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    //MMXClient.registerWakeupListener(this, MyWakeupListener.class);
    //MMXClient.setWakeupInterval(this, 60 * 1000);
  }
}
