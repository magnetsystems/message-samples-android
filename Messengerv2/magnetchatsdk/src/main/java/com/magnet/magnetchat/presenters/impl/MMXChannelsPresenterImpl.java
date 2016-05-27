package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.helpers.AsyncHelper;
import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.MMXChannelsContract;
import com.magnet.magnetchat.util.LazyLoadUtil;
import com.magnet.magnetchat.util.Logger;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;

/**
 * Created by aorehov on 27.05.16.
 */
public class MMXChannelsPresenterImpl implements MMXChannelsContract.Presenter {

    private MMXChannelsContract.View view;
    private MMXChannelsContract.RouterCallback router;

    private ChannelDetailOptions opts = new ChannelDetailOptions.Builder()
            .numOfMessages(1)
            .numOfSubcribers(0)
            .build();
    private final int PAGE_SIZE;
    private final BaseConverter<ChannelDetail, MMXChannelWrapper> converter;

    private LazyLoadUtil lazyLoadUtil;
    private int channelAmount;
    private String term = "";

    private boolean isLoading = false;
    private boolean isResetData = false;


    public MMXChannelsPresenterImpl(MMXChannelsContract.View view, BaseConverter<ChannelDetail, MMXChannelWrapper> converter, int pageSize) {
        PAGE_SIZE = pageSize;
        this.view = view;
        this.converter = converter;
        lazyLoadUtil = new LazyLoadUtil(PAGE_SIZE, (int) (PAGE_SIZE * 0.50), loadingCallback);
    }


    @Override
    public void doLeaveChannel(MMXChannelWrapper channel) {
        if (view != null) view.onDelete(channel);
    }

    @Override
    public void doOpenChannel(MMXChannelWrapper channel) {
        if (router != null) router.onOpenChannel(channel);
        else Logger.debug("ROUTER IN MMXChannelContract.Presenter is empty");
    }

    @Override
    public void doRefresh() {
        isResetData = true;
        doLoad(0, PAGE_SIZE);
    }

    @Override
    public void onScrollToItem(int localSize, int index) {
        lazyLoadUtil.checkLazyLoad(channelAmount, localSize, index);
    }

    @Override
    public void search(String term) {
        if (this.term.equals(term)) {
            return;
        }
        this.term = term;
        isResetData = true;
        doRefresh();
    }

    @Override
    public void setRouterCallback(MMXChannelsContract.RouterCallback routerCallback) {
        this.router = routerCallback;
    }

    private void doLoad(int offset, int pagesize) {
        if (isLoading) return;
        onLoading();
        MMXChannel.getAllPublicChannels(pagesize, offset, callback);
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
        if (isLoading) AsyncHelper.UI.post(new Runnable() {
            @Override
            public void run() {
                view.onLoading();
            }
        });

    }

    @Override
    public void onCreate() {
        doLoad(0, PAGE_SIZE);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bundle onSaveInstance(Bundle savedInstances) {
        return null;
    }

    @Override
    public void onRestore(Bundle savedInstances) {

    }

    private void onLoading() {
        if (view != null) {
            isLoading = true;
            view.onLoading();
        }
    }

    private void onLoadingFinished() {
        if (view != null) {
            isLoading = false;
            view.onLoadingFinished();
        }
    }

    private void showMessage(CharSequence sequence) {
        if (view != null) view.showMessage(sequence);
    }

    private void showMessage(int resId) {
        if (view != null) view.showMessage(resId);
    }


    private void putData(List<MMXChannelWrapper> action) {
        if (view == null) return;
        if (isResetData) {
            view.onSet(action);
        } else {
            view.onPut(action);
        }
    }

    private void onLoadedChannels(List<ChannelDetail> result) {
        converter.convert(result, new MMXAction<List<MMXChannelWrapper>>() {
            @Override
            public void call(List<MMXChannelWrapper> action) {
                putData(action);
            }
        });
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXChannel>> callback = new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
        @Override
        public void onSuccess(ListResult<MMXChannel> result) {
            channelAmount = result.totalCount;
            MMXChannel.getChannelDetail(result.items, opts, new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                @Override
                public void onSuccess(List<ChannelDetail> result) {
                    onLoadedChannels(result);
                }

                @Override
                public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                    showMessage(R.string.err_channels_get);
                    onLoadingFinished();
                }
            });
        }

        @Override
        public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
            showMessage(R.string.err_channels_get);
            onLoadingFinished();
        }
    };

    private final LazyLoadUtil.OnNeedLoadingCallback loadingCallback = new LazyLoadUtil.OnNeedLoadingCallback() {
        @Override
        public void onNeedLoad(int loadFromPosition) {
            doLoad(loadFromPosition - 2, PAGE_SIZE);
        }
    };
}
