package com.magnet.magnetchat.presenters.impl;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
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
    public ChatListContract.Presenter createChatPresenter(ChatListContract.View view) {
        MMXMessageWrapperConverter converter = ChatSDK.getMmxObjectConverterFactory().createMMXMessageConverter();
        return new ChatListV2PresenterImpl(view, converter);
    }

    @Override
    public PostMMXMessageContract.Presenter createPostMessagePresenter(PostMMXMessageContract.View view) {
        return new PostMMXMessagePresenterImpl(view);
    }

}
