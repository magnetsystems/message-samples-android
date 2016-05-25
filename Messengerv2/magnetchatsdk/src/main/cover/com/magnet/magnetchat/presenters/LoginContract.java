package com.magnet.magnetchat.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.presenters.core.BaseContract;

/**
 * Created by dlernatovich on 3/11/16.
 */
public interface LoginContract {

    interface View extends BaseContract.BaseView {
        /**
         * Method which provide the message showing
         */
        void showNotification(@NonNull String message);

        /**
         * Method which provide the field verifying
         *
         * @return checking results
         */
        boolean verifyFields();

        /**
         * Method which provide to show/hide pregress view
         *
         * @param visible
         */
        void switchProgress(boolean visible);

        /**
         * Method which provide to getting of the user name (email)
         *
         * @return email
         */
        @NonNull
        String getEmail();

        /**
         * Method which provide the getting of the password
         *
         * @return current password
         */
        @NonNull
        String getPassword();

        /**
         * Method which provide to getting of the value if user should be remember
         *
         * @return getting value
         */
        boolean getShouldRemember();

        /**
         * Method which provide to getting of the login callback
         *
         * @return login callback
         */
        @Nullable
        OnLoginActionCallback getActionCallback();
    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * Method which provide the login starting
         */
        void startLogIn();

    }

//    CALLBACKS

    /**
     * Interface which provide the action listening inside the Login view
     */
    interface OnLoginActionCallback {
        /**
         * Method which provide the action when login is success
         */
        void onLoginSuccess();

        /**
         * Method which provide the action when login error
         *
         * @param errorMessage error message
         */
        void onLoginError(@NonNull String errorMessage);

        /**
         * Method which provide the action when on register button pressed
         */
        void onRegisterPressed();
    }

}
