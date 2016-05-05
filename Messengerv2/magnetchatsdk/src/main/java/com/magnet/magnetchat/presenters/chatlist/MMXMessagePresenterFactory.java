package com.magnet.magnetchat.presenters.chatlist;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXMessagePresenterFactory {

    MMXLocationContract.Presenter createLocationPresenter(MMXLocationContract.View view);

    MMXMessageContract.Presenter createMessagePresenter(MMXMessageContract.View view);

    MMXPicMessageContract.Presenter createPicMessagePresenter(MMXPicMessageContract.View view);

    MMXPollContract.Presenter createPollPresenter(MMXPollContract.View view);

}
