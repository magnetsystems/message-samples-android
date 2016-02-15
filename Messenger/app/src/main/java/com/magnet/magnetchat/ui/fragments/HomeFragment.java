package com.magnet.magnetchat.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.SearchView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.sections.chat.ChatActivity;
import com.magnet.magnetchat.ui.activities.sections.chat.ChooseUserActivity;
import com.magnet.magnetchat.ui.adapters.BaseConversationsAdapter;
import com.magnet.magnetchat.ui.adapters.ConversationsAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.ChannelDetailOptions;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends BaseChannelsFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private AlertDialog leaveDialog;

    private FrameLayout flPrimary;
    private FTextView tvPrimarySubscribers;
    private FrameLayout flSecondary;

    private ChannelDetail primaryChannel;
    private static final String PRIMARY_CHANNEL_TAG = "active";
    private ChannelDetail secondaryChannel;
    private static final String SECONDARY_CHANNEL_NAME = "askMagnet";

    @Override
    protected void onFragmentCreated(View containerView) {
        loadHighlightedChannel(PRIMARY_CHANNEL_TAG);

        View header = getLayoutInflater(getArguments()).inflate(R.layout.list_header_home, null);
        flPrimary = (FrameLayout) header.findViewById(R.id.flPrimary);
        tvPrimarySubscribers = (FTextView) header.findViewById(R.id.tvPrimarySubscribers);
        flSecondary = (FrameLayout) header.findViewById(R.id.flSecondary);
        flPrimary.setVisibility(View.GONE);
        if (UserHelper.isMagnetSupportMember()) {
            flSecondary.setVisibility(View.GONE);
        }

        final ListView conversationsList = getConversationsListView();
        conversationsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLeaveDialog(getConversationsAdapter().getItem(position - conversationsList.getHeaderViewsCount()));
                return true;
            }
        });
        conversationsList.addHeaderView(header);

        LinearLayout llPrimary = (LinearLayout) header.findViewById(R.id.llPrimary);
        ImageView ivPrimaryBackground = (ImageView) header.findViewById(R.id.ivPrimaryBackground);
        LinearLayout llSecondary = (LinearLayout) header.findViewById(R.id.llSecondary);
        ImageView ivSecondaryBackground = (ImageView) header.findViewById(R.id.ivSecondaryBackground);
        setOnClickListeners(llPrimary, ivPrimaryBackground, llSecondary, ivSecondaryBackground);

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
                    loadMagnetSupportChannel();
                }
                break;
        }
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

    @Override
    protected void showAllConversations() {
        showList(ChannelCacheManager.getInstance().getConversations());
    }

    @Override
    protected BaseConversationsAdapter createAdapter(List<Conversation> conversations) {
        return new ConversationsAdapter(getActivity(), conversations);
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
                            Set<String> userIds = new HashSet<String>();
                            for (User u : users) {
                                userIds.add(u.getUserIdentifier());
                            }
                            userIds.add(User.getCurrentUserId());
                            if (null != users && !users.isEmpty()) {
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
                setProgressBarVisibility(View.VISIBLE);
                ChannelHelper.getInstance().unsubscribeFromChannel(conversation, new ChannelHelper.OnLeaveChannelListener() {
                    @Override
                    public void onSuccess() {
                        setProgressBarVisibility(View.GONE);
                        ChannelCacheManager.getInstance().removeConversation(conversation.getChannel().getName());
                        showAllConversations();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        setProgressBarVisibility(View.GONE);
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
        if (searchResult.isEmpty()) {
            Utils.showMessage(getActivity(), "Nothing found");
        }
        showList(searchResult);
    }

}
