package com.magnet.magnetchat.ui.factories;

import android.content.Context;

import com.magnet.magnetchat.ui.dialogs.AttachmentDialogFragment;
import com.magnet.magnetchat.ui.dialogs.DefaultAttachmentDialogFragment;
import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.DefaultMMXPostMessageView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView;
import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;
import com.magnet.magnetchat.ui.views.poll.DefaultEditPollView;
import com.magnet.magnetchat.ui.views.users.DefaultMMXAllUserListView;
import com.magnet.magnetchat.ui.views.users.DefaultMMXUserListView;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXViewFactory implements MMXViewFactory {

    @Override
    public AbstractEditPollView createPolView(Context context) {
        return new DefaultEditPollView(context);
    }

    @Override
    public MMXChatListView createMMXChatListView(Context context) {
        return new DefaultMMXChatListView(context);
    }

    @Override
    public MMXPostMessageView createMMXPostMessageView(Context context) {
        return new DefaultMMXPostMessageView(context);
    }

    @Override
    public MMXChatView createMMXChatView(Context context) {
        return new DefaultMMXChatView(context);
    }

    @Override
    public AttachmentDialogFragment createAttachmentDialogFragment(Context context) {
        return new DefaultAttachmentDialogFragment();
    }

    @Override
    public MMXUserListView createMmxUserListView(Context context) {
        return new DefaultMMXUserListView(context);
    }

    @Override
    public MMXUserListView createMmxAllUserListView(Context context) {
        return new DefaultMMXAllUserListView(context);
    }

}
