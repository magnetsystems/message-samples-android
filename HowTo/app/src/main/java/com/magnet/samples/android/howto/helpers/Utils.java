/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.samples.android.howto.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {
  public static AlertDialog showWarning(final Context context, final String title, final String message) {
    AlertDialog dialog = new AlertDialog.Builder(context).setMessage(message)
        .setTitle(title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    dialog.show();
    return dialog;
  }
}
