package com.magnet.magnetchat.presenters;

import android.location.Location;

import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Map;

/**
 * Created by aorehov on 29.04.16.
 */
public interface PostMMXMessageContract {

    interface Presenter extends MMXPresenter {
        void sendTextMessage();

        void sendTextMessage(String text);

        void sendTextMessage(String text, Map<String, String> message);

        void sendPhotoMessage(String filePath, String mimeType);

        void sendPhotoMessage(Map<String, String> message, String filePath, String mimeType);

        void sendVideoMessage(String filePath, String mimeType);

        void sendVideoMessage(Map<String, String> message, String filePath, String mimeType);

        void sendLocationMessage(Location location);

        void sendLocationMessage(Map<String, String> message, Location location);

        void sendCustomMessage(String type, Map<String, String> message);

        void sendCustomMessage(String type, Map<String, String> message, String filePath, String mimeType);

        void sendCustomMessage(String type, Map<String, String> content, Attachment... attachments);

        void setMMXChannel(MMXChannel mmxChannel);

        MMXChannel getMMXChannel();
    }

    interface View extends MMXInfoView {
        String getMessageText();

        void onChannelAvailable();

        void onChannelNotAvailable();
    }

}
