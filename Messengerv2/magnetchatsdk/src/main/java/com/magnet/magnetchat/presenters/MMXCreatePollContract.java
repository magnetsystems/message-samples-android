package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * abstraction of create poll flow
 * Created by aorehov on 17.05.16.
 */
public interface MMXCreatePollContract {

    interface Presenter extends MMXPresenter {
        /**
         * call this method if you want to create poll
         */
        void doCreate();

        /**
         * init presenter with channel
         *
         * @param mmxChannel
         */
        void setMMXChannel(MMXChannel mmxChannel);
    }

    interface View extends MMXInfoView {

        /**
         * @return the name of poll
         */
        String getName();

        /**
         * @return the question of the poll
         */
        String getQuestion();

        /**
         * @return the list of poll answers. should be at least two answers
         */
        List<String> getAnswers();

        /**
         * @return return true if you want to create multiple choice poll otherwise false
         */
        boolean isAllowMultipleChoice();

        /**
         * @return return true if you want to hide result
         */
        boolean isHiderResult();

        /**
         * poll create callback
         *
         * @param mmxMessage
         */
        void onPollCreatedSuccess(MMXMessage mmxMessage);

        /**
         * you can lock action during poll create request
         */
        void onLock();

        /**
         * you can unlock action when poll has been created
         */
        void onUnlock();
    }

}
