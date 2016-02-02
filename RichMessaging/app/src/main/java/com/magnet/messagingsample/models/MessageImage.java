package com.magnet.messagingsample.models;

import com.magnet.max.android.Attachment;

/**
 * Created by edwardyang on 9/10/15.
 */
public class MessageImage {

    public boolean left;
    public Attachment imageAttachment;
    public String username;

    public MessageImage(boolean left, Attachment imageAttachment, String username) {
        super();
        this.left = left;
        this.imageAttachment = imageAttachment;
        this.username = username;
    }

}
