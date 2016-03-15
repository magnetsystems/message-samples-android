package com.magnet.magntetchatapp.mvp.presenters;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.MagnetError;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.magntetchatapp.mvp.api.RegisterContract;
import com.magnet.max.android.ApiError;

/**
 * Created by dlernatovich on 3/15/16.
 */
public class DefaultRegisterPresenter implements RegisterContract.Presenter {

    private final RegisterContract.View view;

    public DefaultRegisterPresenter(RegisterContract.View view) {
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
     * Method which provide the register starting
     */
    @Override
    public void startRegister() {
        if (view.verifyFields() == true) {
            view.switchProgress(true);
            UserHelper.register(view.getFirstName(), view.getLastName(), view.getEmail(), view.getPassword(), onRegisterListener);
        }
    }

    /**
     * Callback which provide the register process listening
     */
    private final UserHelper.OnRegisterListener onRegisterListener = new UserHelper.OnRegisterListener() {
        @Override
        public void onFailedRegistration(ApiError apiError) {
            makeErrorMessage(apiError);
        }

        @Override
        public void onSuccess() {
            if (view != null) {
                view.switchProgress(false);
                if (view.getRegisterCallback() != null) {
                    view.getRegisterCallback().onRegisterSuccess();
                }
            }
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            makeErrorMessage(apiError);
        }

        /**
         * Method which provide the make error message form the ApiError class
         * @param apiError current error
         */
        private void makeErrorMessage(@Nullable ApiError apiError) {
            String message = "Error while user creating.";
            if (apiError != null) {
                try {
                    MagnetError error = new Gson().fromJson(apiError.getMessage(), MagnetError.class);
                    if (error != null) {
                        message = error.getErrorMessage();
                    }
                } catch (Exception ex) {
                    AppLogger.error(this, ex.toString());
                }
            }

            if (view != null) {
                view.switchProgress(false);
                if (view.getRegisterCallback() != null) {
                    view.getRegisterCallback().onRegisterError(message);
                }
            }
        }
    };
}
