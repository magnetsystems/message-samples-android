package com.magnet.magnetchat.ui.factories;

import android.content.Context;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.dialogs.AttachmentDialogFragment;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatListView;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView;
import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;

/**
 * Created by aorehov on 27.04.16.
 */
public interface MMXViewFactory {
    AbstractEditPollView createPolView(Context context, ViewGroup parent);

    AbstractEditPollView createPolView(Context context);

    MMXChatListView createMMXChatListView(Context context);

    MMXChatListView createMMXChatListView(Context context, ViewGroup parent);

    MMXPostMessageView createMMXPostMessageView(Context context);

    MMXPostMessageView createMMXPostMessageView(Context context, ViewGroup viewGroup);

    MMXChatView createMMXChatView(Context context);

    MMXChatView createMMXChatView(Context context, ViewGroup viewGroup);

    AttachmentDialogFragment createAttachmentDialogFragment(Context context);
}
