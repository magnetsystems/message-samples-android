package com.magnet.magnetchat.ui.adapters;

import android.content.Context;

import com.magnet.magnetchat.model.Conversation;

import java.util.List;

public class SupportConversationsAdapter extends BaseConversationsAdapter {

    public SupportConversationsAdapter(Context context, List<Conversation> conversations) {
        super(context, conversations);
    }

    @Override
    protected void prepareTitleAndAvatar(Conversation conversation, ConversationViewHolder viewHolder) {
        setUserAvatar(conversation.getOwner(), viewHolder);
    }
}
