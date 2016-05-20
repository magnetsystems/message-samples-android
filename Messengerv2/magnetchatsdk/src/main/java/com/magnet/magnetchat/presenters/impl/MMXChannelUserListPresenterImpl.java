package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.util.LazyLoadUtil;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
class MMXChannelUserListPresenterImpl implements UserListContract.Presenter {

    private final BaseConverter<User, MMXUserWrapper> converter;
    private UserListContract.View view;
    private LazyLoadUtil lazyLoadUtil;
    private List<MMXUserWrapper> userWrappers;
    private MMXChannelWrapper mmxChannel;
    private boolean isStarted = false;
    private final int PAGE_SIZE = 40;

    public MMXChannelUserListPresenterImpl(UserListContract.View view, BaseConverter<User, MMXUserWrapper> converter) {
        this.lazyLoadUtil = new LazyLoadUtil(PAGE_SIZE, (int) (PAGE_SIZE * 0.35), lazyLoadingCallback);
        this.view = view;
        this.converter = converter;
    }


    @Override
    public void onInit(Bundle bundle) {
        MMXChannel channel = BundleHelper.readMMXChannelFromBundle(bundle);
        if (channel == null) {
            view.onCantLoadChannel();
        } else {
            setMMXChannel(channel);
        }
    }

    @Override
    public void search(String query) {

    }

    @Override
    public void setSelectUserEvent(UserListContract.OnSelectUserEvent selectUserEvent) {
//        STUB
    }

    @Override
    public void doGetAllSelectedUsers() {
//        STUB
    }

    @Override
    public void setOnGetAllSelectedUsersListener(UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener) {
// STUB
    }

    public void setMMXChannel(MMXChannel mmxChannel) {
        Chat chat = ChatManager.getInstance().getConversationByName(mmxChannel.getName());
        if (chat != null) {
            setMMXChannelDetails(chat);
        } else {
            ChannelDetailOptions opts = new ChannelDetailOptions.Builder().build();
            mmxChannel.getChannelDetail(Arrays.asList(mmxChannel), opts, new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                @Override
                public void onSuccess(List<ChannelDetail> channelDetails) {
                    if (channelDetails == null || channelDetails.isEmpty()) {
                        view.onCantLoadChannel();
                    } else {
                        ChannelDetail detail = channelDetails.get(0);
                        ChatManager.getInstance().addConversation(new Chat(detail));
                        setMMXChannelDetails(detail);
                        if (isStarted) doRefresh();
                    }
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    view.onCantLoadChannel();
                }
            });
        }
    }

    public void setMMXChannelDetails(ChannelDetail details) {
        setMMXChannelWrapper(new MMXChannelWrapper(details));
    }

    public void setMMXChannelWrapper(MMXChannelWrapper mmxChannelWrapper) {
        this.mmxChannel = mmxChannelWrapper;
    }


    @Override
    public void doRefresh() {
        loadUsers(0, 3000);
    }

    @Override
    public void onCurrentPosition(int localSize, int index) {
        lazyLoadUtil.checkLazyLoad(mmxChannel.getSubscribersAmount(), localSize, index);
    }

    @Override
    public void doClickOn(MMXUserWrapper wrapper) {
        wrapper.setSelected(!wrapper.isSelected());
        view.onPut(wrapper);
    }

    @Override
    public void onStart() {
        if (userWrappers == null || (mmxChannel != null && userWrappers.size() != mmxChannel.getSubscribersAmount())) {
            doRefresh();
        }
        isStarted = true;
    }

    @Override
    public void onStop() {
        isStarted = false;
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    private void onUsersReceived(List<User> items) {
        converter.convert(items, new MMXAction<List<MMXUserWrapper>>() {
            @Override
            public void call(List<MMXUserWrapper> action) {
                userWrappers = action;
                view.onPut(userWrappers);
            }
        });
    }

    @Override
    public Bundle onSaveInstance(Bundle savedInstances) {
        return null;
    }

    @Override
    public void onRestore(Bundle savedInstances) {

    }

    private void loadUsers(int offset, int pageSize) {
        view.onLoading();
        mmxChannel.getObj().getChannel().getAllSubscribers(pageSize, offset, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                List<User> items = userListResult.items;
                onUsersReceived(items);
                view.onLoadingComplete();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.onLoadingComplete();
                view.showMessage(R.string.err_user_loading);
            }
        });
    }

    private final LazyLoadUtil.OnNeedLoadingCallback lazyLoadingCallback = new LazyLoadUtil.OnNeedLoadingCallback() {
        @Override
        public void onNeedLoad(int loadFromPosition) {
            loadUsers(loadFromPosition, 3000);
        }
    };
}
