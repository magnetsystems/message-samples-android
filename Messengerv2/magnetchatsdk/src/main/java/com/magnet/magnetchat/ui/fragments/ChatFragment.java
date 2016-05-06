package com.magnet.magnetchat.ui.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;

/**
 * Created by aorehov on 04.05.16.
 */
public class ChatFragment extends MMXBaseFragment {
    private MMXChatView mmxChatView;
    private ChatListContract.ChannelNameListener listener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    public void onStart() {
        super.onStart();
        mmxChatView.onStart();
    }

    @Override
    public void onStop() {
        mmxChatView.onStop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mmxChatView.onResume();
    }

    @Override
    public void onPause() {
        mmxChatView.onPause();
        super.onPause();
    }

    @Override
    protected void onCreateFragment(View containerView) {
        Bundle bundle = getArguments();
        MMXChannel channel = BundleHelper.readMMXChannelFromBundle(bundle);


        ArrayList<User> recipients = null;
        if (channel == null) {
            recipients = BundleHelper.readRecipients(bundle);
        }

        if (channel == null && recipients == null) {
            throw new IllegalArgumentException("MMXChannel or List of recipients cannot be null!");
        }

        FrameLayout uiContainer = findView(containerView, R.id.container);
        mmxChatView = ChatSDK.getViewFactory().createMMXChatView(getContext());
        uiContainer.addView(mmxChatView);
        mmxChatView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mmxChatView != null) {
            mmxChatView.setMMXChannel(channel);
        } else if (recipients != null) {
            mmxChatView.setRecipients(recipients);
        }

        setChatNameListener(listener);
    }


    public void setChatNameListener(ChatListContract.ChannelNameListener listener) {
        if (mmxChatView == null) {
            this.listener = listener;
        } else {
            mmxChatView.setChannelNameListener(listener);
            this.listener = null;
        }
    }
}
