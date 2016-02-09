package com.magnet.magnetchat.helpers;

import android.content.Intent;

import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ChannelMatchType;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelHelper {
    private static final String TAG = ChannelHelper.class.getSimpleName();
    public static final String ACTION_ADDED_CONVERSATION = "com.magnet.magnetchat.ADDED_CONVERSATION";

    private static ChannelHelper instance;

    public interface OnReadChannelInfoListener {
        void onSuccessFinish(Conversation conversation);

        void onFailure(Throwable throwable);
    }

    public interface OnCreateChannelListener {
        void onSuccessCreated(MMXChannel channel);

        void onChannelExists(MMXChannel channel);

        void onFailureCreated(Throwable throwable);
    }

    public interface OnLeaveChannelListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    public interface OnFindChannelByUsersListener {
        void onSuccessFound(List<MMXChannel> mmxChannels);

        void onFailure(Throwable throwable);
    }

    public interface OnAddUserListener {
        void onSuccessAdded();

        void onUserSetExists(String channelSetName);

        void onWasAlreadyAdded();

        void onFailure(Throwable throwable);
    }

    private ChannelHelper() {

    }

    public static ChannelHelper getInstance() {
        if (instance == null) {
            instance = new ChannelHelper();
        }
        return instance;
    }

    public void readConversations(final OnReadChannelInfoListener listener) {
        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
            @Override
            public void onSuccess(final List<MMXChannel> channels) {
                Logger.debug(TAG, "getAllSubscriptions success : " + channels);
                ChannelCacheManager.getInstance().resetConversations();
                if(null != channels && channels.size() > 0) {
                    fetchChannelDetails(channels, listener);
                } else {
                    if (listener != null) {
                        listener.onSuccessFinish(null);
                    }
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error(TAG, throwable, "getAllSubscriptions failed");
                if (listener != null) {
                    listener.onFailure(throwable);
                }
            }
        });
    }

    public void readChannelInfo(final MMXChannel channel, final OnReadChannelInfoListener listener) {
        if (channel == null) {
            if (null != listener) {
                listener.onFailure(new Exception("channel shouldn't be null"));
            }
            return;
        }
        fetchChannelDetails(Arrays.asList(channel), listener);
    }

    public void fetchChannelDetails(final List<MMXChannel> channels, final OnReadChannelInfoListener listener) {
        MMXChannel.getChannelDetail(channels,
                new ChannelDetailOptions.Builder().numOfMessages(100).numOfSubcribers(10).build(),
                new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                    @Override
                    public void onSuccess(List<ChannelDetail> channelDetails) {
                        Logger.debug(TAG, "getChannelDetail successfully : " + channelDetails);
                        Conversation lastConversation = null;
                        for (int i = 0; i < channelDetails.size(); i++) {
                            final ChannelDetail channelDetail = channelDetails.get(i);
                            final MMXChannel channel = channelDetail.getChannel();
                            final Conversation conversation = new Conversation();
                            lastConversation = conversation;
                            conversation.setChannel(channel);

                            Logger.debug(TAG, "channel subscribers ", channelDetail.getSubscribers(), " channel ", channel.getName());
                            for (UserProfile up : channelDetail.getSubscribers()) {
                                if (!up.getUserIdentifier().equals(User.getCurrentUserId())) {
                                    conversation.addSupplier(up);
                                }
                            }

                            Logger.debug(TAG, "channel messages ", channelDetail.getMessages(), " channel ", channel.getName());
                            for (MMXMessage mmxMessage : channelDetail.getMessages()) {
                                conversation.addMessage(Message.createMessageFrom(mmxMessage));
                            }

                            ChannelCacheManager.getInstance().addConversation(channel.getName(), conversation);
                        }

                        if (listener != null) {
                            listener.onSuccessFinish(lastConversation);
                        }
                        CurrentApplication.getInstance().sendBroadcast(new Intent(ACTION_ADDED_CONVERSATION));
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode,
                                          Throwable throwable) {
                        Logger.error(TAG, "getChannelDetail failed : ", throwable);
                        if (listener != null) {
                            listener.onFailure(throwable);
                        }
                    }
                });
    }

    public void addUserToConversation(final Conversation conversation, final List<UserProfile> userList, final OnAddUserListener listener) {
        Set<User> userSet = new HashSet<>();
        for (UserProfile user : userList) {
            if (null == conversation.getSupplier(user.getUserIdentifier())) {
                userSet.add((User) user);
            }
        }
        conversation.getChannel().addSubscribers(userSet, new MMXChannel.OnFinishedListener<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                Logger.debug("add user", "success");
                for (UserProfile user : userList) {
                    conversation.addSupplier(user);
                }

                if (listener != null) {
                    listener.onSuccessAdded();
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("add user", throwable);
                if (listener != null) {
                    listener.onFailure(throwable);
                }
            }
        });
    }

    public String getNameForChannel() {
        return DateHelper.getDateWithoutSpaces();
    }

    public void createChannelForUsers(final String[] userIds, final OnCreateChannelListener listener) {
        List<String> userIdList = new ArrayList<>(Arrays.asList(userIds));
        userIdList.add(User.getCurrentUserId());
        findChannelByUsers(userIdList, new OnFindChannelByUsersListener() {
            @Override
            public void onSuccessFound(List<MMXChannel> mmxChannels) {
                if (mmxChannels.size() == 1) { // Use existing one if only one found
                    Logger.debug("channel with same subscribers exists, use it");
                    if (listener != null)
                        listener.onChannelExists(mmxChannels.get(0));
                } else {
                    Set<String> users = new HashSet<>();
                    users.addAll(Arrays.asList(userIds));
                    String summary = User.getCurrentUser().getUserName();
                    MMXChannel.create(getNameForChannel(), summary, false, MMXChannel.PublishPermission.SUBSCRIBER, users, new MMXChannel.OnFinishedListener<MMXChannel>() {
                        @Override
                        public void onSuccess(MMXChannel channel) {
                            Logger.debug("create conversation", "success");
                            if (listener != null) {
                                listener.onSuccessCreated(channel);
                            }
                        }

                        @Override
                        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                            Logger.error("create conversation", throwable);
                            if (listener != null) {
                                listener.onFailureCreated(throwable);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (listener != null) listener.onFailureCreated(throwable);
            }
        });
    }

    public void findChannelByUsers(List<String> userIds, final OnFindChannelByUsersListener listener) {
        User.getUsersByUserIds(userIds, new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> userList) {
                MMXChannel.findChannelsBySubscribers(new HashSet<>(userList), ChannelMatchType.EXACT_MATCH, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
                    @Override
                    public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                        if (listener != null) {
                            listener.onSuccessFound(mmxChannelListResult.items);
                        }
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        Logger.error("find chan by users", throwable);
                        if (listener != null) {
                            listener.onFailure(throwable);
                        }
                    }
                });
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("find users by ids", apiError);
                if (listener != null) {
                    listener.onFailure(apiError);
                }
            }
        });
    }

    public void receiveMessage(final MMXMessage mmxMessage) {
        Logger.debug("new message");
        Message message = Message.createMessageFrom(mmxMessage);
        if (mmxMessage.getChannel() != null) {
            Conversation conversation = ChannelCacheManager.getInstance().getConversationByName(mmxMessage.getChannel().getName());
            if (conversation != null) {
                conversation.addMessage(message);
                conversation.setLastActiveTime(new Date());
                User sender = message.getMmxMessage().getSender();
                if (sender != null && !sender.equals(User.getCurrentUser())) {
                    if (conversation.getSupplier(sender.getUserIdentifier()) == null) {
                        conversation.addSupplier(sender);
                    }
                    conversation.setHasUnreadMessage(true);
                }
            } else {
                readChannelInfo(mmxMessage.getChannel(), new OnReadChannelInfoListener() {
                    @Override
                    public void onSuccessFinish(Conversation conversation) {
                        if(null != conversation) {
                            conversation.setHasUnreadMessage(true);
                            conversation.setLastActiveTime(new Date());
                        } else {
                            Logger.error(TAG, "Can't load conversation for new message : " + mmxMessage);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
            }
        }
    }

    public void unsubscribeFromChannel(final Conversation conversation, final OnLeaveChannelListener listener) {
        final MMXChannel channel = conversation.getChannel();
        if (channel != null) {
            channel.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    ChannelCacheManager.getInstance().removeConversation(channel.getName());
                    Logger.debug("unsubscribe", "success");
                    if (listener != null)
                        listener.onSuccess();
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Logger.error("unsubscribe", throwable);
                    if (listener != null)
                        listener.onFailure(throwable);
                }
            });
        }
    }

    public void deleteChannel(final Conversation conversation, final OnLeaveChannelListener listener) {
        final MMXChannel channel = conversation.getChannel();
        if (channel != null) {
            channel.delete(new MMXChannel.OnFinishedListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    ChannelCacheManager.getInstance().removeConversation(channel.getName());
                    Logger.debug("delete", "success");
                    if (listener != null)
                        listener.onSuccess();
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Logger.error("delete", throwable);
                    if (listener != null)
                        listener.onFailure(throwable);
                }
            });
        }
    }

}
