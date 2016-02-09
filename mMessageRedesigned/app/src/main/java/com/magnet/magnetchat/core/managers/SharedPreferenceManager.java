package com.magnet.magnetchat.core.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.lang.ref.WeakReference;

public class SharedPreferenceManager {

    private static final String USER_PREFERENCE = "com.magnet.imessage.preferences.xml";
    private static final String KEY = String.valueOf("value".hashCode());

    private static SharedPreferenceManager instance;

    private final WeakReference<Context> applicationReference;
    private SharedPreferences usernamePref;

    private SharedPreferenceManager(Context context) {
        applicationReference = new WeakReference<Context>(context);
        usernamePref = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance() {
        return instance;
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager(context);
        }
        return instance;
    }

    private synchronized SharedPreferences getPreferences() {
        return usernamePref;
    }

    public String[] readCredence() {
        String[] credence = new String[2];
        String text = getPreferences().getString(KEY, null);
        if (text == null) {
            return null;
        }
        String converted = new String(Base64.decode(text, Base64.DEFAULT));
        int pointerIdx = converted.indexOf(":");
        if (pointerIdx < 0) {
            return null;
        }
        credence[0] = converted.substring(0, pointerIdx);
        credence[1] = converted.substring(pointerIdx + 1);
        return credence;
    }

    public void saveCredence(String username, String password) {
        String text = username + ":" + password;
        String saveText = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(KEY, saveText);
        editor.apply();
    }

    public void cleanCredence() {
        getPreferences().edit().clear().apply();
    }

}
