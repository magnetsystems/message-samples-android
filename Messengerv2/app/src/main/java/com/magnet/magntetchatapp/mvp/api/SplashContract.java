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

    }
}
