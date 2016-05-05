package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;

import java.util.Collections;

/**
 * Created by aorehov on 05.05.16.
 */
class DefaultMMXPollPresenter extends BaseMMXMessagePresenterImpl<MMXPollContract.View> implements MMXPollContract.Presenter {
    @Override
    public void setMMXMessage(MMXMessageWrapper wrapper) {
        setWrapper(wrapper);
    }

    @Override
    void updateUI(@NonNull MMXPollContract.View view, @NonNull MMXMessageWrapper wrapper) {
        view.onPollAnswers(Collections.EMPTY_LIST);
        view.onPollType("Type");
        view.onPollQuestion("Question");
    }
}
