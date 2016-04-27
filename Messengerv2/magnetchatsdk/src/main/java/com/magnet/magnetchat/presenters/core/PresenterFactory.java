package com.magnet.magnetchat.presenters.core;

import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 27.04.16.
 */
public interface PresenterFactory {

    PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view, MMXChannel channel);

    PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view);

}
