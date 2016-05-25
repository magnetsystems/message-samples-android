package com.magnet.magnetchat.mvp.api.abs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.mvp.abs.BaseContract;

/**
 * Created by dlernatovich on 3/15/16.
 */
public interface RegisterContract {

    interface View extends BaseContract.BaseView {

        /**
         * Method which provide to getting of the OnRegisterActionCallback from the view
         *
         * @return current callback
         */
        @Nullable
        OnRegisterActionCallback getRegisterCallback();

        /**
         * Method which provide the fields verifying
         *
         * @return checking result
         */
        boolean verifyFields();

        /**
         * Method which provide the getting of the first name
         *
         * @return current first name
         */
        @NonNull
        String getFirstName();

        /**
         * Method which provide the getting of the last name
         *
         * @return current last name
         */
        @NonNull
        String getLastName();

        /**
         * Method which provide the getting of the password
         *
         * @return current password
         */
        @NonNull
        String getPassword();

        /**
         * Method which provide the getting of the email
         *
         * @return current email
         */
        @NonNull
        String getEmail();

        /**
         * Method which provide the progress switching
         *
         * @param isNeedProgress is need progress
         */
        void switchProgress(boolean isNeedProgress);

    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * Method which provide the register starting
         */
        void startRegister();

    }

    /**
     * Callback which provide the listening actions inside the AbstractRegisterView
     */
    interface OnRegisterActionCallback {
        /**
         * Method which provide the action when register is success
         */
        void onRegisterSuccess();

        /**
         * Method which provide the action when register have error
         *
         * @param message error message
         */
        void onRegisterError(@NonNull String message);

    }

}
