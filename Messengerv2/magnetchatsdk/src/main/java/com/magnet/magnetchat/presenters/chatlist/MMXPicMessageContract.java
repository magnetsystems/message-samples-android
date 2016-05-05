package com.magnet.magnetchat.presenters.chatlist;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXPicMessageContract {
    interface Presenter extends BaseMMXMessagePresenter {

    }

    interface View extends BaseMMXMessageView {
        void onPicture(String url);
    }
}
