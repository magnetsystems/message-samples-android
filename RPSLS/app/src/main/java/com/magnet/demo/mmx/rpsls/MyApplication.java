package com.magnet.demo.mmx.rpsls;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.common.Log;

import android.app.Application;

public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    MMX.init(this, R.raw.rpsls);
    MMX.registerListener(new MMX.EventListener() {
      public boolean onMessageReceived(MMXMessage mmxMessage) {
        RPSLS.Util.handleIncomingMessage(MyApplication.this, mmxMessage);
        return false;
      }
    });
  }
}
