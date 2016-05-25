package com.magnet.magnetchat.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.layers.UserListContractLayer;
import com.magnet.magnetchat.presenters.core.BaseContract;
import com.magnet.magnetchat.ui.custom.AdapteredRecyclerView;
import com.magnet.max.android.User;

import java.util.List;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public interface UsersListContract extends UserListContractLayer {
    /**
     * View
     */
    interface View extends BaseContract.BaseView {

        /**
         * Method which privide the users adding to the list
         *
         * @param userObjects users list
         */
        void addUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the users list
         *
         * @param userObjects users list
         */
        void setUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the OnLazyLoadCallback
         *
         * @param lazyLoadCallback current lazyLoadCallback
         */
        void setLazyLoadCallback(@NonNull final AdapteredRecyclerView.OnLazyLoadCallback lazyLoadCallback);

        /**
         * Method which provide to getting of the selected user objects list
         *
         * @return user objects list
         */
        List<UserObject> getSelectedUserObjects();

        /**
         * Method which provide to getting of the selecting user objects
         *
         * @return user objects
         */
        List<User> getSelectedUsers();

        /**
         * Method which provide the switch loading message
         *
         * @param message    message
         * @param isNeedShow is need show loading message
         */
        void switchLoading(@Nullable final String message, final boolean isNeedShow);

        /**
         * Method which provide the user searching
         *
         * @param query query
         */
        void searchUsers(@NonNull final String query);

    }

    /**
     * Presenter
     */
    interface Presenter extends BaseContract.BasePresenter, AdapteredRecyclerView.OnLazyLoadCallback {

        /**
         * Method which provide the user searching
         *
         * @param query query
         */
        void searchUsers(@NonNull final String query, @NonNull final List<User> users);

        /**
         * Method which provide to getting of the all users with offset
         *
         * @param offset current offset
         */
        void getAllUsers(final int offset);

        /**
         * Method which provide the users getting
         *
         * @param filter filter by last name
         * @param offset offset
         */
        void getUsers(@NonNull final String filter, final int offset);

        /**
         * Method which provide the users getting
         *
         * @param filter    filter by last name
         * @param offset    offset
         * @param sortOrder sort order
         */
        void getUsers(@NonNull final String filter, final int offset, @NonNull final String sortOrder);

        /**
         * Method which provide the adding of the users list
         *
         * @param userObjects users list
         */
        void addUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the users
         *
         * @param userObjects current users
         */
        void setUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the switch loading message
         *
         * @param message    message
         * @param isNeedShow is need show loading message
         */
        void switchLoading(@Nullable final String message, final boolean isNeedShow);

    }

}
