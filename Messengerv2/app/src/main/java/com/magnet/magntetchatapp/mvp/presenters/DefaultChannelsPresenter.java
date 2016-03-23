package com.magnet.magntetchatapp.mvp.presenters;

import android.annotation.SuppressLint;
import android.util.Log;

import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class DefaultChannelsPresenter implements ChannelsListContract.Presenter {

    private static final String TAG = "DefaultChannelsPresenter";
    private static int K_PAGE_SIZE = 10;

    private final ChannelsListContract.View view;

    public DefaultChannelsPresenter(ChannelsListContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
        view.setLazyLoadCallback(this);
        startChannelReceiving(0);
    }

    /**
     * Method which provide the action when Activity/Fragment call method onResume
     */
    @Override
    public void onActivityResume() {

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
        view.switchLoadingMessage("Loading channels", true);
        ChannelHelper.getSubscriptionDetails(offset, K_PAGE_SIZE, new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
            @Override
            public void onSuccess(List<ChannelDetail> result) {
                if (result != null && result.isEmpty() == false) {
                    List<ChannelsListContract.ChannelObject> objects = new ArrayList<ChannelsListContract.ChannelObject>();
                    for (ChannelDetail channelDetail : result) {
                        objects.add(new ChannelsListContract.ChannelObject(channelDetail));
                    }
                    if (view != null) {
                        if (offset == 0) {
                            view.setChannels(objects);
                        } else {
                            view.addChannels(objects);
                        }
                    }
                }
                if (view != null) {
                    view.switchLoadingMessage(null, false);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                if (view != null) {
                    view.switchLoadingMessage(null, false);
                }
            }
        });
    }

    /**
     * Method which provide the notifying about end of list
     *
     * @param listSize list size
     */
    @Override
    public void onAlmostAtBottom(int listSize) {
        startChannelReceiving(listSize);
    }
}
