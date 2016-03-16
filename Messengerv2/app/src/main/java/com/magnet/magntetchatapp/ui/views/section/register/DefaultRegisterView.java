package com.magnet.magntetchatapp.ui.views.section.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.mvp.api.RegisterContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultRegisterPresenter;
import com.magnet.magntetchatapp.mvp.views.AbstractRegisterView;

/**
 * Created by dlernatovich on 3/15/16.
 */
public class DefaultRegisterView extends AbstractRegisterView {
    public DefaultRegisterView(Context context) {
        super(context);
    }

    public DefaultRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultRegisterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public RegisterContract.Presenter getPresenter() {
        return new DefaultRegisterPresenter(this);
    }
}
