package com.magnet.magnetchat.ui.views.section.channels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magnetchat.mvp.api.abs.ChannelsListContract;
import com.magnet.magnetchat.mvp.presenters.DefaultChannelsPresenter;
import com.magnet.magnetchat.mvp.views.AbstractChannelsView;

/**
 * Created by dlernatovich on 3/24/16.
 */
public class DefaultChannelsView extends AbstractChannelsView {

    public DefaultChannelsView(Context context) {
        super(context);
    }

    public DefaultChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public ChannelsListContract.Presenter getPresenter() {
        return new DefaultChannelsPresenter(this);
    }
}
