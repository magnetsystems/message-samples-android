package com.magnet.magnetchat.presenters;

import android.os.Bundle;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
public interface UserListContract {

    interface Presenter extends MMXPresenter {

        /**
         * call this method if you need refresh the list of users
         */
        void doRefresh();

        /**
         * call this method if you scroll user's list
         */
        void onCurrentPosition(int localSize, int index);

        /**
         * call this method if you have clicked on item
         */
        void doClickOn(MMXUserWrapper typed);

        /**
         * call init method
         *
         * @param bundle
         */
        void onInit(Bundle bundle);

        /**
         * do search in the user's list
         *
         * @param query
         */
        void search(String query);

        /**
         * Set user's selector listener
         *
         * @param selectUserEvent
         */
        void setSelectUserEvent(OnSelectUserEvent selectUserEvent);

        /**
         * if you call this method you will receive selected users into OnGetAllSelectedUsersListener
         */
        void doGetAllSelectedUsers();

        /**
         * set selected user's callback into presenter
         *
         * @param onGetAllSelectedUsersListener
         */
        void setOnGetAllSelectedUsersListener(OnGetAllSelectedUsersListener onGetAllSelectedUsersListener);

        /**
         * set user's ids which you don't want to display in list
         *
         * @param ids
         */
        void setExcludeUserIds(Collection<String> ids);

        /**
         * @return the list of user's ids of the current list
         */
        ArrayList<String> getUserIds();

    }

    interface View extends MMXInfoView {
        /**
         * You should add instance of MMXUserWrapper to current list
         *
         * @param wrapper
         */
        void onPut(MMXUserWrapper wrapper);

        /**
         * you should remove instance of user from list
         *
         * @param wrapper
         */
        void onDelete(MMXUserWrapper wrapper);

        /**
         * You should set new user's list
         *
         * @param wrapper
         */
        void onSet(List<MMXUserWrapper> wrapper);

        /**
         * You should add received user's list to current ui list
         *
         * @param wrappers
         */
        void onPut(List<MMXUserWrapper> wrappers);

        /**
         * called if user's list are loading
         */
        void onLoading();

        /**
         * called if user's list has been loaded
         */
        void onLoadingComplete();

        /**
         * called if can't load user's list
         */
        void onCantLoadChannel();

    }

    interface OnSelectUserEvent {
        void onSelectEvent(MMXUserWrapper wrapper);
    }

    interface OnGetAllSelectedUsersListener {
        void onGetAllSelectedUsers(List<MMXUserWrapper> selectedUsers);
    }

}
