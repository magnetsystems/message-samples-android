package com.magnet.magnetchat.mvp.api.abs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.mvp.abs.BaseContract;
import com.magnet.max.android.User;

/**
 * Created by dlernatovich on 3/16/16.
 */
public interface EditProfileContract {

    interface View extends BaseContract.BaseView {

        /**
         * Method which provide the receiving of the activity results
         * (WARNING: Should be always call in the onActivityResult inside the view)
         *
         * @param requestCode request code
         * @param data        data
         */
        void onActivityResult(int requestCode, Intent data);

        /**
         * Method which provide the image choosing from the gallery
         */
        void onChooseImage();

        /**
         * Method which provide the setting up of the user information inside the view
         *
         * @param email     user email
         * @param firstName user first name
         * @param lastName  user last name
         */
        void onSetupUserInformation(@NonNull String email, @NonNull String firstName, @NonNull String lastName);

        /**
         * Method which provide the updating of the user avatar
         */
        void onUpdateUserAvatar(@Nullable User currentUser);

        /**
         * Method which provide to getting of the getting user
         *
         * @return current callback
         */
        @Nullable
        OnEditUserCallback getCallback();

        /**
         * Method which provide the fields verifying
         *
         * @return checking result
         */
        boolean verifyFields();

        /**
         * Method which provide to getting of the first name
         *
         * @return first name
         */
        @NonNull
        String getFirstName();

        /**
         * Method which provide to getting of the last name
         *
         * @return last name
         */
        @NonNull
        String getLastName();

        /**
         * Method which provide to show/hide pregress view
         *
         * @param visible
         */
        void switchProgress(boolean visible);

    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * Method which provide to receiving of the current user
         */
        void onReceiveCurrentUser();

        /**
         * Method which provide the updating of the server avatar
         *
         * @param bitmap   current bitmap
         * @param mimeType myme type
         */
        void updateServerAvatar(@NonNull Bitmap bitmap, @NonNull final String mimeType);

        /**
         * Method which provide the updating of the user profile
         */
        void updateUserProfile();

    }

    interface OnEditUserCallback {
        /**
         * method which provide the action when saving changes is success
         *
         * @param message message
         */
        void onSavedSuccess(@Nullable String message);

        /**
         * method which provide the action when saving changes is error
         *
         * @param message message
         */
        void onSavedError(@NonNull String message);
    }
}
