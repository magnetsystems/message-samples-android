package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.constants.AppFragment;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.factories.FragmentFactory;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import butterknife.InjectView;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends BaseActivity implements BaseActivityCallback {
    private static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.listHomeDrawer)
    ListView listHomeDrawer;
    @InjectView(R.id.textUserName)
    FTextView textUserFullName;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.viewEvents)
    View viewEvents;

    @InjectView(R.id.flPrimary)
    FrameLayout flPrimary;
    @InjectView(R.id.tvPrimarySubscribers)
    FTextView tvPrimarySubscribers;

    @InjectView(R.id.flSecondary)
    FrameLayout flSecondary;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @InjectView(R.id.llUserProfile)
    LinearLayout llUserProfile;

    @InjectView(R.id.ivUserAvatar) CircleImageView ivUserAvatar;

    private AppFragment currentFragment;

    private ChannelDetail primaryChannel;
    private static final String PRIMARY_CHANNEL_TAG = "active";
    private ChannelDetail secondaryChannel;
    private static final String SECONDARY_CHANNEL_TAG = "global";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flPrimary.setVisibility(View.GONE);
        flSecondary.setVisibility(View.GONE);

        loadHighlightedChannel(PRIMARY_CHANNEL_TAG);
        loadHighlightedChannel(SECONDARY_CHANNEL_TAG);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        listHomeDrawer.setOnItemClickListener(menuClickListener);

        setFragment(AppFragment.HOME);
    }

    @Override protected void onResume() {
        super.onResume();

        if (User.getCurrentUser() != null) {
            textUserFullName.setSafeText(User.getCurrentUser().getDisplayName());
            toolbar.setTitle(User.getCurrentUser().getDisplayName());
        }

        Glide.with(this).load(User.getCurrentUser().getAvatarUrl()).placeholder(R.mipmap.ic_user).centerCrop().into(ivUserAvatar);
    }

    @OnClick({R.id.llPrimary, R.id.ivPrimaryBackground})
    public void onPrimaryFrameClick(View v) {
        //Log.d(TAG, "------------------------ clicked " + v);
        if(null != primaryChannel) {
            startActivity(ChatActivity.getIntentWithChannel(ChannelCacheManager.getInstance().getConversation(primaryChannel.getChannel().getName())));
        }
    }

    @OnClick({R.id.llSecondary, R.id.ivSecondaryBackground})
    public void onSecondaryFrameClick(View v) {
        //Log.d(TAG, "------------------------ clicked " + v);
        if(null != primaryChannel) {
            startActivity(ChatActivity.getIntentWithChannel(ChannelCacheManager.getInstance().getConversation(secondaryChannel.getChannel().getName())));
        }
    }

    @OnClick(R.id.llUserProfile)
    public void onEditUserProfileClick(View v) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void loadHighlightedChannel(final String tag) {
        MMXChannel.findByTags(new HashSet<String>(Arrays.asList(tag)), 1, 0, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                if(null != mmxChannelListResult.items && mmxChannelListResult.items.size() > 0) {
                    MMXChannel channel = mmxChannelListResult.items.get(0);
                    subscribeChannel(channel);
                    getChannelDetail(channel, tag);
                } else {
                    Log.w(TAG, "Couldn't find channel for tag " + tag);
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "Failed to load channel for tag " + tag);
            }
        });
    }

    private void getChannelDetail(final MMXChannel channel, final String tag) {
        MMXChannel.getChannelDetail(Arrays.asList(channel),
            new ChannelDetailOptions.Builder().numOfMessages(20).numOfSubcribers(10).build(), new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                @Override public void onSuccess(List<ChannelDetail> channelDetails) {
                    if(null != channelDetails && channelDetails.size() > 0) {
                        if(PRIMARY_CHANNEL_TAG.equals(tag)) {
                            flPrimary.setVisibility(View.VISIBLE);
                            primaryChannel = channelDetails.get(0);
                            ChannelCacheManager.getInstance().addConversation(channel.getName(), new Conversation(primaryChannel));
                            tvPrimarySubscribers.setText(primaryChannel.getTotalSubscribers() + " Subscribers");
                        } else if(SECONDARY_CHANNEL_TAG.equals(tag)) {
                            flSecondary.setVisibility(View.VISIBLE);
                            secondaryChannel = channelDetails.get(0);
                            ChannelCacheManager.getInstance().addConversation(channel.getName(), new Conversation(secondaryChannel));
                        }
                    } else {
                        Log.w(TAG, "Couldn't find channel detail for channel " + channel);
                    }
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Log.e(TAG, "Failed to load channel detail for channel " + channel);
                }
            });
    }

    private void subscribeChannel(final MMXChannel channel) {
        channel.subscribe(new MMXChannel.OnFinishedListener<String>() {
            @Override public void onSuccess(String s) {
                Log.d(TAG, "Subscribed to channel " + channel.getName());
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "Failed to subscribe channel " + channel.getName());
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * method which provide the setting of the current fragment co container view
     *
     * @param fragment current fragment type
     */
    private void setFragment(AppFragment fragment) {
        AppLogger.info(this, "setFragment: " + fragment + " : " + currentFragment);

        if (fragment.equals(currentFragment)) {
            return;
        }

        currentFragment = fragment;

        switch (fragment) {
            case HOME:
                viewEvents.setVisibility(View.VISIBLE);
                break;
            //case EVENTS:
            //    viewEvents.setVisibility(View.GONE);
            //    break;
        }
        replace(FragmentFactory.getFragment(fragment, this), R.id.container);
    }

    @Override
    public void onReceiveFragmentEvent(Event event) {

    }

    /**
     * Listener which provide the menu item functional
     */
    private final AdapterView.OnItemClickListener menuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawer.closeDrawer(GravityCompat.START);
            switch (position) {
                case 0:
                    setFragment(AppFragment.HOME);
                    break;
                //case 1:
                //    setFragment(AppFragment.EVENTS);
                //    break;
                case 1:
                    UserHelper.logout(logoutListener);
                    break;
                default:
                    setFragment(AppFragment.HOME);
                    break;
            }

        }
    };

    /**
     * Listener which provide the logout functional
     */
    private final UserHelper.OnLogoutListener logoutListener = new UserHelper.OnLogoutListener() {
        @Override
        public void onSuccess() {
            goToHomeActivity();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            goToHomeActivity();
        }

        private void goToHomeActivity() {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    };
}
