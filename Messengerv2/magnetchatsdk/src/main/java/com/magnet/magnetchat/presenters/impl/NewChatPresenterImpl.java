package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.presenters.updated.ChatContract;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public class NewChatPresenterImpl implements ChatContract.Presenter {

    private MMXChannelWrapper channel;
    private ChatContract.View view;
    private MMXMessageWrapperConverter converter;

    public NewChatPresenterImpl(ChatContract.View view, MMXMessageWrapperConverter converter) {
        this.view = view;
        this.converter = converter;
    }

    @Override
    public void setChat(MMXChannelWrapper chat) {
        setChannelWrapper(chat);
    }

    @Override
    public void setChat(MMXChannel channel) {
        setChannelWrapper(new MMXChannelWrapper(channel));
    }

    @Override
    public void setChat(List<UserProfile> users) {
        List<String> list = MMXObjectsHelper.convertToIdList(users);
        ChannelHelper.createChannelForUsers(list, new ChannelHelper.OnCreateChannelListener() {
            @Override
            public void onSuccessCreated(MMXChannel channel) {
                setChat(channel);
            }

            @Override
            public void onChannelExists(MMXChannel channel) {
                setChat(channel);
            }

            @Override
            public void onFailureCreated(Throwable throwable) {
                view.onChannelCreationFailure();
            }
        });
    }

    @Override
    public void doRefresh() {
        loadMessages(0, Constants.MESSAGE_PAGE_SIZE);
    }

    @Override
    public void onScrolledTo(int visibleItemIndex, int size) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

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
        channel.getObj().getMessages(null, null, messagePageSize, offset, true, listener);
    }

    private void setChannelWrapper(MMXChannelWrapper chat) {
        this.channel = chat;
        updateUI();
    }

    private void updateUI() {

    }

    private void updateAmountOfMessages(int totalCount) {
        channel.setMessagesAnount(totalCount);
    }

    private void convert(ListResult<MMXMessage> mmxMessageListResult) {
        int totalCount = mmxMessageListResult.totalCount;
        List<MMXMessage> items = mmxMessageListResult.items;
        updateAmountOfMessages(totalCount);

        converter.convert(items, new MMXAction<List<MMXMessageWrapper>>() {
            @Override
            public void call(List<MMXMessageWrapper> action) {
                view.onPutMessage(action);
            }
        });
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXMessage>> listener = new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
        @Override
        public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
            convert(mmxMessageListResult);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            view.showMessage(R.string.err_mmx_msg_loading);
        }
    };

}
