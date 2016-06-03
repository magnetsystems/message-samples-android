package com.magnet.magnetchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.presenters.MMXChannelSettingsContract;
import com.magnet.magnetchat.ui.fragments.MMXUserListFragment;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;

/**
 * See static method how to create activity intent;
 *
 * @see MMXChatDetailsActivity.createIntent
 * <p/>
 * Created by aorehov on 12.05.16.
 */
public class MMXChatDetailsActivity extends MMXBaseActivity implements CompoundButton.OnCheckedChangeListener, MMXChannelSettingsContract.View {

    private static final int RC_ADD_USERS = 0xFC00;
    private MMXUserListFragment userListFragment;
    private SwitchCompat uiMute;
    private MMXChannelSettingsContract.Presenter presenter;
    private Boolean muteState;
    private MMXChannel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar uiToolbar = findView(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(uiToolbar);
            uiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            View view = uiToolbar.getRootView();
            uiToolbar.setVisibility(View.GONE);
            if (view instanceof ViewGroup) {
                ((ViewGroup) view).removeView(uiToolbar);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        Bundle extras = getIntent().getExtras();
        userListFragment = new MMXUserListFragment();
        userListFragment.setArguments(extras);
        replace(userListFragment, R.id.mmx_chat, userListFragment.getTag());

        channel = BundleHelper.readMMXChannelFromBundle(extras);
        presenter = ChatSDK.getPresenterFactory().createChannelSettingsPresenter(this);
        presenter.setMMXChannelName(channel.getName());
        presenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResumed();
    }

    @Override
    protected void onPause() {
        presenter.onPaused();
        super.onPause();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId = R.menu.menu_chat_details;
        getMenuInflater().inflate(menuId, menu);
        uiMute = (SwitchCompat) menu.findItem(R.id.mmx_mute).getActionView();
        if (muteState != null) {
            uiMute.setChecked(muteState);
            muteState = null;
        }

        MenuItem item = menu.findItem(R.id.mmx_add);
        item.setVisible(channel != null && channel.getOwnerId().equals(User.getCurrentUserId()));

        uiMute.setOnCheckedChangeListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.mmx_add) {
            if (channel != null) {
                ArrayList<String> ids = userListFragment.getUserIds();
                Intent intent = MMXUsersActivity.createActivityIntent(this, channel, ids);
                if (intent == null) return true;
                startActivityForResult(intent, RC_ADD_USERS);
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_mmxchat;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        presenter.doMute(isChecked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_ADD_USERS && resultCode == RESULT_OK) {
            userListFragment.refresh();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMuteState(boolean isMute) {
        if (uiMute == null) {
            muteState = isMute;
            return;
        }
        uiMute.setOnCheckedChangeListener(null);
        uiMute.setChecked(isMute);
        uiMute.setOnCheckedChangeListener(this);
    }

    @Override
    public void onLoading() {
        if(uiMute == null) return;
        uiMute.setEnabled(false);
    }

    @Override
    public void onLoadingCompleted() {
        if(uiMute == null) return;
        uiMute.setEnabled(true);
    }

    @Override
    public void onChannelDeleted() {
        finish();
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

//    ===========================================================
//    static methods
//    ===========================================================

    /**
     * @param mmxChannel instance of MMXChannel
     * @return MMXChatDetailsDetailsActivity intent or null
     * @see MMXChannel
     */
    @Nullable
    public static Intent createIntent(Context context, MMXChannel mmxChannel) {
        Bundle bundle = BundleHelper.packChannel(mmxChannel);
        if (bundle == null) return null;
        Intent intent = new Intent(context, MMXChatDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }
}
