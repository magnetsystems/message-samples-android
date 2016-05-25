package com.magnet.magnetchat.presenters.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.presenters.LoginContract;
import com.magnet.max.android.ApiError;
import com.magnet.mmx.client.api.MMX;

import java.net.SocketTimeoutException;

/**
 * Created by dlernatovich on 3/11/16.
 */
class DefaultLoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "DefaultLoginPresenter";

    private final LoginContract.View view;

    /**
     * Constructor
     *
     * @param view
     */
    public DefaultLoginPresenter(@NonNull LoginContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
        view.switchProgress(false);
    }

    /**
     * Method which provide the action when Activity/Fragment call method onResume
     */
    @Override
    public void onActivityResume() {

    }

    /**
     * Method which provide the action when Activity/Fragment call method onPauseActivity
     */
    @Override
    public void onActivityPause() {

    }

    /**
     * Method which provide the action when Activity/Fragment call method onDestroy
     */
    @Override
    public void onActivityDestroy() {
    }

    /**
     * Method which provide the login starting
     */
    @Override
    public void startLogIn() {
        if (InternetConnectionManager.getInstance().isAnyConnectionAvailable()) {
            if (view.verifyFields() == true) {
                final String userName = view.getEmail();
                final String password = view.getPassword();
                boolean isShouldRemember = view.getShouldRemember();
                view.switchProgress(true);
                UserHelper.login(userName, password, isShouldRemember, loginListener);
            }
        } else {
            if (view.getActionCallback() != null) {
                view.getActionCallback().onLoginError("No internet connection");
            }
        }

    }

    /**
     * Listener which provide the watchdog for the logining functional
     */
    private final UserHelper.OnLoginListener loginListener = new UserHelper.OnLoginListener() {
        @Override
        public void onSuccess() {
            if (view != null) {
                view.switchProgress(false);
                if (view.getActionCallback() != null) {
                    view.getActionCallback().onLoginSuccess();
                }
            }
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            try {
                Log.e(TAG, apiError.toString());
                if (view != null) {
                    view.switchProgress(false);
                    String errorMessage;
                    if (apiError.getMessage().contains(MMX.FailureCode.BAD_REQUEST.getDescription())) {
                        errorMessage = "A bad request submitted to the server.";
                    } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_AUTH_FAILED.getDescription())) {
                        errorMessage = "Server authentication failure.";
                    } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_CONCURRENT_LOGIN.getDescription())) {
                        errorMessage = "Concurrent logins are attempted.";
                    } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_ERROR.getDescription())) {
                        errorMessage = "A client error.";
                    } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_ERROR.getDescription())) {
                        errorMessage = "A server error.";
                    } else if (apiError.getMessage().contains(MMX.FailureCode.SERVICE_UNAVAILABLE.getDescription())) {
                        errorMessage = "Service is not available due to network or server issue.";
                    } else if (null != apiError.getCause() && apiError.getCause() instanceof SocketTimeoutException) {
                        errorMessage = "Request timeout. Please check network.";
                    } else {
                        errorMessage = "Unknown login error. Please try again.";
                    }

                    if (view.getActionCallback() != null) {
                        view.getActionCallback().onLoginError(errorMessage);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };
}
