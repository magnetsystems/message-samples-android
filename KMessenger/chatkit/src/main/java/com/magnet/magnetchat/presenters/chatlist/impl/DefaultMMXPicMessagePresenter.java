package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXPicMessageContract;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.Attachment;

/**
 * Created by aorehov on 05.05.16.
 */
class DefaultMMXPicMessagePresenter extends BaseMMXMessagePresenterImpl<MMXPicMessageContract.View> implements MMXPicMessageContract.Presenter {

    @Override
    void updateUI(@NonNull MMXPicMessageContract.View view, @NonNull MMXMessageWrapper wrapper) {
        Attachment attachment = wrapper.getAttachment();
        try {
            String url = attachment.getDownloadUrl();
            view.onPicture(url);
        } catch (IllegalArgumentException ex) {
            Logger.error(this.getClass().getSimpleName(), ex);
            view.onPicture(null);
        }
    }

    @Override
    public void setMMXMessage(MMXMessageWrapper wrapper) {
        setWrapper(wrapper);
    }

    @Override
    public String getImageURL() {
        Attachment attachment = wrapper.getAttachment();
        if (attachment != null) {
            return attachment.getDownloadUrl();
        }
        return null;
    }
}
