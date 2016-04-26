package com.magnet.magnetchat.model;

import android.location.Location;

import android.util.Log;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

import com.magnet.mmx.client.ext.poll.MMXPoll;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Message {
    private static final String TAG = "Message";

    public enum MessageStatus {DELIVERED, PENDING, ERROR}

    public static final String FILE_TYPE_VIDEO = "video";
    public static final String FILE_TYPE_PHOTO = "image";

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_PHOTO = "photo";
    public static final String TYPE_MAP = "location";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_POLL = "poll";
    public static final String TYPE_POLL_ANSWER = "pollAnswer";

    private static final String TAG_TYPE = "type";
    private static final String TAG_TEXT = "message";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";

    private MMXMessage mmxMessage;
    private MessageStatus messageStatus;
    private Date creationDate;

    private String type;

    private MMXPoll poll;

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
        return (Attachment) mmxMessage.getAttachments().get(0);
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

    public void getPoll(final ApiCallback<MMXPoll> callback) {
        String errorMessage = null;
        if(TYPE_POLL.equals(getType())) {
            if (null == poll) {
                MMXPoll.MMXPollIdentifier pollIdentifier =
                    (MMXPoll.MMXPollIdentifier) mmxMessage.getPayload();
                if(null != pollIdentifier) {
                    MMXPoll.get(pollIdentifier.getPollId(), new MMX.OnFinishedListener<MMXPoll>() {
                        @Override public void onSuccess(MMXPoll result) {
                            poll = result;
                            if (null != callback) {
                                callback.success(poll);
                            }
                        }

                        @Override public void onFailure(MMX.FailureCode code, Throwable ex) {
                            Log.e(TAG, "Failed to get poll by id " + code, ex);
                        }
                    });
                } else {
                    errorMessage = "MMXPollIdentifier is null";
                }
            } else {
                if (null != callback) {
                    callback.success(poll);
                }
            }
        } else {
            errorMessage = "Message type is not poll";
        }

        if(null != errorMessage) {
            if (null != callback) {
                callback.failure(new ApiError(errorMessage));
            }
        }
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
        return (String) mmxMessage.getContent().get(TAG_TEXT);
    }

    public String getType() {
        if (mmxMessage == null) {
            return null;
        }

        if(null != type) {
            return type;
        }

        String contentType = mmxMessage.getContentType();
        if(StringUtil.isNotEmpty(contentType) && contentType.startsWith("object/")) {
            String objectType = contentType.substring("object/".length());
            if(MMXPoll.MMXPollIdentifier.TYPE.equals(objectType)) {
                type = TYPE_POLL;
            } else if(MMXPoll.MMXPollAnswer.TYPE.equals(objectType)){
                type =  TYPE_POLL_ANSWER;
            } else {
                type =  objectType;
            }
        } else {
            type =  mmxMessage.getContent().get(TAG_TYPE);
        }

        return type;
    }

    public String getMessageSummary() {
        String msgType = getType();
        if (msgType == null) {
            msgType = Message.TYPE_TEXT;
        }
        switch (msgType) {
            case Message.TYPE_MAP:
                return "User's location";
            case Message.TYPE_VIDEO:
                return "User's video";
            case Message.TYPE_PHOTO:
                return "User's photo";
            case Message.TYPE_TEXT:
                if(StringUtil.isNotEmpty(getText())) {
                    String text = getText().replace(System.getProperty("line.separator"), " ");
                    if (text.length() > 23) {
                        text = text.substring(0, 20) + "...";
                    }
                    return text;
                } else {
                    return "Text message";
                }
            case Message.TYPE_POLL:
                return "Poll";
            case Message.TYPE_POLL_ANSWER:
                return "Poll answer";
        }

        return null;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
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
        message.setMessageStatus(MessageStatus.DELIVERED);
        if(null == mmxMessage.getTimestamp()) {
            message.creationDate = new Date();
        }
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

    @Override public String toString() {
        return null != mmxMessage ? mmxMessage.toString() : "";
    }
}
