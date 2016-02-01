package com.magnet.imessage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.magnet.imessage.R;
import com.magnet.imessage.core.CurrentApplication;
import com.magnet.imessage.helpers.ChannelHelper;
import com.magnet.imessage.helpers.UserHelper;
import com.magnet.imessage.model.Conversation;
import com.magnet.imessage.model.Message;
import com.magnet.imessage.util.Logger;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.Arrays;
import java.util.List;

public class ChatActivity extends BaseActivity {

    public static final String TAG_IDX_FROM_CHANNELS_LIST = "idxFromChannelList";
    public static final String TAG_CREATE_WITH_USER_ID = "createWithUserId";
    public static final String TAG_CREATE_NEW = "createNew";

    private Conversation currentConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findViewById(R.id.chatSendBtn).setOnClickListener(this);
        if (getIntent().getBooleanExtra(TAG_CREATE_NEW, false)) {
            String userId = getIntent().getStringExtra(TAG_CREATE_WITH_USER_ID);
            if (userId != null) {
                ChannelHelper.getInstance().createChannelForUsers(userId, createListener);
            }
        } else {
            int channelIdx = getIntent().getIntExtra(TAG_IDX_FROM_CHANNELS_LIST, -1);
            if (channelIdx >= 0) {
                currentConversation = CurrentApplication.getInstance().getConversationByIdx(channelIdx);
                if (currentConversation.getSuppliers().size() == 1) {
                    setTitle(currentConversation.getSuppliers().get(0).getDisplayName());
                } else {
                    setTitle("Group");
                }
                String suppliers = UserHelper.getInstance().userNamesAsString(currentConversation.getSuppliers());
                setText(R.id.chatSuppliers, "To: " + suppliers);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatSendBtn:
                String text = getFieldText(R.id.chatMessageField);
                if (text != null) {
                    Message message = Message.createMessage(currentConversation.getChannel(), text);
                    currentConversation.sendMessage(message, new MMXChannel.OnFinishedListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            clearFieldText(R.id.chatMessageField);
                        }

                        @Override
                        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                            Logger.error("send messages", throwable);
                            showMessage("Can't send message");
                        }
                    });
                }
                break;
        }
    }

    private ChannelHelper.OnCreateChannelListener createListener = new ChannelHelper.OnCreateChannelListener() {
        @Override
        public void onSuccessCreated(MMXChannel channel) {
            ChannelHelper.getInstance().readChannelsInfo(Arrays.asList(channel), readChannelInfoListener);
        }

        @Override
        public void onChannelExists(MMXChannel channel) {
            List<Conversation> conversations = CurrentApplication.getInstance().getConversations();
            for (Conversation conversation : conversations) {
                if (conversation.getChannel().getName().equals(channel.getName())) {
                    currentConversation = conversation;
                    break;
                }
            }
            if (currentConversation == null) {
                ChannelHelper.getInstance().readChannelsInfo(Arrays.asList(channel), readChannelInfoListener);
            }
        }

        @Override
        public void onFailureCreated(Throwable throwable) {
            showMessage("Can't create conversation");
        }
    };

    private ChannelHelper.OnReadChannelInfoListener readChannelInfoListener = new ChannelHelper.OnReadChannelInfoListener() {
        @Override
        public void onSuccessFinish(Conversation conversation) {
            currentConversation = conversation;
            String suppliers = UserHelper.getInstance().userNamesAsString(currentConversation.getSuppliers());
            setText(R.id.chatSuppliers, "To: " + suppliers);
        }

        @Override
        public void onFailure(Throwable throwable) {
        }
    };

    public static Intent getIntentWithChannel(Conversation conversation) {
        int channelIdx = CurrentApplication.getInstance().getConversations().indexOf(conversation);
        Intent intent = new Intent(CurrentApplication.getInstance(), ChatActivity.class);
        intent.putExtra(TAG_IDX_FROM_CHANNELS_LIST, channelIdx);
        return intent;
    }

    public static Intent getIntentForNewChannel(String userId) {
        Intent intent = new Intent(CurrentApplication.getInstance(), ChatActivity.class);
        intent.putExtra(TAG_CREATE_NEW, true);
        intent.putExtra(TAG_CREATE_WITH_USER_ID, userId);
        return intent;
    }

}
