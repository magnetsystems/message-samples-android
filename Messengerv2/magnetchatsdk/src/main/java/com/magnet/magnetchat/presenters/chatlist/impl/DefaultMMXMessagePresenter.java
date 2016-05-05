package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;

/**
 * Created by aorehov on 05.05.16.
 */
class DefaultMMXMessagePresenter extends BaseMMXMessagePresenterImpl<MMXMessageContract.View> implements MMXMessageContract.Presenter {
    @Override
    public void setMMXMessage(MMXMessageWrapper wrapper) {
        setWrapper(wrapper);
    }

    @Override
    void updateUI(@NonNull MMXMessageContract.View view, @NonNull MMXMessageWrapper wrapper) {
        view.onShowTextMessage(wrapper.getTextMessage());
    }
}
