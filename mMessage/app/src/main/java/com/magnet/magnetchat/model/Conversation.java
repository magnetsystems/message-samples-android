package com.magnet.magnetchat.model;

import android.location.Location;

import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversation {

    private Map<String, User> suppliers;
    private List<Message> messages;
    private boolean hasUnreadMessage;
    private MMXChannel channel;

    public interface OnSendMessageListener {
        void onSuccessSend(Message message);

        void onFailure(Throwable throwable);
    }

    public Conversation() {
    }

    public Map<String, User> getSuppliers() {
        if (suppliers == null) {
            suppliers = new HashMap<>();
        }
        return suppliers;
    }

    public List<User> getSuppliersList() {
        return new ArrayList<>(getSuppliers().values());
    }

    public void setSuppliers(Map<String, User> suppliers) {
        this.suppliers = suppliers;
    }

    public void addSupplier(User user) {
        getSuppliers().put(user.getUserIdentifier(), user);
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
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void addMessage(Message message) {
        if (!getMessages().contains(message)) {
            messages.add(message);
        }
    }

    public void sendTextMessage(final String text, final OnSendMessageListener listener) {
        if (channel != null) {
            Map<String, String> content = Message.makeContent(text);
            sendMessage(content, listener);
        } else {
            throw new Error();
        }
    }

    public void sendLocation(Location location, final OnSendMessageListener listener) {
        if (channel != null) {
            Map<String, String> content = Message.makeContent(location);
            sendMessage(content, listener);
        } else {
            throw new Error();
        }
    }

    public void sendVideo(final String filePath, final OnSendMessageListener listener) {
        if (channel != null) {
            File file = new File(filePath);
            Attachment attachment = new Attachment(file, Message.makeVideoFileType(filePath), file.getName(), "From " + UserHelper.getInstance().userNameAsString(User.getCurrentUser()));
            Map<String, String> content = Message.makeVideoContent();
            sendMessage(content, attachment, listener);
        } else {
            throw new Error();
        }
    }

    public void sendPhoto(final String filePath, final OnSendMessageListener listener) {
        if (channel != null) {
            File file = new File(filePath);
            Attachment attachment = new Attachment(file, Message.FILE_TYPE_PHOTO, file.getName(), "From " + UserHelper.getInstance().userNameAsString(User.getCurrentUser()));
            Map<String, String> content = Message.makePhotoContent();
            sendMessage(content, attachment, listener);
        } else {
            throw new Error();
        }
    }

    private void sendMessage(Map<String, String> content, final OnSendMessageListener listener) {
        sendMessage(content, null, listener);
    }

    private void sendMessage(Map<String, String> content, Attachment attachment, final OnSendMessageListener listener) {
        MMXMessage.Builder builder = new MMXMessage.Builder();
        builder.channel(channel).content(content);
        if (attachment != null) {
            builder.attachments(attachment);
        }
        final Message message = Message.createMessageFrom(builder.build());
        message.setCreationDate(new Date());
        channel.publish(message.getMmxMessage(), new MMXChannel.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.debug("send message", "success");
                addMessage(message);
                listener.onSuccessSend(message);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                listener.onFailure(throwable);
            }
        });
    }

    public String ownerId() {
        if (channel == null) {
            return null;
        }
        return channel.getOwnerId();
    }

}
