package com.magnet.magnetchat.ui.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.sections.chat.ChatActivity;
import com.magnet.magnetchat.ui.adapters.BaseConversationsAdapter;
import com.magnet.magnetchat.ui.adapters.SupportConversationsAdapter;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

public class SupportFragment extends BaseChannelsFragment {

    private static final String TAG = SupportFragment.class.getSimpleName();

    @Override
    protected void onFragmentCreated(View containerView) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_support, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override protected List<Conversation> getAllConversations() {
        return ChannelCacheManager.getInstance().getSupportConversations();
    }

    @Override
    protected BaseConversationsAdapter createAdapter(List<Conversation> conversations) {
        return new SupportConversationsAdapter(getActivity(), conversations);
    }

    @Override
    protected void onConversationListIsEmpty(boolean isEmpty) {

    }

    @Override
    protected void onSelectConversation(Conversation conversation) {
        startActivity(ChatActivity.getIntentWithChannelOwner(conversation));
    }

    protected void onReceiveMessage(MMXMessage mmxMessage) {

    }

}
