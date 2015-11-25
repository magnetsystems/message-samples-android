package com.magnet.demo.mmx.starter;

import com.magnet.mmx.client.api.MMXPushEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive(): received intent: " + intent);
    MMXPushEvent event = MMXPushEvent.fromIntent(intent);
    if (event == null) {
      Toast.makeText(context, "Received a non-MMX GCM", Toast.LENGTH_LONG)
           .show();
    } else {
      Toast.makeText(context, "Received a push event: "+event,Toast.LENGTH_LONG)
           .show();
    }
  }
}
