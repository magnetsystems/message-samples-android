/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.quickstart.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.samples.android.quickstart.R;
import com.magnet.samples.android.quickstart.fragments.ChannelMessagesFragment;
import com.magnet.samples.android.quickstart.fragments.ChannelSubscribersFragment;
import com.magnet.samples.android.quickstart.util.Logger;

public class ChannelActivity extends BaseActivity implements ChannelSubscribersFragment.OnChannelSubscribersInteractionListener {

    public static final String TAG_SELECTED_CHANNEL = "selected_channel";
    public static final String TAG_IS_PUBLIC = "is_public";

    private MMXChannel currentChannel;
    private AlertDialog dialog;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        String channelName = getIntent().getStringExtra(TAG_SELECTED_CHANNEL);
        boolean isPublic = getIntent().getBooleanExtra(TAG_IS_PUBLIC, false);
        if (channelName != null) {
            if (isPublic) {
                MMXChannel.getPublicChannel(channelName, getChannelListener);
            } else {
                MMXChannel.getPrivateChannel(channelName, getChannelListener);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        MMX.unregisterListener(eventListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MMX.registerListener(eventListener);
    }

    @Override
    public void readSubscribers(MMXChannel.OnFinishedListener<ListResult<User>> resultOnFinishedListener) {
        currentChannel.getAllSubscribers(100, 0, resultOnFinishedListener);
    }

    @Override
    public void inviteUser() {
        currentChannel.inviteUser(User.getCurrentUser(), "Join to our channel", new MMXChannel.OnFinishedListener<MMXChannel.MMXInvite>() {
            @Override
            public void onSuccess(MMXChannel.MMXInvite mmxInvite) {
                Logger.debug("invite user", "success");
                showMessage("Invite was sent. Wait while user accepts it");
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                showMessage("Can't invite user : " + failureCode + " : " + throwable.getMessage());
                Logger.error("invite user", throwable, "error : ", failureCode);
            }
        });
    }

    private void initTabLayout() {
        viewPager = (ViewPager) findViewById(R.id.vpChannelDetail);
        viewPager.setAdapter(new ChannelFragmentPagerAdapter(getSupportFragmentManager(), this));

        tabLayout = (TabLayout) findViewById(R.id.tlChannelDetail);
        tabLayout.setupWithViewPager(viewPager);
    }

    private final MMXChannel.OnFinishedListener<MMXChannel> getChannelListener = new MMXChannel.OnFinishedListener<MMXChannel>() {
        @Override
        public void onSuccess(MMXChannel mmxChannel) {
            Logger.debug("get channel", "success");
            currentChannel = mmxChannel;
            setTitle(currentChannel.getName());

            initTabLayout();
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            showMessage("Can't get channel : " + failureCode + " : " + throwable.getMessage());
            Logger.error("get channel", throwable, "error : ", failureCode);
            finish();
        }
    };

    private final MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(final MMXMessage mmxMessage) {
            Logger.debug("received message", "from " + mmxMessage.getSender().getUserName());
            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
            String title = "Message received";
            if (mmxMessage.getAttachments().size() > 0) {
                title += "\n(has attachment)";
            }
            builder.setTitle(title).setCancelable(false);
            builder.setMessage(mmxMessage.getContent().get("content"));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog = builder.show();
            return true;
        }

        @Override
        public boolean onInviteReceived(final MMXChannel.MMXInvite invite) {
            MMXChannel.MMXInviteInfo inviteInfo = invite.getInviteInfo();
            Logger.debug("received invite", "from " + inviteInfo.getInviter().getUserName());
            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
            String title = "Invite received";
            builder.setTitle(title).setCancelable(false);
            builder.setMessage(String.format("You have received an invite from %s to channel %s", inviteInfo.getInviter().getUserName(), inviteInfo.getChannel().getName()));
            builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    invite.decline("No, thanks", inviteAnswerListener);
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    invite.accept("Yes, thanks", inviteAnswerListener);
                    dialog.dismiss();
                }
            });
            dialog = builder.show();
            return true;
        }

        @Override
        public boolean onInviteResponseReceived(MMXChannel.MMXInviteResponse inviteResponse) {
            Logger.debug("invite response received");
            if (inviteResponse.isAccepted()) {
                showMessage("Your invite was accepted");
            } else {
                showMessage("Your invite was declined");
            }
            return super.onInviteResponseReceived(inviteResponse);
        }
    };

    private final MMXChannel.OnFinishedListener<MMXChannel.MMXInvite> inviteAnswerListener = new MMXChannel.OnFinishedListener<MMXChannel.MMXInvite>() {
        @Override
        public void onSuccess(MMXChannel.MMXInvite mmxInvite) {
            Logger.debug("answer to invite", "success");
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            showMessage("Can't answer to invite : " + failureCode + " : " + throwable.getMessage());
            Logger.error("answer to invite", throwable, "error : ", failureCode);
        }
    };

    public class ChannelFragmentPagerAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles = new String[] { "Messages", "Subscribers" };
        private final Fragment[] fragments = new Fragment[2];
        private final Context context;

        public ChannelFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = fragments[position];
            if(null == f) {
                if(position == 0) {
                    f = ChannelMessagesFragment.newInstance(currentChannel);
                } else {
                    f =  new ChannelSubscribersFragment();
                }

                fragments[position] = f;
            }

            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
