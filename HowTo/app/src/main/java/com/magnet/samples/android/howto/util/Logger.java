/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.howto.util;

import android.util.Log;

public class Logger {

    private static final String APP_TAG = "HowTo";

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
