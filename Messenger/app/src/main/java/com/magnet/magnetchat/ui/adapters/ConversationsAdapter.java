package com.magnet.magnetchat.ui.adapters;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

public class ConversationsAdapter extends BaseConversationsAdapter {

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        super(context, conversations);
    }

    @Override
    protected void prepareTitleAndAvatar(Conversation conversation, ConversationViewHolder viewHolder) {
        List<UserProfile> suppliers = conversation.getSuppliersList();
        //If all suppliers left conversation, show current user.
        if (suppliers.size() == 0) {
            User currentUser = User.getCurrentUser();
            if (currentUser != null) {
                viewHolder.title.setText(String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName()));
                setUserAvatar(currentUser, viewHolder);
            }
        } else {
            viewHolder.title.setText(UserHelper.getDisplayNames(conversation.getSuppliersList()));
            if (suppliers.size() > 1) {
                Glide.with(getContext()).load(R.drawable.user_group).fitCenter().into(viewHolder.imageAvatar);
            } else {
                //If there is one supplier, show his avatar.
                setUserAvatar(suppliers.get(0), viewHolder);
            }
        }
    }

}