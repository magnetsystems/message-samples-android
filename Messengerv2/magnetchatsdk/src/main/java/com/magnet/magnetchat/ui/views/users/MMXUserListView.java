package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private boolean isAddCharactersToView = true;

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
        MMXListItemFactory itemFactory = createItemViewFactory(getItemViewFactoryByName());
        itemFactory = ChatSDK.getMmxListItemFactory();
        adapter = new RecyclerViewTypedAdapter(itemFactory, MMXUserWrapper.class, getItemComparator());

        adapter.setClickListener(getItemClickListener());
        adapter.setLongClickListener(getLongClickListener());
        adapter.setCustomEventListener(getCustomEventClickListener());

        uiRecyclerView = getRecyclerView();
        uiRecyclerView.setLayoutManager(createLayoutManager());
        uiRecyclerView.setAdapter(adapter);
        uiRecyclerView.setRecyclerListener(RV_LISTENER);

        presenter = createUserListPresenter(createUserListPresenterByName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createUserListPresenter(this);
    }

    protected abstract RecyclerViewTypedAdapter.OnItemCustomEventListener getCustomEventClickListener();

    protected abstract RecyclerViewTypedAdapter.OnItemLongClickListener getLongClickListener();

    protected abstract RecyclerViewTypedAdapter.OnItemClickListener getItemClickListener();

    /**
     * return factory name here
     *
     * @return name of the item factory
     */
    protected String getItemViewFactoryByName() {
        return null;
    }

    /**
     * find MMXListItemFactory by name
     *
     * @param name of the item factory
     * @return instance of MMXListItemFactory
     */
    protected MMXListItemFactory createItemViewFactory(@Nullable String name) {
        return null;
    }

    protected String createUserListPresenterByName() {
        return null;
    }

    protected UserListContract.Presenter createUserListPresenter(String name) {
        return null;
    }

    public void onInit(Bundle bundle) {
        presenter.onInit(bundle);
    }

    public void onInit(MMXChannel mmxChannel) {
        onInit(BundleHelper.packChannel(mmxChannel));
    }

    public void search(String term) {
        presenter.search(term);
    }

    protected RecyclerViewTypedAdapter<MMXUserWrapper> getAdapter() {
        return adapter;
    }

    protected UserListContract.Presenter getPresenter() {
        return presenter;
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

    public void setOnUserSelectEventListener(UserListContract.OnSelectUserEvent eventListener) {
        presenter.setSelectUserEvent(eventListener);
    }

    public void setOnGetAllSelectedUsersListener(UserListContract.OnGetAllSelectedUsersListener onGetAllSelectedUsersListener) {
        presenter.setOnGetAllSelectedUsersListener(onGetAllSelectedUsersListener);
    }

    public void doGetSelectedUsersEvent() {
        presenter.doGetAllSelectedUsers();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.onDestroy();
        super.onDetachedFromWindow();
    }

    protected void refresh() {
        presenter.doRefresh();
    }

    public void setAddCharactersToView(boolean addCharactersToView) {
        if (isAddCharactersToView != addCharactersToView) doEnableCharacters();
        isAddCharactersToView = addCharactersToView;
    }

    @Override
    public void onPut(MMXUserWrapper wrapper) {
        int position = adapter.put(wrapper);
        boolean letter = wrapper.isShowLetter();
        if (position == 0) {
            if (!letter) {
                wrapper.setShowLetter(true);
                adapter.notifyItemChanged(position);
            }
        } else {
            MMXUserWrapper prev = adapter.getItem(position - 1);
            if (prev.getFirstLetter().equals(wrapper.getFirstLetter())) {
                wrapper.setShowLetter(false);
            } else {
                wrapper.setShowLetter(true);
            }
            if (letter != wrapper.isShowLetter()) {
                adapter.notifyItemChanged(position);
            }
        }

    }

    @Override
    public void onPut(List<MMXUserWrapper> wrappers) {
        getRecyclerView().setRecyclerListener(null);
        adapter.put(wrappers);
        if (isAddCharactersToView)
            doEnableCharacters();
        getRecyclerView().setRecyclerListener(RV_LISTENER);
    }

    @Override
    public void onDelete(MMXUserWrapper wrapper) {
        adapter.delete(wrapper);
    }

    @Override
    public void onSet(List<MMXUserWrapper> wrapper) {
        getRecyclerView().setRecyclerListener(null);
        adapter.set(wrapper);
        if (isAddCharactersToView)
            doEnableCharacters();
        getRecyclerView().setRecyclerListener(RV_LISTENER);
    }

    private void doEnableCharacters() {
        handler.removeCallbacks(ACTION);
        handler.post(ACTION);
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

    private Runnable ACTION = new Runnable() {
        @Override
        public void run() {
            if (adapter.getItemCount() == 0) return;
            for (int index = 0; index < adapter.getItemCount(); index++) {
                MMXUserWrapper wrapper = adapter.getItem(index);
                boolean letter = wrapper.isShowLetter();
                if (index == 0) {
                    wrapper.setShowLetter(true);
                    if (letter != wrapper.isShowLetter()) adapter.notifyItemChanged(index);
                } else {
                    MMXUserWrapper prev = adapter.getItem(index - 1);
                    if (prev.getFirstLetter().equals(wrapper.getFirstLetter())) {
                        wrapper.setShowLetter(false);
                    } else {
                        wrapper.setShowLetter(true);
                    }
                    if (letter != wrapper.isShowLetter()) adapter.notifyItemChanged(index);
                }
            }
        }
    };

    private final RecyclerView.RecyclerListener RV_LISTENER = new RecyclerView.RecyclerListener() {
        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            presenter.onCurrentPosition(adapter.getItemCount(), holder.getAdapterPosition());
        }
    };

}
