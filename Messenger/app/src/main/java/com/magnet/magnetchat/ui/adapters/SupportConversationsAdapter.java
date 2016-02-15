package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.max.android.UserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SupportConversationsAdapter extends BaseConversationsAdapter {

    private class ConversationViewHolder {
        ImageView newMessage;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        TextView title;
        TextView date;
        TextView lastMessage;
    }

    public SupportConversationsAdapter(Context context, List<Conversation> conversations) {
        super(context, conversations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConversationViewHolder viewHolder;
        if (convertView == null) {
            convertView = getInflater().inflate(R.layout.item_conversation_support, parent, false);
            viewHolder = new ConversationViewHolder();
            viewHolder.newMessage = (ImageView) convertView.findViewById(R.id.imAskNewMsg);
            viewHolder.imageAvatar = (CircleImageView) convertView.findViewById(R.id.imageAskOwnerAvatar);
            viewHolder.viewAvatar = (CircleNameView) convertView.findViewById(R.id.viewAskOwnerAvatar);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvAskTitle);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvAskDate);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.tvAskLastMsg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ConversationViewHolder) convertView.getTag();
        }
        if (position >= getCount()) {
            return convertView;
        }
        Conversation conversation = getItem(position);
        UserProfile owner = conversation.getOwner();
        if (owner != null) {
            viewHolder.title.setText(owner.getDisplayName());
            viewHolder.viewAvatar.setUserName(owner.getDisplayName());
            if (owner.getAvatarUrl() != null) {
                Glide.with(getContext()).load(owner.getAvatarUrl()).fitCenter().into(viewHolder.imageAvatar);
            }
        }
        if (conversation.hasUnreadMessage()) {
            viewHolder.newMessage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.newMessage.setVisibility(View.INVISIBLE);
        }
        viewHolder.date.setText(DateHelper.getConversationLastDate(conversation.getLastActiveTime()));
        viewHolder.lastMessage.setText(getLastMessage(conversation));
        return convertView;
    }
}
