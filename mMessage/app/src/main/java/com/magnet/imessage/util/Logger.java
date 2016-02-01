package com.magnet.imessage.util;

import android.util.Log;

public class Logger {

    private static final String APP_TAG = "MagnetMessage";

    public static void debug (Object... args) {
        debug(null, args);
    }

    private static String makeTag(String tag) {
        String fullTag = APP_TAG;
        if (tag != null) {
            fullTag += " - " + tag;
        }
        return fullTag;
    }

    private static String makeMessage(Object... args) {
        String msg = "---------";
        for (int i = 0; i < args.length - 1; i++) {
            msg += args[i] + ", ";
        }
        if (args.length > 0) {
            msg += args[args.length - 1];
        }
        return msg;
    }

    public static void debug(String tag, Object... args) {
        String fullTag = makeTag(tag);
        String msg = makeMessage(args);
        Log.d(fullTag, msg);
    }

    public static void error (String tag, Throwable throwable, Object... args) {
        String fullTag = makeTag(tag);
        String msg = makeMessage(args);
        Log.e(fullTag, msg, throwable);
    }

    public static void error (String tag, Object... args) {
        String fullTag = makeTag(tag);
        String msg = makeMessage(args);
        Log.e(fullTag, msg);
    }

    public static void error (String tag, Throwable throwable) {
        String fullTag = makeTag(tag);
        Log.e(fullTag, "ERROR", throwable);
    }


}
