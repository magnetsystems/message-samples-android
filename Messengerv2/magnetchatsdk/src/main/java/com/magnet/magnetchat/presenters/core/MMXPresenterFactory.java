package com.magnet.magnetchat.presenters.core;

import android.content.Context;

import com.magnet.magnetchat.presenters.ChannelsListContract;
import com.magnet.magnetchat.presenters.EditProfileContract;
import com.magnet.magnetchat.presenters.LoginContract;
import com.magnet.magnetchat.presenters.RegisterContract;
import com.magnet.magnetchat.presenters.MMXChannelSettingsContract;
import com.magnet.magnetchat.presenters.MMXCreatePollContract;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.presenters.updated.ChatListContract;

/**
 * Created by aorehov on 27.04.16.
 */
public interface MMXPresenterFactory {

    ChatListContract.Presenter createChatPresenter(Context context, ChatListContract.View view);

    PostMMXMessageContract.Presenter createPostMessagePresenter(PostMMXMessageContract.View view);

    UserListContract.Presenter createUserListPresenter(UserListContract.View view);

    UserListContract.Presenter createAllUserListPresenter(UserListContract.View view);

    MMXChannelSettingsContract.Presenter createChannelSettingsPresenter(MMXChannelSettingsContract.View view);

    MMXCreatePollContract.Presenter createMMXCreatePollPresenter(MMXCreatePollContract.View view);

    LoginContract.Presenter createLoginPresenter(LoginContract.View view);

    RegisterContract.Presenter createRegisterPresenter(RegisterContract.View view);

    EditProfileContract.Presenter createEditProfilePresenter(EditProfileContract.View view);

    ChannelsListContract.Presenter createChannelListPresenter(ChannelsListContract.View view);
}
