package com.magnet.magnetchat.model;

import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;

import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public class MMXChannelWrapper extends MMXObjectWrapper<ChannelDetail> {

    public static final int TYPE_CHANNEL = 0xFC01;

    private long messagesAmount;

    public MMXChannelWrapper(ChannelDetail obj) {
        super(obj, TYPE_CHANNEL);
        messagesAmount = obj.getChannel().getNumberOfMessages();
    }

    public MMXChannelWrapper(ChannelDetail obj, int type) {
        super(obj, type);
        this.messagesAmount = obj.getChannel().getNumberOfMessages();
    }

    public long getMessagesAmount() {
        return messagesAmount;
    }

    public void setMessagesAmount(long messagesAmount) {
        this.messagesAmount = messagesAmount;
    }

    public int getSubscribersAmount() {
        return obj.getTotalSubscribers();
    }

    public String getName(String defaultName) {
        if (obj.getTotalSubscribers() <= 2) {
            List<UserProfile> list = obj.getSubscribers();
            if (list == null || list.isEmpty()) {
                return "Chat";
            } else if (list.size() == 1) {
                UserProfile profile = list.get(0);
                return profile.getDisplayName();
            } else {
                UserProfile profile = list.get(0);
                if (profile.getUserIdentifier().equals(User.getCurrentUserId())) {
                    return list.get(1).getDisplayName();
                } else {
                    return profile.getDisplayName();
                }
            }
        } else
            return defaultName;
    }
}
