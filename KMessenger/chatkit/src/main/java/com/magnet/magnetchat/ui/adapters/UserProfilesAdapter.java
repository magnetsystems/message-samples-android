package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import java.util.List;

@Deprecated
public class UserProfilesAdapter extends BaseUsersAdapter<BaseUsersAdapter.UserViewHolder, UserProfile> {

    public UserProfilesAdapter(Context context, List<UserProfile> users, ItemComparator<UserProfile> comparator) {
        super(context, users, UserProfile.class, comparator);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }
}