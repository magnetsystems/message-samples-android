package com.magnet.magnetchat.presenters.chatlist.impl;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.converters.MMXPollOptionWrapperConverter;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.presenters.chatlist.MMXPicMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;

/**
 * Created by aorehov on 05.05.16.
 */
public class DefaultMMXMessageFactory implements MMXMessagePresenterFactory {
    @Override
    public MMXLocationContract.Presenter createLocationPresenter(MMXLocationContract.View view) {
        DefaultMMXLocationPresenter presenter = new DefaultMMXLocationPresenter();
        presenter.setView(view);
        return presenter;
    }

    @Override
    public MMXMessageContract.Presenter createMessagePresenter(MMXMessageContract.View view) {
        DefaultMMXMessagePresenter presenter = new DefaultMMXMessagePresenter();
        presenter.setView(view);
        return presenter;
    }

    @Override
    public MMXPicMessageContract.Presenter createPicMessagePresenter(MMXPicMessageContract.View view) {
        DefaultMMXPicMessagePresenter presenter = new DefaultMMXPicMessagePresenter();
        presenter.setView(view);
        return presenter;
    }

    @Override
    public MMXPollContract.Presenter createPollPresenter(MMXPollContract.View view) {
        MMXPollOptionWrapperConverter converter = ChatSDK.getMmxObjectConverterFactory().createMmxPollOptionWrapperConverter();
        DefaultMMXPollPresenter presenter = new DefaultMMXPollPresenter(converter, view);
        presenter.setView(view);
        return presenter;
    }
}
