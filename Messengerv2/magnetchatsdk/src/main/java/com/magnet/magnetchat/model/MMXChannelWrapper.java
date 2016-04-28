package com.magnet.magnetchat.model;

import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 28.04.16.
 */
public class MMXChannelWrapper extends MMXObjectWrapper<MMXChannel> {

    private long messagesAnount;

    public MMXChannelWrapper(MMXChannel obj) {
        super(obj);
        messagesAnount = obj.getNumberOfMessages();
    }

    public long getMessagesAnount() {
        return messagesAnount;
    }

    public void setMessagesAnount(long messagesAnount) {
        this.messagesAnount = messagesAnount;
    }
}
