package com.magnet.magnetchat.presenters.chatlist;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public interface BaseMMXMessageView {
    void isNeedShowDate(boolean isShowDate);

    void onSetPostDate(Date date);

    void onShowUserPicture(String url, String name);

    void onSenderName(String name);
}
