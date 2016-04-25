package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.max.android.UserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_USER = 0;
    private final int VIEW_TYPE_ADD_BTN = 1;

    private LayoutInflater inflater;
    private AddUserListener addUser;
    private List<UserProfile> selectedUsers;
    private List<UserProfile> userList;
    private Context context;
    private OnUserClickListener onUserClickListener;

    /**
     * Listener which provides actions when user click on item
     */
    public interface OnUserClickListener {
        void onUserClick(UserProfile user, int position);
    }

    /**
     * ViewHolder for user items.
     */
    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View currentView;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        AppCompatTextView firstName;
        AppCompatTextView lastName;
        UserProfile user;
        int position;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.currentView = itemView;
            imageAvatar = (CircleImageView) itemView.findViewById(R.id.imageUserAvatar);
            viewAvatar = (CircleNameView) itemView.findViewById(R.id.viewUserAvatar);
            firstName = (AppCompatTextView) itemView.findViewById(R.id.itemUserFirstName);
            lastName = (AppCompatTextView) itemView.findViewById(R.id.itemUserLastName);
            itemView.setOnClickListener(this);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user, position);
            }
        }
    }

    /**
     * ViewHolder for "Add user" button.
     */
    public class AddUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AddUserViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (addUser != null) {
                addUser.addUser();
            }
        }
    }

    public interface AddUserListener {
        void addUser();
    }

    public UsersAdapter(Context context, List<? extends UserProfile> users, AddUserListener addUser) {
        this(context, users, null, addUser);
    }

    public UsersAdapter(Context context, List<? extends UserProfile> users, List<? extends UserProfile> selectedUsers) {
        this(context, users, selectedUsers, null);
    }

    public UsersAdapter(Context context, List<? extends UserProfile> users, List<? extends UserProfile> selectedUsers, AddUserListener addUser) {
        this.context = context;
        this.userList = (List<UserProfile>) users;
        this.selectedUsers = (List<UserProfile>) selectedUsers;
        inflater = LayoutInflater.from(context);
        if (addUser != null) {
            this.addUser = addUser;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (addUser != null && position == 0) {
            return VIEW_TYPE_ADD_BTN;
        }
        return VIEW_TYPE_USER;
    }

    @Override
    public int getItemCount() {
        if (addUser != null) {
            return userList.size() + 1;
        }
        return userList.size();
    }

    private UserProfile getItem(int position) {
        if (addUser != null) {
            return userList.get(position - 1);
        }
        return userList.get(position);
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case VIEW_TYPE_USER:
                view = inflater.inflate(R.layout.item_user, parent, false);
                viewHolder = new UserViewHolder(view);
                break;
            case VIEW_TYPE_ADD_BTN:
                view = inflater.inflate(R.layout.item_add_user, parent, false);
                viewHolder = new AddUserViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_USER:
                    final UserViewHolder viewHolder = (UserViewHolder) holder;
                    viewHolder.setPosition(position);
                    UserProfile user = getItem(position);
                    viewHolder.user = user;
                    if (user.getFirstName() != null) {
                        viewHolder.firstName.setText(user.getFirstName());
                    }
                    if (user.getLastName() != null) {
                        viewHolder.lastName.setText(user.getLastName());
                    }
                    if (user.getFirstName() == null && user.getLastName() == null) {
                        viewHolder.firstName.setText(user.getDisplayName());
                    }

                    viewHolder.viewAvatar.setText(UserHelper.getInitialName(user.getDisplayName()));
                    if (user.getAvatarUrl() != null) {
                        viewHolder.imageAvatar.setVisibility(View.VISIBLE);
                        Glide.with(context).load(user.getAvatarUrl()).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                                viewHolder.imageAvatar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                                return false;
                            }
                        }).into(viewHolder.imageAvatar);
                    } else {
                        viewHolder.imageAvatar.setVisibility(View.GONE);
                    }

                    colorSelected(viewHolder.currentView, user);
                    break;
                case VIEW_TYPE_ADD_BTN:
                    break;
            }
        }
    }

    /**
     * Colors the item view, if item is selected or returns to default color
     *
     * @param view
     * @param user
     */
    private void colorSelected(View view, UserProfile user) {
        if (selectedUsers != null && selectedUsers.contains(user)) {
            view.setBackgroundResource(R.color.itemSelected);
        } else {
            view.setBackgroundResource(R.color.itemNotSelected);
        }
    }

}