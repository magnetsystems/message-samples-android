package com.magnet.magnetchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.mvp.api.ChatDetailsContract;
import com.magnet.magnetchat.mvp.presenters.ChatDetailsPresenterImpl;
import com.magnet.magnetchat.ui.adapters.UserProfilesAdapter;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;


public class ChatDetailsActivity extends BaseActivity implements ChatDetailsContract.View {

    public static final String TAG_CHANNEL = "channel";

    RecyclerView listView;
    UserProfilesAdapter mUserAdapter;

    ProgressBar detailsProgress;
    LinearLayout llAddRecipients;

    ChatDetailsContract.Presenter mPresenter;


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

        setTitle("Chat Details");

        detailsProgress = (ProgressBar) findViewById(R.id.detailsProgress);
        listView = (RecyclerView) findViewById(R.id.detailsSubscribersList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        listView.setLayoutManager(layoutManager);

        llAddRecipients = (LinearLayout) findViewById(R.id.llAddRecipients);

        MMXChannel currentChannel = getIntent().getParcelableExtra(TAG_CHANNEL);
        if (currentChannel != null) {
//            if (StringUtil.isStringValueEqual(currentChannel.getOwnerId(), User.getCurrentUserId())) {
//                llAddRecipients.setVisibility(View.VISIBLE);
//
//                setOnClickListeners(llAddRecipients);
//            }

            mPresenter = new ChatDetailsPresenterImpl(this, currentChannel, this);
            mPresenter.onLoadRecipients(true);
        } else {
            exit("Channel not set");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llAddRecipients) {
            mPresenter.onAddRecipients();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPresenter.isChannelOwner()) {
            int menuId = R.menu.menu_chat_details;
            getMenuInflater().inflate(menuId, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();
        if (itemID == android.R.id.home) {
            finish();
        } else if (itemID == R.id.menuItemAddUser) {
            mPresenter.onAddRecipients();
        }

//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                //NavUtils.navigateUpFromSameTask(this);
//                finish();
//                return true;
//            case :
//
//            default:
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent createIntentForChannel(Context context, Chat conversation) {
        Intent intent = new Intent(context, ChatDetailsActivity.class);
        intent.putExtra(TAG_CHANNEL, conversation.getChannel());
        return intent;
    }


    private void exit(String error) {
        showMessage("Couldn't load channel due to " + error + ". Please try later");
        finish();
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (detailsProgress != null) {
            detailsProgress.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void showRecipients(List<UserProfile> recipients) {
        if (null == mUserAdapter) {
            mUserAdapter = new UserProfilesAdapter(this, recipients, mPresenter.getItemComparator());
            listView.setAdapter(mUserAdapter);
        } else {
            mUserAdapter.swapData(recipients);
        }
    }

    @Override
    public void finishDetails() {
        finish();
    }
}
