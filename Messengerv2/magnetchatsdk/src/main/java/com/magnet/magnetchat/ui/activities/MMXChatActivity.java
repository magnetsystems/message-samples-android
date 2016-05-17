package com.magnet.magnetchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.ui.fragments.MMXChatFragment;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 04.05.16.
 */
public class MMXChatActivity extends BaseActivity implements ChatListContract.ChannelNameListener {

    private MMXChatFragment chatFragment;

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

        uiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle bundle = null;
        MMXChannel channel = IntentHelper.getMMXChannelFromIntent(getIntent());
        if (channel != null) {
            bundle = BundleHelper.packChannel(channel);
        }

        if (bundle == null) {
            ArrayList<User> list = IntentHelper.getRecipientsFromIntent(getIntent());
            bundle = BundleHelper.packRecipients(list);
        }

        if (bundle == null) {
            finish();
            return;
        }

        chatFragment = new MMXChatFragment();
        chatFragment.setArguments(bundle);
        chatFragment.setChatNameListener(this);
        replace(chatFragment, R.id.mmx_chat, chatFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    public static Intent createIntent(Context context, @NonNull MMXChannel mmxChannel) {
        return IntentHelper.chatActivity(context, mmxChannel, MMXChatActivity.class);
    }

    public static Intent createIntent(Context context, @NonNull List<User> recipients) {
        return IntentHelper.chatActivity(context, recipients, MMXChatActivity.class);
    }

    void onSetName(CharSequence sequence) {
        getSupportActionBar().setTitle(sequence);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chatFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.mmxchat_edit) {
            PostMMXMessageContract.Presenter contract = chatFragment.getMessageContract();
            if (contract != null) {
                MMXChannel channel = contract.getMMXChannel();
                if (channel != null) {
//                    Intent intent = ChatDetailsActivity.createIntentForChannel(this, channel);
                    Intent intent = MMXChatDetailsActivity.createIntent(this, channel);
                    startActivity(intent);
                }
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onName(String name) {
        onSetName(name);
    }
}
