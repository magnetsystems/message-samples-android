package com.magnet.chatsdkcover.mvp.presenters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.api.UsersListContract;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public class DefaultUsersListPresenter implements UsersListContract.Presenter {

    private static final String TAG = "DefaultUsersListPresenter";

    private final UsersListContract.View view;
    private final List<String> selectedUsers;
    private String filter;
    private String sortOrder;

    /**
     * Constructor
     *
     * @param view current view
     */
    public DefaultUsersListPresenter(UsersListContract.View view) {
        selectedUsers = new ArrayList<String>();
        this.view = view;
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
        view.setLazyLoadCallback(this);
        switchLoading(null, false);
        getAllUsers(0);
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

    /**
     * Method which provide the user searching
     *
     * @param query query
     */
    @Override
    public void searchUsers(@NonNull final String query, @NonNull final List<User> users) {
        selectedUsers.clear();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (User user : users) {
                    selectedUsers.add(user.getUserIdentifier());
                }
                getUsers(query, 0);
            }
        };
        new Thread(runnable).start();
    }

    /**
     * Method which provide to getting of the all users with offset
     *
     * @param offset current offset
     */
    @Override
    public void getAllUsers(final int offset) {
        getUsers(UsersListContract.QUERY_SEARCH_ALL_USERS, offset);
    }

    /**
     * Method which provide the users getting
     *
     * @param filter filter by last name
     * @param offset offset
     */
    @Override
    public void getUsers(@NonNull final String filter, final int offset) {
        getUsers(filter, offset, UsersListContract.SORT_ORDER);
    }

    /**
     * Method which provide the users getting
     *
     * @param filter    filter by last name
     * @param offset    offset
     * @param sortOrder sort order
     */
    @Override
    public void getUsers(@NonNull final String filter, final int offset, @NonNull final String sortOrder) {

        if (view != null
                && view.getCurrentContext() != null) {
            switchLoading(view.getCurrentContext().getString(R.string.text_loading_users_cover), true);
        }

        this.filter = filter;
        this.sortOrder = sortOrder;

        String fullQuery = String.format(UsersListContract.QUERY_TEMPLATE, filter);
        User.search(fullQuery, UsersListContract.LIMIT, offset, sortOrder, new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                final String currentUserID = User.getCurrentUserId();
                List<UsersListContract.UserObject> userObjects = new ArrayList<UsersListContract.UserObject>();
                if (users != null
                        && users.isEmpty() == false) {
                    for (User user : users) {
                        String userID = user.getUserIdentifier();
                        if (userID.equalsIgnoreCase(currentUserID) == false) {
                            UsersListContract.UserObject userObject = new UsersListContract.UserObject(user);
                            userObjects.add(userObject);
                            if (selectedUsers.contains(currentUserID)) {
                                userObject.setIsSelected(true);
                            }
                        }
                    }
                }

                if (userObjects != null
                        && userObjects.isEmpty() == false) {
                    if (offset == 0) {
                        setUsers(userObjects);
                    } else {
                        addUsers(userObjects);
                    }
                }

                switchLoading(null, false);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void failure(ApiError error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                }
                switchLoading(null, false);
            }
        });
    }

    /**
     * Method which provide the adding of the users list
     *
     * @param userObjects users list
     */
    @Override
    public void addUsers(@NonNull List<UsersListContract.UserObject> userObjects) {
        if (view != null) {
            view.addUsers(userObjects);
        }
    }

    /**
     * Method which provide the setting of the users
     *
     * @param userObjects current users
     */
    @Override
    public void setUsers(@NonNull List<UsersListContract.UserObject> userObjects) {
        if (view != null) {
            view.setUsers(userObjects);
        }
    }

    /**
     * Method which provide the switch loading message
     *
     * @param message    message
     * @param isNeedShow is need show loading message
     */
    @Override
    public void switchLoading(@Nullable final String message, final boolean isNeedShow) {
        if (view != null) {
            view.switchLoading(message, isNeedShow);
        }
    }

    /**
     * Method which provide the notifying about end of list
     *
     * @param listSize list size
     */
    @Override
    public void onAlmostAtBottom(int listSize) {
        getUsers(filter, listSize, sortOrder);
    }
}
