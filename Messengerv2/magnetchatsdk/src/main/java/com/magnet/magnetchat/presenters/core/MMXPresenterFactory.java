package com.magnet.magnetchat.presenters.core;

import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.presenters.updated.ChatContract;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 27.04.16.
 */
public interface MMXPresenterFactory {

    PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view, MMXChannel channel);

    PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view);

    ChatContract.Presenter createChatPresenter(ChatContract.View view);

}
