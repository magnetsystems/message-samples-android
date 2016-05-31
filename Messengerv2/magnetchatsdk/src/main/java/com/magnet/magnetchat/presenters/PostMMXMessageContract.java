package com.magnet.magnetchat.presenters;

import android.location.Location;

import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Map;

/**
 * the post message abstraction
 * Created by aorehov on 29.04.16.
 */
public interface PostMMXMessageContract {

    interface Presenter extends MMXPresenter {

        /**
         * The method takes text from View and post it
         */
        void sendTextMessage();

        /**
         * The method sends text message
         * @param text
         */
        void sendTextMessage(String text);

        /**
         * The method sends text message wit additional information
         * @param text
         * @param message
         */
        void sendTextMessage(String text, Map<String, String> message);

        /**
         * the method sends photo with mimetype
         * @param filePath
         * @param mimeType
         */
        void sendPhotoMessage(String filePath, String mimeType);

        void sendPhotoMessage(Map<String, String> message, String filePath, String mimeType);

        void sendVideoMessage(String filePath, String mimeType);

        void sendVideoMessage(Map<String, String> message, String filePath, String mimeType);

        void sendLocationMessage(Location location);

        void sendLocationMessage(Map<String, String> message, Location location);

        void sendCustomMessage(String type, Map<String, String> message);

        void sendCustomMessage(String type, Map<String, String> message, String filePath, String mimeType);

        void sendCustomMessage(String type, Map<String, String> content, Attachment... attachments);

        /**
         * init presenter with instance of channel
         *
         * @param mmxChannel
         */
        void setMMXChannel(MMXChannel mmxChannel);


        /**
         * the method returns instance of MMXChannel
         *
         * @return
         */
        MMXChannel getMMXChannel();
    }

    interface View extends MMXInfoView {

        /**
         * @return message text from view
         */
        String getMessageText();

        /**
         * called if channel is available for message posting
         */
        void onChannelAvailable();

        /**
         * called if message isn't available for message posting
         */
        void onChannelNotAvailable();
    }

}
