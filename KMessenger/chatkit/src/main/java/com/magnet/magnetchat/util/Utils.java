/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.util;

import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.magnet.max.android.Max;
import com.magnet.max.android.util.StringUtil;

public class Utils {
  private static final String TAG = Utils.class.getSimpleName();

  public static boolean isGooglePlayServiceInstalled() {
    final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
        Max.getApplicationContext());
    com.magnet.mmx.client.common.Log.d(TAG, "----------------GooglePlayServicesUtil.isGooglePlayServicesAvailable : " + status);
    if (status == ConnectionResult.SUCCESS) {
      return true;
    }
    return false;
  }

  public static void showMessage(String message) {
    showMessage(Max.getApplicationContext(), message);
  }

  public static void showMessage(Context context, String message) {
    if(null != context && StringUtil.isNotEmpty(message)) {
      Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
  }

  public static int compareString(String s1, String s2) {
    if(null == s1) {
      return null == s2 ? 0 : -1;
    } else {
      return null == s2 ? 1 : s1.compareTo(s2);
    }
  }
}
