package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;

import java.util.List;

/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXUserListView extends MMXUserListView<MMXUserListProperty> implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout uiRefresher;
    private RecyclerView uiRecyclerView;
    private String factoryName;

    public DefaultMMXUserListView(Context context) {
        super(context);
    }

    public DefaultMMXUserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXUserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected MMXUserListProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXUserListView);
        try {
            MMXUserListProperty prop = new MMXUserListProperty();

            prop.factory_name = arr.getString(R.styleable.DefaultMMXUserListView_factory_name);

            return prop;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXUserListProperty prop) {
        factoryName = prop.factory_name;
    }

    @Override
    protected String getItemViewFactoryByName() {
        return factoryName;
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


    @Override
    protected RecyclerViewTypedAdapter.OnItemCustomEventListener getCustomEventClickListener() {
        return null;
    }

    @Override
    protected RecyclerViewTypedAdapter.OnItemLongClickListener getLongClickListener() {
        return null;
    }

    @Override
    protected RecyclerViewTypedAdapter.OnItemClickListener getItemClickListener() {
        return null;
    }
}
