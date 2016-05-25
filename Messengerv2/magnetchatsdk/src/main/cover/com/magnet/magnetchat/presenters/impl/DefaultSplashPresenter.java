package com.magnet.magnetchat.presenters.impl;

import android.util.Log;

import com.magnet.magnetchat.presenters.SplashContract;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class DefaultSplashPresenter implements SplashContract.Presenter {

    private static final String TAG = "DefaultSplashPresenter";

    private final SplashContract.View view;

    public DefaultSplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the performing of the splash actions
     */
    @Override
    public void onSplashAction() {
        Log.e(TAG, "Start splash action");
        if (User.getSessionStatus() == null) {
            Log.e(TAG, "User.getSessionStatus() == null");
            navigateToLogin();
            return;
        }

        Log.d("SessionStatus", User.getSessionStatus().toString());

        if (User.SessionStatus.LoggedIn == User.getSessionStatus()) {
            User.resumeSession(callbackResumeSession);
        } else if (User.SessionStatus.CanResume == User.getSessionStatus()) {
            User.resumeSession(callbackResumeSession);
        } else {
            navigateToLogin();
        }
    }

    /**
     * Method which provide the navigation to the login screen
     */
    @Override
    public void navigateToLogin() {
        if (view != null) {
            view.navigate(SplashContract.NavigationType.LOGIN);
        }
    }

    /**
     * Method which provide the navigation to the home screen
     */
    @Override
    public void navigateToHome() {
        if (view != null) {
            view.navigate(SplashContract.NavigationType.HOME);
        }
    }

    /**
     * Callback which provide to listening of the resume session action
     */
    private final ApiCallback<Boolean> callbackResumeSession = new ApiCallback<Boolean>() {
        @Override
        public void success(Boolean aBoolean) {
            if (aBoolean) {
                MMX.start();
                navigateToHome();
            } else {
                handleError("");
            }
        }

        @Override
        public void failure(ApiError apiError) {
            handleError(apiError.getMessage());
        }

        private void handleError(String errorMessage) {
            navigateToLogin();
        }
    };
}
