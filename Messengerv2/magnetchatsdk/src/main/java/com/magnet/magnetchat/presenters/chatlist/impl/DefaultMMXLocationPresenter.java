package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;

/**
 * Created by aorehov on 05.05.16.
 */
class DefaultMMXLocationPresenter extends BaseMMXMessagePresenterImpl<MMXLocationContract.View> implements MMXLocationContract.Presenter {
    @Override
    public void setMMXMessage(MMXMessageWrapper wrapper) {
        setWrapper(wrapper);
    }

    @Override
    void updateUI(@NonNull MMXLocationContract.View view, @NonNull MMXMessageWrapper wrapper) {
        double lat = wrapper.getLat();
        double lon = wrapper.getLon();

        if (lat != Double.MAX_VALUE && lon != Double.MIN_VALUE) {
            view.onLocation(wrapper.getMapLocationUrl());
        } else {
            view.onCantGetLocation();
        }
    }
}
