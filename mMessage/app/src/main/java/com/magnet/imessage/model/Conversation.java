package com.magnet.imessage.model;

import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.internal.channel.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private List<UserInfo> suppliers;
    private List<Message> messages;
    private boolean hasUnreadMessage;
    private MMXChannel channel;

    public Conversation() {
    }

    public List<UserInfo> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<UserInfo> suppliers) {
        this.suppliers = suppliers;
    }

    public boolean hasUnreadMessage() {
        return hasUnreadMessage;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

    public MMXChannel getChannel() {
        return channel;
    }

    public void setChannel(MMXChannel channel) {
        this.channel = channel;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<MMXMessage> mmxMessages) {
        if (mmxMessages != null) {
            messages = new ArrayList<>(mmxMessages.size());
            for (MMXMessage mmxMessage : mmxMessages) {
                Message message = new Message();
                message.setMmxMessage(mmxMessage);
                message.setDelivered(true);
                messages.add(message);
            }
        } else {
            messages = new ArrayList<>();
        }
    }

    public boolean isLoaded() {
        return suppliers != null && messages != null && channel != null;
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    public void sendMessage(final Message message, final MMXChannel.OnFinishedListener<String> listener) {
        if (channel != null) {
            channel.publish(message.getMmxMessage(), new MMXChannel.OnFinishedListener<String>() {
                @Override
                public void onSuccess(String s) {
                    addMessage(message);
                    listener.onSuccess(s);
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    listener.onFailure(failureCode, throwable);
                }
            });
        }
    }

}
