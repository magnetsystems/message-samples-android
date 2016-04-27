package com.magnet.magnetchat.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.ui.fragments.BaseFragment;
import com.magnet.magnetchat.ui.fragments.ChatListFragment;
import com.magnet.max.android.User;

public class ChannelListActivity extends BaseActivity implements BaseActivityCallback {
    private static final String TAG = ChannelListActivity.class.getSimpleName();

    Toolbar toolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (User.getCurrentUser() != null && User.getCurrentUser().getDisplayName() != null) {
            getSupportActionBar().setTitle(User.getCurrentUser().getDisplayName());
        }

        setFragment();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (User.getCurrentUser() != null) {

        } else {
            Log.w(TAG, "CurrentUser is null, logout");
            //TODO :
            //UserHelper.logout(logoutListener);
        }
    }

    /**
     * method which provide the setting of the current fragment co container mView
     */
    private void setFragment() {
        BaseFragment baseFragment = new ChatListFragment();
        baseFragment.setBaseActivityCallback(this);
        replace(baseFragment, R.id.container, "chats");
    }

    @Override
    public void onReceiveFragmentEvent(Event event) {

    }

    @Override
    public void onClick(View v) {

    }
}
