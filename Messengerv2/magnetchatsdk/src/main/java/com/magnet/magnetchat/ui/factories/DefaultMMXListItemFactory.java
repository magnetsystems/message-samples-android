package com.magnet.magnetchat.ui.factories;

import android.content.Context;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXLocAnotherMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXLocMyMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXMessageAnotherView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXMessageMyView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXPicAnotherMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXPicMyMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXPollAnotherMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXPollAnswerMessageView;
import com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXPollMyMessageView;
import com.magnet.magnetchat.ui.views.chatlist.poll.DefaultMMXPollItemAnotherView;
import com.magnet.magnetchat.ui.views.chatlist.poll.DefaultMMXPollItemMyView;
import com.magnet.magnetchat.ui.views.users.DefaultMMXUserItemView;

/**
 * Created by aorehov on 28.04.16.
 */
public class DefaultMMXListItemFactory implements MMXListItemFactory {

    private MMXListItemFactory factory;

    public void setFactory(MMXListItemFactory factory) {
        this.factory = factory;
    }

    @Override
    final public BaseMMXTypedView createView(Context context, int type) {
        BaseMMXTypedView view = factory == null ? null : factory.createView(context, type);
        if (view == null)
            switch (type) {
                case MMXMessageWrapper.TYPE_MAP_ANOTHER:
                    return new DefaultMMXLocAnotherMessageView(context);
                case MMXMessageWrapper.TYPE_MAP_MY:
                    return new DefaultMMXLocMyMessageView(context);
                case MMXMessageWrapper.TYPE_TEXT_ANOTHER:
                    return new DefaultMMXMessageAnotherView(context);
                case MMXMessageWrapper.TYPE_TEXT_MY:
                    return new DefaultMMXMessageMyView(context);
                case MMXMessageWrapper.TYPE_PHOTO_MY:
                    return new DefaultMMXPicMyMessageView(context);
                case MMXMessageWrapper.TYPE_PHOTO_ANOTHER:
                    return new DefaultMMXPicAnotherMessageView(context);
                case MMXMessageWrapper.TYPE_POLL_ANOTHER:
                    return new DefaultMMXPollAnotherMessageView(context);
                case MMXMessageWrapper.TYPE_POLL_MY:
                    return new DefaultMMXPollMyMessageView(context);
                case MMXPollOptionWrapper.TYPE_POLL_ITEM_ANOTHER:
                    return new DefaultMMXPollItemAnotherView(context);
                case MMXPollOptionWrapper.TYPE_POLL_ITEM_MY:
                    return new DefaultMMXPollItemMyView(context);
                case MMXMessageWrapper.TYPE_VOTE_ANSWER:
                    return new DefaultMMXPollAnswerMessageView(context);
                case MMXUserWrapper.TYPE_USER:
                    return new DefaultMMXUserItemView(context);
                default: {
                    if ((MMXMessageWrapper.MY_MESSAGE_MASK & type) == MMXMessageWrapper.MY_MESSAGE_MASK) {
                        return new DefaultMMXMessageMyView(context);
                    } else {
                        return new DefaultMMXMessageAnotherView(context);
                    }
                }
            }
        return view;
    }
}
