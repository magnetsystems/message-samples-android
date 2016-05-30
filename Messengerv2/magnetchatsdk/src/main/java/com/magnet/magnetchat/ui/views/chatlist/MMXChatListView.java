package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.magnetchat.util.Logger;

import java.util.List;

/**
 * Created by aorehov on 29.04.16.
 */
public abstract class MMXChatListView<T extends ViewProperty> extends BaseView<T> implements ChatListContract.View {
    private ChatListContract.Presenter presenter;
    private RecyclerViewTypedAdapter<MMXMessageWrapper> adapter;
    private RecyclerView.RecyclerListener clientCallback;
    private ChatListContract.ChannelNameListener listener;


    public MMXChatListView(Context context) {
        super(context);
    }

    public MMXChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        MMXListItemFactory factory = createMMXListItemFactory(getMMXListFactoryName());
        if (factory == null) factory = ChatSDK.getMmxListItemFactory();

        RecyclerView uiRecyclerView = getRecyclerView();
        adapter = new RecyclerViewTypedAdapter<>(factory, MMXMessageWrapper.class, itemComparator());
        uiRecyclerView.setAdapter(adapter);
        uiRecyclerView.setLayoutManager(createLayoutManager());

        presenter = createChatPresenter(getMMXChatPresenterName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createChatPresenter(getContext(), this);

    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        manager.setReverseLayout(true);
        return manager;
    }

    protected abstract RecyclerView getRecyclerView();

    private String getMMXChatPresenterName() {
        return null;
    }

    private String getMMXListFactoryName() {
        return null;
    }


    protected MMXListItemFactory createMMXListItemFactory(String name) {
        return null;
    }

    protected ChatListContract.Presenter createChatPresenter(String name) {
        return null;
    }

    protected RecyclerViewTypedAdapter.ItemComparator<MMXMessageWrapper> itemComparator() {
        return MMXMessageWrapper.COMPARATOR;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.onCreate();
        getRecyclerView().setRecyclerListener(recyclerCallback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
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

    @Override
    public void onSetMessage(List<MMXMessageWrapper> messages) {
        adapter.set(messages);
    }

    @Override
    public void onPutMessage(List<MMXMessageWrapper> messages) {
        adapter.put(messages);
    }

    @Override
    public void onPutMessage(MMXMessageWrapper message, boolean isNeedScroll) {
        int position = adapter.put(message);
        if (position == 0 && isNeedScroll)
            getRecyclerView().scrollToPosition(position);
    }

    @Override
    public void onDelete(MMXMessageWrapper message) {
        adapter.delete(message);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    public void setRecyclerListener(RecyclerView.RecyclerListener callback) {
        this.clientCallback = callback;
    }

    @Override
    public void setChannelNameListener(ChatListContract.ChannelNameListener channelNameListener) {
        this.listener = channelNameListener;
        onChannelName(presenter.getChannelName());
    }

    @Override
    public void onChannelName(String name) {
        if (listener != null)
            listener.onName(name);
    }

    public ChatListContract.Presenter getPresenter() {
        return presenter;
    }

    private final RecyclerView.RecyclerListener recyclerCallback = new RecyclerView.RecyclerListener() {
        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            presenter.onScrolledTo(holder.getAdapterPosition(), adapter.getItemCount());
            if (clientCallback != null) clientCallback.onViewRecycled(holder);
        }
    };

    public void onCreatedPoll() {
        presenter.onCreatedPoll();
    }
}
