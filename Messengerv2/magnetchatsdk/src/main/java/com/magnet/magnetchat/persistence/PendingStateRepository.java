package com.magnet.magnetchat.persistence;

import android.support.annotation.NonNull;

/**
 * The methods which start from put* should create new or override old value by key
 * <p/>
 * The methods which start from take Should return value by key or default value
 * <p/>
 * Created by aorehov on 25.04.16.
 */
public interface PendingStateRepository {

    void putString(@NonNull String key, @NonNull String string);

    String takeString(@NonNull String key);


    void putBool(@NonNull String key, boolean bool);

    boolean takeBool(@NonNull String key);


    void putInt(@NonNull String key, int value);

    int takeInt(@NonNull String key);


    void putLong(@NonNull String key, long value);

    long takeLong(@NonNull String key);


}
