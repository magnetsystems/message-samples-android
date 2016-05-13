package com.magnet.magnetchat.ui.factories;

import android.content.Context;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.dialogs.AttachmentDialogFragment;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView;
import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

/**
 * Created by aorehov on 27.04.16.
 */
public interface MMXViewFactory {
    AbstractEditPollView createPolView(Context context);

    MMXChatListView createMMXChatListView(Context context);

    MMXPostMessageView createMMXPostMessageView(Context context);

    MMXChatView createMMXChatView(Context context);

    AttachmentDialogFragment createAttachmentDialogFragment(Context context);

    MMXUserListView createMmxUserListView(Context context);

    MMXUserListView createMmxAllUserListView(Context context);

}
