package com.magnet.magnetchat.util;

import android.util.Log;

/**
 * Created by dlernatovich on 2/9/16.
 */
public class AppLogger {
    private static final String PREFIX = "===>";
    private static final String ERROR_PREFIX = "===> [ERROR]:";

    public static void info(Object owner, String message) {
        Log.d(String.format("%s %s", PREFIX, owner.getClass().getSimpleName()), message);
    }

    public static void info(Object owner, String message, String additionalMessage) {
        Log.d(String.format("%s %s", PREFIX, owner.getClass().getSimpleName()), String.format("[%s] %s", additionalMessage.toUpperCase(), message));
    }

    public static void error(Object owner, String message) {
        Log.e(String.format("%s %s", ERROR_PREFIX, owner.getClass().getSimpleName()), message);
    }

    public static void error(Object owner, String message, String additionalMessage) {
        Log.e(String.format("%s %s", ERROR_PREFIX, owner.getClass().getSimpleName()), String.format("[%s] %s", additionalMessage.toUpperCase(), message));
    }
}
