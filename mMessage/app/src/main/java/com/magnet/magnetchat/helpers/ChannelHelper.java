package com.magnet.magnetchat.helpers;

import android.content.Intent;

import com.magnet.magnetchat.core.CurrentApplication;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ChannelMatchType;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelHelper {

    public static final String ACTION_ADDED_CONVERSATION = "com.magnet.imessage.ADDED_CONVERSATION";

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
            public void onSuccess(List<MMXChannel> channels) {
                Logger.debug("read conversations", "success");
                for (MMXChannel channel : channels) {
                    readChannelInfo(channel, new OnReadChannelInfoListener() {
                        @Override
                        public void onSuccessFinish(Conversation conversation) {
                            CurrentApplication.getInstance().addConversation(conversation.getChannel().getName(), conversation);
                            CurrentApplication.getInstance().sendBroadcast(new Intent(ACTION_ADDED_CONVERSATION));
                            if (listener != null)
                                listener.onSuccessFinish(conversation);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (listener != null)
                                listener.onFailure(throwable);
                        }
                    });
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("read conversations", throwable);
                if (listener != null)
                    listener.onFailure(throwable);
            }
        });
    }

    public void rereadConversations(final OnReadChannelInfoListener listener) {
        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
            @Override
            public void onSuccess(List<MMXChannel> channels) {
                Logger.debug("reread conversations", "success");
                for (MMXChannel channel : channels) {
                    if (CurrentApplication.getInstance().getConversations().get(channel.getName()) == null) {
                        readChannelInfo(channel, new OnReadChannelInfoListener() {
                            @Override
                            public void onSuccessFinish(Conversation conversation) {
                                CurrentApplication.getInstance().addConversation(conversation.getChannel().getName(), conversation);
                                CurrentApplication.getInstance().sendBroadcast(new Intent(ACTION_ADDED_CONVERSATION));
                                if (listener != null)
                                    listener.onSuccessFinish(conversation);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                if (listener != null)
                                    listener.onFailure(throwable);
                            }
                        });
                    }
                }
                if (CurrentApplication.getInstance().getConversations().size() > channels.size()) {
                    List<String> channelsNames = new ArrayList<>(CurrentApplication.getInstance().getConversations().keySet());
                    for (String name : channelsNames) {
                        boolean contains = false;
                        for (MMXChannel channel : channels) {
                            if (channel.getName().equals(name)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            CurrentApplication.getInstance().getConversations().remove(name);
                        }
                    }
                    CurrentApplication.getInstance().sendBroadcast(new Intent(ACTION_ADDED_CONVERSATION));
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("reread conversations", throwable);
                if (listener != null)
                    listener.onFailure(throwable);
            }
        });
    }

    public void readChannelInfo(final MMXChannel channel, final OnReadChannelInfoListener listener) {
        if (channel == null) {
            return;
        }
        final Conversation conversation = new Conversation();
        conversation.setChannel(channel);
        channel.getAllSubscribers(100, 0, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                conversation.setSuppliers(new HashMap<String, User>());
                Logger.debug("channel subscribers", "success. channel " + channel.getName());
                for (User user : userListResult.items) {
                    if (!user.getUserIdentifier().equals(User.getCurrentUserId())) {
                        conversation.addSupplier(user);
                    }
                }
                Date now = new Date();
                Date weekAgo = DateHelper.getWeekAgo();
                channel.getMessages(weekAgo, now, 1000, 0, true, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
                    @Override
                    public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
                        Logger.debug("channel messages", "success. channel " + channel.getName());
                        for (MMXMessage mmxMessage : mmxMessageListResult.items) {
                            conversation.addMessage(Message.createMessageFrom(mmxMessage));
                        }
                        if (listener != null) {
                            listener.onSuccessFinish(conversation);
                        }
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        Logger.error("channel messages", throwable);
                        if (listener != null) {
                            listener.onFailure(throwable);
                        }
                    }
                });
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("channel subscribers", throwable);
                if (listener != null)
                    listener.onFailure(throwable);
            }
        });
    }

    public void updateConversationUserList(final Conversation conversation, final OnReadChannelInfoListener listener) {
        final MMXChannel channel = conversation.getChannel();
        if (channel == null) {
            return;
        }
        channel.getAllSubscribers(100, 0, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                conversation.setSuppliers(new HashMap<String, User>());
                Logger.debug("channel subscribers", "success. channel " + channel.getName());
                for (User user : userListResult.items) {
                    if (!user.getUserIdentifier().equals(User.getCurrentUserId())) {
                        conversation.addSupplier(user);
                    }
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("channel messages", throwable);
                if (listener != null)
                    listener.onFailure(throwable);
            }
        });
    }

    public void addUserToConversation(final Conversation conversation, final User user, final OnAddUserListener listener) {
        if (conversation.getSuppliers().get(user.getUserIdentifier()) == null) {
            List<String> userInConversation = new ArrayList<>();
            for (User supplier : conversation.getSuppliers().values()) {
                userInConversation.add(supplier.getUserIdentifier());
            }
            userInConversation.add(User.getCurrentUserId());
            userInConversation.add(user.getUserIdentifier());
            findChannelByUsers(userInConversation, new OnFindChannelByUsersListener() {
                @Override
                public void onSuccessFound(final List<MMXChannel> mmxChannels) {
                    if (mmxChannels.size() == 0) {
                        Set<User> userSet = new HashSet<>();
                        userSet.add(user);
                        conversation.getChannel().addSubscribers(userSet, new MMXChannel.OnFinishedListener<List<String>>() {
                            @Override
                            public void onSuccess(List<String> strings) {
                                Logger.debug("add user", "success");
                                conversation.addSupplier(user);
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
                    } else {
                        if (listener != null) {
                            listener.onUserSetExists(mmxChannels.get(0).getName());
                        }
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (listener != null) {
                        listener.onFailure(throwable);
                    }
                }
            });
        } else {
            if (listener != null) listener.onWasAlreadyAdded();
        }
    }

    public String getNameForChannel() {
        return DateHelper.getDateWithoutSpaces();
    }

    public void createChannelForUsers(final String userId, final OnCreateChannelListener listener) {
        findChannelByUsers(Arrays.asList(userId, User.getCurrentUserId()), new OnFindChannelByUsersListener() {
            @Override
            public void onSuccessFound(List<MMXChannel> mmxChannels) {
                if (mmxChannels.size() > 0) {
                    Logger.debug("channel exists");
                    if (listener != null)
                        listener.onChannelExists(mmxChannels.get(0));
                } else {
                    Set<String> users = new HashSet<>();
                    users.add(userId);
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

    public void receiveMessage(MMXMessage mmxMessage) {
        Message message = Message.createMessageFrom(mmxMessage);
        if (mmxMessage.getChannel() != null) {
            Conversation conversation = CurrentApplication.getInstance().getConversationByName(mmxMessage.getChannel().getName());
            if (conversation != null) {
                conversation.addMessage(message);
                User sender = message.getMmxMessage().getSender();
                if (sender != null && !sender.equals(User.getCurrentUser())) {
                    if (conversation.getSuppliers().get(sender.getUserIdentifier()) == null) {
                        conversation.addSupplier(sender);
                    }
                    conversation.setHasUnreadMessage(true);
                }
            } else {
                readChannelInfo(mmxMessage.getChannel(), null);
            }
        }
        Logger.debug("new message");
        mmxMessage.acknowledge(new MMXMessage.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.debug("acknowledge", "success");
            }

            @Override
            public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                Logger.error("acknowledge", throwable, "error");
            }
        });
    }

    public void unsubscribeFromChannel(final Conversation conversation, final OnLeaveChannelListener listener) {
        final MMXChannel channel = conversation.getChannel();
        if (channel != null) {
            channel.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    CurrentApplication.getInstance().getConversations().remove(channel.getName());
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
                    CurrentApplication.getInstance().getConversations().remove(channel.getName());
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
