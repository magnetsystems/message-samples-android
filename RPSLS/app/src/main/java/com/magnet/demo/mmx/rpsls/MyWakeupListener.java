package com.magnet.demo.mmx.rpsls;

import android.content.Context;
import android.content.Intent;

import com.magnet.mmx.client.MMXClient.MMXWakeupListener;

/**
 * This listener is registered during Application.onCreate() to handle GCM and AlarmManager wakeups.
 * See MyApplication.onCreate().
 */
public class MyWakeupListener implements MMXWakeupListener {
  private static final String TAG = MyWakeupListener.class.getSimpleName();

  public void onWakeupReceived(final Context applicationContext, Intent intent) {
    //TODO: Upon receiving a wakeup, the application may choose to connect.
    //MyMMXListener myListener = MyMMXListener.getInstance(applicationContext);
    //MMXClient client = MMXClient.getInstance(applicationContext, R.raw.quickstart);
    //client.connectWithCredentials(username, password, myListener, null);
  }
}
