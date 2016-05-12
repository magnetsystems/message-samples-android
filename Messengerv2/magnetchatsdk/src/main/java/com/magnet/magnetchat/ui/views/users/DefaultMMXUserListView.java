package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXUserListView extends MMXUserListView<MMXUserListProperty> implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout uiRefresher;
    private RecyclerView uiRecyclerView;

    public DefaultMMXUserListView(Context context) {
        super(context);
    }

    public DefaultMMXUserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXUserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    protected RecyclerView getRecyclerView() {
        return uiRecyclerView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_list_users;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiRefresher = findView(baseView, R.id.mmx_refresher);
        uiRecyclerView = findView(baseView, R.id.mmx_recycler_view);
        uiRefresher.setOnRefreshListener(this);
    }

    @Override
    public void setProperties(MMXUserListProperty property) {

    }

    @Override
    public void onLoading() {
        uiRefresher.setRefreshing(true);
    }

    @Override
    public void onLoadingComplete() {
        uiRefresher.setRefreshing(false);
    }

    @Override
    public void onCantLoadChannel() {

    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
