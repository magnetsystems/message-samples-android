package com.magnet.magnetchat.ui.factories;

import android.content.Context;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXPostMessageView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView;
import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;
import com.magnet.magnetchat.ui.views.poll.DefaultEditPollView;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXViewFactory implements MMXViewFactory {

    @Override
    public AbstractEditPollView createPolView(Context context, ViewGroup parent) {
        return createPolView(context);
    }

    @Override
    public AbstractEditPollView createPolView(Context context) {
        return new DefaultEditPollView(context);
    }

    @Override
    public MMXChatListView createMMXChatListView(Context context) {
        return new DefaultMMXChatListView(context);
    }

    @Override
    public MMXChatListView createMMXChatListView(Context context, ViewGroup parent) {
        return createMMXChatListView(context);
    }

    @Override
    public MMXPostMessageView createMMXPostMessageView(Context context) {
        return new DefaultMMXPostMessageView(context);
    }

    @Override
    public MMXPostMessageView createMMXPostMessageView(Context context, ViewGroup viewGroup) {
        return createMMXPostMessageView(context);
    }

    @Override
    public MMXChatView createMMXChatView(Context context) {
        return new DefaultMMXChatView(context);
    }

    @Override
    public MMXChatView createMMXChatView(Context context, ViewGroup viewGroup) {
        return createMMXChatView(context);
    }

}
