package com.magnet.imessage.helpers;

import com.magnet.imessage.core.CurrentApplication;
import com.magnet.imessage.model.Conversation;
import com.magnet.imessage.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ChannelMatchType;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.internal.channel.ChannelSummaryResponse;
import com.magnet.mmx.client.internal.channel.PubSubItemChannel;
import com.magnet.mmx.client.internal.channel.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChannelHelper {

    private static ChannelHelper instance;

    public interface OnReadChannelInfoListener {
        void onSuccessFinish(Conversation lastConversation);

        void onFailure(Throwable throwable);
    }

    public interface OnCreateChannelListener {
        void onSuccessCreated(MMXChannel channel);

        void onChannelExists(MMXChannel channel);

        void onFailureCreated(Throwable throwable);
    }

    private ChannelHelper() {

    }

    public static ChannelHelper getInstance() {
        if (instance == null) {
            instance = new ChannelHelper();
        }
        return instance;
    }

    /*public void readSubscribersToConversation(final MMXChannel channel, final Conversation conversation, final OnReadChannelInfoListener listener) {
        channel.getAllSubscribers(100, 0, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                Logger.debug("channel subscribers", "success. channel " + channel.getName());
                List<User> users = new ArrayList<User>(userListResult.totalCount);
                for (User user : userListResult.items) {
                    if (!user.getUserIdentifier().equals(User.getCurrentUserId())) {
                        users.add(user);
                    }
                }
                conversation.setSuppliers(users);
                listener.onSuccessFinish();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("channel messages", throwable);
                listener.onFailure(throwable);
            }
        });
    }

    public void readMessagesToConversation(final MMXChannel channel, final Conversation conversation, final OnReadChannelInfoListener listener) {
        Date now = new Date();
        Date dayAgo = new Date(now.getTime() - (24 * 60 * 60 * 1000l));
        channel.getMessages(dayAgo, now, 1000, 0, false, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
            @Override
            public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
                Logger.debug("channel messages", "success. channel " + channel.getName());
                conversation.setMessages(mmxMessageListResult.items);
                listener.onSuccessFinish();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("channel messages", throwable);
                listener.onFailure(throwable);
            }
        });
    }*/

    public void readConversations(final OnReadChannelInfoListener listener) {
        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
            @Override
            public void onSuccess(List<MMXChannel> channels) {
                Logger.debug("read conversations", "success");
                CurrentApplication.getInstance().setConversations(new ArrayList<Conversation>(channels.size()));
                readChannelsInfo(channels, listener);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("read conversations", throwable);
                listener.onFailure(throwable);
            }
        });
    }

    public void readChannelsInfo(List<MMXChannel> channels, final OnReadChannelInfoListener listener) {
        final Map<String, MMXChannel> channelMap = new HashMap<>(channels.size());
        for (MMXChannel channel : channels) {
            channelMap.put(channel.getName(), channel);
        }
        MMXChannel.getChannelSummary(new HashSet<>(channels), 1000, 100, new MMXChannel.OnFinishedListener<List<ChannelSummaryResponse>>() {
            @Override
            public void onSuccess(List<ChannelSummaryResponse> channelSummaryResponses) {
                Conversation lastConversation = null;
                for (ChannelSummaryResponse channelResponse : channelSummaryResponses) {
                    Logger.debug("HOONUNU name", channelResponse.getChannelName());
                    Logger.debug("HOONUNU time", channelResponse.getLastPublishedTime());
                    Logger.debug("HOONUNU items", channelResponse.getPublishedItemCount());
                    for (PubSubItemChannel itemChannel : channelResponse.getMessages()) {
                        Logger.debug("HOONUNU message date", itemChannel.getMetaData().getCreationDate());
                        Logger.debug("HOONUNU message data", itemChannel.getMetaData().getData());
                        Logger.debug("HOONUNU message type", itemChannel.getMetaData().getMtype());
                        Logger.debug("HOONUNU message user", itemChannel.getPublisherInfo().getDisplayName());
                    }
                    for (UserInfo info : channelResponse.getSubscribers()) {
                        Logger.debug("HOONUNU user name", info.getDisplayName());
                    }

                    Conversation conversation = new Conversation();
                    conversation.setChannel(channelMap.get(channelResponse.getChannelName()));
                    conversation.setSuppliers(channelResponse.getSubscribers());
//                            conversation.setMessages();
                    CurrentApplication.getInstance().getConversations().add(conversation);
                    lastConversation = conversation;
                }
                listener.onSuccessFinish(lastConversation);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("read conversations", throwable);
                listener.onFailure(throwable);
            }
        });
    }

    public String getNameForChannel(String userId) {
        return "" + new Random().nextInt();
    }

    public void createChannelForUsers(final String userId, final OnCreateChannelListener listener) {
        User.getUsersByUserIds(Arrays.asList(userId, User.getCurrentUserId()), new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> userList) {
                MMXChannel.findChannelsBySubscribers(new HashSet<User>(userList), ChannelMatchType.EXACT_MATCH, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
                    @Override
                    public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                        if (mmxChannelListResult.totalCount > 0) {
                            listener.onChannelExists(mmxChannelListResult.items.get(0));
                        } else {
                            Set<String> users = new HashSet<>();
                            users.add(userId);
                            MMXChannel.create(getNameForChannel(userId), "", false, MMXChannel.PublishPermission.SUBSCRIBER, users, new MMXChannel.OnFinishedListener<MMXChannel>() {
                                @Override
                                public void onSuccess(MMXChannel channel) {
                                    Logger.debug("create conversation", "success");
                                    listener.onSuccessCreated(channel);
                                }

                                @Override
                                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                                    Logger.error("create conversation", throwable);
                                    listener.onFailureCreated(throwable);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        Logger.error("find chan by users", throwable);
                        listener.onFailureCreated(throwable);
                    }
                });
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("find users by ids", apiError);
                listener.onFailureCreated(apiError);
            }
        });


    }

}
