package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMXMessage;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class ChatsAdapter extends BaseSortedAdapter<ChatsAdapter.ConversationViewHolder, Chat> {
    private static final String TAG = "ChatsAdapter";
    /**
     * View holder to show mConversations with user's avatars and messages
     */
    protected class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        Chat conversation;
        ImageView newMessage;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        TextView title;
        TextView date;
        TextView lastMessage;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            newMessage = (ImageView) itemView.findViewById(R.id.imConversationNewMsg);
            imageAvatar = (CircleImageView) itemView.findViewById(R.id.imageConversationOwnerAvatar);
            viewAvatar = (CircleNameView) itemView.findViewById(R.id.viewConversationOwnerAvatar);
            title = (TextView) itemView.findViewById(R.id.tvConversationTitle);
            date = (TextView) itemView.findViewById(R.id.tvConversationDate);
            lastMessage = (TextView) itemView.findViewById(R.id.tvConversationLastMsg);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onLongClick(getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public ChatsAdapter(Context context, List<Chat> conversations, ItemComparator<Chat> comparator) {
       super(context, conversations, Chat.class, comparator);
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        ConversationViewHolder viewHolder = holder;
        Chat conversation = getItem(position);
        if (viewHolder != null && conversation != null) {
            viewHolder.conversation = conversation;
            prepareTitleAndAvatar(conversation, viewHolder);
            if (conversation.hasUnreadMessage()) {
                viewHolder.newMessage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.newMessage.setVisibility(View.INVISIBLE);
            }
            viewHolder.date.setText(DateHelper.getConversationLastDate(conversation.getLastPublishedTime()));
            //viewHolder.date.setText(DateHelper.getDateWithoutSpaces(conversation.getLastPublishedTime()));
            viewHolder.lastMessage.setText(getLastMessage(conversation));
        }
    }

    /**
     * Searches last message for conversation
     *
     * @param conversation
     * @return empty line, if conversation has not any massage
     */
    protected String getLastMessage(Chat conversation) {
        String lastMessage = conversation.getLastMessageSummary();
        if(StringUtil.isEmpty(lastMessage)) {
            lastMessage = "No message";
        }
        return lastMessage;
    }

    /**
     * If user is not null, configures avatar for current conversation.
     * If user has no avatar, sets his initials
     *
     * @param user
     * @param viewHolder
     */
    protected void setUserAvatar(final UserProfile user, final ConversationViewHolder viewHolder) {
        if (user != null) {
            if (user.getAvatarUrl() != null) {
                Glide.with(mContext).load(user.getAvatarUrl()).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target,
                        boolean b) {
                        //Log.e("BaseConversation", "failed to load image ", e);
                        showNameAvatar(user.getDisplayName(), viewHolder);
                        return false;
                    }

                    @Override public boolean onResourceReady(GlideDrawable glideDrawable, String s,
                        Target<GlideDrawable> target, boolean b, boolean b1) {
                        showImageAvatar(user.getDisplayName(), viewHolder);
                        return false;
                    }
                }).into(viewHolder.imageAvatar);
            } else {
                showNameAvatar(user.getDisplayName(), viewHolder);
            }
        } else {
            showNameAvatar("Unknown User", viewHolder);
        }
    }

    private void showImageAvatar(String name, ConversationViewHolder viewHolder) {
        if(viewHolder.imageAvatar.getVisibility() == View.GONE) {
            viewHolder.imageAvatar.setVisibility(View.VISIBLE);
        }
        if(viewHolder.viewAvatar.getVisibility() != View.GONE) {
            viewHolder.viewAvatar.setVisibility(View.GONE);
        }
    }

    private void showNameAvatar(String name, ConversationViewHolder viewHolder) {
        viewHolder.viewAvatar.setUserName(name);

        if(viewHolder.viewAvatar.getVisibility() == View.GONE) {
            viewHolder.viewAvatar.setVisibility(View.VISIBLE);
        }
        if(viewHolder.imageAvatar.getVisibility() != View.GONE) {
            viewHolder.imageAvatar.setVisibility(View.GONE);
        }
    }

    private void showTitle(ConversationViewHolder viewHolder, Chat chat) {
        String title = null;
        if(!chat.getSubscribers().isEmpty()) {
            title = UserHelper.getDisplayNames(chat.getSubscribers());
        } else {
            title = UserHelper.getDisplayName(User.getCurrentUser());
        }
        if(StringUtil.isEmpty(title)) {
            Log.e(TAG, "----------title is empty for Chat : " + chat);
        }
        viewHolder.title.setText(title);
    }

    /**
     * Sets title to conversation item(supplier name) and avatar image.
     *
     * @param conversation object for current item
     * @param viewHolder
     */
    protected void prepareTitleAndAvatar(Chat conversation, ConversationViewHolder viewHolder) {
        //If all suppliers left conversation, show current user.
        showTitle(viewHolder, conversation);
        if (conversation.getSubscribers().size() == 0) {
            User currentUser = User.getCurrentUser();
            if (currentUser != null) {
                setUserAvatar(currentUser, viewHolder);
            }
        } else {
            if (conversation.getSubscribers().size() > 1) {
                Glide.with(getContext()).load(R.drawable.user_group).fitCenter().into(viewHolder.imageAvatar);
                showImageAvatar(UserHelper.getDisplayNames(conversation.getSortedSubscribers()), viewHolder);
            } else {
                //If there is one supplier, show his avatar.
                setUserAvatar(conversation.getSubscribers().get(0), viewHolder);
            }
        }
    }
}
