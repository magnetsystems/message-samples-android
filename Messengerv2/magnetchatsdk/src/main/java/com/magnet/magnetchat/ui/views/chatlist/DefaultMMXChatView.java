package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 04.05.16.
 */
public class DefaultMMXChatView extends MMXChatView<MMXChatProperty> {
    private FrameLayout uiListContainer;
    private FrameLayout uiPostContainer;

    public DefaultMMXChatView(Context context) {
        super(context);
    }

    public DefaultMMXChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected MMXPostMessageView.OnAttachmentSelectListener getAttachmentListener() {
        return null;
    }

    @Override
    protected void onAttachViewToParent(MMXChatListView mmxChatListView, MMXPostMessageView mmxPostMessageView) {
        if (uiListContainer.getChildCount() != 0) uiListContainer.removeAllViews();
        uiListContainer.addView(mmxChatListView);

        if (uiPostContainer.getChildCount() != 0) uiPostContainer.removeAllViews();
        uiPostContainer.addView(mmxPostMessageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiListContainer = findView(baseView, R.id.mmx_chat_list);
        uiPostContainer = findView(baseView, R.id.mmx_chat_post);
    }

    @Override
    public void setProperties(MMXChatProperty property) {

    }
}
