package com.magnet.magnetchat.ui;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.adapters.ConversationsAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private AlertDialog leaveDialog;

    private ListView conversationsList;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeContainer;

    private List<Conversation> conversations;
    private String username;
    private ConversationsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        mProgressBar = (ProgressBar) containerView.findViewById(R.id.homeProgress);
        conversationsList = (ListView) containerView.findViewById(R.id.homeConversationsList);
        conversationsList.setOnItemClickListener(this);
        conversationsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLeaveDialog(adapter.getItem(position));
                return true;
            }
        });
        getConversations(true);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            Conversation conversation = adapter.getItem(position);
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
//                        hideKeyboard();
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
                        showAllConversations();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Can't leave the conversation", Toast.LENGTH_LONG).show();
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
            for (Message message : conversation.getMessages()) {
                if (message.getText() != null && message.getText().toLowerCase().contains(query.toLowerCase())) {
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
                Toast.makeText(getActivity(), "No conversation is available", Toast.LENGTH_LONG).show();
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
