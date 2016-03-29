package com.magnet.chatsdkcover.mvp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.abs.BasePresenterView;
import com.magnet.chatsdkcover.mvp.api.UsersListContract;
import com.magnet.chatsdkcover.mvp.presenters.DefaultUsersListPresenter;
import com.magnet.chatsdkcover.ui.custom.AdapteredRecyclerView;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public class AbstractUsersListView extends BasePresenterView<UsersListContract.Presenter> implements UsersListContract.View {

    private AdapteredRecyclerView recyclerView;

    public AbstractUsersListView(Context context) {
        super(context);
    }

    public AbstractUsersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractUsersListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public UsersListContract.Presenter getPresenter() {
        return new DefaultUsersListPresenter(this);
    }

    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_users_list_cover;
    }

    /**
     * Method which provide the interface linking
     */
    @Override
    protected void onLinkInterface() {
        recyclerView = (AdapteredRecyclerView) findViewById(R.id.recyclerView);
    }

    /**
     * Method which provide the action when view will create
     */
    @Override
    protected void onCreateView() {

    }

    /**
     * Method which provide to getting of the context inside the View/Activity/Fragment
     *
     * @return current view
     */
    @NonNull
    @Override
    public Context getCurrentContext() {
        return getContext();
    }
}
