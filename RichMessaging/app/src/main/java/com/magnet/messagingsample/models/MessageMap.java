package com.magnet.messagingsample.models;

/**
 * Created by edwardyang on 9/10/15.
 */
public class MessageMap {

    public boolean left;
    public String latlng;
    public String username;

    public MessageMap(boolean left, String latlng, String username) {
        super();
        this.left = left;
        this.latlng = latlng;
        this.username = username;
    }

}
