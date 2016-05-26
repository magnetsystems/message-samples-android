package com.magnet.magntetchatapp.ui.activities.section.users;

import android.view.MenuItem;

import com.magnet.magnetchat.ui.views.users.DefaultUsersListView;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;

import butterknife.InjectView;

public class CreateChannelActivity extends BaseActivity {

    @InjectView(R.id.viewUsers)
    DefaultUsersListView viewChannels;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_channel;
    }

    @Override
    protected int getMenuId() {
        return NONE_MENU;
    }

    @Override
    protected void onCreateActivity() {
        enableBackButton();
        viewChannels.onCreateActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewChannels.onResumeActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewChannels.onPauseActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewChannels.onDestroyActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
