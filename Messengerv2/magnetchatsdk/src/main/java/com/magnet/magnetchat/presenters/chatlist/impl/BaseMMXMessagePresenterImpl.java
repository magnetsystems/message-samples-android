package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.BaseMMXMessageView;

/**
 * Created by aorehov on 05.05.16.
 */
abstract class BaseMMXMessagePresenterImpl<T extends BaseMMXMessageView> {

    T view;
    MMXMessageWrapper wrapper;

    public void setView(T view) {
        this.view = view;
        doUpdate();
    }

    public void setWrapper(MMXMessageWrapper wrapper) {
        this.wrapper = wrapper;
        doUpdate();
    }

    private void doUpdate() {
        if (view != null && wrapper != null) {
            updateBaseUI(view, wrapper);
            updateUI(view, wrapper);
        }
    }

    abstract void updateUI(@NonNull T view, @NonNull MMXMessageWrapper wrapper);

    void updateBaseUI(@NonNull BaseMMXMessageView view, @NonNull MMXMessageWrapper wrapper) {
        String picture = wrapper.getSenderPicture();
        String name = wrapper.getSenderName();
        view.onShowUserPicture(picture, name);
//        if (picture != null)
//            view.onShowUserPicture(picture);
//        else
//            view.onShowUserLetters(wrapper.getSenderName());

        if (wrapper.isShowDate()) {
            view.onSetPostDate(wrapper.getPublishDate());
        }
        view.isNeedShowDate(wrapper.isShowDate());


    }
}
