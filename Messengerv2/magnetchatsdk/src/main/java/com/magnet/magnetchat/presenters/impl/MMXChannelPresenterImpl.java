package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.presenters.MMXChannelSettingsContract;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 13.05.16.
 */
class MMXChannelPresenterImpl implements MMXChannelSettingsContract.Presenter {

    private MMXChannelSettingsContract.View view;
    private MMXChannel mmxChannel;
    private MMXAction<MMXChannel> action;

    public MMXChannelPresenterImpl(MMXChannelSettingsContract.View view) {
        this.view = view;
    }

    @Override
    public void doMute(boolean isMute) {
        if (mmxChannel == null) return;
        if (mmxChannel.isMuted()) {
            unmute();
        } else {
            mute();
        }
    }

    @Override
    public void delete() {
        view.onLoading();
        mmxChannel.delete(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.onChannelDeleted();
                view.onLoadingCompleted();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.showMessage(R.string.err_mmxchannel_delete);
                view.onLoadingCompleted();
            }
        });
    }

    private void unmute() {
        mmxChannel.unMute(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateUI();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.showMessage(R.string.err_channel_unmute);
                updateUI();
            }
        });
    }

    private void mute() {
        mmxChannel.mute(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateUI();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.showMessage(R.string.err_channel_mute);
                updateUI();
            }
        });
    }

    @Override
    public void setMMXChannel(MMXChannel mmxChannel) {
        this.mmxChannel = mmxChannel;
        updateUI();
    }

    private void updateUI() {
        if (mmxChannel == null) return;
        view.onMuteState(mmxChannel.isMuted());
    }

    @Override
    public void setMMXChannelName(String name) {
        loadByName(name);
    }

    private void loadByName(String name) {
        view.onLoading();
        MMXChannel.getChannel(name, false, new MMXChannel.OnFinishedListener<MMXChannel>() {
            @Override
            public void onSuccess(MMXChannel mmxChannel) {
                setMMXChannel(mmxChannel);
                view.onLoadingCompleted();
                if (action != null) action.call(mmxChannel);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.onLoadingCompleted();
            }
        });
    }

    @Override
    public void setMMXChannelLoadingListener(MMXAction<MMXChannel> action) {
        this.action = action;
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
}
