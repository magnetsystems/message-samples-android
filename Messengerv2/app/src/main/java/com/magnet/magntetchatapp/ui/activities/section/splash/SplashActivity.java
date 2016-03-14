package com.magnet.magntetchatapp.ui.activities.section.splash;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.api.SplashContract;
import com.magnet.magntetchatapp.mvp.presenters.SplashPresenter;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;

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
        presenter = new SplashPresenter(this);
        runOnMainThread(2, splashAction);
    }

    /**
     * Method which provide to performing of the navigation
     *
     * @param activityClass activity class
     */
    @Override
    public void navigate(Class activityClass) {
        startActivity(activityClass, true);
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
