package com.magnet.magnetchat.presenters.impl;

import android.location.Location;
import android.os.Bundle;

import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.MMXMessageUtil;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.common.Log;

import java.util.Map;

/**
 * Created by aorehov on 29.04.16.
 */
class PostMMXMessagePresenterImpl implements PostMMXMessageContract.Presenter {

    private PostMMXMessageContract.View view;
    private MMXMessageUtil util;
    private MMXChannel channel;


    public PostMMXMessagePresenterImpl(PostMMXMessageContract.View view) {
        this.view = view;
        util = new MMXMessageUtil();
    }

    @Override
    public void setMMXChannel(MMXChannel mmxChannel) {
        this.channel = mmxChannel;
        updateUI();
    }

    @Override
    public MMXChannel getMMXChannel() {
        return channel;
    }

    private void updateUI() {
        if (this.channel != null) {
            view.onChannelAvailable();
        } else {
            view.onChannelNotAvailable();
        }
    }

    @Override
    public void sendTextMessage() {
        String text = view.getMessageText();
        if (text == null || text.length() < 1 || channel == null) return;
        util.sendTextMessage(channel, text, callback);
    }

    @Override
    public void sendTextMessage(String text) {
        util.sendTextMessage(channel, text, callback);
    }

    @Override
    public void sendTextMessage(String text, Map<String, String> message) {
        util.sendTextMessage(channel, text, message, callback);
    }

    @Override
    public void sendPhotoMessage(String filePath, String mimeType) {
        util.sendPhotoMessage(channel, filePath, mimeType, callback);
    }

    @Override
    public void sendPhotoMessage(Map<String, String> message, String filePath, String mimeType) {
        util.sendPhotoMessage(channel, message, filePath, mimeType, callback);
    }

    @Override
    public void sendVideoMessage(String filePath, String mimeType) {
        util.sendVideoMessage(channel, filePath, mimeType, callback);
    }

    @Override
    public void sendVideoMessage(Map<String, String> message, String filePath, String mimeType) {
        util.sendVideoMessage(channel, message, filePath, mimeType, callback);
    }

    @Override
    public void sendLocationMessage(Location location) {
        util.sendLocationMessage(channel, location, callback);
    }

    @Override
    public void sendLocationMessage(Map<String, String> message, Location location) {
        util.sendLocationMessage(channel, message, location, callback);
    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> message) {
        util.sendCustomMessage(channel, type, message, callback);
    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> message, String filePath, String mimeType) {
        util.sendCustomMessage(channel, type, message, filePath, mimeType, callback);
    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> content, Attachment... attachments) {
        util.sendCustomMessage(channel, type, content, callback, attachments);
    }

    @Override
    public void onStart() {
        updateUI();
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

    private MMXMessage.OnFinishedListener<String> callback = new MMXMessage.OnFinishedListener<String>() {
        @Override
        public void onSuccess(String s) {
            Logger.debug(getClass().getSimpleName(), s);
        }

        @Override
        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
            Logger.error(getClass().getSimpleName(), throwable, failureCode);
        }
    };

}
