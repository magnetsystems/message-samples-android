/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.util;

import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class Utils {
  private static final String TAG = Utils.class.getSimpleName();

  public static boolean isGooglePlayServiceInstalled(Context context) {
    final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context.getApplicationContext());
    com.magnet.mmx.client.common.Log.d(TAG, "----------------GooglePlayServicesUtil.isGooglePlayServicesAvailable : " + status);
    if (status == ConnectionResult.SUCCESS) {
      return true;
    }
    return false;
  }

  public static void showMessage(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }
}
