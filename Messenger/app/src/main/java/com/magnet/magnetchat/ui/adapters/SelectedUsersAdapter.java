package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
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

public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.AvatarViewHolder> {

    private LayoutInflater inflater;
    private List<UserProfile> userList;
    private Context context;

    public class AvatarViewHolder extends RecyclerView.ViewHolder {

        CircleNameView nameView;
        CircleImageView imageView;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            nameView = (CircleNameView) itemView.findViewById(R.id.viewUserName);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageUserAvatar);
        }
    }

    public SelectedUsersAdapter(Context context, List<? extends UserProfile> users) {
        this.context = context;
        this.userList = (List<UserProfile>) users;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_selected_user, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AvatarViewHolder holder, int position) {
        UserProfile user = userList.get(position);
        if (user != null) {
            holder.nameView.setText(UserHelper.getInitialName(user.getDisplayName()));
            if (user.getAvatarUrl() != null) {
                holder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(user.getAvatarUrl()).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        holder.imageView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        return false;
                    }
                }).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
