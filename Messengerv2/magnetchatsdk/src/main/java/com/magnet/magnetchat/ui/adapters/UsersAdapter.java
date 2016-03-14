package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends BaseUsersAdapter<BaseUsersAdapter.UserViewHolder, User> {
    private List<User> selectedUsers;

    public UsersAdapter(Context context, List<User> users, List<User> selectedUsers, ItemComparator<User> comparator) {
        super(context, users, User.class, comparator);
        this.selectedUsers = selectedUsers;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BaseUsersAdapter.UserViewHolder viewHolder, int position) {
        if (viewHolder != null) {
            super.onBindViewHolder(viewHolder, position);

            colorSelected(viewHolder.currentView, getItem(position));
        }
    }

    /**
     * Colors the item mView, if item is selected or returns to default color
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