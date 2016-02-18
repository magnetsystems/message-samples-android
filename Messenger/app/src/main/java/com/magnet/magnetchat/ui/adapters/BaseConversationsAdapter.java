package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.UserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseConversationsAdapter extends BaseAdapter {
    private static final String TAG = BaseConversationsAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<Conversation> conversations;
    private Context context;

    protected class ConversationViewHolder {
        ImageView newMessage;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        TextView title;
        TextView date;
        TextView lastMessage;
    }

    public BaseConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
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
        prepareTitleAndAvatar(conversation, viewHolder);
        if (conversation.hasUnreadMessage()) {
            viewHolder.newMessage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.newMessage.setVisibility(View.INVISIBLE);
        }
        viewHolder.date.setText(DateHelper.getConversationLastDate(conversation.getLastActiveTime()));
        viewHolder.lastMessage.setText(getLastMessage(conversation));
        return convertView;
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Searches last message for conversation
     * @param conversation
     * @return empty line, if conversation has not any massage
     */
    protected String getLastMessage(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        if (messages != null && messages.size() > 0) {
            Message message = messages.get(messages.size() - 1);
            String msgType = message.getType();
            switch (msgType) {
                case Message.TYPE_MAP:
                    return "User's location";
                case Message.TYPE_VIDEO:
                    return "User's video";
                case Message.TYPE_PHOTO:
                    return "User's photo";
                default: //case Message.TYPE_TEXT:
                    String text = message.getText().replace(System.getProperty("line.separator"), " ");
                    if (text.length() > 23) {
                        text = text.substring(0, 20) + "...";
                    }
                    return text;
            }
        } else {
            Logger.debug(TAG, "\n----------------------No message in conversation : \n " + conversation);
        }
        return "";
    }

    /**
     * If user is not null, configures avatar for current conversation.
     * If user has no avatar, sets his initials
     * @param user
     * @param viewHolder
     */
    protected void setUserAvatar(UserProfile user, ConversationViewHolder viewHolder) {
        if (user != null) {
            viewHolder.title.setText(user.getDisplayName());
            viewHolder.viewAvatar.setUserName(user.getDisplayName());
            if (user.getAvatarUrl() != null) {
                Glide.with(context).load(user.getAvatarUrl()).fitCenter().into(viewHolder.imageAvatar);
            }
        } else {
            Log.e(TAG, "UserProfile is null");
        }
    }

    /**
     * Sets title to conversation item(supplier name) and avatar image.
     * @param conversation object for current item
     * @param viewHolder
     */
    protected abstract void prepareTitleAndAvatar(Conversation conversation, ConversationViewHolder viewHolder);

}
