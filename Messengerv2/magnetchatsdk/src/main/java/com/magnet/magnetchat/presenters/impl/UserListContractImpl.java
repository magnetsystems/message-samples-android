package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.Typed;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.util.LazyLoadUtil;

/**
 * Created by aorehov on 11.05.16.
 */
public class UserListContractImpl implements UserListContract.Presenter {

    private LazyLoadUtil lazyLoadUtil;

    public UserListContractImpl(LazyLoadUtil lazyLoadUtil) {
        this.lazyLoadUtil = lazyLoadUtil;
    }

    @Override
    public void doRefresh() {

    }

    @Override
    public void onCurrentPosition(int localSize, int index) {

    }

    @Override
    public void doClickOn(Typed typed) {
        if (typed instanceof MMXUserWrapper) {
            MMXUserWrapper wrapper = (MMXUserWrapper) typed;
            wrapper.setSelected(!wrapper.isSelected());
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bundle onSaveInstance(Bundle savedInstances) {
        return null;
    }

    @Override
    public void onRestore(Bundle savedInstances) {

    }
}
