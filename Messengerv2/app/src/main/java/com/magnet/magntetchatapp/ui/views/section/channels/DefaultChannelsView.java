package com.magnet.magntetchatapp.ui.views.section.channels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultChannelsPresenter;
import com.magnet.magntetchatapp.mvp.views.AbstractChannelsView;

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
