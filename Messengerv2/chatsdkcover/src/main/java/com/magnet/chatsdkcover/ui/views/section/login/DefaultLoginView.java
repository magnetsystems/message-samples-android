package com.magnet.chatsdkcover.ui.views.section.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.chatsdkcover.mvp.api.abs.LoginContract;
import com.magnet.chatsdkcover.mvp.presenters.DefaultLoginPresenter;
import com.magnet.chatsdkcover.mvp.views.AbstractLoginView;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class DefaultLoginView extends AbstractLoginView {

    public DefaultLoginView(Context context) {
        super(context);
    }

    public DefaultLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public LoginContract.Presenter getPresenter() {
        return new DefaultLoginPresenter(this);
    }
}
