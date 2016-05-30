package com.magnet.magnetchat.persistence.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.persistence.core.BaseSharedPreferenceRepository;

/**
 * Created by aorehov on 25.04.16.
 */
class AppScopePendingStateRepositoryImpl extends BaseSharedPreferenceRepository implements AppScopePendingStateRepository {

    private static final String NEED_UPDATE_CHANNELS = "needUpdatedChannels";

    public AppScopePendingStateRepositoryImpl(Context context) {
        super(context, "APPLICATION_SCOPE");
    }

    @Override
    public void putString(@NonNull String key, @NonNull String string) {
        getPreferences().edit().putString(key, string).commit();
    }

    @Override
    public String takeString(@NonNull String key) {
        SharedPreferences preferences = getPreferences();
        try {
            return preferences.getString(key, null);
        } finally {
            clear(key);
        }
    }

    @Override
    public void putBool(@NonNull String key, boolean bool) {
        putBoolean(key, bool);
    }

    @Override
    public boolean takeBool(@NonNull String key) {
        try {
            return getPreferences().getBoolean(key, false);
        } finally {
            clear(key);
        }
    }

    @Override
    public void putInt(@NonNull String key, int value) {
        putInteger(key, value);
    }

    @Override
    public int takeInt(@NonNull String key) {
        try {
            return getPreferences().getInt(key, Integer.MIN_VALUE);
        } finally {
            clear(key);
        }
    }

    @Override
    public void putLong(@NonNull String key, long value) {
        putL(key, value);
    }

    @Override
    public long takeLong(@NonNull String key) {
        try {
            return getPreferences().getLong(key, Long.MIN_VALUE);
        } finally {
            clear(key);
        }
    }

    @Override
    public boolean isNeedToUpdateChannels() {
        return takeBool(NEED_UPDATE_CHANNELS);
    }

    @Override
    public void setNeedToUpdateChannel(boolean isNeedUpdate) {
        putBool(NEED_UPDATE_CHANNELS, isNeedUpdate);
    }

    @Override
    public void setActiveChannel(@Nullable String idChannel) {
        putString(Constants.TAG_CHANNEL, idChannel);
    }

    @Nullable
    @Override
    public String getActiveChannel() {
        return getPreferences().getString(Constants.TAG_CHANNEL, null);
    }
}
