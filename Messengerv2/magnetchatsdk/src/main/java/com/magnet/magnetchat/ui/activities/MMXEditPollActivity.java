package com.magnet.magnetchat.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.ui.fragments.MMXEditPollFragment;
import com.magnet.magnetchat.ui.views.poll.MMXEditPollView;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by aorehov on 18.05.16.
 */
public class MMXEditPollActivity extends MMXBaseActivity implements MMXEditPollView.OnPollCreatedListener {

    private MMXEditPollFragment mmxEditPollFragment;
    private MenuItem uiCreate;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_mmxchat;
    }

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

        MMXChannel mmxChannel = BundleHelper.readMMXChannelFromBundle(getIntent().getExtras());
        if (mmxChannel == null) {
            toast("Cant create poll for empty channel");
            finish();
            return;
        }

        mmxEditPollFragment = new MMXEditPollFragment();
        mmxEditPollFragment.setArguments(getIntent().getExtras());
        mmxEditPollFragment.setOnPollCreateListener(this);
        replace(mmxEditPollFragment, R.id.mmx_chat, mmxEditPollFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_poll, menu);
        uiCreate = menu.findItem(R.id.mmx_create);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(item.getItemId() == R.id.mmx_create){
            mmxEditPollFragment.doCreatePoll();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocked() {
        uiCreate.setEnabled(false);
    }

    @Override
    public void onUnlocked() {
        uiCreate.setEnabled(true);
    }

    @Override
    public void onPollSaveSuccess(MMXMessage mmxMessage) {
        uiCreate.setEnabled(false);
        setResult(RESULT_OK);
        finish();
    }
}
