package com.magnet.magntetchatapp.mvp.abs;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by dlernatovich on 3/14/16.
 */
public interface BaseContract {
    interface BaseView {

        /**
         * Method which provide to getting of the context inside the View/Activity/Fragment
         *
         * @return current view
         */
        @NonNull
        Context getCurrentContext();

    }

    interface BasePresenter {

        /**
         * Method which provide the action when Activity/Fragment call method onCreate
         */
        void onActivityCreate();

        /**
         * Method which provide the action when Activity/Fragment call method onResume
         */
        void onActivityResume();

        /**
         * Method which provide the action when Activity/Fragment call method onPauseActivity
         */
        void onActivityPause();

        /**
         * Method which provide the action when Activity/Fragment call method onDestroy
         */
        void onActivityDestroy();

    }

}
