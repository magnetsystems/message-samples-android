package com.magnet.magnetchat.presenters.impl;

import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.presenters.core.PresenterFactory;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultPresenterFactory implements PresenterFactory {

    @Override
    public PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view, MMXChannel channel) {
        return new PollEditPresenterImpl(view, channel);
    }

    @Override
    public PollEditContract.Presenter createPollEditPresenter(PollEditContract.View view) {
        PollEditContract.Presenter presenter = new PollEditPresenterImpl();
        presenter.setView(view);
        return presenter;
    }

}
