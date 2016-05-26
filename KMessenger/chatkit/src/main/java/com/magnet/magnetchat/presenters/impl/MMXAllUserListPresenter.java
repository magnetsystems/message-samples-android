package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.callbacks.MMXAction2;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.util.LazyLoadUtil;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 13.05.16.
 */
class MMXAllUserListPresenter implements UserListContract.Presenter, LazyLoadUtil.OnNeedLoadingCallback {

    private final String DEFAULT_USER_ORDER = "firstName:asc";
    private final String NAME_SEARCH_QUERY = "firstName:%s* OR lastName:%s*";
    private static final int PAGE_SIZE = 40;

    private UserListContract.View view;
    private LazyLoadUtil lazyLoadUtil;
    private BaseConverter<User, MMXUserWrapper> converter;
    private List<MMXUserWrapper> selected = new ArrayList<>();
    private int userAmount = Integer.MAX_VALUE;
    private int localSize;
    private String searchQuery = "";
    private boolean isSearch = false;

    private UserListContract.OnSelectUserEvent selectUserEvent;
    private UserListContract.OnGetAllSelectedUsersListener allUsersEventListener;

    public MMXAllUserListPresenter(UserListContract.View view, BaseConverter<User, MMXUserWrapper> converter) {
        this.view = view;
        this.converter = converter;
        lazyLoadUtil = new LazyLoadUtil(PAGE_SIZE, (int) (PAGE_SIZE * 0.35), this);
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
        if (index == -1) {
            return;
        }

        this.localSize = localSize;
        lazyLoadUtil.checkLazyLoad(userAmount, localSize, index);
    }

    @Override
    public void doClickOn(MMXUserWrapper typed) {
        MMXUserWrapper newWrapper = new MMXUserWrapper(typed);
        newWrapper.setSelected(!typed.isSelected());

        if (newWrapper.isSelected()) selected.add(newWrapper);
        else selected.remove(newWrapper);

        view.onPut(newWrapper);
        if (selectUserEvent != null) selectUserEvent.onSelectEvent(newWrapper);
    }

    @Override
    public void onInit(Bundle bundle) {

    }

    @Override
    public void search(String query) {
        if (query == null) {
            query = "";
        } else {
            query = query.trim();
        }

        if (searchQuery.equals(query)) {
            return;
        }

        this.searchQuery = query;
        this.isSearch = true;
        doSearch();
    }

    @Override
    public void setSelectUserEvent(UserListContract.OnSelectUserEvent selectUserEvent) {
        this.selectUserEvent = selectUserEvent;
    }

    @Override
    public void doGetAllSelectedUsers() {
        if (allUsersEventListener != null) allUsersEventListener.onGetAllSelectedUsers(selected);
    }

    @Override
    public void setOnGetAllSelectedUsersListener(UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener) {
        this.allUsersEventListener = onGetAllSelectedUsersListener;
    }

    private void doSearch() {
        load(0);
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

    private void onUsersReceived(List<User> users, final boolean isReplace) {
        if (!isReplace) {
            if (users.size() != PAGE_SIZE)
                userAmount = localSize;
        } else {
            userAmount = Integer.MAX_VALUE;
        }
        converter
                .filtre(new MMXAction2<User, Boolean>() {
                    @Override
                    public Boolean call(User action) {
                        return !action.getUserIdentifier().equals(User.getCurrentUserId());
                    }
                })
                .map(new MMXAction<MMXUserWrapper>() {
                    @Override
                    public void call(MMXUserWrapper action) {
                        action.setSelected(selected.contains(action));
                    }
                }).convert(users, new MMXAction<List<MMXUserWrapper>>() {
            @Override
            public void call(List<MMXUserWrapper> action) {
                view.onLoadingComplete();
                lazyLoadUtil.onLoadingFinished();
                if (!isReplace) {
                    view.onPut(action);
                } else {
                    view.onSet(action);
                }
            }
        });
    }

    private final ApiCallback<List<User>> callback = new ApiCallback<List<User>>() {
        @Override
        public void success(List<User> users) {
            onUsersReceived(users, isSearch);
            isSearch = false;
        }

        @Override
        public void failure(ApiError apiError) {
            view.showMessage(R.string.err_user_loading);
            view.onLoadingComplete();
            lazyLoadUtil.onLoadingFinished();
        }
    };
}
