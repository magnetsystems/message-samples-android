package com.magnet.magnetchat.mvp.presenters;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.util.Log;

import com.magnet.magnetchat.mvp.api.abs.ChannelsListContract;
import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.persistence.PendingStateRepository;
import com.magnet.magnetchat.persistence.impl.PersistenceComponentImpl;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class DefaultChannelsPresenter implements ChannelsListContract.Presenter {

    private static final String TAG = "DefaultChannelsPresenter";
    private static int K_PAGE_SIZE = 10;
    private static final ChannelDetailOptions CHANNEL_DETAIL_OPTIONS = new ChannelDetailOptions
            .Builder()
            .numOfMessages(Constants.PRE_FETCHED_MESSAGE_SIZE)
            .numOfSubcribers(Constants.PRE_FETCHED_SUBSCRIBER_SIZE)
            .build();

    private final ChannelsListContract.View view;
    private AppScopePendingStateRepository repository;

    public DefaultChannelsPresenter(ChannelsListContract.View view) {
        this.view = view;
        repository = new PersistenceComponentImpl(view.getCurrentContext()).getApplicationPendingStateRepository();
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
        view.setLazyLoadCallback(this);
        repository.setNeedToUpdateChannel(false);
        startChannelReceiving(0);
    }

    /**
     * Method which provide the action when Activity/Fragment call method onResume
     */
    @Override
    public void onActivityResume() {
        if (repository.isNeedToUpdateChannels())
            startChannelReceiving(0);

    }

    /**
     * Method which provide the action when Activity/Fragment call method onPauseActivity
     */
    @Override
    public void onActivityPause() {

    }

    /**
     * Method which provide the action when Activity/Fragment call method onDestroy
     */
    @Override
    public void onActivityDestroy() {

    }

    /**
     * Method which provide to start of channel receiving
     */
    @Override
    public void startChannelReceiving(final int offset) {
        showProgress("Loading channels");
        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
            @Override
            public void onSuccess(List<MMXChannel> result) {
                getChannelsDetails(result);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                if (throwable != null) {
                    Log.e(TAG, throwable.toString());
                }
                hideProgress();
            }
        });
    }

    /**
     * Method which provide the getting of the channel details
     *
     * @param channels
     */
    @Override
    public void getChannelsDetails(@Nullable List<MMXChannel> channels) {
        if (channels != null) {
            MMXChannel.getChannelDetail(channels, CHANNEL_DETAIL_OPTIONS,
                    new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                        @Override
                        public void onSuccess(List<ChannelDetail> result) {
                            onChannelsPostProcessing(result);
                        }

                        @Override
                        public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                            hideProgress();
                        }
                    });
        } else {
            hideProgress();
        }
    }

    /**
     * Method which provide the hannel post processing
     *
     * @param channelDetails channel details
     */
    @Override
    public void onChannelsPostProcessing(@Nullable final List<ChannelDetail> channelDetails) {
        if (channelDetails != null && channelDetails.isEmpty() == false) {
            List<ChannelsListContract.ChannelObject> objects = new ArrayList<ChannelsListContract.ChannelObject>();
            for (ChannelDetail channelDetail : channelDetails) {
                objects.add(new ChannelsListContract.ChannelObject(channelDetail));
            }
            if (view != null) {
                view.setChannels(objects);
                view.sortChannels();
            }
        }
        hideProgress();
    }

    /**
     * Method which provide the notifying about end of list
     *
     * @param listSize list size
     */
    @Override
    public void onAlmostAtBottom(int listSize) {
        //TODO Implement this after implementing of the normal paging on the server side
//        startChannelReceiving(listSize);
    }

    /**
     * Method which provide the progress hidding
     */
    private void hideProgress() {
        if (view != null) {
            view.switchLoadingMessage(null, false);
        }
    }

    /**
     * Method which provide the progress hidding
     */
    private void showProgress(@Nullable String message) {
        if (view != null) {
            view.switchLoadingMessage(message, true);
        }
    }

}
