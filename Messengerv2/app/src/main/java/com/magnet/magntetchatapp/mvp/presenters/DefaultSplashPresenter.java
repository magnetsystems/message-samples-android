package com.magnet.magntetchatapp.mvp.presenters;

import android.util.Log;

import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magntetchatapp.mvp.api.SplashContract;
import com.magnet.magntetchatapp.ui.activities.section.home.HomeActivity;
import com.magnet.magntetchatapp.ui.activities.section.login.LoginActivity;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class DefaultSplashPresenter implements SplashContract.Presenter {

    private final SplashContract.View view;

    public DefaultSplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the performing of the splash actions
     */
    @Override
    public void onSplashAction() {
        if (User.getSessionStatus() == null) {
            navigateToLogin();
            return;
        }

        Log.d("SessionStatus", User.getSessionStatus().toString());

        if (User.SessionStatus.LoggedIn == User.getSessionStatus()) {
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
            view.navigate(LoginActivity.class);
        }
    }

    /**
     * Method which provide the navigation to the home screen
     */
    @Override
    public void navigateToHome() {
        if (view != null) {
            view.navigate(HomeActivity.class);
        }
    }

    /**
     * Method which provide the creating of the template data
     */
    @Override
    public void onCreateTemplateData() {
        List<String> userIDs = new ArrayList<String>(Arrays.asList(User.getCurrentUserId()));
        for (int i = 0; i < 10; i++) {
            ChannelHelper.createChannelForUsers(userIDs, new ChannelHelper.OnCreateChannelListener() {
                @Override
                public void onSuccessCreated(MMXChannel channel) {

                }

                @Override
                public void onChannelExists(MMXChannel channel) {

                }

                @Override
                public void onFailureCreated(Throwable throwable) {

                }
            });
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
                onCreateTemplateData();
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
