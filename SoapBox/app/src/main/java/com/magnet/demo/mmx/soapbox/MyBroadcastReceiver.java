package com.magnet.demo.mmx.soapbox;

import java.util.Map;

import com.magnet.mmx.client.api.MMXPushEvent;
import com.magnet.mmx.protocol.PubSubNotification;

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
    Log.d(TAG, "onReceive(): push event="+event);
    if (event == null) {
      Toast.makeText(context, "Received a non-MMX GCM", Toast.LENGTH_LONG)
           .show();
      return;
    }
    if (event.getType() == null || event.getType().isEmpty()) {
      // Push notification from console
      Map<String, ? super Object> payload = event.getPayload();
      Toast.makeText(context, payload==null ? "No payload" : payload.toString(),
          Toast.LENGTH_LONG).show();
      return;
    }
    if (PubSubNotification.getType().equals(event.getType())) {
      // Pubsub wake-up.
      PubSubNotification pubsub = event.getCustomObject(PubSubNotification.class);
      Toast.makeText(context, pubsub.getTitle()+"@"+pubsub.getChannel().toString(),
          Toast.LENGTH_LONG).show();
      return;
    } else {
      // Push messaging from client.
      Map<String, Object> payload = event.getCustomMap();
      Toast.makeText(context, payload.toString(), Toast.LENGTH_LONG).show();
    }
  }
}
