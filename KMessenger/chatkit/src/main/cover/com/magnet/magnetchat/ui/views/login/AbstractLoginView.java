package com.magnet.magnetchat.ui.views.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.ui.views.abs.BasePresenterView;
import com.magnet.magnetchat.presenters.LoginContract;
import com.magnet.magnetchat.presenters.RegisterContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;

/**
 * Created by dlernatovich on 3/11/16.
 */
public abstract class AbstractLoginView extends BasePresenterView<LoginContract.Presenter> implements LoginContract.View {

    private LoginContract.OnLoginActionCallback loginActionCallback;
    //VARIABLES
    private RegisterContract.OnRegisterActionCallback registerActionCallback;

    private LoginContract.Presenter presenter;

    public AbstractLoginView(Context context) {
        super(context);
    }

    public AbstractLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the action when view will create
     */
    @Override
    protected void onCreateView() {
        presenter = createPresenterByName(getPresenterName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createLoginPresenter(this);
    }

    protected abstract String getPresenterName();

    private LoginContract.Presenter createPresenterByName(String presenterName) {
        MMXPresenterFactory factory = ChatSDK.getMMXFactotyByName(presenterName);
        return factory != null ? factory.createLoginPresenter(this) : null;
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public LoginContract.Presenter getPresenter() {
        return presenter;
    }

    //=================| MVP |=================

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
     * Method which provide the message showing
     */
    @Override
    public void showNotification(@NonNull String message) {
        showMessage(message);
    }


    protected void startLogin() {
        presenter.startLogIn();
    }

    /**
     * Method which provide the setting of the login callback
     *
     * @param loginActionCallback
     */
    public void setLoginActionCallback(LoginContract.OnLoginActionCallback loginActionCallback) {
        this.loginActionCallback = loginActionCallback;
    }

    public LoginContract.OnLoginActionCallback getLoginActionCallback() {
        return loginActionCallback;
    }


    /**
     * Method which provide to getting of the login callback
     *
     * @return login callback
     */
    @Nullable
    @Override
    public LoginContract.OnLoginActionCallback getActionCallback() {
        return loginActionCallback;
    }

}
