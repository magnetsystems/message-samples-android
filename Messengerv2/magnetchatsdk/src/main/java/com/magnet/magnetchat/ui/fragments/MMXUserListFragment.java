package com.magnet.magnetchat.ui.fragments;

import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

/**
 * Created by aorehov on 12.05.16.
 */
public class MMXUserListFragment extends MMXBaseFragment {

    private MMXUserListView userListView;
    private UserListContract.OnSelectUserEvent eventListener;
    private UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener;

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

    @Override
    public void onStart() {
        super.onStart();
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

}
