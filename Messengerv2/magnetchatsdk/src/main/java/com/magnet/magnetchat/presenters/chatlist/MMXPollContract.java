package com.magnet.magnetchat.presenters.chatlist;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;

import java.util.List;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXPollContract {

    interface Presenter extends BaseMMXMessagePresenter {
        void onNeedChangedState(MMXPollOptionWrapper wrapper);

        void submitAnswers();

        void doRefresh();
    }

    interface View extends BaseMMXMessageView, MMXInfoView {
        void onPollType(int resString);

        void onPollQuestion(String question);

        void onPollAnswersReceived(List<MMXPollOptionWrapper> data);

        void onEnableSubmitButton(boolean isEnable);

        void onRefreshing();

        void onRefreshingFinished();
    }

}
