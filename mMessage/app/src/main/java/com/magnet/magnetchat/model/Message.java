package com.magnet.magnetchat.model;

import android.location.Location;
import android.webkit.MimeTypeMap;

import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Message {

    private static final String FILE_TYPE_VIDEO = "video/*";
    public static final String FILE_TYPE_PHOTO = "image/*";

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_PHOTO = "photo";
    public static final String TYPE_MAP = "location";
    public static final String TYPE_VIDEO = "video";

    private static final String TAG_TYPE = "type";
    private static final String TAG_TEXT = "message";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";

    private MMXMessage mmxMessage;
    private boolean isDelivered;
    private Date creationDate;

    public MMXMessage getMmxMessage() {
        return mmxMessage;
    }

    public void setMmxMessage(MMXMessage mmxMessage) {
        this.mmxMessage = mmxMessage;
    }

    public Attachment getAttachment() {
        if (mmxMessage == null) {
            return null;
        }
        if (mmxMessage.getAttachments() == null || mmxMessage.getAttachments().size() == 0) {
            return null;
        }
        return mmxMessage.getAttachments().get(0);
    }

    public Date getCreateTime() {
        if (mmxMessage == null) {
            return null;
        }
        if (mmxMessage.getTimestamp() == null) {
            return creationDate;
        }
        return mmxMessage.getTimestamp();
    }

    public String getLatitudeLongitude() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getContent().get(TAG_LATITUDE) + "," + mmxMessage.getContent().get(TAG_LONGITUDE);
    }

    public String getMessageId() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getId();
    }

    public User getSender() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getSender();
    }

    public String getText() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getContent().get(TAG_TEXT);
    }

    public String getType() {
        if (mmxMessage == null) {
            return null;
        }
        return mmxMessage.getContent().get(TAG_TYPE);
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        if (mmxMessage != null && message.mmxMessage != null) {
            return mmxMessage.getId().equals(message.mmxMessage.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mmxMessage != null ? mmxMessage.hashCode() : 0;
    }

    public static Message createMessageFrom(MMXMessage mmxMessage) {
        Message message = new Message();
        message.setMmxMessage(mmxMessage);
        return message;
    }

    public static Map<String, String> makeContent(String text) {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_TEXT);
        content.put(TAG_TEXT, text);
        return content;
    }

    public static Map<String, String> makeContent(Location location) {
        Map<String, String> content = new HashMap<>();
        content.put(TAG_TYPE, TYPE_MAP);
        content.put(TAG_LATITUDE, String.format(Locale.ENGLISH, "%.6f", location.getLatitude()));
        content.put(TAG_LONGITUDE, String.format(Locale.ENGLISH, "%.6f", location.getLongitude()));
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

    public static String makeVideoFileType(String fileName) {
        int idx = fileName.lastIndexOf(".");
        if (idx >= 0 && idx < fileName.length() - 1) {
            String format = fileName.substring(idx + 1);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(format);
        }
        return FILE_TYPE_VIDEO;
    }

}
