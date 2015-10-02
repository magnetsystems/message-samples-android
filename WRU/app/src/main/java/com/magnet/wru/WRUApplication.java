package com.magnet.wru;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.common.Log;

public class WRUApplication extends Application {
  public static Context sContext = null;
  public static final String ACTION_WAKEUP = "com.magnet.wru.action.WAKEUP";

  public void onCreate() {
    super.onCreate();
    sContext = this;
    Log.setLoggable(null, Log.VERBOSE);
    MMX.init(this, R.raw.wru);
    Intent intent = new Intent(ACTION_WAKEUP);
    MMX.registerWakeupBroadcast(intent);
  }
}
