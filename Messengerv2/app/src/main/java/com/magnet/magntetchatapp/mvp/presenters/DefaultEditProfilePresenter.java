package com.magnet.magntetchatapp.mvp.presenters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.magnet.magntetchatapp.mvp.api.EditProfileContract;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

/**
 * Created by dlernatovich on 3/16/16.
 */
public class DefaultEditProfilePresenter implements EditProfileContract.Presenter {

    private final EditProfileContract.View view;
    private User currentUser;

    public DefaultEditProfilePresenter(EditProfileContract.View view) {
        this.view = view;
    }

    /**
     * Method which provide the action when Activity/Fragment call method onCreate
     */
    @Override
    public void onActivityCreate() {
        onReceiveCurrentUser();
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
     * Method which provide to receiving of the current user
     */
    @Override
    public void onReceiveCurrentUser() {
        currentUser = User.getCurrentUser();
        if (currentUser != null) {
            view.onUpdateUserAvatar(currentUser);
            view.onSetupUserInformation(currentUser.getEmail(),
                    currentUser.getFirstName(),
                    currentUser.getLastName());
        }

    }

    /**
     * Method which provide the updating of the server avatar
     *
     * @param bitmap   current bitmap
     * @param mimeType myme type
     */
    @Override
    public void updateServerAvatar(@NonNull Bitmap bitmap, @NonNull String mimeType) {
        if (currentUser != null) {
            currentUser.setAvatar(bitmap, mimeType, updateServerCallback);
        } else {
            if (view != null && view.getCallback() != null) {
                view.getCallback().onSavedError("No user found");
            }
        }
    }

    //CALLBACKS

    /**
     * Callback which provide to listening the server image updating
     */
    private ApiCallback<String> updateServerCallback = new ApiCallback<String>() {
        @Override
        public void success(String s) {
            if (view != null && view.getCallback() != null) {
                view.getCallback().onSavedSucess("Image saved");
            }
        }

        @Override
        public void failure(ApiError apiError) {
            if (view != null && view.getCallback() != null) {
                view.getCallback().onSavedError("Image not saved");
            }
        }
    };
}
