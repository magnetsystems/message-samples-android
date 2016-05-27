package com.magnet.magnetchat;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public interface Constants {

    String ACTION_CHAT = "com.magnet.magnetchat.CHAT";

    int CONVERSATION_PAGE_SIZE = 20;

    int MESSAGE_PAGE_SIZE = 30;

    int MESSAGE_LOADING_OFFSET = 7;

    int MESSAGE_SAFE_BUFFER = 2;

    int USER_PAGE_SIZE = 10;

    int PRE_FETCHED_MESSAGE_SIZE = 30;

    int PRE_FETCHED_SUBSCRIBER_SIZE = 10;

    String TAG_CHANNEL = "mmx.channel";

    String TAG_CHANNEL_NAME = "mmx.channel.name";

    String TAG_CREATE_WITH_RECIPIENTS = "mmx.channel.recipients";

    String MAP_URL = "https://www.google.com.ua/maps/@%f,%f,%dz?hl=en";

    String MMX_DATE_FORMAT = "MMM d, h:mm a";

    int MMX_RC_TAKE_PIC = 0x00F0;
    int MMX_RC_CREATE_POLL = 0x00F1;
    int MMX_RC_GET_PIC = 0x00F4;
    String NA = "NA";
}
