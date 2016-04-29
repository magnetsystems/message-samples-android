package com.magnet.magnetchat.presenters.impl;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;
import com.magnet.magnetchat.presenters.updated.ChatContract;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXPresenterFactory implements MMXPresenterFactory {

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

    @Override
    public ChatContract.Presenter createChatPresenter(ChatContract.View view) {
        MMXMessageWrapperConverter converter = ChatSDK.getMmxObjectConverterFactory().createMMXMessageConverter();
        return new NewChatPresenterImpl(view, converter);
    }

}
