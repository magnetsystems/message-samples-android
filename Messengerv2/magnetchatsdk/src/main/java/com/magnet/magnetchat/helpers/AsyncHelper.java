package com.magnet.magnetchat.helpers;


import android.os.Handler;
import android.os.Looper;

/**
 * Created by aorehov on 28.04.16.
 */
public class AsyncHelper {

    /**
     * ui thread handler
     *
     */
    public static final Handler UI = new Handler(Looper.getMainLooper());
}
