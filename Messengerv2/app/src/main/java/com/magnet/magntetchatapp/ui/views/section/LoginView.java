package com.magnet.magntetchatapp.ui.views.section;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.mvp.api.LoginContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultLoginPresenter;
import com.magnet.magntetchatapp.mvp.views.AbstractLoginView;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class LoginView extends AbstractLoginView {

    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public LoginContract.Presenter getPresenter() {
        return new DefaultLoginPresenter(this);
    }
}
