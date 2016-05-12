package com.magnet.magnetchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.ui.fragments.UserListFragment;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 12.05.16.
 */
public class ChatDetailsV2Activity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private UserListFragment userListFragment;
    private SwitchCompat uiMute;

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


        userListFragment = new UserListFragment();
        userListFragment.setArguments(getIntent().getExtras());
        replace(userListFragment, R.id.mmx_chat, userListFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId = R.menu.menu_chat_details;
        getMenuInflater().inflate(menuId, menu);
        uiMute = (SwitchCompat) menu.findItem(R.id.muteAction).getActionView();
        uiMute.setOnCheckedChangeListener(this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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

    }

    public static Intent createIntent(Context context, MMXChannel mmxChannel) {
        Bundle bundle = BundleHelper.packChannel(mmxChannel);
        Intent intent = new Intent(context, ChatDetailsV2Activity.class);
        intent.putExtras(bundle);
        return intent;
    }
}
