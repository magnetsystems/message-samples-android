/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.core.managers;

import android.util.Log;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelCacheManager {
    private static final String TAG = "ChannelCacheManager";

    private static ChannelCacheManager _instance;

    /**
     * Key is channel name
     */
    private Map<String, Conversation> conversations;
    /**
     * Key is channel owner id
     */
    private Map<String, Conversation> askConversations;
    private Map<String, Message> messagesToApproveDeliver;

    private List<MMXChannel> askChannels;
    private List<MMXChannel> personalChannels;

    private AtomicBoolean isConversationListUpdated = new AtomicBoolean(false);

    private Comparator<Conversation> conversationComparator = new Comparator<Conversation>() {
        @Override
        public int compare(Conversation lhs, Conversation rhs) {
            return 0 - lhs.getLastActiveTime().compareTo(rhs.getLastActiveTime());
        }
    };

    private ChannelCacheManager() {
        conversations = new HashMap<>();
        askConversations = new HashMap<>();
    }

    public static ChannelCacheManager getInstance() {
        if (null == _instance) {
            _instance = new ChannelCacheManager();
        }

        return _instance;
    }

    public void setSubscriptions(List<MMXChannel> channels) {
        resetConversations();

        if(null == askChannels) {
            askChannels = new ArrayList<>();
        } else {
            askChannels.clear();
        }
        if(null == personalChannels) {
            personalChannels = new ArrayList<>();
        } else {
            personalChannels.clear();
        }
        for(MMXChannel c : channels) {
            if(isAskChannel(c.getName())) {
                askChannels.add(c);
            } else if(!isGlobalChannel(c)) {
                personalChannels.add(c);
            }
        }

        Log.d(TAG, "-----------setSubscriptions, askChannels " + askChannels.size() + ",  personalChannels " + personalChannels.size());
    }

    public List<MMXChannel> getAskChannels() {
        return askChannels;
    }

    public List<MMXChannel> getAskChannels(int offset, int limit) {
        return getSubList(askChannels, offset, limit);
    }

    public List<MMXChannel> getPersonalChannels() {
        return personalChannels;
    }

    public List<MMXChannel> getPersonalChannels(int offset, int limit) {
        return getSubList(personalChannels, offset, limit);
    }

    public List<Conversation> getConversations() {
        ArrayList<Conversation> list = new ArrayList<>();
        for (Conversation c : conversations.values()) {
            if (isPersonalChannel(c.getChannel())) {
                list.add(c);
            }
        }
        Collections.sort(list, conversationComparator);
        return list;
    }

    private boolean isPersonalChannel(MMXChannel channel) {
        return !isGlobalChannel(channel)
                && !channel.getName().toLowerCase().startsWith(ChannelHelper.ASK_MAGNET.toLowerCase());
    }

    private boolean isGlobalChannel(MMXChannel channel) {
        return channel.getName().startsWith("global_");
    }

    public List<Conversation> getSupportConversations() {
        ArrayList<Conversation> list = new ArrayList<>();
        for (Conversation c : askConversations.values()) {
            if(null != c.getMessages() && !c.getMessages().isEmpty()) {
                list.add(c);
            }
        }
        Collections.sort(list, conversationComparator);
        return list;
    }

    public Map<String, Message> getMessagesToApproveDeliver() {
        if (messagesToApproveDeliver == null) {
            messagesToApproveDeliver = new HashMap<>();
        }
        return messagesToApproveDeliver;
    }

    public void addConversation(String channelName, Conversation conversation) {
        if (isAskChannel(channelName)) {
            askConversations.put(conversation.getChannel().getOwnerId().toLowerCase(), conversation);
            //Log.d(TAG, "----add ask magnet channel " + askConversations.size() + " / " + askChannels.size());
        } else {
            conversations.put(channelName.toLowerCase(), conversation);
            //Log.d(TAG, "----add ask personal channel " + conversations.size() + " / " + personalChannels.size());
        }
        isConversationListUpdated.set(true);
    }

    private boolean isAskChannel(String channelName) {
        return channelName.equalsIgnoreCase(ChannelHelper.ASK_MAGNET) && UserHelper.isMagnetSupportMember();
    }

    public void removeConversation(String channelName) {
        if (conversations != null && conversations.containsKey(channelName)) {
            conversations.remove(channelName);
            isConversationListUpdated.set(true);
        }
    }

    public boolean isConversationListUpdated() {
        return isConversationListUpdated.get();
    }

    public void resetConversationListUpdated() {
        isConversationListUpdated.set(false);
    }

    public void setConversationListUpdated() {
        isConversationListUpdated.set(true);
    }

    public void approveMessage(String messageId) {
        Message message = getMessagesToApproveDeliver().get(messageId);
        if (message != null) {
            message.setMessageStatus(Message.MessageStatus.DELIVERED);
            messagesToApproveDeliver.remove(messageId);
        }
    }

    public Conversation getConversationByChannel(MMXChannel channel) {
        if (channel != null && channel.getName() != null) {
            if (channel.getName().equalsIgnoreCase(ChannelHelper.ASK_MAGNET)) {
                return getAskConversationByOwnerId(channel.getOwnerId());
            } else {
                return getConversationByName(channel.getName());
            }
        }
        return null;
    }

    public Conversation getConversationByName(String name) {
        if (name == null) {
            return null;
        }
        return conversations.get(name.toLowerCase());
    }

    public Conversation getAskConversationByOwnerId(String ownerId) {
        if (ownerId == null) {
            return null;
        }
        return askConversations.get(ownerId.toLowerCase());
    }

    public int getSupportUnreadCount() {
        int supportUnreadCount = 0;
        for (Conversation conversation : getSupportConversations()) {
            if (conversation.hasUnreadMessage()) {
                supportUnreadCount++;
            }
        }
        return supportUnreadCount;
    }

    public void resetConversations() {
        conversations.clear();
        askConversations.clear();
        isConversationListUpdated.set(true);
    }

    private List<MMXChannel> getSubList(List<MMXChannel> channels, int offset, int limit) {
        if (null != channels && !channels.isEmpty()) {
            int size = channels.size();

            if (limit > 0) {
                if (offset >= 0 && offset < size) {
                    return (offset + limit) > size ? channels.subList(offset, size)
                        : channels.subList(offset, offset + limit);
                }
            } else {
                return channels;
            }
        }

        return Collections.EMPTY_LIST;
    }

}
