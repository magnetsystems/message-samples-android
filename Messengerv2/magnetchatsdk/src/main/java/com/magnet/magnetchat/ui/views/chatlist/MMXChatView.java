package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;

/**
 * Created by aorehov on 04.05.16.
 */
public abstract class MMXChatView<T extends ViewProperty> extends BaseView<T> implements ChatListContract.MMXChannelListener {

    private MMXPostMessageView mmxPostMessageView;
    private MMXChatListView mmxChatListView;

    public MMXChatView(Context context) {
        super(context);
    }

    public MMXChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        mmxChatListView = ChatSDK.getViewFactory().createMMXChatListView(getContext());
        mmxChatListView.getPresenter().setPresenterChatReceiveListener(this);
        mmxPostMessageView = ChatSDK.getViewFactory().createMMXPostMessageView(getContext());
        mmxPostMessageView.setListener(getAttachmentListener());
        onAttachViewToParent(mmxChatListView, mmxPostMessageView);
    }

    protected abstract MMXPostMessageView.OnAttachmentSelectListener getAttachmentListener();

    public PostMMXMessageContract.Presenter getPostPresenter() {
        return mmxPostMessageView.getPresenter();
    }

    @Override
    public void onStart() {
        super.onStart();
        mmxPostMessageView.onStart();
        mmxChatListView.onStart();
    }

    @Override
    public void onStop() {
        mmxChatListView.onStop();
        mmxPostMessageView.onStop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mmxChatListView.onResume();
        mmxPostMessageView.onResume();
    }

    @Override
    public void onPause() {
        mmxChatListView.onPause();
        mmxPostMessageView.onPause();
        super.onPause();
    }

    @Override
    public void onChannelReceived(MMXChannelWrapper mmxChannel) {
        mmxPostMessageView.getPresenter().setMMXChannel(mmxChannel.getObj().getChannel());
    }

    public void setChannelNameListener(ChatListContract.ChannelNameListener listener) {
        if (mmxChatListView != null)
            mmxChatListView.setChannelNameListener(listener);
    }

    protected abstract void onAttachViewToParent(MMXChatListView mmxChatListView, MMXPostMessageView mmxPostMessageView);

    public void setMMXChannel(ChannelDetail channel) {
        mmxChatListView.getPresenter().setChat(channel);
    }

    public void setMMXChannel(MMXChannel channel) {
        mmxChatListView.getPresenter().setChat(channel);
    }

    public void setRecipients(ArrayList<UserProfile> recipients) {
        mmxChatListView.getPresenter().setChat(recipients);
    }


}

