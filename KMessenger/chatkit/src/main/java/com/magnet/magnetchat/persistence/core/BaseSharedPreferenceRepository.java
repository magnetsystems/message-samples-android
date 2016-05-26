package com.magnet.magnetchat.persistence.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aorehov on 25.04.16.
 */
public abstract class BaseSharedPreferenceRepository {

    private final Context context;
    private String name;
    private int accessMode = Context.MODE_PRIVATE;

    public BaseSharedPreferenceRepository(Context context, String name, int accessMode) {
        this.context = context;
        this.name = name;
        this.accessMode = accessMode;
    }

    public BaseSharedPreferenceRepository(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    protected SharedPreferences getPreferences() {
        return context.getSharedPreferences(name, accessMode);
    }

    protected void putStr(String key, String string) {
        getPreferences().edit().putString(key, string).commit();
    }

    protected void putBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).commit();
    }

    protected void putInteger(String key, int value) {
        getPreferences().edit().putInt(key, value).commit();
    }

    protected void putL(String key, long value) {
        getPreferences().edit().putLong(key, value).commit();
    }

    protected void clear(String key) {
        getPreferences().edit().remove(key).commit();
    }
}
