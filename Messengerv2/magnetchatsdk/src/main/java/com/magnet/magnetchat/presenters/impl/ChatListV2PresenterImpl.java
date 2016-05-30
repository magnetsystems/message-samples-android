package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.util.LazyLoadUtil;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Arrays;
import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
class ChatListV2PresenterImpl implements ChatListContract.Presenter, LazyLoadUtil.OnNeedLoadingCallback {

    private MMXChannelWrapper channel;
    private ChatListContract.View view;
    private BaseConverter<MMXMessage, MMXMessageWrapper> converter;
    private LazyLoadUtil lazyLoadUtil;
    private ChatListContract.MMXChannelListener channelListener;
    private final AppScopePendingStateRepository repository;

    public ChatListV2PresenterImpl(ChatListContract.View view, BaseConverter<MMXMessage, MMXMessageWrapper> converter, AppScopePendingStateRepository repository) {
        this.view = view;
        this.converter = converter;
        lazyLoadUtil = new LazyLoadUtil(Constants.MESSAGE_PAGE_SIZE, (int) (Constants.MESSAGE_PAGE_SIZE * 0.60), this);
        this.repository = repository;
    }

    @Override
    public void setChat(MMXChannelWrapper chat) {
        setChannelWrapper(chat);
    }

    @Override
    public void setChat(ChannelDetail channel) {
        setChannelWrapper(new MMXChannelWrapper(channel));
    }

    @Override
    public void onCreatedPoll() {
        loadMessages(0, 3);
    }

    @Override
    public void setChat(List<UserProfile> users) {
        List<String> list = MMXObjectsHelper.convertToIdList(users);
        String currentUserId = User.getCurrentUserId();
        if (currentUserId != null && !list.contains(User.getCurrentUserId()))
            list.add(currentUserId);

        view.onRefreshing();
        ChannelHelper.createChannelForUsers(list, new ChannelHelper.OnCreateChannelListener() {
            @Override
            public void onSuccessCreated(MMXChannel channel) {
                repository.setNeedToUpdateChannel(true);
                loadDetails(channel);
            }

            @Override
            public void onChannelExists(MMXChannel channel) {
                loadDetails(channel);
            }

            @Override
            public void onFailureCreated(Throwable throwable) {
                view.onRefreshingFinished();
                view.onChannelCreationFailure();
            }
        });
    }

    @Override
    public void setChat(MMXChannel channel) {
        loadDetails(channel);
    }

    private void loadDetails(final MMXChannel channel) {
        Chat chat = ChatManager.getInstance().getConversationByName(channel.getName());
        if (chat != null) {
            view.onRefreshingFinished();
            setChat(chat);
            doRefresh();
            return;
        }

        ChannelDetailOptions options = new ChannelDetailOptions.Builder()
                .numOfMessages(0).numOfSubcribers(2).build();
        view.onRefreshing();
        List<MMXChannel> list = Arrays.asList(channel);
        MMXChannel.getChannelDetail(list, options, new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
            @Override
            public void onSuccess(List<ChannelDetail> channelDetails) {
                view.onRefreshingFinished();
                if (channelDetails != null && !channelDetails.isEmpty()) {
                    ChannelDetail detail = channelDetails.get(0);
                    setChat(detail);
                    doRefresh();
                } else {
                    view.onChannelCreationFailure();
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.onRefreshingFinished();
            }
        });
    }

    @Override
    public void doRefresh() {
        loadMessages(0, Constants.MESSAGE_PAGE_SIZE);
    }

    @Override
    public void onScrolledTo(int visibleItemIndex, int size) {
        lazyLoadUtil.checkLazyLoad((int) channel.getMessagesAmount(), size, visibleItemIndex);
    }

    @Override
    public String getChannelName() {
        return channel == null ? "Chat" : channel.getName("Group chat (" + channel.getObj().getTotalSubscribers() + ")");
    }

    @Override
    public void setPresenterChatReceiveListener(ChatListContract.MMXChannelListener listener) {
        this.channelListener = listener;
    }

    @Override
    public void onStart() {
        MMX.registerListener(eventListener);
        updateChannel();
    }

    /**
     * The method call channel's update
     * Will be changed in the future
     */
    private void updateChannel() {
        if (channel != null && channel.getObj() != null) {
            ChannelDetail obj = channel.getObj();
            final MMXChannel channel = obj.getChannel();
            MMXChannel.getChannelDetail(Arrays.asList(channel), new ChannelDetailOptions.Builder().numOfMessages(0).numOfSubcribers(2).build(), new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                @Override
                public void onSuccess(List<ChannelDetail> channelDetails) {
                    if (channelDetails == null || channelDetails.isEmpty()) return;
                    setChat(channelDetails.get(0));
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                    STUB
                }
            });
        }
    }

    @Override
    public void onStop() {
        MMX.unregisterListener(eventListener);
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public void onCreate() {
        doRefresh();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bundle onSaveInstance(Bundle savedInstances) {
        return savedInstances;
    }

    @Override
    public void onRestore(Bundle savedInstances) {

    }

    private void loadMessages(int offset, int messagePageSize) {
        if (channel == null) {
            return;
        }
        view.onRefreshing();
        channel.getObj().getChannel().getMessages(null, null, messagePageSize, offset, true, listener);
    }

    private void setChannelWrapper(MMXChannelWrapper chat) {
        this.channel = chat;
        updateUI();
        if (channelListener != null) channelListener.onChannelReceived(chat);
    }

    private void updateUI() {
        view.onChannelName(getChannelName());
    }

    private void updateAmountOfMessages(int totalCount) {
        channel.setMessagesAmount(totalCount);
    }

    private void convert(ListResult<MMXMessage> mmxMessageListResult) {
        view.onRefreshingFinished();
        int totalCount = mmxMessageListResult.totalCount;
        List<MMXMessage> items = mmxMessageListResult.items;
        updateAmountOfMessages(totalCount);

        converter.convert(items, new MMXAction<List<MMXMessageWrapper>>() {
            @Override
            public void call(List<MMXMessageWrapper> action) {
                view.onPutMessage(action);
                lazyLoadUtil.onLoadingFinished();
            }
        });
    }

    @Override
    public void onNeedLoad(int loadFromPosition) {
        lazyLoadUtil.onLoading();
        loadMessages(loadFromPosition - 2, Constants.MESSAGE_PAGE_SIZE);
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXMessage>> listener = new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
        @Override
        public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
            convert(mmxMessageListResult);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            view.showMessage(R.string.err_mmx_msg_loading);
            lazyLoadUtil.onLoadingFinished();
            view.onRefreshingFinished();
        }
    };

    private void log(String message) {
        Logger.debug(getClass().getSimpleName(), message);
    }

    private final MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            MMXChannel mmxChannel = mmxMessage.getChannel();
            if (channel == null || !channel.getObj().getChannel().getName().equals(mmxChannel.getName()))
                return false;

            log("onMessageReceived");
            MMXMessageWrapper wrapper = converter.convert(mmxMessage);

            boolean isNeedScroll = wrapper.getType() != MMXMessageWrapper.TYPE_VOTE_ANSWER;

            view.onPutMessage(wrapper, isNeedScroll);
            if (mmxMessage != null && mmxMessage.getChannel() != null && mmxMessage.getChannel().getNumberOfMessages() != null) {
                Integer integer = mmxMessage.getChannel().getNumberOfMessages();
                ChatListV2PresenterImpl.this.channel.setMessagesAmount(integer);
            }
            ChatManager.getInstance().handleIncomingMessage(mmxMessage, ChatManager.MOCK);

            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            log("onMessageAcknowledgementReceived");
            return false;
        }

        @Override
        public boolean onInviteReceived(MMXChannel.MMXInvite invite) {
            log("onInviteReceived");
            return false;
        }

        @Override
        public boolean onInviteResponseReceived(MMXChannel.MMXInviteResponse inviteResponse) {
            log("onInviteResponseReceived");
            return false;
        }

        @Override
        public boolean onMessageSendError(String messageId, MMXMessage.FailureCode code, String text) {
            log("onMessageSendError");
            return false;
        }
    };
}
