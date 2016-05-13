package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.MMXUserConverter;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.util.LazyLoadUtil;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import java.util.List;

/**
 * Created by aorehov on 13.05.16.
 */
class MMXAllUserListPresenter implements UserListContract.Presenter, LazyLoadUtil.OnNeedLoadingCallback {

    private UserListContract.View view;
    private LazyLoadUtil lazyLoadUtil;
    private static final int PAGE_SIZE = 50;
    private MMXUserConverter converter;

    private final String DEFAULT_USER_ORDER = "firstName:asc";
    private final String NAME_SEARCH_QUERY = "firstName:%s* OR lastName:%s*";
    private int userAmount = Integer.MAX_VALUE;
    private int localSize;
    private String searchQuery = "";

    public MMXAllUserListPresenter(UserListContract.View view, MMXUserConverter converter) {
        this.view = view;
        this.converter = converter;
        lazyLoadUtil = new LazyLoadUtil((int) (PAGE_SIZE * 0.33), this);
    }

    private void load(int offset) {
        load(offset, PAGE_SIZE);
    }

    private void load(int offset, int pageSize) {
        view.onLoading();
        lazyLoadUtil.onLoading();
        User.search(String.format(NAME_SEARCH_QUERY, searchQuery, searchQuery), pageSize, offset, DEFAULT_USER_ORDER, callback);
    }

    @Override
    public void doRefresh() {
        load(0);
    }

    @Override
    public void onCurrentPosition(int localSize, int index) {
        this.localSize = localSize;
        lazyLoadUtil.checkLazyLoad(userAmount, localSize, index);
    }

    @Override
    public void doClickOn(MMXUserWrapper typed) {

    }

    @Override
    public void onInit(Bundle bundle) {

    }

    @Override
    public void search(String query) {
        this.searchQuery = query;
        doSearch();
    }

    private void doSearch() {
    }

    @Override
    public void onStart() {
        doRefresh();
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

    @Override
    public void onNeedLoad(int loadFromPosition) {
        load(loadFromPosition);
    }

    private void onUsersReceived(List<User> users) {
        if (users.size() != PAGE_SIZE)
            userAmount = localSize;
        converter.convert(users, new MMXAction<List<MMXUserWrapper>>() {
            @Override
            public void call(List<MMXUserWrapper> action) {
                view.onLoadingComplete();
                lazyLoadUtil.onLoadingFinished();
                view.onPut(action);
            }
        });
    }

    private final ApiCallback<List<User>> callback = new ApiCallback<List<User>>() {
        @Override
        public void success(List<User> users) {
            onUsersReceived(users);
        }

        @Override
        public void failure(ApiError apiError) {
            view.showMessage(R.string.err_user_loading);
            view.onLoadingComplete();
            lazyLoadUtil.onLoadingFinished();
        }
    };
}
