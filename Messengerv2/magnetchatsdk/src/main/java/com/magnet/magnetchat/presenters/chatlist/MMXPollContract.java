package com.magnet.magnetchat.presenters.chatlist;

import java.util.List;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXPollContract {

    interface Presenter extends BaseMMXMessagePresenter {

    }

    interface View extends BaseMMXMessageView {
        void onPollType(String type);

        void onPollQuestion(String question);

        void onPollAnswers(List data);
    }

}
