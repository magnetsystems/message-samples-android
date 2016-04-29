package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 29.04.16.
 */
public class DefaultMMXChatListView extends MMXChatListView<MMXChatListProperty> {
    private RecyclerView uiRecyclerView;

    public DefaultMMXChatListView(Context context) {
        super(context);
    }

    public DefaultMMXChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return uiRecyclerView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_list;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiRecyclerView = findView(R.id.mmx_recycler_view);
    }

    @Override
    public void setProperties(MMXChatListProperty property) {

    }

    @Override
    public void onChannelName(String name) {

    }

    @Override
    public void onChannelCreationFailure() {

    }
}
