package com.magnet.magnetchat.persistence.core;

import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;

/**
 * Created by aorehov on 25.04.16.
 */
public interface PersistenceComponent {

    AppScopePendingStateRepository getApplicationPendingStateRepository();
}
