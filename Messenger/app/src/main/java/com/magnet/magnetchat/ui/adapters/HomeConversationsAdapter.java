package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.views.AskMagnetView;
import com.magnet.magnetchat.ui.views.EventView;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

public class HomeConversationsAdapter extends BaseConversationsAdapter {

    private final int VIEW_TYPE_CONVERSATION = 0;
    private final int VIEW_TYPE_EVENT = 1;
    private final int VIEW_TYPE_ASK_MAGNET = 2;

    private EventView eventView;
    private AskMagnetView askMagnetView;

    private boolean showEvent;
    private boolean showAskMagnet;

    private int additionalItemsCount = 0;

    private onClickHeaderListener onClickHeaderListener;

    public interface onClickHeaderListener {
        void onClickEvent();

        void onClickAskMagnet();
    }

    public HomeConversationsAdapter(Context context, List<Conversation> conversations, EventView eventView, AskMagnetView askMagnetView, onClickHeaderListener headerListener) {
        super(context, conversations);
        this.eventView = eventView;
        this.askMagnetView = askMagnetView;
        if (!UserHelper.isMagnetSupportMember()) {
            additionalItemsCount++;
            showAskMagnet = true;
        }
        this.onClickHeaderListener = headerListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (showEvent) {
            if (position == 0) {
                return VIEW_TYPE_EVENT;
            } else if (position == 1 && showAskMagnet) {
                return VIEW_TYPE_ASK_MAGNET;
            }
        } else if (position == 0 && showAskMagnet) {
            return VIEW_TYPE_ASK_MAGNET;
        }
        return VIEW_TYPE_CONVERSATION;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + additionalItemsCount;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConversationViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_EVENT:
                viewHolder = new ConversationViewHolder(eventView);
                break;
            case VIEW_TYPE_ASK_MAGNET:
                viewHolder = new ConversationViewHolder(askMagnetView);
                break;
        }
        if (viewHolder != null) {
            return viewHolder;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_EVENT:
                holder.setOnCLickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickHeaderListener != null) {
                            onClickHeaderListener.onClickEvent();
                        }
                    }
                });
                break;
            case VIEW_TYPE_ASK_MAGNET:
                holder.setOnCLickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickHeaderListener != null) {
                            onClickHeaderListener.onClickAskMagnet();
                        }
                    }
                });
                break;
            default:
                super.onBindViewHolder(holder, position - additionalItemsCount);
                break;
        }
    }

    @Override
    protected void prepareTitleAndAvatar(Conversation conversation, AvatarConversationViewHolder viewHolder) {
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

    public void setEventConversationEnabled(boolean enabled) {
        if (!showEvent) {
            showEvent = enabled;
            additionalItemsCount++;
            notifyDataSetChanged();
        }
    }

}