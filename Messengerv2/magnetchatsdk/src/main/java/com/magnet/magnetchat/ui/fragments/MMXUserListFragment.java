package com.magnet.magnetchat.ui.fragments;

import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

import java.util.ArrayList;

/**
 * Created by aorehov on 12.05.16.
 */
public class MMXUserListFragment extends MMXBaseFragment {

    private MMXUserListView userListView;
    private UserListContract.OnSelectUserEvent eventListener;
    private UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener;
    private ArrayList<String> pendingIds;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        FrameLayout uiFrameLayout = findView(containerView, R.id.mmx_users);
        userListView = createMMXUserListView();
        uiFrameLayout.addView(userListView);
        userListView.onInit(getArguments());
        userListView.setOnUserSelectEventListener(eventListener);
        userListView.setOnGetAllSelectedUsersListener(onGetAllSelectedUsersListener);
    }

    protected MMXUserListView createMMXUserListView() {
        return ChatSDK.getViewFactory().createMmxUserListView(getContext());
    }

    public ArrayList<String> getUserIds() {
        return userListView.getUserIds();
    }

    @Override
    public void onStart() {
        super.onStart();
        setExcludeUserIds(pendingIds);
        userListView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        userListView.onResume();
    }

    @Override
    public void onPause() {
        userListView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        userListView.onStop();
        super.onStop();
    }

    public void search(String term) {
        if (userListView == null) return;
        userListView.search(term);
    }

    public void doGetAllSelectedUsersEvent() {
        if (userListView != null) userListView.doGetSelectedUsersEvent();
    }

    public void setOnUserSelectEventListener(UserListContract.OnSelectUserEvent eventListener) {
        if (userListView != null) {
            userListView.setOnUserSelectEventListener(eventListener);
            this.eventListener = null;
        } else {
            this.eventListener = eventListener;
        }
    }

    public void setOnGetAllSelectedUsersListener(UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener) {
        if (userListView != null) {
            userListView.setOnGetAllSelectedUsersListener(onGetAllSelectedUsersListener);
            this.onGetAllSelectedUsersListener = null;
        } else {
            this.onGetAllSelectedUsersListener = onGetAllSelectedUsersListener;
        }
    }

    public void refresh() {
        if (userListView != null) userListView.refresh();
    }

    public void setExcludeUserIds(ArrayList<String> ids) {
        if (userListView == null) {
            pendingIds = ids;
        } else if (ids != null) {
            userListView.setExcludeUserIdsList(ids);
            pendingIds = null;
        }
    }
}
