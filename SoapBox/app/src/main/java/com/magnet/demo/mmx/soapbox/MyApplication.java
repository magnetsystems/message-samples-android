package com.magnet.demo.mmx.soapbox;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.common.Log;

import android.app.Application;

public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    MMXClient.registerWakeupListener(this, MyWakeupListener.class);
    Log.setLoggable(null, Log.VERBOSE);
    //MMXClient.setWakeupInterval(this, 60 * 1000);
  }
}
