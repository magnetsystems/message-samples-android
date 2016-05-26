package com.magnet.magnetchat.ui.views.channels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.ui.views.abs.BasePresenterView;
import com.magnet.magnetchat.presenters.ChannelsListContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public abstract class AbstractChannelsView extends BasePresenterView<ChannelsListContract.Presenter> implements ChannelsListContract.View {

    private static final String TAG = "AbstractChannelsView";

    public AbstractChannelsView(Context context) {
        super(context);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void doForceLoadAction() {
        presenter.startChannelReceiving(0);
    }


    @Override
    protected void onCreateView() {
        presenter = createPresenterByName(getPresenterName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createChannelListPresenter(this);
    }

    protected ChannelsListContract.Presenter createPresenterByName(String factoryName) {
        MMXPresenterFactory factory = ChatSDK.getMMXFactotyByName(factoryName);
        return factory != null ? factory.createChannelListPresenter(this) : null;
    }

    protected abstract String getPresenterName();

    @NonNull
    @Override
    public ChannelsListContract.Presenter getPresenter() {
        return presenter;
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
     * Method which provide the clearing filter
     */
    @Override
    public void clearFilter() {
        filterChannels(null);
    }

    /**
     * Method which provide the setting of the channel list callback
     *
     * @param channelListCallback channel list callback
     */
    public abstract void setChannelListCallback(ChannelsListContract.OnChannelsListCallback channelListCallback);

    /**
     * Method which provide the setting of the OnChannelsViewCallback
     *
     * @param channelsViewCallback object channels view callback
     */
    public abstract void setChannelsViewCallback(ChannelsListContract.OnChannelsViewCallback channelsViewCallback);

}


