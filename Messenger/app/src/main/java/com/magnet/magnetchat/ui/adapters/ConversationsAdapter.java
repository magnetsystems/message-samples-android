package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

public class ConversationsAdapter extends BaseConversationsAdapter {

    private class ConversationViewHolder {
        ImageView newMessage;
        ImageView icon;
        TextView users;
        TextView date;
        TextView lastMessage;
    }

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        super(context, conversations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConversationViewHolder viewHolder;
        if (convertView == null) {
            convertView = getInflater().inflate(R.layout.item_conversation, parent, false);
            viewHolder = new ConversationViewHolder();
            viewHolder.newMessage = (ImageView) convertView.findViewById(R.id.itemConversationNewMsg);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.itemConversationIcon);
            viewHolder.users = (TextView) convertView.findViewById(R.id.itemConversationUsers);
            viewHolder.date = (TextView) convertView.findViewById(R.id.itemConversationDate);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.itemConversationLastMsg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ConversationViewHolder) convertView.getTag();
        }
        if (position >= getCount()) {
            return convertView;
        }
        Conversation conversation = getItem(position);
        List<UserProfile> suppliers = conversation.getSuppliersList();
        if (suppliers.size() == 0) {
            User currentUser = User.getCurrentUser();
            viewHolder.users.setText(String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName()));
        } else {
            viewHolder.users.setText(UserHelper.getDisplayNames(conversation.getSuppliersList()));
            if (suppliers.size() > 1) {
                viewHolder.icon.setImageResource(R.mipmap.ic_many);
            } else {
                viewHolder.icon.setImageResource(R.mipmap.ic_one);
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