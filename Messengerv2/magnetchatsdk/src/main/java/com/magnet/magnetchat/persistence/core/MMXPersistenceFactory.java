package com.magnet.magnetchat.persistence.core;

import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;

/**
 * Created by aorehov on 30.05.16.
 */
public interface MMXPersistenceFactory {
    AppScopePendingStateRepository getAppScopePendingStateRepository();
}
