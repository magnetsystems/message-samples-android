package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;

/**
 * Created by aorehov on 12.05.16.
 */
public abstract class MMXUserListView<T extends ViewProperty> extends BaseView<T> implements UserListContract.View {

    private RecyclerView uiRecyclerView;
    private RecyclerViewTypedAdapter<MMXUserWrapper> adapter;
    private UserListContract.Presenter presenter;
    private Handler handler = new Handler(Looper.myLooper());

    public MMXUserListView(Context context) {
        super(context);
    }

    public MMXUserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXUserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        MMXListItemFactory itemFactory = ChatSDK.getMmxListItemFactory();
        adapter = new RecyclerViewTypedAdapter(itemFactory, MMXUserWrapper.class, getItemComparator());

        uiRecyclerView = getRecyclerView();
        uiRecyclerView.setLayoutManager(createLayoutManager());
        uiRecyclerView.setAdapter(adapter);

        presenter = ChatSDK.getPresenterFactory().createUserListPresenter(this);
    }

    public void onInit(Bundle bundle) {
        presenter.onInit(bundle);
    }

    public void onInit(MMXChannel mmxChannel) {
        onInit(BundleHelper.packChannel(mmxChannel));
    }

    public void onInit(Chat chat) {
        ChatManager.getInstance().addConversation(chat);
        onInit(chat.getChannel());
    }

    @NonNull
    protected RecyclerViewTypedAdapter.ItemComparator<MMXUserWrapper> getItemComparator() {
        return MMXUserWrapper.ITEM_COMPARATOR;
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getContext(), 1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.onCreate();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResumed();
    }

    @Override
    public void onPause() {
        presenter.onPaused();
        super.onPause();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.onDestroy();
        super.onDetachedFromWindow();
    }

    protected void refresh() {
        presenter.doRefresh();
    }

    @Override
    public void onPut(MMXUserWrapper wrapper) {
        adapter.put(wrapper);
    }

    @Override
    public void onDelete(MMXUserWrapper wrapper) {
        adapter.delete(wrapper);
    }

    @Override
    public void onSet(List<MMXUserWrapper> wrapper) {
        adapter.set(wrapper);
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

    @NonNull
    protected abstract RecyclerView getRecyclerView();

}
