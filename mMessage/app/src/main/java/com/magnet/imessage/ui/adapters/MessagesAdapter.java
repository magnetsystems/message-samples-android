package com.magnet.imessage.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.imessage.R;
import com.magnet.imessage.model.Message;
import com.magnet.max.android.User;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private LayoutInflater inflater;

    private class ViewHolder {
        LinearLayout messageArea;
        TextView date;
        TextView sender;
        TextView text;
        TextView delivered;
    }

    public MessagesAdapter(Context context, List<Message> messages) {
        super(context, R.layout.item_message, messages);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.messageArea = (LinearLayout) convertView.findViewById(R.id.itemMessageArea);
            viewHolder.date = (TextView) convertView.findViewById(R.id.itemMessageDate);
            viewHolder.sender = (TextView) convertView.findViewById(R.id.itemMessageSender);
            viewHolder.text = (TextView) convertView.findViewById(R.id.itemMessageText);
            viewHolder.delivered = (TextView) convertView.findViewById(R.id.itemMessageDelivered);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Message message = getItem(position);
        if (message.getSender().getUserIdentifier().equals(User.getCurrentUserId())) {
            makeMessageFromMe(viewHolder, message);
        } else {
            makeMessageToMe(viewHolder, message);
        }
        return convertView;
    }

    private void makeMessageToMe(ViewHolder viewHolder, Message message) {
        viewHolder.messageArea.setGravity(Gravity.LEFT | Gravity.START);
        viewHolder.text.setText(message.getText());
        viewHolder.text.setBackgroundColor(getContext().getResources().getColor(R.color.messageBackgroundToMe));
        viewHolder.text.setTextColor(Color.BLACK);
        viewHolder.delivered.setVisibility(View.GONE);
        viewHolder.sender.setText(message.getSenderFullName());
    }

    private void makeMessageFromMe(ViewHolder viewHolder, Message message) {
        viewHolder.messageArea.setGravity(Gravity.RIGHT | Gravity.END);
        viewHolder.text.setText(message.getText());
        viewHolder.text.setBackgroundColor(getContext().getResources().getColor(R.color.messageBackgroundFromMe));
        viewHolder.text.setTextColor(Color.WHITE);
        viewHolder.sender.setVisibility(View.GONE);
        if (message.isDelivered()) {
            viewHolder.delivered.setVisibility(View.VISIBLE);
        } else {
            viewHolder.delivered.setVisibility(View.GONE);
        }
    }

}
