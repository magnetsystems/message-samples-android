package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.max.android.User;

import com.magnet.max.android.UserProfile;
import java.util.List;

public class ConversationsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Conversation> conversations;

    private class ConversationViewHolder {
        ImageView newMessage;
        ImageView icon;
        TextView users;
        TextView date;
        TextView lastMessage;
    }

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.conversations = conversations;
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Conversation getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConversationViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
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
        List<Message> messages = conversation.getMessages();
        if (messages != null && messages.size() > 0) {
            Message message = messages.get(messages.size() - 1);
            String msgType = message.getType();
            if (msgType == null) {
                msgType = Message.TYPE_TEXT;
            }
            switch (msgType) {
                case Message.TYPE_MAP:
                    viewHolder.lastMessage.setText("User's location");
                    break;
                case Message.TYPE_VIDEO:
                    viewHolder.lastMessage.setText("User's video");
                    break;
                case Message.TYPE_PHOTO:
                    viewHolder.lastMessage.setText("User's photo");
                    break;
                case Message.TYPE_TEXT:
                    String text = message.getText().replace(System.getProperty("line.separator"), " ");
                    if (text.length() > 23) {
                        text = text.substring(0, 20) + "...";
                    }
                    viewHolder.lastMessage.setText(text);
                    break;
            }
        } else {
            viewHolder.lastMessage.setText("");
        }
        return convertView;
    }

}