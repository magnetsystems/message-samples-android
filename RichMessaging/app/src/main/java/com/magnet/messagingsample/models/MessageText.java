package com.magnet.messagingsample.models;

/**
 * Created by edwardyang on 9/10/15.
 */
public class MessageText {

    public boolean left;
    public String text;
    public String username;

    public MessageText(boolean left, String text, String username) {
        super();
        this.left = left;
        this.text = text;
        this.username = username;
    }

}
