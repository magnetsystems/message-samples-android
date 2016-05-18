package com.magnet.magnetchat.presenters.chatlist;

/**
 * Created by aorehov on 05.05.16.
 */
public interface MMXPicMessageContract {
    interface Presenter extends BaseMMXMessagePresenter {

        String getImageURL();
    }

    interface View extends BaseMMXMessageView {
        void onPicture(String url);
    }
}
