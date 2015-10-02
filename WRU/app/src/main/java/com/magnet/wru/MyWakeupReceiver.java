package com.magnet.wru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.api.MMX;

import java.net.URISyntaxException;

public class MyWakeupReceiver extends BroadcastReceiver {
  private static final String TAG = MyWakeupReceiver.class.getSimpleName();

  public MyWakeupReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive(): intent=" + intent);
    WRU wru = WRU.getInstance(context);
    try {
      Intent nestedIntent = Intent.parseUri(intent.getStringExtra(MMX.EXTRA_NESTED_INTENT), Intent.URI_INTENT_SCHEME);
      Log.d(TAG, "onReceive():  successfully parsed intent. extras: " + nestedIntent.getExtras());
      String pushBody = nestedIntent.getStringExtra(MMXClient.EXTRA_PUSH_BODY);
      Log.d(TAG, "onReceive():  here is push content from the console: " + pushBody);
    } catch (URISyntaxException e) {
      Log.e(TAG, "onReceive(): unable to parse intent", e);
    }
  }
}
