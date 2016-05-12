package com.magnet.magnetchat.ui.fragments;

import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

/**
 * Created by aorehov on 12.05.16.
 */
public class UserListFragment extends MMXBaseFragment {

    private MMXUserListView userListView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        FrameLayout uiFrameLayout = findView(containerView, R.id.mmx_users);
        userListView = ChatSDK.getViewFactory().createMmxUserListView(getContext());
        uiFrameLayout.addView(userListView);
        userListView.onInit(getArguments());
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

}
