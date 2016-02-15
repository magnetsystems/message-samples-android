package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;

import java.util.List;

public abstract class BaseConversationsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Conversation> conversations;
    private Context context;

    public BaseConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.conversations = conversations;
    }

    public Context getContext() {
        return context;
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

    protected LayoutInflater getInflater() {
        return inflater;
    }

    protected String getLastMessage(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        if (messages != null && messages.size() > 0) {
            Message message = messages.get(messages.size() - 1);
            String msgType = message.getType();
            if (msgType == null) {
                msgType = Message.TYPE_TEXT;
            }
            switch (msgType) {
                case Message.TYPE_MAP:
                    return "User's location";
                case Message.TYPE_VIDEO:
                    return "User's video";
                case Message.TYPE_PHOTO:
                    return "User's photo";
                case Message.TYPE_TEXT:
                    String text = message.getText().replace(System.getProperty("line.separator"), " ");
                    if (text.length() > 23) {
                        text = text.substring(0, 20) + "...";
                    }
                    return text;
            }
        }
        return "";
    }

}
