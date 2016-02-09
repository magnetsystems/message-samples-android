package com.magnet.magnetchat.helpers;

import android.graphics.Color;
import android.support.design.widget.Snackbar;

/**
 * Created by dlernatovich on 2/9/16.
 */
public class SnackBarHelper {
    public static void show(String message) {
        Snackbar.make(null, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }
}
