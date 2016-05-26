package com.magnet.magnetchat.presenters.core;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXInfoView {

    void showMessage(CharSequence sequence);

    void showMessage(int resId, Object... objects);

}
