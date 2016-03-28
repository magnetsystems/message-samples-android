package com.magnet.chatsdkcover.mvp.api;

/**
 * Created by dlernatovich on 3/11/16.
 */
public interface SplashContract {

    /**
     * Navigation types
     */
    enum NavigationType {
        HOME,
        LOGIN
    }

    interface View {
        /**
         * Method which provide to performing of the navigation
         *
         * @param navigationType navigation type
         */
        void navigate(NavigationType navigationType);
    }

    interface Presenter {

        /**
         * Method which provide the performing of the splash actions
         */
        void onSplashAction();

        /**
         * Method which provide the navigation to the login screen
         */
        void navigateToLogin();

        /**
         * Method which provide the navigation to the home screen
         */
        void navigateToHome();
    }

}
