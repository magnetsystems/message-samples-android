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
import com.magnet.max.android.UserProfile;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public abstract class BaseUsersAdapter<V extends BaseUsersAdapter.UserViewHolder, T extends UserProfile> extends BaseSortedAdapter<V, T> {

    public BaseUsersAdapter(Context context, List<T> data, Class<T> clazz,
        ItemComparator comparator) {
        super(context, data, clazz, comparator);
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
        TextView firstLetter;
        UserProfile user;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.currentView = itemView;
            imageAvatar = (CircleImageView) itemView.findViewById(R.id.imageUserAvatar);
            viewAvatar = (CircleNameView) itemView.findViewById(R.id.viewUserAvatar);
            firstName = (AppCompatTextView) itemView.findViewById(R.id.itemUserFirstName);
            lastName = (AppCompatTextView) itemView.findViewById(R.id.itemUserLastName);
            firstLetter = (TextView) itemView.findViewById(R.id.itemUserFirstLetter);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(getAdapterPosition());
            }
        }
    }

    @Override
    public void onBindViewHolder(final V viewHolder, int position) {
        if (viewHolder != null) {
            UserProfile user = getItem(position);
            UserProfile previous = null;
            viewHolder.user = user;
            if (position > 0) {
                previous = getItem(position - 1);
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

            char currentFirstLetter = getCharToGroup(user);
            char previousFirstLetter = getCharToGroup(previous);
            if (previous == null || currentFirstLetter != previousFirstLetter) {
                viewHolder.firstLetter.setVisibility(View.VISIBLE);
                viewHolder.firstLetter.setText(String.valueOf(currentFirstLetter).toUpperCase());
            } else {
                viewHolder.firstLetter.setVisibility(View.GONE);
            }

            viewHolder.viewAvatar.setUserName(user.getDisplayName());
            if (user.getAvatarUrl() != null) {
                viewHolder.imageAvatar.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(user.getAvatarUrl()).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
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
        }
    }

    private char getCharToGroup(UserProfile userProfile) {
        char letter = ' ';
        String str = UserHelper.getUserNameToCompare(userProfile);
        if (str.length() > 0) {
            letter = str.charAt(0);
        }
        return letter;
    }
}