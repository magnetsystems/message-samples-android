/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.core.managers;

import com.magnet.magnetchat.callbacks.NewMessageProcessListener;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.User;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatManager {
    private static final String TAG = "ChannelCacheManager";

    private static ChatManager _instance;

    private List<MMXChannel> allSubscriptions;

    /**
     * Key is channel name
     */
    private Map<String, Chat> conversations;
    /**
     * Key is channel owner id
     */
    private Map<String, MMXMessage> messagesToApproveDeliver;

    private AtomicBoolean hasNewChat = new AtomicBoolean(false);

    private final Comparator<Chat> conversationComparator = new Comparator<Chat>() {
        @Override
        public int compare(Chat lhs, Chat rhs) {
            return 0 - lhs.getLastPublishedTime().compareTo(rhs.getLastPublishedTime());
        }
    };

    private ChatManager() {
        conversations = new HashMap<>();
    }

    public static ChatManager getInstance() {
        if (null == _instance) {
            _instance = new ChatManager();
        }

        return _instance;
    }

    public void setAllSubscriptions(List<MMXChannel> allSubscriptions) {
        if (null != this.allSubscriptions) {
            this.allSubscriptions.clear();
            conversations.clear();
        }
        this.allSubscriptions = allSubscriptions;
    }

    public List<MMXChannel> getAllSubscriptions() {
        return allSubscriptions;
    }

    public List<MMXChannel> getSubscriptions(int offset, int limit) {
        if (null != allSubscriptions) {
            int size = allSubscriptions.size();

            if (limit > 0) {
                if (offset >= 0 && offset < size) {
                    return (offset + limit) > size ? allSubscriptions.subList(offset, size)
                            : allSubscriptions.subList(offset, offset + limit);
                }
            } else {
                return allSubscriptions;
            }
        }

        return Collections.EMPTY_LIST;
    }

    public Collection<Chat> getConversations() {
        return conversations.values();
    }

    public Map<String, MMXMessage> getMessagesToApproveDeliver() {
        if (messagesToApproveDeliver == null) {
            messagesToApproveDeliver = new HashMap<>();
        }
        return messagesToApproveDeliver;
    }

    public void addConversation(Chat conversation) {
        if (null != conversation) {
            Chat existingConversation = getConversationByName(conversation.getChannel().getName());
            if (existingConversation == null) {
                if (allSubscriptions != null && !allSubscriptions.contains(conversation.getChannel())) {
                    if (allSubscriptions.size() >= conversations.size()) {
                        allSubscriptions.add(conversations.size(), conversation.getChannel());
                    }
                }

                conversations.put(conversation.getChannel().getName(), conversation);
                //TODO : handling new message
                //conversation.setHasUnreadMessage(true);
                //conversation.setLastPublishedTime(new Date());
            } else {
                boolean newMessageAdded = existingConversation.mergeFrom(conversation);
            }

            hasNewChat.set(true);
        }
    }

    public void removeConversation(String channelName) {
        if (conversations != null && conversations.containsKey(channelName)) {
            conversations.remove(channelName);
            hasNewChat.set(true);
        }
    }

    public boolean isConversationListUpdated() {
        if(hasNewChat.get()) {
            return true;
        }

        for(Chat c : getConversations()) {
            if(c.hasUpdate()) {
                return true;
            }
        }

        return false;
    }

    public void resetConversationListUpdated() {
        hasNewChat.set(false);

        for(Chat c : getConversations()) {
            c.resetUpdate();
        }
    }

    public void approveMessage(String messageId) {
        MMXMessage message = getMessagesToApproveDeliver().get(messageId);
        if (message != null) {
            //message.setIsDelivered(true);
            messagesToApproveDeliver.remove(messageId);
        }
    }

    public Chat getConversationByName(String name) {
        if (name == null) {
            return null;
        }
        return conversations.get(name.toLowerCase());
    }

    public void resetConversations() {
        conversations.clear();
        hasNewChat.set(true);
    }

    public void handleIncomingMessage(final MMXMessage mmxMessage, final NewMessageProcessListener listener) {
        Logger.debug(TAG, "handle incoming  new message : " + mmxMessage);
        MMXChannel channel = mmxMessage.getChannel();
        if (channel != null && !StringUtil.isStringValueEqual(mmxMessage.getSender().getUserIdentifier(),
            User.getCurrentUserId())) {
            final String channelName = channel.getName();
            Chat conversation = ChatManager.getInstance().getConversationByName(channelName);
            if (conversation != null) {
                conversation.addMessage(mmxMessage, true);
                if (null != listener) {
                    listener.onProcessSuccess(conversation, mmxMessage, false);
                }
            } else {
                ChannelHelper.getChannelDetails(mmxMessage.getChannel(), null, new ChannelHelper.OnReadChannelDetailListener() {
                    @Override
                    public void onSuccessFinish(Chat conversation) {
                        addConversation(conversation);
                        conversation.addMessage(mmxMessage, true);

                        if (null != listener) {
                            listener.onProcessSuccess(conversation, mmxMessage, true);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Logger.error(TAG, "Failed to load channel details for channel : " + channelName);

                        if (null != listener) {
                            listener.onProcessFailure(throwable);
                        }
                    }
                });
            }
        }
    }

}
