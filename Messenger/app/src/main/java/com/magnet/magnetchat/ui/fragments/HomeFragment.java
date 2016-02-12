package com.magnet.magnetchat.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.activities.sections.chat.ChatActivity;
import com.magnet.magnetchat.ui.activities.sections.chat.ChooseUserActivity;
import com.magnet.magnetchat.ui.adapters.ConversationsAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ASK_MAGNET = "askMagnet";

    private AlertDialog leaveDialog;

    FrameLayout flPrimary;
    FTextView tvPrimarySubscribers;

    FrameLayout flSecondary;

    private ListView conversationsList;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeContainer;

    private List<Conversation> conversations;
    private ConversationsAdapter adapter;

    private ChannelDetail primaryChannel;
    private static final String PRIMARY_CHANNEL_TAG = "active";
    private ChannelDetail secondaryChannel;
    private static final String SECONDARY_CHANNEL_NAME = "askMagnet";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        if (!UserHelper.isMagnetSupportMember()) {
            loadMagnetSupportChannel();
        }

        loadHighlightedChannel(PRIMARY_CHANNEL_TAG);

        mProgressBar = (ProgressBar) containerView.findViewById(R.id.homeProgress);
        conversationsList = (ListView) containerView.findViewById(R.id.homeConversationsList);
        conversationsList.setOnItemClickListener(this);
        conversationsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLeaveDialog(adapter.getItem(position - 1));
                return true;
            }
        });
        getConversations(true);

        View header = getLayoutInflater(getArguments()).inflate(R.layout.list_header_home, null);
        conversationsList.addHeaderView(header);
        flPrimary = (FrameLayout) header.findViewById(R.id.flPrimary);
        tvPrimarySubscribers = (FTextView) header.findViewById(R.id.tvPrimarySubscribers);
        flSecondary = (FrameLayout) header.findViewById(R.id.flSecondary);
        flPrimary.setVisibility(View.GONE);
        flSecondary.setVisibility(View.GONE);

        LinearLayout llPrimary = (LinearLayout) header.findViewById(R.id.llPrimary);
        ImageView ivPrimaryBackground = (ImageView) header.findViewById(R.id.ivPrimaryBackground);
        LinearLayout llSecondary = (LinearLayout) header.findViewById(R.id.llSecondary);
        ImageView ivSecondaryBackground = (ImageView) header.findViewById(R.id.ivSecondaryBackground);
        setOnClickListeners(llPrimary, ivPrimaryBackground, llSecondary, ivSecondaryBackground);

        swipeContainer = (SwipeRefreshLayout) containerView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConversations(false);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.llPrimary:
            case R.id.ivPrimaryBackground:
                if (null != primaryChannel) {
                    startActivity(ChatActivity.getIntentWithChannel(ChannelCacheManager.getInstance().getConversation(primaryChannel.getChannel().getName())));
                }
                break;
            case R.id.llSecondary:
            case R.id.ivSecondaryBackground:
                if (null != primaryChannel) {
                    startActivity(ChatActivity.getIntentWithChannel(ChannelCacheManager.getInstance().getConversation(secondaryChannel.getChannel().getName())));
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            Conversation conversation = adapter.getItem(position - 1);
            if (conversation != null) {
                Log.d(TAG, "Channel " + conversation.getChannel().getName() + " is selected");
                startActivity(ChatActivity.getIntentWithChannel(conversation));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ChannelCacheManager.getInstance().isConversationListUpdated()) {
            showAllConversations();
            ChannelCacheManager.getInstance().resetConversationListUpdated();
        }
        MMX.registerListener(eventListener);
        getActivity().registerReceiver(onAddedConversation, new IntentFilter(ChannelHelper.ACTION_ADDED_CONVERSATION));
    }

    @Override
    public void onPause() {
        MMX.unregisterListener(eventListener);
        getActivity().unregisterReceiver(onAddedConversation);
        if (leaveDialog != null && leaveDialog.isShowing()) {
            leaveDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            final CustomSearchView search = (CustomSearchView) menu.findItem(R.id.menu_search).getActionView();
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchMessage(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()) {
                        hideKeyboard();
                        showAllConversations();
                    }
                    return false;
                }
            });

            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    search.onActionViewCollapsed();
                    showAllConversations();
                    return true;
                }
            });
        }
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

    private void loadHighlightedChannel(final String tag) {
        MMXChannel.findByTags(new HashSet<String>(Arrays.asList(tag)), 1, 0, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
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
                new ChannelDetailOptions.Builder().numOfMessages(20).numOfSubcribers(10).build(), new MMXChannel.OnFinishedListener<List<ChannelDetail>>() {
                    @Override
                    public void onSuccess(List<ChannelDetail> channelDetails) {
                        if (null != channelDetails && channelDetails.size() > 0) {
                            if (PRIMARY_CHANNEL_TAG.equals(tag)) {
                                flPrimary.setVisibility(View.VISIBLE);
                                primaryChannel = channelDetails.get(0);
                                ChannelCacheManager.getInstance().addConversation(channel.getName(), new Conversation(primaryChannel));
                                tvPrimarySubscribers.setText(primaryChannel.getTotalSubscribers() + " Subscribers");
                            } else {
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

    private void loadMagnetSupportChannel() {
        MMXChannel.findPrivateChannelsByName(ASK_MAGNET, 1, 0, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                if (null != mmxChannelListResult.items && mmxChannelListResult.items.size() > 0) {
                    getChannelDetail(mmxChannelListResult.items.get(0), null);
                } else {
                    Log.w(TAG, "Couldn't find channel askMagnet, creating one");

                    User.search("tags:" + UserHelper.MAGNET_SUPPORT_TAG, 100, 0, "firstName:asc", new ApiCallback<List<User>>() {
                        @Override
                        public void success(List<User> users) {
                            Set<String> userIds = new HashSet<String>();
                            for (User u : users) {
                                userIds.add(u.getUserIdentifier());
                            }
                            userIds.add(User.getCurrentUserId());
                            if (null != users && !users.isEmpty()) {
                                MMXChannel.create(ASK_MAGNET, "Magnet Support for " + User.getCurrentUser().getDisplayName(), false,
                                        MMXChannel.PublishPermission.SUBSCRIBER, userIds, new MMXChannel.OnFinishedListener<MMXChannel>() {
                                            @Override
                                            public void onSuccess(MMXChannel channel) {
                                                getChannelDetail(channel, null);
                                            }

                                            @Override
                                            public void onFailure(MMXChannel.FailureCode failureCode,
                                                                  Throwable throwable) {
                                                Log.e(TAG, "Failed to create askMagnet channel due to" + failureCode, throwable);
                                            }
                                        });
                            } else {
                                Log.e(TAG, "Couldn't find any magnetsupport users");
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

    private void getConversations(boolean showProgress) {
        if (showProgress) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        ChannelHelper.getInstance().readConversations(readChannelInfoListener);
    }

    private void showAllConversations() {
        showList(ChannelCacheManager.getInstance().getConversations());
    }

    private void showList(List<Conversation> conversationsToShow) {
        if (null == adapter) {
            conversations = new ArrayList<>(conversationsToShow);
            adapter = new ConversationsAdapter(getActivity(), conversations);
            conversationsList.setAdapter(adapter);
        } else {
            conversations.clear();
            conversations.addAll(conversationsToShow);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
                mProgressBar.setVisibility(View.VISIBLE);
                ChannelHelper.getInstance().unsubscribeFromChannel(conversation, new ChannelHelper.OnLeaveChannelListener() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                        ChannelCacheManager.getInstance().removeConversation(conversation.getChannel().getName());
                        showAllConversations();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        mProgressBar.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "Can't leave the conversation", Toast.LENGTH_LONG).show();
                    }
                });
                leaveDialog.dismiss();
            }
        });
        leaveDialog.show();
    }

    private void searchMessage(final String query) {
        final List<Conversation> searchResult = new ArrayList<>();
        for (Conversation conversation : ChannelCacheManager.getInstance().getConversations()) {
            for (UserProfile userProfile : conversation.getSuppliersList()) {
                if (userProfile.getDisplayName() != null && userProfile.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
                    searchResult.add(conversation);
                    break;
                }
            }
        }
        showList(searchResult);
    }

    private ChannelHelper.OnReadChannelInfoListener readChannelInfoListener = new ChannelHelper.OnReadChannelInfoListener() {
        @Override
        public void onSuccessFinish(Conversation lastConversation) {
            finishGetChannels();

            if (null != lastConversation) {
                showAllConversations();
            } else {
                Log.w(TAG, "No conversation is available");
//                Toast.makeText(getActivity(), "No conversation is available", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            finishGetChannels();
        }

        private void finishGetChannels() {
            swipeContainer.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug(TAG, "onMessageReceived");
            showAllConversations();
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            Logger.debug(TAG, "onMessageAcknowledgementReceived");
            updateList();
            return false;
        }

        @Override
        public boolean onInviteReceived(MMXChannel.MMXInvite invite) {
            Logger.debug(TAG, "onInviteReceived");
            updateList();
            return false;
        }

        @Override
        public boolean onInviteResponseReceived(MMXChannel.MMXInviteResponse inviteResponse) {
            Logger.debug(TAG, "onInviteResponseReceived");
            updateList();
            return false;
        }

        @Override
        public boolean onMessageSendError(String messageId, MMXMessage.FailureCode code, String text) {
            Logger.debug("onMessageSendError");
            updateList();
            return false;
        }
    };

    private BroadcastReceiver onAddedConversation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAllConversations();
        }
    };

}
