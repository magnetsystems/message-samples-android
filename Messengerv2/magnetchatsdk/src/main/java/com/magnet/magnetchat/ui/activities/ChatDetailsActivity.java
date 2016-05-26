package com.magnet.magnetchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.presenters.ChatDetailsContract;
import com.magnet.magnetchat.presenters.impl.ChatDetailsPresenterImpl;
import com.magnet.magnetchat.ui.adapters.UserProfilesAdapter;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;

@Deprecated
public class ChatDetailsActivity extends MMXBaseActivity implements ChatDetailsContract.View, CompoundButton.OnCheckedChangeListener {

    public static final String TAG_CHANNEL = "channel";

    SwitchCompat uiMute;
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
//        if (mPresenter.isChannelOwner()) {
        int menuId = R.menu.menu_chat_details;
        getMenuInflater().inflate(menuId, menu);
        uiMute = (SwitchCompat) menu.findItem(R.id.mmx_mute).getActionView();
        uiMute.setOnCheckedChangeListener(this);
        mPresenter.requestMuteChannelState();
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();
        if (itemID == android.R.id.home) {
            finish();
        } else if (itemID == R.id.mmx_add) {
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
        return createIntentForChannel(context, conversation.getChannel());
    }

    public static Intent createIntentForChannel(Context context, MMXChannel mmxChannel) {
        Intent intent = new Intent(context, ChatDetailsActivity.class);
        intent.putExtra(TAG_CHANNEL, mmxChannel);
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

    @Override
    public void onMute(boolean isMuted) {
        uiMute.setOnCheckedChangeListener(null);
        uiMute.setChecked(isMuted);
        uiMute.setOnCheckedChangeListener(this);
    }

    @Override
    public void onMessage(int stringRes) {
        toast(stringRes);
    }

    @Override
    public void onMessage(CharSequence message) {
        toast(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.changeMuteAction();
    }
}
