package com.magnet.magntetchatapp.mvp.presenters;

import com.magnet.magntetchatapp.mvp.api.SplashContract;
import com.magnet.magntetchatapp.ui.activities.section.login.LoginActivity;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class SplashPresenter implements SplashContract.Presenter {

    private final SplashContract.View view;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the performing of the splash actions
     */
    @Override
    public void onSplashAction() {
        view.navigate(LoginActivity.class);
    }
}
