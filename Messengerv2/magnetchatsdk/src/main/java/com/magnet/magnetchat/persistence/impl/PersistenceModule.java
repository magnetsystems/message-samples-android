package com.magnet.magnetchat.persistence.impl;

import android.content.Context;

import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;

/**
 * Created by aorehov on 25.04.16.
 */
public class PersistenceModule {

    private final Context context;
    private AppScopePendingStateRepository applicationPendingStateRepository;

    public PersistenceModule(Context context) {
        this.context = context;
    }

    public AppScopePendingStateRepository getApplicationPendingStateRepository() {
        if (applicationPendingStateRepository == null) {
            applicationPendingStateRepository = new AppScopePendingStateRepositoryImpl(context);
        }
        return applicationPendingStateRepository;
    }
}
