package com.magnet.magnetchat.presenters.chatlist;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public interface BaseMMXMessageView {
    void isNeedShowDate(boolean isShowDate);

    void onSetPostDate(Date date);

    void onShowUserLetters(String letters);

    void onShowUserPicture(String url);

    void onSenderName(String name);
}
