package com.magnet.magnetchat.util;

import android.location.Location;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.magnet.magnetchat.model.Message.*;

/**
 * Created by aorehov on 29.04.16.
 */
public class MMXMessageUtil {

    public void sendTextMessage(MMXChannel channel, String text, MMXMessage.OnFinishedListener<String> listener) {
        sendMMXMessage(channel, makeContent(text), listener);
    }

    public void sendTextMessage(MMXChannel channel, String text, Map<String, String> message, MMXMessage.OnFinishedListener<String> listener) {
        sendMMXMessage(channel, merge(makeContent(text), message), listener);
    }

    public void sendPhotoMessage(MMXChannel channel, Map<String, String> message, String filePath, String mimeType, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = merge(message, makePhotoContent());
        File file = new File(filePath);
        sendMMXMessage(channel, content, listener, new Attachment(file, mimeType, file.getName(), ""));
    }

    public void sendPhotoMessage(MMXChannel channel, String filePath, String mimeType, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = makePhotoContent();
        File file = new File(filePath);
        sendMMXMessage(channel, content, listener, new Attachment(file, mimeType, file.getName(), ""));
    }

    public void sendVideoMessage(MMXChannel channel, String filePath, String mimeType, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = makePhotoContent();
        sendMMXMessage(channel, content, listener, new Attachment(filePath, mimeType));
    }

    public void sendVideoMessage(MMXChannel channel, Map<String, String> message, String filePath, String mimeType, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = merge(makePhotoContent(), message);
        sendMMXMessage(channel, content, listener, new Attachment(filePath, mimeType));
    }

    public void sendLocationMessage(MMXChannel channel, Map<String, String> message, Location location, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = merge(message, makeContent(location));
        sendMMXMessage(channel, content, listener);
    }

    public void sendLocationMessage(MMXChannel channel, Location location, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = makeContent(location);
        sendMMXMessage(channel, content, listener);
    }

    public void sendCustomMessage(MMXChannel channel, String type, Map<String, String> message, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = applyType(type, message);
        sendMMXMessage(channel, content, listener);
    }

    public void sendCustomMessage(MMXChannel channel, String type, Map<String, String> message, String filePath, String mimeType, MMXMessage.OnFinishedListener<String> listener) {
        Map<String, String> content = applyType(type, message);
        sendMMXMessage(channel, content, listener, new Attachment(filePath, mimeType));
    }

    public void sendCustomMessage(MMXChannel channel, String type, Map<String, String> message, MMXMessage.OnFinishedListener<String> listener, Attachment... attachments) {
        Map<String, String> content = applyType(type, message);
        sendMMXMessage(channel, content, listener, attachments);
    }

    public void sendMMXMessage(MMXChannel channel, Map<String, String> content, MMXMessage.OnFinishedListener<String> listener, Attachment... attachment) {
        MMXMessage message = new MMXMessage.Builder()
                .metaData(content)
                .attachments(attachment)
                .channel(channel)
                .build();

        message.send(listener);
    }


    private Map<String, String> applyType(String type, Map<String, String> message) {
        return applyPair(TAG_TYPE, type, message);
    }

    private Map<String, String> applyPair(String key, String value, Map<String, String> message) {
        Map<String, String> content = message == null ? new HashMap<String, String>() : message;
        content.put(key, value);
        return content;
    }

    private Map<String, String> merge(Map<String, String> base, Map<String, String> attached) {
        if (base == null && attached == null) {
            return new HashMap<>();
        } else if (base == null && attached != null) {
            return attached;
        } else if (base != null && attached == null) {
            return base;
        } else {
            base.putAll(attached);
            return base;
        }
    }

    public static String getTextMessage(Map<String, String> content) {
        return content.get(TAG_TEXT);
    }

    public static String getTextMessage(MMXMessage mmxMessage) {
        if (mmxMessage == null) return null;
        Map<String, String> content = mmxMessage.getContent();
        return content == null ? null : getTextMessage(content);
    }

    public static String getTextMessage(MMXMessageWrapper wrapper) {
        if (wrapper == null) return null;
        return getTextMessage(wrapper.getObj());
    }

    public static double getLat(MMXMessage mmxMessage) {
        if (mmxMessage == null) return Double.MAX_VALUE;
        return getLat(mmxMessage.getContent());
    }

    public static double getLon(MMXMessage mmxMessage) {
        if (mmxMessage == null) return Double.MAX_VALUE;
        return getLon(mmxMessage.getContent());
    }

    public static double getLat(Map<String, String> content) {
        if (content == null) return Double.MAX_VALUE;
        String value = content.get(TAG_LATITUDE);
        return value == null ? Double.MAX_VALUE : Double.valueOf(value);
    }

    public static double getLon(Map<String, String> content) {
        if (content == null) return Double.MAX_VALUE;
        String value = content.get(TAG_LONGITUDE);
        return value == null ? Double.MAX_VALUE : Double.valueOf(value);
    }

    //    STATIC METHODS
    public static Map<String, String> makeContent(String text) {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_TEXT);
        content.put(TAG_TEXT, text);
        return content;
    }

    public static Map<String, String> makeContent(Location location) {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_MAP);
        content.put(TAG_LATITUDE, String.valueOf(location.getLatitude()));
        content.put(TAG_LONGITUDE, String.valueOf(location.getLongitude()));
        return content;
    }

    public static Map<String, String> makeVideoContent() {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_VIDEO);
        return content;
    }

    public static Map<String, String> makePhotoContent() {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_PHOTO);
        return content;
    }

    public static Map<String, String> makePollContent() {
        Map<String, String> map = new HashMap<>();
        map.put(TAG_TYPE, TYPE_POLL);
        return map;
    }

    public static String getMapPicUrl(MMXMessage mmxMessage) {
        if (mmxMessage == null) return null;
        return getMapPicUrl(mmxMessage.getContent());
    }

    public static String getMapPicUrl(Map<String, String> content) {
        if (content == null) return null;
        String lon = content.get(TAG_LONGITUDE);
        String lat = content.get(TAG_LATITUDE);
        if (lon == null || lat == null) return null;

        return new StringBuilder()
                .append("http://maps.google.com/maps/api/staticmap?center=")
                .append(lat).append(',').append(lon)
                .append("&zoom=18&size=480x300&sensor=false&markers=color:blue%7Clabel:S%7C")
                .append(lat).append(',').append(lon)
                .toString();
    }
}
