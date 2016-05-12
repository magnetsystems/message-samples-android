package com.magnet.magnetchat.presenters.impl;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.model.converters.MMXUserConverter;
import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.presenters.chatlist.MMXPicMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;
import com.magnet.magnetchat.presenters.chatlist.impl.DefaultMMXMessageFactory;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXPresenterFactory implements MMXPresenterFactory, MMXMessagePresenterFactory {

    private MMXMessagePresenterFactory messagePresenterFactory;

    public DefaultMMXPresenterFactory() {
        messagePresenterFactory = new DefaultMMXMessageFactory();
    }

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

    @Override
    public UserListContract.Presenter createUserListPresenter(UserListContract.View view) {
        MMXUserConverter converter = ChatSDK.getMmxObjectConverterFactory().createMMXUserConverter();
        return new UserListContractPresenterImpl(view, converter);
    }

    @Override
    public MMXLocationContract.Presenter createLocationPresenter(MMXLocationContract.View view) {
        return messagePresenterFactory.createLocationPresenter(view);
    }

    @Override
    public MMXMessageContract.Presenter createMessagePresenter(MMXMessageContract.View view) {
        return messagePresenterFactory.createMessagePresenter(view);
    }

    @Override
    public MMXPicMessageContract.Presenter createPicMessagePresenter(MMXPicMessageContract.View view) {
        return messagePresenterFactory.createPicMessagePresenter(view);
    }

    @Override
    public MMXPollContract.Presenter createPollPresenter(MMXPollContract.View view) {
        return messagePresenterFactory.createPollPresenter(view);
    }
}
