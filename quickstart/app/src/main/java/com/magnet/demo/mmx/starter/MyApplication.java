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
package com.magnet.demo.mmx.starter;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.common.MMXid;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;

import java.util.Date;

/**
 * Quick-start application extending the android Application class.  It registers
 * the <code>MyWakeupListener</code> to handle the GCM wake-up message sent by
 * MMX server.  Alternatively, the application may use a timer to wake itself
 * up; it is functionally equivalent to Android AlarmManager.  Typically a GCM
 * wake-up message is sent when there is a message waiting for the disconnected
 * client to retrieve, and the wake-up listener is responsible to establish the
 * connection.  Currently <code>MyWakeupListener</code> has this logic disabled
 * by a flag.
 * @see MyActivity
 * @see MyWakeupListener
 */
public class MyApplication extends Application {
  private final static boolean WAKEUP_BY_TIMER = false;
  private int mNoteId = 0;

  private MMX.EventListener mListener =
          new MMX.EventListener() {
            public boolean onMessageReceived(MMXMessage mmxMessage) {
              MyMessageStore.addMessage(mmxMessage, null,
                      new Date(), true);
              doNotify(mmxMessage);
              return false;
            }

            public boolean onMessageAcknowledgementReceived(MMXid mmXid, String s) {
              return false;
            }
          };
  
  public void onCreate() {
    super.onCreate();

    MMX.init(this, R.raw.quickstart);
    MMX.registerListener(mListener);

    // register a wakeup listener for GCM and/or timer.
    MMXClient.registerWakeupListener(this, MyWakeupListener.class);
    
    if (WAKEUP_BY_TIMER) {
      // Alternatively, use a 60-second timer to wake up this application.
      MMXClient.setWakeupInterval(this, 60 * 1000);
    }
  }

  private void doNotify(com.magnet.mmx.client.api.MMXMessage message) {
    Object textObj = message.getContent().get(MyActivity.KEY_MESSAGE_TEXT);
    if (textObj != null) {
      String messageText = textObj.toString();
      MMXUser from = message.getSender();
      NotificationManager noteMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
      Notification note = new Notification.Builder(this).setAutoCancel(true)
              .setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis())
              .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
              .setContentTitle("Message from " + from.getUsername()).setContentText(messageText).build();
      noteMgr.notify(mNoteId++, note);
    }
  }

}
