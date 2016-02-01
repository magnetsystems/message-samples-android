package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.max.android.User;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;
    private AddUserListener addUser;

    private class ViewHolder {
        ImageView icon;
        TextView firstName;
        TextView lastName;
    }

    public interface AddUserListener {
        void addUser();
    }

    public UsersAdapter(Context context, List<User> users) {
        this(context, users, null);
    }

    public UsersAdapter(Context context, List<User> users, AddUserListener addUser) {
        super(context, R.layout.item_user, users);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (addUser != null) {
            this.addUser = addUser;
        }
    }

    @Override
    public int getCount() {
        if (addUser != null) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (addUser != null && position == getCount() - 1) {
            View addUserView = inflater.inflate(R.layout.item_add_user, parent, false);
            addUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addUser.addUser();
                }
            });
            return addUserView;
        } else {
            User user = getItem(position);
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
            if (user.getFirstName() != null) {
                viewHolder.firstName.setText(user.getFirstName());
            }
            if (user.getLastName() != null) {
                viewHolder.lastName.setText(user.getLastName());
            }
        }
        return convertView;
    }

}
