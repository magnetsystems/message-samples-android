package com.magnet.magnetchat.persistence.impl;

import android.content.Context;

import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.persistence.core.MMXPersistenceFactory;

/**
 * Created by aorehov on 30.05.16.
 */
public class MMXPersistenceFactoryImpl implements MMXPersistenceFactory {

    private final Context appContext;

    public MMXPersistenceFactoryImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public AppScopePendingStateRepository getAppScopePendingStateRepository() {
        return new AppScopePendingStateRepositoryImpl(appContext);
    }
}
