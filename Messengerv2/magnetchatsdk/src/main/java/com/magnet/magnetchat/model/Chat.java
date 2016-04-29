package com.magnet.magnetchat.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import com.magnet.magnetchat.helpers.MessageHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.MMXMessageUtil;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Chat extends ChannelDetail {
    private static final String TAG = Chat.class.getSimpleName();

    //private List<Message> mMessages = new ArrayList();
    private boolean hasUnreadMessage;
    private boolean hasRecipientsUpdate;
    private boolean hasMessageUpdate;

    private Comparator<UserProfile> userProfileComparator = new Comparator<UserProfile>() {
        @Override public int compare(UserProfile lhs, UserProfile rhs) {
            return 0 - lhs.getDisplayName().compareTo(rhs.getDisplayName());
        }
    };

    public Chat(Parcel source) {
        super(source);
    }

    public interface OnSendMessageListener {
        void onSuccessSend(MMXMessage message);

        void onFailure(Throwable throwable);
    }

    public Chat(MMXChannel channel, List<UserProfile> subscribers, UserProfile owner) {
        this(channel, subscribers, null, owner, null);

        for(UserProfile up : subscribers) {
            addSubscriber(up);
        }
    }

    public Chat(ChannelDetail channelDetail) {
        this(channelDetail.getChannel(), null, //Subscribers will be added below
            null, // Messages will be added below
            channelDetail.getOwner(), channelDetail.getLastPublishedTime());

        this.totalMessages = channelDetail.getTotalMessages();
        this.totalSubscribers = channelDetail.getTotalSubscribers();

        //Logger.debug(TAG, "channel subscribers ", channelDetail.getSortedSubscribers(), " channel ", channel.getName());
        mergeFrom(channelDetail.getSubscribers(), channelDetail.getMessages(), true);
    }

    protected Chat(MMXChannel channel, List<UserProfile> subscribers, List<MMXMessage> messages,
        UserProfile owner, Date lastPublishedTime) {
        this.channel = channel;
        this.owner = owner;
        this.lastPublishedTime = null != lastPublishedTime ? lastPublishedTime : new Date();
        this.messages = null != messages ? messages : new ArrayList<MMXMessage>();
        this.subscribers = null != subscribers ? subscribers : new ArrayList<UserProfile>();
    }

    @Override
    public List<UserProfile> getSubscribers() {
        return subscribers;
    }

    public boolean containSubscriber(UserProfile userProfile) {
        if(null == subscribers || subscribers.isEmpty()) {
            return false;
        }
        for(UserProfile up : subscribers) {
            if(up.getUserIdentifier().equals(userProfile.getUserIdentifier())) {
                return true;
            }
        }

        return false;
    }

    public List<UserProfile> getSortedSubscribers() {
        ArrayList<UserProfile> list = new ArrayList<>(getSubscribers());
        Collections.sort(list, userProfileComparator);
        return list;
    }

    public void addSubscriber(UserProfile user) {
        if (user != null && !user.getUserIdentifier().equals(User.getCurrentUserId()) && !containSubscriber(user)) {
            subscribers.add(user);
            hasRecipientsUpdate = true;
        }
    }

    public boolean hasUnreadMessage() {
        return hasUnreadMessage;
    }

    public boolean hasRecipientsUpdate() {
        return hasRecipientsUpdate;
    }

    public boolean hasMessageUpdate() {
        return hasMessageUpdate;
    }

    public void resetUpdate() {
        hasMessageUpdate = false;
        hasRecipientsUpdate = false;
    }

    public boolean hasUpdate() {
        return hasMessageUpdate || hasRecipientsUpdate;
    }

    public void setHasRecipientsUpdate(boolean hasRecipientsUpdate) {
        this.hasRecipientsUpdate = hasRecipientsUpdate;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

    public List<MMXMessage> getMessages(int offset, int limit) {
        if (limit > 0) {
            int size = messages.size();
            if (offset >= 0 && offset < size) {
                return (offset + limit) > size ? messages.subList(offset, size)
                    : messages.subList(offset, offset + limit);
            }
        } else {
            // return a copy
            return new ArrayList<>(messages);
        }

        return Collections.EMPTY_LIST;
    }

    public boolean addMessage(MMXMessage message, boolean isNewMessage) {
        if (!messages.contains(message)) {
            appendMessage(message, isNewMessage);
            hasMessageUpdate = true;

            lastPublishedTime = null != message.getTimestamp() ? message.getTimestamp() : new Date();

            totalMessages++;

            return true;
        }

        return false;
    }

    public boolean insertMessages(List<MMXMessage> mmxMessages) {
        boolean addedResult = false;
        if(null != mmxMessages && ! mmxMessages.isEmpty()) {
            for(int i = mmxMessages.size() - 1; i >=0 ; i--) {
                boolean thisAddResult = insertMessage(mmxMessages.get(i));
                addedResult = addedResult || thisAddResult;
            }
        }

        return addedResult;
    }

    public boolean mergeFrom(Chat conversation) {
        boolean newMessageAdded = false;
        if(null != conversation) {
            newMessageAdded = mergeFrom(conversation.getSubscribers(), conversation.getMessages(), false);
        }

        if (newMessageAdded) {
            setHasUnreadMessage(true);
        }

        if(null != conversation.getLastPublishedTime() && (conversation.getLastPublishedTime().getTime() > lastPublishedTime.getTime())) {
            lastPublishedTime = conversation.getLastPublishedTime();
        }

        return newMessageAdded;
    }

    public void sendTextMessage(final String text, final OnSendMessageListener listener) {
        if (channel != null) {
            Map<String, String> content = MMXMessageUtil.makeContent(text);
            sendMessage(content, listener);
        } else {
            throw new Error();
        }
    }

    public void sendLocation(Location location, final OnSendMessageListener listener) {
        if (channel != null) {
            Map<String, String> content = MMXMessageUtil.makeContent(location);
            sendMessage(content, listener);
        } else {
            throw new Error();
        }
    }

    public void sendVideo(final String filePath, final String mimeType, final OnSendMessageListener listener) {
        Logger.debug(TAG, "sending video " + filePath);
        if (channel != null) {
            File file = new File(filePath);
            Attachment attachment = new Attachment(file, mimeType, file.getName(), "From " + UserHelper.getDisplayName(User.getCurrentUser()));
            Map<String, String> content = MMXMessageUtil.makeVideoContent();
            sendMessage(content, attachment, listener);
        } else {
            throw new Error();
        }
    }

    public void sendPhoto(final String filePath, final String mimeType, final OnSendMessageListener listener) {
        Logger.debug(TAG, "sending photo " + filePath);
        if (channel != null) {
            File file = new File(filePath);
            Attachment attachment = new Attachment(file, mimeType, file.getName(), "From " + UserHelper.getDisplayName(User.getCurrentUser()));
            Map<String, String> content = MMXMessageUtil.makePhotoContent();
            sendMessage(content, attachment, listener);
        } else {
            throw new Error();
        }
    }

    public String getLastMessageSummary() {
        if (messages != null && messages.size() > 0) {
          return MessageHelper.getMessageSummary(messages.get(messages.size() - 1));
        }

        return "";
    }

    @Override public String toString() {
        return new StringBuilder("Chat { \n").append("channel : ").append(channel)
            .append("\nmessages : ").append(Arrays.deepToString(messages.toArray()))
            .append("\nsubscribers : ").append(Arrays.deepToString(subscribers.toArray()))
            .toString();
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
        final MMXMessage message = builder.build();
        channel.publish(message, new MMXChannel.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.debug("send message", "success");
                addMessage(message, false);
                listener.onSuccessSend(message);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                listener.onFailure(throwable);
            }
        });
    }

    private boolean insertMessage(MMXMessage message) {
        if (!messages.contains(message)) {
            messages.add(0, message);

            addSubscriber(message.getSender());

            return true;
        }

        return false;
    }

    private void appendMessage(MMXMessage message, boolean isNew) {
        messages.add(message);

        addSubscriber(message.getSender());

        if(isNew) {
            hasUnreadMessage = true;
        }
    }

    private boolean mergeFrom(List<UserProfile> subscribers, List<MMXMessage> messages, boolean toAppend) {
        boolean newMessageAdded = false;
        for (UserProfile up : subscribers) {
            if (owner == null && up.getUserIdentifier().equals(channel.getOwnerId())) {
                owner = up;
            }
            if (!up.getUserIdentifier().equals(User.getCurrentUserId())) {
                this.addSubscriber(up);
            }
        }

        for (MMXMessage message : messages) {
            if(toAppend) {
                appendMessage(message, false);
            } else {
                if (!messages.contains(message)) {
                    appendMessage(message, false);

                    newMessageAdded = true;
                }
            }
        }

        return newMessageAdded;
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
