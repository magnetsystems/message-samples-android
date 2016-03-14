package com.magnet.magnetchat.mvp.api;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

/**
 * Created by dlernatovich on 3/2/16.
 */
public interface ChooseUserContract {

    enum ChooseMode {MODE_NEW_CHAT, MODE_ADD_USER}

    String DEFAULT_USER_ORDER = "lastName:asc";

    class UserQuery {
        private final String query;
        private final String order;
        private final boolean isDefault;
        private int currentOffset;

        public UserQuery(String query, String order, boolean isDefault) {
            this.query = query;
            this.order = order;
            this.isDefault = isDefault;
            this.currentOffset = 0;
        }

        public String getQuery() {
            return query;
        }

        public String getOrder() {
            return order;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public int getCurrentOffset() {
            return currentOffset;
        }

        public void setCurrentOffset(int currentOffset) {
            this.currentOffset = currentOffset;
        }

        public void addCurrentOffset(int offset) {
            this.currentOffset += offset;
        }
    }

    interface View {

        /**
         * Method which provide to switching of the search user progress
         *
         * @param active
         */
        void setProgressIndicator(boolean active);

        /**
         * Method which provide the list updating from the list of users object
         *
         * @param users users list
         */
        void showUsers(@NonNull List<User> users, boolean toAppend);

        /**
         * Method which provide the closing of the Activity
         */
        void finishSelection();

        /**
         * Method which provide the getting of the activity
         *
         * @return current activity
         */
        Activity getActivity();

    }

    interface Presenter extends IListPresenter<User> {

        /**
         * Method which provide the user selection
         *
         * @param userList user list
         */
        void onUsersSelected(@NonNull final List<User> userList);

        /**
         * Method which provide to adding of the user to the chat
         *
         * @param selectedUsers selected users
         */
        void onAddUsersToChat(@NonNull List<User> selectedUsers);

        /**
         * Method which provide the creating of the new chat
         *
         * @param selectedUsers selected users
         */
        void onNewChat(@NonNull List<User> selectedUsers);

        UserQuery getDefaultQuery();
    }

}
