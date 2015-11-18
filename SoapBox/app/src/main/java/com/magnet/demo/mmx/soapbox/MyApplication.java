package com.magnet.demo.mmx.soapbox;

import android.app.Application;
import android.content.Intent;

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

    com.magnet.mmx.client.common.Log.setLoggable(null,
      com.magnet.mmx.client.common.Log.VERBOSE);

    //First thing to do is init the Max API.
    Max.init(this.getApplicationContext(),
            new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
    MMX.registerListener(new MMX.EventListener() {
      @Override
      public boolean onMessageReceived(MMXMessage mmxMessage) {
        return false;
      }
    });

    // Optionally register a wakeup broadcast intent.  This will be broadcast
    // when a GCM message is for this MMX application.  If configure properly,
    // the MMX server will send this GCM to wakeup the device when a message
    // needs to be delivered.  It is up to the developer to define this intent
    // and implement/declare the BroadcastReceiver to handle this intent and
    // thus to call MMXChannel to retrieve pending messages.
    Intent intent = new Intent("SOAPBOX_WAKEUP");
    MMX.registerWakeupBroadcast(this, intent);
  }
}
