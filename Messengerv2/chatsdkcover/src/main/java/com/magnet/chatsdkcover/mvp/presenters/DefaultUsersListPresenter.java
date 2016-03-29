package com.magnet.chatsdkcover.mvp.presenters;

import com.magnet.chatsdkcover.mvp.api.UsersListContract;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public class DefaultUsersListPresenter implements UsersListContract.Presenter {

    private final UsersListContract.View view;

    /**
     * Constructor
     *
     * @param view current view
     */
    public DefaultUsersListPresenter(UsersListContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
    }

    /**
     * Method which provide the action when Activity/Fragment call method onResume
     */
    @Override
    public void onActivityResume() {

    }

    /**
     * Method which provide the action when Activity/Fragment call method onPauseActivity
     */
    @Override
    public void onActivityPause() {

    }

    /**
     * Method which provide the action when Activity/Fragment call method onDestroy
     */
    @Override
    public void onActivityDestroy() {

    }
}
