package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.max.android.UserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseConversationsAdapter extends RecyclerView.Adapter<BaseConversationsAdapter.ConversationViewHolder> {

    private LayoutInflater inflater;
    private List<Conversation> conversations;
    private Context context;
    protected OnConversationClick onConversationClick;
    protected OnConversationLongClick onConversationLongClick;

    /**
     * Listener which provides actions when user click on item
     */
    public interface OnConversationClick {
        void onClick(Conversation conversation);
    }

    /**
     * Listener which provides actions when user click long on item
     */
    public interface OnConversationLongClick {
        void onLongClick(Conversation conversation);
    }

    protected class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Conversation conversation;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }

        @Override
        public void onClick(View v) {
            if (onConversationClick != null) {
                onConversationClick.onClick(conversation);
            }
        }

        public void setDefaultOnClickListener() {
            setOnCLickListener(this);
        }

        public void setOnCLickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    /**
     * View holder to show conversations with user's avatars and messages
     */
    protected class AvatarConversationViewHolder extends ConversationViewHolder implements View.OnLongClickListener {
        ImageView newMessage;
        CircleImageView imageAvatar;
        CircleNameView viewAvatar;
        TextView title;
        TextView date;
        TextView lastMessage;

        public AvatarConversationViewHolder(View itemView) {
            super(itemView);
            newMessage = (ImageView) itemView.findViewById(R.id.imConversationNewMsg);
            imageAvatar = (CircleImageView) itemView.findViewById(R.id.imageConversationOwnerAvatar);
            viewAvatar = (CircleNameView) itemView.findViewById(R.id.viewConversationOwnerAvatar);
            title = (TextView) itemView.findViewById(R.id.tvConversationTitle);
            date = (TextView) itemView.findViewById(R.id.tvConversationDate);
            lastMessage = (TextView) itemView.findViewById(R.id.tvConversationLastMsg);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onConversationLongClick != null) {
                onConversationLongClick.onLongClick(conversation);
                return true;
            }
            return false;
        }
    }

    public BaseConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.conversations = conversations;
    }

    /**
     * @param position
     * @return an item by position in list
     */
    public Conversation getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_conversation, parent, false);
        return new AvatarConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        AvatarConversationViewHolder viewHolder = (AvatarConversationViewHolder) holder;
        Conversation conversation = getItem(position);
        if (viewHolder != null && conversation != null) {
            viewHolder.conversation = conversation;
            prepareTitleAndAvatar(conversation, viewHolder);
            if (conversation.hasUnreadMessage()) {
                viewHolder.newMessage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.newMessage.setVisibility(View.INVISIBLE);
            }
            viewHolder.date.setText(DateHelper.getConversationLastDate(conversation.getLastActiveTime()));
            viewHolder.lastMessage.setText(getLastMessage(conversation));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setOnConversationLongClick(OnConversationLongClick onConversationLongClick) {
        this.onConversationLongClick = onConversationLongClick;
    }

    public void setOnConversationClick(OnConversationClick onConversationClick) {
        this.onConversationClick = onConversationClick;
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Searches last message for conversation
     *
     * @param conversation
     * @return empty line, if conversation has not any massage
     */
    protected String getLastMessage(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        if (messages != null && messages.size() > 0) {
            Message message = messages.get(messages.size() - 1);
            return message.getMessageSummary();
        }
        return "";
    }

    /**
     * If user is not null, configures avatar for current conversation.
     * If user has no avatar, sets his initials
     *
     * @param user
     * @param viewHolder
     */
    protected void setUserAvatar(UserProfile user, final AvatarConversationViewHolder viewHolder) {
        if (user != null) {
            viewHolder.title.setText(user.getDisplayName());
            viewHolder.viewAvatar.setUserName(user.getDisplayName());
            if (user.getAvatarUrl() != null) {
                viewHolder.imageAvatar.setVisibility(View.VISIBLE);
                Glide.with(context).load(user.getAvatarUrl()).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target,
                        boolean b) {
                        //Log.e("BaseConversation", "failed to load image ", e);
                        viewHolder.imageAvatar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override public boolean onResourceReady(GlideDrawable glideDrawable, String s,
                        Target<GlideDrawable> target, boolean b, boolean b1) {
                        return false;
                    }
                }).into(viewHolder.imageAvatar);
            } else {
                viewHolder.imageAvatar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Sets title to conversation item(supplier name) and avatar image.
     *
     * @param conversation object for current item
     * @param viewHolder
     */
    protected abstract void prepareTitleAndAvatar(Conversation conversation, AvatarConversationViewHolder viewHolder);

}
