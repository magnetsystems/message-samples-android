package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * Created by aorehov on 17.05.16.
 */
public interface MMXCreatePollContract {

    interface Presenter extends MMXPresenter {
        void doCreate();

        void setMMXChannel(MMXChannel mmxChannel);
    }

    interface View extends MMXInfoView {
        String getName();

        String getQuestion();

        List<String> getAnswers();

        boolean isAllowMultipleChoice();

        boolean isHiderResult();

        void onPollCreatedSuccess(MMXMessage mmxMessage);

        void onLock();

        void onUnlock();
    }

}
