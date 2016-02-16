package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsAdapter extends BaseConversationsAdapter {

    private class ConversationViewHolder {
        ImageView newMessage;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        TextView title;
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
            viewHolder.newMessage = (ImageView) convertView.findViewById(R.id.imConversationNewMsg);
            viewHolder.imageAvatar = (CircleImageView) convertView.findViewById(R.id.imageConversationOwnerAvatar);
            viewHolder.viewAvatar = (CircleNameView) convertView.findViewById(R.id.viewConversationOwnerAvatar);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvConversationTitle);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvConversationDate);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.tvConversationLastMsg);
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
            viewHolder.title.setText(String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName()));
        } else {
            viewHolder.title.setText(UserHelper.getDisplayNames(conversation.getSuppliersList()));
            if (suppliers.size() > 1) {
                viewHolder.imageAvatar.setImageResource(R.drawable.user_group);
            } else {
                UserProfile user = suppliers.get(0);
                if (user != null) {
                    viewHolder.title.setText(user.getDisplayName());
                    viewHolder.viewAvatar.setUserName(user.getDisplayName());
                    if (user.getAvatarUrl() != null) {
                        Glide.with(getContext()).load(user.getAvatarUrl()).fitCenter().into(viewHolder.imageAvatar);
                    }
                }
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