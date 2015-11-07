package com.magnet.demo.mmx.starter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive(): received intent: " + intent);
  }
}
