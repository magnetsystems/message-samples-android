package com.magnet.magnetchat.presenters;

import android.os.Bundle;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;

import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
public interface UserListContract {

    interface Presenter extends MMXPresenter {
        void doRefresh();

        void onCurrentPosition(int localSize, int index);

        void doClickOn(MMXUserWrapper typed);

        void onInit(Bundle bundle);

        void search(String query);

//        void setMMXChannel(MMXChannel mmxChannel);
//
//        void setMMXChannelDetails(ChannelDetail details);
//
//        void setMMXChannelWrapper(MMXChannelWrapper mmxChannelWrapper);

        void setSelectUserEvent(OnSelectUserEvent selectUserEvent);

        void doGetAllSelectedUsers();

        void setOnGetAllSelectedUsersListener(OnGetAllSelectedUsersListener onGetAllSelectedUsersListener);

    }

    interface View extends MMXInfoView {
        void onPut(MMXUserWrapper wrapper);

        void onDelete(MMXUserWrapper wrapper);

        void onSet(List<MMXUserWrapper> wrapper);

        void onPut(List<MMXUserWrapper> wrappers);

        void onLoading();

        void onLoadingComplete();

        void onCantLoadChannel();

    }

    interface OnSelectUserEvent {
        void onSelectEvent(MMXUserWrapper wrapper);
    }

    interface OnGetAllSelectedUsersListener {
        void onGetAllSelectedUsers(List<MMXUserWrapper> selectedUsers);
    }

}
