package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.max.android.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends ArrayAdapter<UserProfile> {

    private LayoutInflater inflater;
    private AddUserListener addUser;
    private List<UserProfile> selectedUsers;

    private class ViewHolder {
        ImageView icon;
        TextView firstName;
        TextView lastName;
    }

    public interface AddUserListener {
        void addUser();
    }

    public UsersAdapter(Context context, List<? extends UserProfile> users) {
        this(context, users, null);
    }

    public UsersAdapter(Context context, List<? extends UserProfile> users, AddUserListener addUser) {
        super(context, R.layout.item_user, (List<UserProfile>) users);
        selectedUsers = new ArrayList<>();
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

    public void setSelectUser(View view, int position) {
        if (view != null) {
            UserProfile selectedUser = getItem(position);
            if (selectedUsers.contains(selectedUser)) {
                selectedUsers.remove(selectedUser);
            } else {
                selectedUsers.add(selectedUser);
            }
            colorSelected(view, position);
        }
    }

    public List<UserProfile> getSelectedUsers() {
        return selectedUsers;
    }

    private void colorSelected(View view, int position) {
        UserProfile selectedUser = getItem(position);
        if (selectedUsers.contains(selectedUser)) {
            view.setBackgroundResource(R.color.itemSelected);
        } else {
            view.setBackgroundResource(R.color.itemNotSelected);
        }
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
            UserProfile user = getItem(position);
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
            if (user.getFirstName() == null && user.getLastName() == null) {
                viewHolder.firstName.setText(user.getDisplayName());
            }
            colorSelected(convertView, position);
        }
        return convertView;
    }

}
