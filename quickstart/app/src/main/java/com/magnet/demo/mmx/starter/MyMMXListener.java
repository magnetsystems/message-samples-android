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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.util.Log;

import com.magnet.mmx.client.AbstractMMXListener;
import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.protocol.MMXTopic;

import java.util.Date;

/**
 * This is the singleton MMXListener that should be passed to
 * MMXClient.connect().  The reason for this is that the application developer needs to implement
 * these methods to handle situations where messages arrive even when there is no UI shown to the developer.
 *
 * In this implementation, messages are dropped if no additional listeners are registered.  In an ideal
 * situation, the application will decide how to handle messages when there are no listeners by overriding
 * the onMessageReceived and onPubsubItemReceived methods.
 */
public class MyMMXListener extends AbstractMMXListener {
  private static final String TAG = MyMMXListener.class.getSimpleName();
  private static MyMMXListener sInstance = null;
  private Context mApplicationContext = null;
  private int mNoteId = 0;

  private MyMMXListener(Context context) {
    mApplicationContext = context.getApplicationContext();
  };

  public synchronized static MyMMXListener getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new MyMMXListener(context);
    }
    return sInstance;
  }

  /**
   * Just handle the incoming message by adding it to the in-memory message
   * store and post a notification to the status bar.
   */
  @Override
  public void handleMessageReceived(MMXClient mmxClient, MMXMessage mmxMessage,
                                      String receiptId) {
    Log.d(TAG, "handleMessageReceived(): start. ");
    MyMessageStore.addMessage(mmxMessage, null,
            new Date(), true);
    doNotify(mmxMessage);
  }

  /**
   * This callback is ignored.
   */
  public void handleMessageDelivered(MMXClient mmxClient, MMXid recipient, String messageId) {
    Log.d(TAG, "handleMessageDelivered(): messageId=" + messageId);
  }

  /**
   * This callback is ignored.
   */
  public void handlePubsubItemReceived(MMXClient mmxClient, MMXTopic mmxTopic, MMXMessage mmxMessage) {
    Log.d(TAG, "handlePubsubItemReceived(): topic=" + mmxTopic);
  }

  private void doNotify(MMXMessage message) {
    String messageText = message.getPayload().getDataAsText().toString();
    MMXid from = message.getFrom();
    NotificationManager noteMgr = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification note = new Notification.Builder(mApplicationContext).setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle("Message from " + from.getUserId()).setContentText(messageText).build();
    noteMgr.notify(mNoteId++, note);
  }
}
