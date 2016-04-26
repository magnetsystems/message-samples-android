package com.magnet.magnetchat.persistence;

/**
 * Created by aorehov on 25.04.16.
 */
public interface AppScopePendingStateRepository extends PendingStateRepository {

    boolean isNeedToUpdateChannels();

    void setNeedToUpdateChannel(boolean isNeedUpdate);

}
