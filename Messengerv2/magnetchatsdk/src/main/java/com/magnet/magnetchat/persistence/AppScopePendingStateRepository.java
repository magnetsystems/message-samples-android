package com.magnet.magnetchat.persistence;

import android.support.annotation.Nullable;

/**
 * Created by aorehov on 25.04.16.
 */
public interface AppScopePendingStateRepository extends PendingStateRepository {

    boolean isNeedToUpdateChannels();

    void setNeedToUpdateChannel(boolean isNeedUpdate);

    void setActiveChannel(@Nullable String idChannel);

    @Nullable
    String getActiveChannel();

}
