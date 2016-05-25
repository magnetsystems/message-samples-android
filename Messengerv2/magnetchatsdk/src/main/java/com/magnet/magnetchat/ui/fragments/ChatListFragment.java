package com.magnet.magnetchat.ui.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.EndlessLinearRecyclerViewScrollListener;
import com.magnet.magnetchat.callbacks.OnRecyclerViewItemClickListener;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.presenters.ChatListContract;
import com.magnet.magnetchat.presenters.impl.ChatListPresenterImpl;
import com.magnet.magnetchat.ui.activities.ChatActivity;
import com.magnet.magnetchat.ui.activities.ChooseUserActivity;
import com.magnet.magnetchat.ui.adapters.ChatsAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.ui.views.DividerItemDecoration;

import java.util.List;

@Deprecated
public class ChatListFragment extends MMXBaseFragment implements ChatListContract.View {
    private final static String TAG = "ChatListFragment";

    private RecyclerView conversationsList;
    private SwipeRefreshLayout swipeContainer;

    private AlertDialog leaveDialog;

    private FloatingActionButton fabCreateMessage;

    private ChatsAdapter mAdapter;

    private ChatListContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        conversationsList = (RecyclerView) containerView.findViewById(R.id.homeConversationsList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        conversationsList.setHasFixedSize(true);
        conversationsList.setLayoutManager(layoutManager);
        conversationsList.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        conversationsList.addOnScrollListener(new EndlessLinearRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "------------onLoadMore channel : " + page + "/" + totalItemsCount + "\n");
                mPresenter.onLoad(totalItemsCount, Constants.CONVERSATION_PAGE_SIZE);
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer = (SwipeRefreshLayout) containerView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onLoad(0, Constants.CONVERSATION_PAGE_SIZE);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.primary_dark, R.color.primary, R.color.accent);

        fabCreateMessage = (FloatingActionButton) containerView.findViewById(R.id.fabHomeCreateMessage);
        fabCreateMessage.setVisibility(View.VISIBLE);
        fabCreateMessage.setOnClickListener(this);

        setHasOptionsMenu(true);

        mPresenter = new ChatListPresenterImpl(this);

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                mPresenter.onLoad(0, Constants.CONVERSATION_PAGE_SIZE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
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
                    mPresenter.onSearch(query, null);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()) {
                        hideKeyboard();
                        mPresenter.onSearchReset();
                    }
                    return false;
                }
            });

            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    search.onActionViewCollapsed();
                    mPresenter.onSearchReset();
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.fabHomeCreateMessage) {
            createNewChat();
        }
    }

    @Override
    public void showList(List<Chat> list, boolean toAppend) {
        if (null != getActivity()) {
            if (mAdapter == null) {
                mAdapter = new ChatsAdapter(getActivity(), list, mPresenter.getItemComparator());
                mAdapter.setOnClickListener(new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Chat conversation = mAdapter.getItem(position);
                        if (conversation != null) {
                            Log.d(TAG, "Channel " + conversation.getChannel().getName() + " is selected");
                            mPresenter.onItemSelect(position, conversation);
                        }
                    }

                    @Override
                    public void onLongClick(int position) {
                        Chat conversation = mAdapter.getItem(position);
                        if (conversation != null) {
                            mPresenter.onItemLongClick(position, conversation);
                            showLeaveDialog(conversation);
                        }
                    }
                });
                conversationsList.setAdapter(mAdapter);
            } else {
                if (toAppend) {
                    mAdapter.addItem(list);
                } else {
                    mAdapter.swapData(list);
                }
            }
        } else {
            Log.w(TAG, "Fragment is detached, won't update list");
        }
    }

    private void showLeaveDialog(final Chat conversation) {
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
                        ChatManager.getInstance().removeConversation(conversation.getChannel().getName());

                        removeItem(mAdapter.getData().indexOf(conversation));
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

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setProgressIndicator(boolean active) {
        swipeContainer.setRefreshing(active);
    }

    @Override
    public void createNewChat() {
        startActivity(ChooseUserActivity.getIntentToCreateChannel(getActivity()));
    }

    @Override
    public void showConversationUpdate(Chat conversation, boolean isNew) {
        if (null != mAdapter) {
            if (isNew) {
                mAdapter.addItem(conversation);
            } else {
                mAdapter.addItem(conversation);
            }
        }
    }

    @Override
    public void showChatDetails(Chat conversation) {
        startActivity(ChatActivity.getIntentWithChannel(getActivity(), conversation));
    }

    @Override
    public void showLeaveConfirmation(Chat conversation) {
        showLeaveDialog(conversation);
    }

    @Override
    public void dismissLeaveDialog() {
        if (leaveDialog != null && leaveDialog.isShowing()) {
            leaveDialog.dismiss();
        }
    }

    private void removeItem(int position) {
        conversationsList.removeViewAt(position);
        mAdapter.removeItem(position);
    }
}
