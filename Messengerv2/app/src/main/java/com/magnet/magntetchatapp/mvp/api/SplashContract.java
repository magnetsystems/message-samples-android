package com.magnet.magntetchatapp.mvp.api;

/**
 * Created by dlernatovich on 3/11/16.
 */
public interface SplashContract {

    interface View {

        /**
         * Method which provide to performing of the navigation
         *
         * @param activityClass activity class
         */
        void navigate(Class activityClass);
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

        /**
         * Method which provide the creating of the template data
         */
        void onCreateTemplateData();

    }
}
