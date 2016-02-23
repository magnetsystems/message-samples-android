package com.magnet.magnetchat.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import android.widget.TextView;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.sections.chat.ChatActivity;
import com.magnet.magnetchat.ui.activities.sections.chat.ChooseUserActivity;
import com.magnet.magnetchat.ui.adapters.BaseConversationsAdapter;
import com.magnet.magnetchat.ui.adapters.HomeConversationsAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.ui.views.AskMagnetView;
import com.magnet.magnetchat.ui.views.EventView;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;

public class HomeFragment extends BaseChannelsFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private AlertDialog leaveDialog;

    @InjectView(R.id.llHomeCreateMsg)
    LinearLayout llCreateMessage;
    @InjectView(R.id.ivHomeCreateMsg)
    ImageView ivCreateMessage;
    @InjectView(R.id.tvHomeCreateMsg)
    AppCompatTextView tvCreateMessage;

    private EventView eventView;
    private AskMagnetView askMagnetView;

    private ChannelDetail primaryChannel;
    private static final String PRIMARY_CHANNEL_TAG = "active";
    private ChannelDetail secondaryChannel;

    @Override
    protected void onFragmentCreated(View containerView) {
        Log.d(TAG, "\n---------------------------------\nHomeFragment created\n---------------------------------\n");

        loadHighlightedChannel(PRIMARY_CHANNEL_TAG);

        eventView = new EventView(getContext());
        askMagnetView = new AskMagnetView(getContext());

        setOnClickListeners(ivCreateMessage, tvCreateMessage);

        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.llPrimary:
            case R.id.ivPrimaryBackground:
                if (null != primaryChannel) {
                    Conversation conversation = addConversation(primaryChannel);
                    Intent i = ChatActivity.getIntentWithChannel(conversation);
                    if (null != i) {
                        startActivity(i);
                    }
                }
                break;
            case R.id.llSecondary:
            case R.id.ivSecondaryBackground:
                if (!UserHelper.isMagnetSupportMember()) {
                    askMagnetView.setUnreadMessage(false);
                    loadMagnetSupportChannel();
                }
                break;
            case R.id.ivHomeCreateMsg:
            case R.id.tvHomeCreateMsg:
                startActivity(ChooseUserActivity.getIntentToCreateChannel());
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHomeCreateConversation:
                startActivity(ChooseUserActivity.getIntentToCreateChannel());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected List<Conversation> getAllConversations() {
        return ChannelCacheManager.getInstance().getConversations();
    }

    @Override
    protected void showAllConversations() {
        super.showAllConversations();

        if (!UserHelper.isMagnetSupportMember()) {
            Conversation conversation = ChannelCacheManager.getInstance().getConversationByName(ChannelHelper.ASK_MAGNET);
            if (conversation != null && conversation.hasUnreadMessage()) {
                askMagnetView.setUnreadMessage(true);
            }
        }
    }

    @Override
    protected BaseConversationsAdapter createAdapter(List<Conversation> conversations) {
        HomeConversationsAdapter adapter = new HomeConversationsAdapter(getActivity(), conversations, eventView, askMagnetView, new HomeConversationsAdapter.onClickHeaderListener() {
            @Override
            public void onClickEvent() {
                Conversation conversation = addConversation(primaryChannel);
                Intent i = ChatActivity.getIntentWithChannel(conversation);
                if (null != i) {
                    startActivity(i);
                }
            }

            @Override
            public void onClickAskMagnet() {
                askMagnetView.setUnreadMessage(false);
                loadMagnetSupportChannel();
            }
        });
        adapter.setOnConversationLongClick(new BaseConversationsAdapter.OnConversationLongClick() {
            @Override
            public void onLongClick(Conversation conversation) {
                showLeaveDialog(conversation);
            }
        });
        if (primaryChannel != null) {
            adapter.setEventConversationEnabled(true);
        }
        return adapter;
    }

    @Override
    protected void onConversationListIsEmpty(boolean isEmpty) {
        if (isEmpty) {
            llCreateMessage.setVisibility(View.VISIBLE);
        } else {
            llCreateMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSelectConversation(Conversation conversation) {
        startActivity(ChatActivity.getIntentWithChannel(conversation));
    }

    @Override
    protected void onReceiveMessage(MMXMessage mmxMessage) {
        if (mmxMessage != null && mmxMessage.getChannel() != null) {
            MMXChannel channel = mmxMessage.getChannel();
            if (!UserHelper.isMagnetSupportMember() && channel.getName().equalsIgnoreCase(ChannelHelper.ASK_MAGNET)) {
                askMagnetView.setUnreadMessage(true);
            }
        }
        if (llCreateMessage.getVisibility() == View.VISIBLE) {
            onConversationListIsEmpty(false);
        }
    }

    @Override
    public void onPause() {
        if (leaveDialog != null && leaveDialog.isShowing()) {
            leaveDialog.dismiss();
        }
        super.onPause();
    }

    private void loadHighlightedChannel(final String tag) {
        MMXChannel.findByTags(new HashSet<>(Arrays.asList(tag)), 1, 0, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                if (null != mmxChannelListResult.items && mmxChannelListResult.items.size() > 0) {
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
                new ChannelDetailOptions.Builder().numOfMessages(10).numOfSubcribers(10).build(), new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                    @Override
                    public void onSuccess(List<ChannelDetail> channelDetails) {
                        if (null != channelDetails && channelDetails.size() > 0) {
                            if (PRIMARY_CHANNEL_TAG.equals(tag)) {
                                primaryChannel = channelDetails.get(0);
                                HomeConversationsAdapter adapter = (HomeConversationsAdapter) getConversationsAdapter();
                                if (adapter != null) {
                                    adapter.setEventConversationEnabled(true);
                                }
                                ChannelCacheManager.getInstance().addConversation(channel.getName(), new Conversation(primaryChannel));
                                eventView.setSubscribersAmount(primaryChannel.getTotalSubscribers());
                            } else {
                                secondaryChannel = channelDetails.get(0);
                                ChannelCacheManager.getInstance().addConversation(channel.getName(), new Conversation(secondaryChannel));
                                goToAskMagnet();
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

    private void loadMagnetSupportChannel() {
        if (null != secondaryChannel) {
            goToAskMagnet();
            return;
        }

        MMXChannel.findPrivateChannelsByName(ChannelHelper.ASK_MAGNET, 1, 0, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                if (null != mmxChannelListResult.items && mmxChannelListResult.items.size() > 0) {
                    getChannelDetail(mmxChannelListResult.items.get(0), null);
                } else {
                    Log.w(TAG, "Couldn't find channel askMagnet, creating one");

                    User.search("tags:" + UserHelper.MAGNET_SUPPORT_TAG, 100, 0, "firstName:asc", new ApiCallback<List<User>>() {
                        @Override
                        public void success(List<User> users) {
                            Set<String> userIds = new HashSet<>();
                            userIds.add(User.getCurrentUserId());
                            if (users != null && !users.isEmpty()) {
                                for (User u : users) {
                                    userIds.add(u.getUserIdentifier());
                                }
                                MMXChannel.create(ChannelHelper.ASK_MAGNET, "Magnet Support for " + User.getCurrentUser().getDisplayName(), false,
                                        MMXChannel.PublishPermission.SUBSCRIBER, userIds, new MMXChannel.OnFinishedListener<MMXChannel>() {
                                            @Override
                                            public void onSuccess(MMXChannel channel) {
                                                getChannelDetail(channel, null);
                                            }

                                            @Override
                                            public void onFailure(MMXChannel.FailureCode failureCode,
                                                                  Throwable throwable) {
                                                Log.e(TAG, "Failed to create askMagnet channel due to" + failureCode, throwable);
                                                Utils.showMessage(getActivity(), "Can't load the channel, please try later.");
                                            }
                                        });
                            } else {
                                Log.e(TAG, "Couldn't find any magnetsupport users");
                                Utils.showMessage(getActivity(), "Can't load the channel, please try later.");
                            }
                        }

                        @Override
                        public void failure(ApiError apiError) {
                            Log.e(TAG, "Failed to search magnetsupport users" + apiError);
                        }
                    });
                }
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {

            }
        });
    }

    private void showLeaveDialog(final Conversation conversation) {
        if (leaveDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    leaveDialog.dismiss();
                }
            });
            leaveDialog = builder.create();
            leaveDialog.setMessage("Are you sure that you want to leave conversation");
        }
        leaveDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //setProgressBarVisibility(View.VISIBLE);
                ChannelHelper.unsubscribeFromChannel(conversation, new ChannelHelper.OnLeaveChannelListener() {
                    @Override
                    public void onSuccess() {
                        //setProgressBarVisibility(View.GONE);
                        ChannelCacheManager.getInstance().removeConversation(conversation.getChannel().getName());
                        showAllConversations();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        //setProgressBarVisibility(View.GONE);
                    }
                });
                leaveDialog.dismiss();
            }
        });
        leaveDialog.show();
    }

    private void subscribeChannel(final MMXChannel channel) {
        channel.subscribe(new MMXChannel.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "Subscribed to channel " + channel.getName());
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "Failed to subscribe channel " + channel.getName());
            }
        });
    }

    private void goToAskMagnet() {
        Conversation conversation = addConversation(secondaryChannel);
        Intent i = ChatActivity.getIntentWithChannel(conversation);
        if (null != i) {
            startActivity(i);
        }
    }

    private Conversation addConversation(ChannelDetail channelDetail) {
        Conversation conversation = ChannelCacheManager.getInstance().getConversationByName(channelDetail.getChannel().getName());
        if (null == conversation) {
            conversation = new Conversation(channelDetail);
            ChannelCacheManager.getInstance()
                    .addConversation(channelDetail.getChannel().getName(), conversation);
        }

        return conversation;
    }
}
