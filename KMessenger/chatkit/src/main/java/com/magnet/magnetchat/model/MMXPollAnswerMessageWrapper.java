package com.magnet.magnetchat.model;

import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;

import java.util.Date;

/**
 * Created by aorehov on 10.05.16.
 */
public class MMXPollAnswerMessageWrapper extends MMXMessageWrapper {

    private String text;

    public MMXPollAnswerMessageWrapper(MMXMessage obj, String text) {
        this(obj, TYPE_VOTE_ANSWER, obj.getTimestamp() == null ? new Date() : obj.getTimestamp(),text);
    }

    public MMXPollAnswerMessageWrapper(MMXMessage obj, int type, Date date, String text) {
        super(obj, type, false, date);
        this.text = text;
    }

    @Override
    public String getTextMessage() {
        return text;
    }
}
