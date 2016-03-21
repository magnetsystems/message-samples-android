package com.magnet.magntetchatapp.mvp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BasePresenterView;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultChannelsPresenter;
import com.magnet.magntetchatapp.ui.custom.AdapteredRecyclerView;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class AbstractChannelsView extends BasePresenterView<ChannelsListContract.Presenter> implements ChannelsListContract.View {


    @InjectView(R.id.listChannels)
    AdapteredRecyclerView recyclerView;

    public AbstractChannelsView(Context context) {
        super(context);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public ChannelsListContract.Presenter getPresenter() {
        return new DefaultChannelsPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_channels;
    }

    @Override
    protected void onCreateView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getCurrentContext(), 1));
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

    /**
     * Method which provide to add the channels to current list object
     *
     * @param objects current objects
     */
    @Override
    public void addChannels(@NonNull List<ChannelsListContract.ChannelObject> objects) {
        recyclerView.addList(objects);
    }

    /**
     * Method which provide to set the channels to current list object
     *
     * @param objects current objects
     */
    @Override
    public void setChannels(@NonNull List<ChannelsListContract.ChannelObject> objects) {
        recyclerView.updateList(objects);
    }

    /**
     * Method which provide to channels clearing
     */
    @Override
    public void clearChannels() {
        recyclerView.clearList();
    }

}
