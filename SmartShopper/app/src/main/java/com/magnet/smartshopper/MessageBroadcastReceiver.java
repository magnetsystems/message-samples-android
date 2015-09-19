package com.magnet.smartshopper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = MessageBroadcastReceiver.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive(): received intent: " + intent);
  }
}
