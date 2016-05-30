package com.magnet.magnetchat.presenters.impl;

import android.content.Context;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.persistence.core.MMXPersistenceFactory;
import com.magnet.magnetchat.presenters.ChannelsListContract;
import com.magnet.magnetchat.presenters.EditProfileContract;
import com.magnet.magnetchat.presenters.LoginContract;
import com.magnet.magnetchat.presenters.RegisterContract;
import com.magnet.magnetchat.presenters.MMXChannelSettingsContract;
import com.magnet.magnetchat.presenters.MMXCreatePollContract;
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
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXPresenterFactory implements MMXPresenterFactory, MMXMessagePresenterFactory {

    private MMXMessagePresenterFactory messagePresenterFactory;

    public DefaultMMXPresenterFactory() {
        messagePresenterFactory = new DefaultMMXMessageFactory();
    }

    @Override
    public ChatListContract.Presenter createChatPresenter(Context context, ChatListContract.View view) {
        BaseConverter<MMXMessage, MMXMessageWrapper> converter = ChatSDK.getMmxObjectConverterFactory().createMMXMessageConverter();
        MMXPersistenceFactory factory = ChatSDK.getMMXPersistenceFactory();
        return new ChatListV2PresenterImpl(view, converter, factory.getAppScopePendingStateRepository());
    }

    @Override
    public PostMMXMessageContract.Presenter createPostMessagePresenter(PostMMXMessageContract.View view) {
        return new PostMMXMessagePresenterImpl(view);
    }

    @Override
    public UserListContract.Presenter createUserListPresenter(UserListContract.View view) {
        BaseConverter<User, MMXUserWrapper> converter = ChatSDK.getMmxObjectConverterFactory().createMMXUserConverter();
        return new MMXChannelUserListPresenterImpl(view, converter);
    }

    @Override
    public UserListContract.Presenter createAllUserListPresenter(UserListContract.View view) {
        BaseConverter<User, MMXUserWrapper> converter = ChatSDK.getMmxObjectConverterFactory().createMMXUserConverter();
        return new MMXAllUserListPresenter(view, converter);
    }

    @Override
    public MMXChannelSettingsContract.Presenter createChannelSettingsPresenter(MMXChannelSettingsContract.View view) {
        return new MMXChannelPresenterImpl(view);
    }

    @Override
    public MMXCreatePollContract.Presenter createMMXCreatePollPresenter(MMXCreatePollContract.View view) {
        BaseConverter<String, MMXPollOption> converter = ChatSDK.getMmxObjectConverterFactory().createMMXPollOptionStringConverter();
        return new MMXCreatePollPresenter(view, converter);
    }

    @Override
    public LoginContract.Presenter createLoginPresenter(LoginContract.View view) {
        return new DefaultLoginPresenter(view);
    }

    @Override
    public RegisterContract.Presenter createRegisterPresenter(RegisterContract.View view) {
        return new DefaultRegisterPresenter(view);
    }

    @Override
    public EditProfileContract.Presenter createEditProfilePresenter(EditProfileContract.View view) {
        return new DefaultEditProfilePresenter(view);
    }

    @Override
    public ChannelsListContract.Presenter createChannelListPresenter(ChannelsListContract.View view) {
        MMXPersistenceFactory factory = ChatSDK.getMMXPersistenceFactory();
        return new DefaultChannelsPresenter(view, factory.getAppScopePendingStateRepository());
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
