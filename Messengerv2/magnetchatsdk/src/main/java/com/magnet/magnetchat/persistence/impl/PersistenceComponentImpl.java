package com.magnet.magnetchat.persistence.impl;

import android.content.Context;

import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.persistence.core.PersistenceComponent;

/**
 * Created by aorehov on 25.04.16.
 */
public class PersistenceComponentImpl implements PersistenceComponent {

    private final PersistenceModule module;

    public PersistenceComponentImpl(PersistenceModule module) {
        this.module = module;
    }

    public PersistenceComponentImpl(Context application) {
        module = new PersistenceModule(application);
    }

    @Override
    public AppScopePendingStateRepository getApplicationPendingStateRepository() {
        return module.getApplicationPendingStateRepository();
    }
}
