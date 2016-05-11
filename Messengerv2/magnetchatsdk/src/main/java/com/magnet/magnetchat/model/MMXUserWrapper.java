package com.magnet.magnetchat.model;

import com.magnet.max.android.User;

/**
 * Created by aorehov on 11.05.16.
 */
public class MMXUserWrapper extends MMXObjectWrapper<User> implements Typed {
    private int type;
    private boolean isSelected;

    public MMXUserWrapper(User obj, int type) {
        super(obj);
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
