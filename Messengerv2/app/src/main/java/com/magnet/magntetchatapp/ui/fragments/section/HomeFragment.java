package com.magnet.magntetchatapp.ui.fragments.section;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.ui.activities.ChatActivity;
import com.magnet.magnetchat.ui.activities.ChooseUserActivity;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.mvp.views.AbstractChannelsView;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_create_channel) {
            startActivity(ChooseUserActivity.getIntentToCreateChannel(getActivity()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method which provide the listening actions inside the channel list view
     *
     * @see com.magnet.magntetchatapp.mvp.views.AbstractChannelsView
     */
    private final ChannelsListContract.OnChannelListCallback channelListCallback = new ChannelsListContract.OnChannelListCallback() {
        @Override
        public void onItemClick(int index, @NonNull ChannelsListContract.ChannelObject object) {
            ChannelDetail channelDetail = object.getChannelDetail();
            final Chat chat = new Chat(channelDetail);
            if (chat != null) {
                Intent i = new Intent(getContext(), ChatActivity.class);
                startActivity(i);
            }
        }

        @Override
        public void onActionPerformed(RecycleEvent recycleEvent, int index, @NonNull ChannelsListContract.ChannelObject object) {

        }
    };
}
