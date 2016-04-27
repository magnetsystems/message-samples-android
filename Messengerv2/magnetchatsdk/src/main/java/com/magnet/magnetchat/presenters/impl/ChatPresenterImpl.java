/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.presenters.impl;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.util.Log;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.FileHelper;
import com.magnet.magnetchat.helpers.MessageHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.presenters.ChatContract;
import com.magnet.magnetchat.ui.activities.ChatDetailsActivity;
import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenterImpl implements ChatContract.Presenter {
    protected static final String TAG = "ChatPresenterImpl";

    protected final ChatContract.View mView;

    protected Chat mCurrentConversation;
    protected final List<UserProfile> mRecipients;

    public ChatPresenterImpl(@NonNull ChatContract.View view, @NonNull Chat conversation) {
        this.mView = view;
        this.mCurrentConversation = conversation;
        this.mRecipients = conversation.getSortedSubscribers();

        showRecipients(conversation.getSortedSubscribers());

        onReadMessage();
    }

    public ChatPresenterImpl(@NonNull ChatContract.View view, @NonNull final ArrayList<UserProfile> recipients) {
        this.mView = view;
        this.mRecipients = recipients;

        showRecipients(mRecipients);

        List<String> userIds = new ArrayList<>(recipients.size());
        for (UserProfile up : recipients) {
            userIds.add(up.getUserIdentifier());
        }
        mView.setProgressIndicator(true);
        ChannelHelper.createChannelForUsers(userIds, new ChannelHelper.OnCreateChannelListener() {
            @Override
            public void onSuccessCreated(MMXChannel channel) {
                addNewConversation(new Chat(channel, recipients, User.getCurrentUser()));
            }

            @Override
            public void onChannelExists(MMXChannel channel) {
                mCurrentConversation = ChatManager.getInstance().getConversationByName(channel.getName());
                if (null == mCurrentConversation) {
                    ChannelHelper.getChannelDetails(channel,
                        null,
                        new ChannelHelper.OnReadChannelDetailListener() {
                        @Override
                        public void onSuccessFinish(Chat conversation) {
                            addNewConversation(conversation);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            mView.setProgressIndicator(false);
                        }
                    });
                } else {
                    mView.setProgressIndicator(false);
                }
            }

            @Override
            public void onFailureCreated(Throwable throwable) {
                Utils.showMessage("Can't create conversation");
                Log.e(TAG, "Can't create conversation", throwable);
                mView.setProgressIndicator(false);
            }

            private void addNewConversation(Chat conversation) {
                mCurrentConversation = conversation;
                ChatManager.getInstance().addConversation(mCurrentConversation);
                mView.setProgressIndicator(false);
                mView.showList(mCurrentConversation.getMessages(), false);

                onReadMessage();
            }
        });
    }

    /**
     * Method which provide the action when Activity/Fragment call onResume method
     * (WARNING: Should be call in the onCreate method)
     */
    @Override
    public void onResume() {
        MMX.registerListener(eventListener);
    }

    /**
     * Method which provide the action when Activity/Fragment call onPause method
     * (WARNING: Should be call in the onPause method)
     */
    @Override
    public void onPause() {
        MMX.unregisterListener(eventListener);
    }

    @Override
    public void onLoad(boolean forceUpdate) {
        if (forceUpdate) {
            //TODO :
        } else {
            if (null != mCurrentConversation && mCurrentConversation.hasUnreadMessage()) {
                mView.showList(mCurrentConversation.getMessages(), false);
                mCurrentConversation.setHasUnreadMessage(false);
            }
        }
    }

    @Override
    public void onLoad(final int offset, final int limit) {
        if(null != mCurrentConversation) {
            if(mCurrentConversation.getMessages().size() < mCurrentConversation.getTotalMessages()) {
                mCurrentConversation.getChannel().getMessages(null, null, limit, offset, true, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
                    @Override public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
                        if (null != mmxMessageListResult && !mmxMessageListResult.items.isEmpty()) {
                            if(0 == offset) {
                                mCurrentConversation.getMessages().clear();
                            }
                            mCurrentConversation.insertMessages(mmxMessageListResult.items);
                            mView.showList(mmxMessageListResult.items, 0 != offset);
                        }
                    }

                    @Override public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        Log.e(TAG, "onLoad : " + offset + "/" + limit, throwable);
                    }
                });
            } else {
                Log.d(TAG, "-----messages were already loaded");
                mView.showList(mCurrentConversation.getMessages(offset, limit), 0 != offset);
            }
        }
    }

    @Override public void onSearch(String query, String sort) {

    }

    @Override public void onSearchReset() {

    }

    @Override public void onItemSelect(int position, Message item) {
        onOpenAttachment(item);
    }

    @Override public void onItemLongClick(int position, Message item) {

    }

    @Override public BaseSortedAdapter.ItemComparator<Message> getItemComparator() {
        return messageItemComparator;
    }

    @Override
    public void onLoadRecipients(boolean forceUpdate) {
        if (forceUpdate) {
            //TODO :
        } else {
            if (null != mCurrentConversation && mCurrentConversation.hasRecipientsUpdate()) {
                showRecipients(mCurrentConversation.getSortedSubscribers());
                mCurrentConversation.setHasRecipientsUpdate(false);
            }
        }
    }

    @Override
    public void onNewMessage(MMXMessage message) {
        if (null != mCurrentConversation) {
            mView.showNewMessage(message);
        }
    }

    private void onReadMessage() {
        if (null != mCurrentConversation) {
            mCurrentConversation.setHasUnreadMessage(false);
        }
    }

    @Override
    public void onSendText(String text) {
        if (mCurrentConversation != null) {
            mView.setSendEnabled(false);
            mCurrentConversation.sendTextMessage(text, sendMessageListener);
        }
    }

    @Override
    public void onSendImages(Uri[] uris) {
        if (mCurrentConversation != null) {
            if (uris.length > 0) {
                for (Uri uri : uris) {
                    mView.setProgressIndicator(true);
                    mView.setSendEnabled(false);

                    String filePath = uri.toString();
                    mCurrentConversation.sendPhoto(filePath,
                            FileHelper.getMimeType(Max.getApplicationContext(), uri, filePath, Message.FILE_TYPE_PHOTO),
                            sendMessageListener);
                }
            }
        }
    }

    @Override
    public void onSendLocation(Location location) {
        if (mCurrentConversation != null) {
            mView.setSendEnabled(false);
            mCurrentConversation.sendLocation(location, sendMessageListener);
        }
    }

    @Override
    public void onChatDetails() {
        Activity activity = mView.getActivity();
        if (mCurrentConversation != null && null != activity) {
            activity.startActivity(ChatDetailsActivity.createIntentForChannel(activity, mCurrentConversation));
        }
    }

    @Override
    public Chat getCurrentConversation() {
        return mCurrentConversation;
    }

    /**
     * Method which provide the opening of the attachment
     *
     * @throws Exception
     */
    private void onOpenAttachment(Message message) {
        if (message.getType() != null) {
            switch (message.getType()) {
                case Message.TYPE_MAP:
                    mView.showLocation(message);
                    break;
                case Message.TYPE_PHOTO:
                    mView.showImage(message);
                    break;
                default:
                    break;
            }
        }
    }

    private void showRecipients(List<UserProfile> recipients) {
        String title = null;
        if (recipients.size() == 1) {
            title = UserHelper.getDisplayNames(recipients);
        } else {
            title = "Group";
        }
        setTitle(title);
    }

    private void setTitle(String title) {
        mView.setTitle(title);

    }

    /**
     * Listener which provide the listening of the message sending notification
     */
    private final Chat.OnSendMessageListener sendMessageListener = new Chat.OnSendMessageListener() {
        @Override
        public void onSuccessSend(MMXMessage message) {
            mView.setProgressIndicator(false);
            mView.setSendEnabled(true);

            ChatManager.getInstance().getMessagesToApproveDeliver().put(message.getId(), message);
            if (StringUtil.isStringValueEqual(MessageHelper.getMessageType(message), Message.TYPE_TEXT)) {
                mView.clearInput();
            }
            onNewMessage(message);
        }

        @Override
        public void onFailure(Throwable throwable) {
            mView.setProgressIndicator(false);
            mView.setSendEnabled(true);
            mView.setProgressIndicator(false);
            Logger.error(TAG, "send message error", throwable);
            Utils.showMessage("Can't send message");
        }
    };

    /**
     * MMX event listener
     */
    private final MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug(TAG, "Received message in : " + mmxMessage);
            MMXChannel channel = mmxMessage.getChannel();
            if (channel != null && mCurrentConversation != null) {
                String messageChannelName = channel.getName();
                if (messageChannelName.equalsIgnoreCase(mCurrentConversation.getChannel().getName())
                    && !mmxMessage.getSender().getUserIdentifier().equals(User.getCurrentUserId())) {
                    mCurrentConversation.addMessage(mmxMessage, false);
                    onNewMessage(mmxMessage);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            return true;
        }
    };

    private final BaseSortedAdapter.ItemComparator<Message> messageItemComparator = new BaseSortedAdapter.ItemComparator<Message>() {
        @Override public int compare(Message o1, Message o2) {
            return (int) (o1.getCreateTime().getTime() - o2.getCreateTime().getTime());
        }

        @Override public boolean areContentsTheSame(Message o1, Message o2) {
            return areItemsTheSame(o1, o2);
        }

        @Override public boolean areItemsTheSame(Message item1, Message item2) {
            return item1 == item2 || item1.getMessageId().equals(item2.getMessageId());
        }
    };
}
