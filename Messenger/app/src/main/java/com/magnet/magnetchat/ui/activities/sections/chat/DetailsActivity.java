package com.magnet.magnetchat.ui.activities.sections.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.adapters.UsersAdapter;
import com.magnet.max.android.User;
import com.magnet.max.android.util.StringUtil;

import butterknife.InjectView;

public class DetailsActivity extends BaseActivity {

    public static final String TAG_CHANNEL_NAME = "channelName";

    @InjectView(R.id.detailsSubscribersList)
    RecyclerView listView;

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        listView.setLayoutManager(layoutManager);

        channelName = getIntent().getStringExtra(TAG_CHANNEL_NAME);
        if (channelName != null) {
            Conversation currentConversation = ChannelCacheManager.getInstance().getConversationByName(channelName);
            if(null != currentConversation && null != currentConversation.getChannel()) {
                UsersAdapter adapter;
                if (StringUtil.isStringValueEqual(currentConversation.ownerId(), User.getCurrentUserId())) {
                    adapter = new UsersAdapter(this, currentConversation.getSuppliersList(), addUserListener);
                } else {
                    adapter = new UsersAdapter(this, currentConversation.getSuppliersList(), null);
                }
                listView.setAdapter(adapter);
                setTitle("Details");
            } else {
                showMessage("Couldn't load channel. Please try later");
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
