package com.magnet.imessage.model;

import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private static final String MESSAGE_TAG = "text";

    private MMXMessage mmxMessage;
    private String messageId;
    private boolean isDelivered;

    public MMXMessage getMmxMessage() {
        return mmxMessage;
    }

    public void setMmxMessage(MMXMessage mmxMessage) {
        if (mmxMessage != null) {
            messageId = mmxMessage.getId();
        }
        this.mmxMessage = mmxMessage;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getText() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getContent().get(MESSAGE_TAG);
    }

    public String getSenderFullName() {
        if (mmxMessage == null) {
            return "";
        }
        User user = mmxMessage.getSender();
        return user.getFirstName() + " " + user.getLastName();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getSender();
    }

    public static Message createMessage(MMXChannel channel, String text) {
        Message message = new Message();
        Map<String, String> content = new HashMap<>();
        content.put(MESSAGE_TAG, text);
        MMXMessage.Builder builder = new MMXMessage.Builder();
        builder.channel(channel).content(content);
        message.mmxMessage = builder.build();
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;
        if (messageId == null && message.messageId == null) {
            return true;
        } else if (messageId == null || message.messageId == null) {
            return false;
        }
        return messageId.equals(message.messageId);
    }

}
