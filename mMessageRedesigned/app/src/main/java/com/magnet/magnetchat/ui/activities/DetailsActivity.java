package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.adapters.UsersAdapter;
import com.magnet.max.android.User;

public class DetailsActivity extends BaseActivity {

    public static final String TAG_CHANNEL_NAME = "channelName";

    private String channelName;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        channelName = getIntent().getStringExtra(TAG_CHANNEL_NAME);
        if (channelName != null) {
            Conversation currentConversation = ChannelCacheManager.getInstance().getConversationByName(channelName);
            UsersAdapter adapter;
            if (currentConversation.getChannel().getOwnerId().equals(User.getCurrentUserId())) {
                adapter = new UsersAdapter(this, currentConversation.getSuppliersList(), addUserListener);
            } else {
                adapter = new UsersAdapter(this, currentConversation.getSuppliersList(), null);
            }
            ListView listView = (ListView) findViewById(R.id.detailsSubscribersList);
            listView.setAdapter(adapter);

            setTitle("Details");
        }
    }

    @Override
    public void onClick(View v) {

    }

    private UsersAdapter.AddUserListener addUserListener = new UsersAdapter.AddUserListener() {
        @Override
        public void addUser() {
            startActivity(ChooseUserActivity.getIntentToAddUserToChannel(channelName));
            finish();
        }
    };

    public static Intent createIntentForChannel(String channelName) {
        Intent intent = new Intent(CurrentApplication.getInstance(), DetailsActivity.class);
        intent.putExtra(TAG_CHANNEL_NAME, channelName);
        return intent;
    }

}
