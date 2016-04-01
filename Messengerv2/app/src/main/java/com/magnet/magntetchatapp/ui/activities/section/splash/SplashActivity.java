package com.magnet.magntetchatapp.ui.activities.section.splash;

import com.magnet.chatsdkcover.mvp.api.abs.SplashContract;
import com.magnet.chatsdkcover.mvp.presenters.DefaultSplashPresenter;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;
import com.magnet.magntetchatapp.ui.activities.section.home.HomeActivity;
import com.magnet.magntetchatapp.ui.activities.section.login.LoginActivity;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    private SplashContract.Presenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getMenuId() {
        return NONE_MENU;
    }

    @Override
    protected void onCreateActivity() {
        presenter = new DefaultSplashPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnMainThread(2, splashAction);
    }

    /**
     * Method which provide to performing of the navigation
     *
     * @param navigationType navigation type
     */
    @Override
    public void navigate(SplashContract.NavigationType navigationType) {
        switch (navigationType) {
            case HOME:
                startActivityWithClearTop(HomeActivity.class);
                break;
            case LOGIN:
                startActivityWithClearTop(LoginActivity.class);
                break;
            default:
                break;
        }

    }

    /**
     * Callback which provide the action after delay
     */
    private final OnActionPerformer splashAction = new OnActionPerformer() {
        @Override
        public void onActionPerform() {
            if (presenter != null) {
                presenter.onSplashAction();
            }
        }
    };


}
