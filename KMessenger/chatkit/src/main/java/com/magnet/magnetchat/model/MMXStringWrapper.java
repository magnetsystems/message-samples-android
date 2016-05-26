package com.magnet.magnetchat.model;

/**
 * Created by aorehov on 17.05.16.
 */
public class MMXStringWrapper extends MMXObjectWrapper<String> {

    public final static int TYPE_STRING_EDITABLE = 0xF001;

    public MMXStringWrapper(String obj) {
        this(obj, TYPE_STRING_EDITABLE);
    }

    public MMXStringWrapper(String obj, int type) {
        super(obj, type);
    }

    public void setString(String obj) {
        this.obj = obj;
    }
}
