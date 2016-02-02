package com.magnet.messagingsample.models;

import com.magnet.max.android.Attachment;

/**
 * Created by edwardyang on 9/10/15.
 */
public class MessageVideo {

    public boolean left;
    public Attachment videoAttachment;
    public String username;

    public MessageVideo(boolean left, Attachment videoAttachment, String username) {
        super();
        this.left = left;
        this.videoAttachment = videoAttachment;
        this.username = username;
    }

}
