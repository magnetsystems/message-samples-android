package com.magnet.imessage.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.imessage.R;
import com.magnet.max.android.User;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;

    private class ViewHolder {
        ImageView icon;
        TextView firstName;
        TextView lastName;
    }

    public UsersAdapter(Context context, List<User> users) {
        super(context, R.layout.item_user, users);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.itemUserIcon);
            viewHolder.firstName = (TextView) convertView.findViewById(R.id.itemUserFirstName);
            viewHolder.lastName = (TextView) convertView.findViewById(R.id.itemUserLastName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User user = getItem(position);
        if (user.getFirstName() != null) {
            viewHolder.firstName.setText(user.getFirstName());
        }
        if (user.getLastName() != null) {
            viewHolder.lastName.setText(user.getLastName());
        }
        return convertView;
    }

}
