package com.magnet.magnetchat.presenters.impl;

import android.location.Location;
import android.os.Bundle;

import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.util.MMXMessageUtil;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Map;

/**
 * Created by aorehov on 29.04.16.
 */
public class PostMMXMessagePresenterImpl implements PostMMXMessageContract.Presenter {

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

    }

    @Override
    public void sendVideoMessage(Map<String, String> message, String filePath, String mimeType) {

    }

    @Override
    public void sendLocationMessage(Location location) {

    }

    @Override
    public void sendLocationMessage(Map<String, String> message, Location location) {

    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> message) {

    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> message, String filePath, String mimeType) {

    }

    @Override
    public void sendCustomMessage(String type, Map<String, String> content, Attachment... attachments) {

    }

    @Override
    public void setCustomMessage(MMXMessage.Builder builder) {

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

    private MMXMessage.OnFinishedListener<String> callback = new MMXMessage.OnFinishedListener<String>() {
        @Override
        public void onSuccess(String s) {

        }

        @Override
        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

        }
    };

}
