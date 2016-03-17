package com.magnet.magnetchat.ui.fragments;

import android.view.View;

import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.sections.chat.ChatActivity;
import com.magnet.magnetchat.ui.adapters.BaseConversationsAdapter;
import com.magnet.magnetchat.ui.adapters.SupportConversationsAdapter;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

public class SupportFragment extends BaseChannelsFragment {

    private static final String TAG = SupportFragment.class.getSimpleName();

    @Override
    protected void getConversations(int offset, int limit) {
        ChannelHelper.readAskMagnetConversations(offset, limit, readChannelInfoListener);
    }

    @Override
    protected void onFragmentCreated(View containerView) {
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
