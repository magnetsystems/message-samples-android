package com.magnet.imessage.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.magnet.imessage.R;
import com.magnet.imessage.helpers.UserHelper;
import com.magnet.imessage.model.Conversation;
import com.magnet.imessage.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationsAdapter extends ArrayAdapter<Conversation> {

    private LayoutInflater inflater;

    private class ViewHolder {
        ImageView newMessage;
        ImageView icon;
        TextView users;
        TextView date;
        TextView lastMessage;
        ProgressBar progress;
    }

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        super(context, R.layout.item_conversation, conversations);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.newMessage = (ImageView) convertView.findViewById(R.id.itemConversationNewMsg);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.itemConversationIcon);
            viewHolder.users = (TextView) convertView.findViewById(R.id.itemConversationUsers);
            viewHolder.date = (TextView) convertView.findViewById(R.id.itemConversationDate);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.itemConversationLastMsg);
            viewHolder.progress = (ProgressBar) convertView.findViewById(R.id.itemConversationProgress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Conversation conversation = getItem(position);
        if (conversation.isLoaded()) {
            setLoaded(true, viewHolder);
            if (conversation.getSuppliers() != null) {
                String suppliers = UserHelper.getInstance().userNamesAsString(conversation.getSuppliers());
                viewHolder.users.setText(suppliers);
                if (conversation.getSuppliers().size() > 1) {
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
            viewHolder.date.setText(getDateString(conversation.getChannel().getLastTimeActive()));
            List<Message> messages = conversation.getMessages();
            if (messages != null && messages.size() > 0) {
                viewHolder.lastMessage.setText(messages.get(0).getText());
            }
        } else {
            setLoaded(false, viewHolder);
        }
        return convertView;
    }

    private void setLoaded(boolean isLoaded, ViewHolder holder) {
        if (isLoaded) {
            holder.progress.setVisibility(View.GONE);
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.progress.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.GONE);
        }
    }

    private String getDateString(Date date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        if (dateFormat.format(date).equals(dateFormat.format(new Date()))) {
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        } else {
            long week = 1000 * 60 * 60 * 24 * 7;
            if ((System.currentTimeMillis() - date.getTime()) < week) {
                return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
            }
        }
        return dateFormat.format(date);
    }

}
