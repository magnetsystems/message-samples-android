package com.magnet.magnetchat.mvp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.mvp.abs.BasePresenterView;
import com.magnet.magnetchat.mvp.api.abs.LoginContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;

/**
 * Created by dlernatovich on 3/11/16.
 */
public abstract class AbstractLoginView extends BasePresenterView<LoginContract.Presenter> implements LoginContract.View {

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

}
