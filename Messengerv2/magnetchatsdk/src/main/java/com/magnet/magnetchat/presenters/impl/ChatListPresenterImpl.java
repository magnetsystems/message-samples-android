package com.magnet.magnetchat.presenters.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.magnet.magnetchat.callbacks.NewMessageProcessListener;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.presenters.ChatListContract;
import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlernatovich on 3/1/16.
 */
public class ChatListPresenterImpl implements ChatListContract.Presenter {
    protected static final String TAG = "ChatListPresenter";

    protected List<Chat> mConversations = new ArrayList<>();
    private boolean isLoadingWhenCreating = false;
    protected ChatListContract.View mView;
    protected boolean mIsSearchMode;

    /**
     * Constructor
     *
     * @param view
     */
    public ChatListPresenterImpl(ChatListContract.View view) {
        this.mView = view;
    }

    /**
     * Method which provide to getting of the reading channels
     */
    @Override
    public void onLoad(final int offset, int limit) {
        if(mIsSearchMode) {
            return;
        }

        ChannelHelper.getSubscriptionDetails(offset, limit, new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
            @Override
            public void onSuccess(List<ChannelDetail> channelDetails) {
                List<Chat> newConversations = new ArrayList<Chat>();
                if (null != channelDetails && !channelDetails.isEmpty()) {
                    for (ChannelDetail cd : channelDetails) {
                        Chat c = new Chat(cd);
                        ChatManager.getInstance().addConversation(c);
                        newConversations.add(c);
                    }
                }

                mView.showList(newConversations, 0 != offset);
                //
                //if(offset == 0) {
                //    mConversations.clear();
                //    mConversations.addAll(ChatManager.getInstance().getConversations());
                //    mView.showList(mConversations, );
                //} else {
                //    mConversations.addAll(newConversations);
                //}

                finishGetChannels();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                handleError(failureCode.toString(), throwable);
                finishGetChannels();
            }

            private void finishGetChannels() {
                isLoadingWhenCreating = false;
                mView.setProgressIndicator(false);
            }

            private void handleError(String message, Throwable throwable) {
                Logger.error(TAG, "Can't get conversations due to "
                    + message
                    + ", throwable : \n"
                    + throwable);
            }
        });
    }

    @Override
    public void onConversationUpdate(Chat conversation, boolean isNew) {
        mView.showConversationUpdate(conversation, isNew);
    }

    /**
     * Method which provide the action when activity or fragment call onResume
     * (WARNING: Should be inside the onCreate method)
     */
    @Override
    public void onResume() {
        if (!isLoadingWhenCreating && ChatManager.getInstance().isConversationListUpdated()) {
            showAllConversations();
            ChatManager.getInstance().resetConversationListUpdated();
        }
        MMX.registerListener(eventListener);

        if (null != Max.getApplicationContext()) {
            Max.getApplicationContext().registerReceiver(onAddedConversation, new IntentFilter(ChannelHelper.ACTION_ADDED_CONVERSATION));
        }
    }

    /**
     * Method which provide the action when activity or fragment call onPause
     * (WARNING: Should be inside the onPause method)
     */
    @Override
    public void onPause() {
        MMX.unregisterListener(eventListener);

        if (null != Max.getApplicationContext()) {
            Max.getApplicationContext().unregisterReceiver(onAddedConversation);
        }
        mView.dismissLeaveDialog();
    }

    /**
     * Method which provide to show of the messages by query
     *
     * @param query search query
     */
    @Override
    public void onSearch(String query, String order) {
        final List<Chat> searchResult = new ArrayList<>();
        for (Chat conversation : getAllConversations()) {
            for (UserProfile userProfile : conversation.getSortedSubscribers()) {
                if (userProfile.getDisplayName() != null && userProfile.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
                    searchResult.add(conversation);
                    break;
                }
            }
        }
        if (searchResult.isEmpty()) {
            Utils.showMessage(Max.getApplicationContext(), "Nothing found");
        }
        mView.showList(searchResult, false);

        mIsSearchMode = true;
    }

    /**
     * Method which provide the search resetting
     */
    @Override
    public void onSearchReset() {
        mIsSearchMode = false;
        showAllConversations();
    }

    /**
     * Method which provide the action when user click on the conversation channel
     *
     * @param conversation channel
     */
    @Override
    public void onItemSelect(int position, Chat conversation) {
        mView.showChatDetails(conversation);
    }

    /**
     * Method which provide the action when user do long click for the conversation
     *
     * @param conversation channel
     */
    @Override
    public void onItemLongClick(int position, Chat conversation) {

    }

    /**
     * Method which provide to getting of the list of the all conversations
     *
     * @return list of all conversations
     */
    private List<Chat> getAllConversations() {
        return mConversations;
    }

    @Override public BaseSortedAdapter.ItemComparator<Chat> getItemComparator() {
        return chatItemComparator;
    }

    /**
     * Method which provide to showing of the all conversations
     */
    private void showAllConversations() {
        mConversations.clear();
        mConversations.addAll(ChatManager.getInstance().getConversations());
        mView.showList(mConversations, false);
    }

    /**
     * Callback which provide the watch dog notification for the MMX events
     */
    private final MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug(TAG, "onMessageReceived");
            ChatManager.getInstance().handleIncomingMessage(mmxMessage, new NewMessageProcessListener() {
                @Override
                public void onProcessSuccess(Chat conversation, MMXMessage message,
                                             boolean isNewChat) {
                    onConversationUpdate(conversation, isNewChat);
                }

                @Override
                public void onProcessFailure(Throwable throwable) {
                    Logger.error(TAG, "onProcessFailure", throwable);
                }
            });
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            Logger.debug(TAG, "onMessageAcknowledgementReceived");
            //mView.showUsers();
            return false;
        }

        @Override
        public boolean onInviteReceived(MMXChannel.MMXInvite invite) {
            Logger.debug(TAG, "onInviteReceived");
            //mView.showUsers();
            return false;
        }

        @Override
        public boolean onInviteResponseReceived(MMXChannel.MMXInviteResponse inviteResponse) {
            Logger.debug(TAG, "onInviteResponseReceived");
            //mView.showUsers();
            return false;
        }

        @Override
        public boolean onMessageSendError(String messageId, MMXMessage.FailureCode code, String text) {
            Logger.debug("onMessageSendError");
            //mView.showUsers();
            return false;
        }
    };

    /**
     * Callbacks which provide the notification for the BroadcastReceiver
     */
    private final BroadcastReceiver onAddedConversation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAllConversations();
        }
    };

    private final BaseSortedAdapter.ItemComparator<Chat> chatItemComparator = new BaseSortedAdapter.ItemComparator<Chat>() {
        @Override public int compare(Chat o1, Chat o2) {
            return 0 - o1.getLastPublishedTime().compareTo(o2.getLastPublishedTime());
        }

        @Override public boolean areContentsTheSame(Chat o1, Chat o2) {
            if (o1 == o2) {
                return !o1.hasUpdate();
            } else {
                return areItemsTheSame(o1, o2)
                    && o1.getMessages().size() == o2.getMessages().size()
                    && o1.getSubscribers().size() == o2.getSubscribers().size();
            }
        }

        @Override public boolean areItemsTheSame(Chat item1, Chat item2) {
            return item1 == item2 ||
                item1.getChannel().getName().equals(item2.getChannel().getName())
                && item1.getChannel().isPublic() == item2.getChannel().isPublic();
        }
    };

}
