package com.magnet.magntetchatapp.ui.fragments.section;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.magnet.chatsdkcover.mvp.api.ChannelsListContract;
import com.magnet.chatsdkcover.mvp.views.AbstractChannelsView;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.ui.activities.ChatActivity;
import com.magnet.magnetchat.ui.activities.ChooseUserActivity;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.core.CurrentApplication;
import com.magnet.magntetchatapp.ui.fragments.abs.BaseFragment;
import com.magnet.mmx.client.api.ChannelDetail;

import butterknife.InjectView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class HomeFragment extends BaseFragment {

    @InjectView(R.id.viewChannels)
    AbstractChannelsView viewChannels;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_fragment_home;
    }

    @Override
    protected void onCreateFragment(View containerView) {
        viewChannels.setChannelListCallback(channelListCallback);
        viewChannels.setChannelsViewCallback(channelsViewCallback);
        viewChannels.onCreateActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewChannels.onResumeActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewChannels.onPauseActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewChannels.onDestroyActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView search = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            search.setOnQueryTextListener(queryTextListener);
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    search.onActionViewCollapsed();
                    onApplyFilter(null);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_create_channel) {
            Intent intent = ChooseUserActivity.getIntentToCreateChannel(getContext());
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method which provide the filter applying
     *
     * @param query filter query
     */
    private void onApplyFilter(@Nullable String query) {
        if (viewChannels != null) {
            viewChannels.filterChannels(query);
        }
    }

    /**
     * Method which provide the creating of the leave conversation dialog
     *
     * @param channelObject   channel object
     * @param onClickListener dialog onClickListener
     */
    private void createLeaveDialog(@NonNull final ChannelsListContract.ChannelObject channelObject,
                                   DialogInterface.OnClickListener onClickListener) {

        if (getContext() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.text_leave_the_conversation);
        builder.setMessage(R.string.text_leave_the_conversation_text);
        builder.setPositiveButton(R.string.text_leave, onClickListener);
        builder.setNegativeButton(R.string.text_stay, null);
        final AlertDialog alertDialog = builder.create();
        if (alertDialog != null) {
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    alertDialog.show();
                }
            });
        }
    }

    /**
     * Method which provide the listening actions inside the channel list view
     *
     * @see com.magnet.chatsdkcover.mvp.views.AbstractChannelsView
     */
    private final ChannelsListContract.OnChannelsListCallback channelListCallback = new ChannelsListContract.OnChannelsListCallback() {
        @Override
        public void onItemClick(int index, @NonNull ChannelsListContract.ChannelObject object) {
            ChannelDetail channelDetail = object.getChannelDetail();
            final Chat chat = new Chat(channelDetail);
            if (chat != null) {
                ChatManager.getInstance().addConversation(chat);
                runOnMainThread(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        Intent intent = ChatActivity.getIntentWithChannel(CurrentApplication.getInstance().getApplicationContext(), chat);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public void onActionPerformed(RecycleEvent recycleEvent, int index, @NonNull final ChannelsListContract.ChannelObject object) {
            if (recycleEvent.equals(ChannelsListContract.ChannelEvent.LONG_CLICK.getRecycleEvent())) {
                createLeaveDialog(object, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (viewChannels != null) {
                            viewChannels.deleteChannel(object);
                        }
                    }
                });
            }
        }
    };

    /**
     * Callback which provide ot listening of the events inside the AbstractChannelsView
     *
     * @see AbstractChannelsView
     */
    private final ChannelsListContract.OnChannelsViewCallback channelsViewCallback = new ChannelsListContract.OnChannelsViewCallback() {
        @Override
        public void onMessageReceived(@NonNull String message) {
            showMessage(message);
        }
    };

    /**
     * Listener which provide the query text listening
     */
    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            onApplyFilter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            onApplyFilter(newText);
            return true;
        }
    };
}
