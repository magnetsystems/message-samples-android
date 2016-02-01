package com.magnet.magnetchat.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class UserPreference {

    private static final String USER_PREFERENCE = "com.magnet.imessage.preferences.xml";
    private static final String KEY = String.valueOf("value".hashCode());

    private static UserPreference instance;

    private SharedPreferences usernamePref;

    private UserPreference(Context context) {
        usernamePref = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static UserPreference getInstance() {
        return instance;
    }

    public static UserPreference getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreference(context);
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
