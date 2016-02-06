/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.core;

import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConversationCache {
    private static ConversationCache _instance;

    private Map<String, Conversation> conversations;
    private Map<String, Message> messagesToApproveDeliver;

    private AtomicBoolean isConversationListUpdated = new AtomicBoolean(false);

    private Comparator<Conversation> conversationComparator = new Comparator<Conversation>() {
        @Override public int compare(Conversation lhs, Conversation rhs) {
            return 0 - lhs.getLastActiveTime().compareTo(rhs.getLastActiveTime());
        }
    };

    private ConversationCache() {
        conversations = new TreeMap<>();
    }

    public static ConversationCache getInstance() {
        if (null == _instance) {
            _instance = new ConversationCache();
        }

        return _instance;
    }

    public List<Conversation> getConversations() {
        ArrayList<Conversation> list = new ArrayList<>(conversations.values());
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
        conversations.put(channelName, conversation);
        isConversationListUpdated.set(true);
    }

    public void removeConversation(String channelName) {
        if (conversations != null && conversations.containsKey(channelName)) {
            conversations.remove(channelName);
            isConversationListUpdated.set(true);
        }
    }

    public Conversation getConversation(String channelName) {
        return conversations.get(channelName);
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
            message.setIsDelivered(true);
            messagesToApproveDeliver.remove(messageId);
        }
    }


    public Conversation getConversationByName(String name) {
        return conversations.get(name);
    }

    public void resetConversations() {
        conversations.clear();
        isConversationListUpdated.set(true);
    }

}
