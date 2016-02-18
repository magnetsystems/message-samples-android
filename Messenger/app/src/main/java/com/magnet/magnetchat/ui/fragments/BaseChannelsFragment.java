package com.magnet.magnetchat.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.adapters.BaseConversationsAdapter;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public abstract class BaseChannelsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static String TAG = BaseChannelsFragment.class.getSimpleName();

    @InjectView(R.id.homeConversationsList)
    ListView conversationsList;
    @InjectView(R.id.homeProgress)
    ProgressBar mProgressBar;
    @InjectView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private List<Conversation> conversations;
    private BaseConversationsAdapter adapter;

    private boolean isLoadingWhenCreating = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        conversationsList.setOnItemClickListener(this);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConversations(false);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);

        onFragmentCreated(containerView);

        isLoadingWhenCreating = true;
        getConversations(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            Conversation conversation = adapter.getItem(position - conversationsList.getHeaderViewsCount());
            if (conversation != null) {
                Log.d(TAG, "Channel " + conversation.getChannel().getName() + " is selected");
                onSelectConversation(conversation);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoadingWhenCreating && ChannelCacheManager.getInstance().isConversationListUpdated()) {
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
        super.onPause();
    }

    protected void getConversations(boolean showProgress) {
        if (showProgress) {
            setProgressBarVisibility(View.VISIBLE);
        }
        ChannelHelper.readConversations(readChannelInfoListener);
    }

    protected ListView getConversationsListView() {
        return conversationsList;
    }

    protected BaseConversationsAdapter getConversationsAdapter() {
        return adapter;
    }

    protected abstract void onFragmentCreated(View containerView);

    protected abstract void showAllConversations();

    protected abstract BaseConversationsAdapter createAdapter(List<Conversation> conversations);

    protected abstract void onConversationListIsEmpty(boolean isEmpty);

    protected abstract void onSelectConversation(Conversation conversation);

    protected abstract void onReceivedMessage(MMXMessage mmxMessage);

    protected void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    protected void showList(List<Conversation> conversationsToShow) {
        if(null != getActivity()) {
            if (adapter == null) {
                conversations = new ArrayList<>(conversationsToShow);
                adapter = createAdapter(conversations);
                conversationsList.setAdapter(adapter);
            } else {
                conversations.clear();
                conversations.addAll(conversationsToShow);
                adapter.notifyDataSetChanged();
            }
        } else {
            Log.w(TAG, "Fragment is detached, won't update list");
        }
    }

    protected void setProgressBarVisibility(int visibility) {
        mProgressBar.setVisibility(visibility);
    }

    private ChannelHelper.OnReadChannelInfoListener readChannelInfoListener = new ChannelHelper.OnReadChannelInfoListener() {
        @Override
        public void onSuccessFinish(Conversation lastConversation) {
            finishGetChannels();
            showAllConversations();
            if (conversations == null || conversations.size() == 0) {
                onConversationListIsEmpty(true);
                Log.w("read channels", "No conversation is available");
            } else {
                onConversationListIsEmpty(false);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            finishGetChannels();
        }

        private void finishGetChannels() {
            isLoadingWhenCreating = false;
            swipeContainer.setRefreshing(false);
            setProgressBarVisibility(View.GONE);
        }
    };

    private MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug(TAG, "onMessageReceived");
            showAllConversations();
            onReceivedMessage(mmxMessage);
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
