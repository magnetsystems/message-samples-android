package com.magnet.magnetchat.presenters.chatlist;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;

import java.util.List;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXPollContract {

    interface Presenter extends BaseMMXMessagePresenter {
        void onNeedChangedState(MMXPollOptionWrapper wrapper);
    }

    interface View extends BaseMMXMessageView {
        void onPollType(int resString);

        void onPollQuestion(String question);

        void onPollAnswersReceived(List<MMXPollOptionWrapper> data);

//        void onPollAnswersUpdate(MMXPollOptionWrapper option);
    }

}
