package com.magnet.demo.mmx.rpsls;

import com.magnet.android.config.MagnetAndroidPropertiesConfig;
import com.magnet.max.android.Max;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

import android.app.Application;

public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    //First thing to do is init the Max API.
    Max.init(this.getApplicationContext(),
            new MagnetAndroidPropertiesConfig(this, R.raw.magnetmax));
    MMX.registerListener(new MMX.EventListener() {
      public boolean onMessageReceived(MMXMessage mmxMessage) {
        RPSLS.Util.handleIncomingMessage(MyApplication.this, mmxMessage);
        return false;
      }
    });
  }
}
