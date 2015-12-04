/*   Copyright (c) 2015 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.magnet.demo.mmx.soapbox;

import java.util.Map;

import com.magnet.mmx.client.api.MMXPushEvent;
import com.magnet.mmx.protocol.PubSubNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Custom broadcast receiver to handle MMX push messages.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
  private static final int PUBSUB_ID = 1111;
  private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    MMXPushEvent event = MMXPushEvent.fromIntent(intent);
    Log.d(TAG, "onReceive(): received intent: "+intent+", push event="+event);
    if (event == null) {
      Toast.makeText(context, "Received a non-MMX GCM", Toast.LENGTH_LONG).show();
    } else if ("retrieve".equals(event.getType())) {
      // Message wake-up; an ad-hoc message is available.
      showNotification(context, "A message is ready from MMX", null);
    } else if (PubSubNotification.getType().equals(event.getType())) {
      // Pubsub wake-up; get the payload and show it in the status bar
      PubSubNotification pubsub = event.getCustomObject(PubSubNotification.class);
      showNotification(context, pubsub.getText(),
          (pubsub.getChannel().getUserId() == null) ?
              pubsub.getChannel().getName() : pubsub.getChannel().toString());
    } else {
      // Push messaging from client; get the custom payload as map (if any)
      Map<String, Object> payload = event.getCustomMap();
      Toast.makeText(context, (payload == null) ? "No custom payload" :
          payload.toString(), Toast.LENGTH_LONG).show();
    }
  }

  // Show the notification in status bar and launch the application if tapped.
  private void showNotification(Context context, String title, String text) {
    PendingIntent pIntent = PendingIntent.getActivity(context, 0,
        new Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setPackage(context.getPackageName()),
        PendingIntent.FLAG_UPDATE_CURRENT);
    Notification note = new Notification.Builder(context)
      .setAutoCancel(true)
      .setSmallIcon(context.getApplicationInfo().icon)
      .setContentTitle(title)
      .setContentText(text)
      .setContentIntent(pIntent)
      .build();
    NotificationManager noteMgr = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    noteMgr.notify(PUBSUB_ID, note);
  }
}
