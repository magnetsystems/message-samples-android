package com.magnet.magnetchat.presenters.core;

import android.os.Bundle;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXPresenter {
    void onStart();

    void onStop();

    void onPaused();

    void onResumed();

    void onCreate();

    void onDestroy();

    Bundle onSaveInstance(Bundle savedInstances);

    void onRestore(Bundle savedInstances);
}
