package com.magnet.chatsdkcover.mvp.presenters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magnet.chatsdkcover.mvp.api.EditProfileContract;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UpdateProfileRequest;

/**
 * Created by dlernatovich on 3/16/16.
 */
public class DefaultEditProfilePresenter implements EditProfileContract.Presenter {

    private static final String TAG = "DefaultEditProfilePresenter";

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
                view.getCallback().onSavedError("User not found");
            }
        }
    }

    /**
     * Method which provide the updating of the user profile
     */
    @Override
    public void updateUserProfile() {
        if (view.verifyFields() == true) {
            view.switchProgress(true);
            UpdateProfileRequest request = new UpdateProfileRequest.Builder()
                    .firstName(view.getFirstName())
                    .lastName(view.getLastName())
                    .build();

            User.updateProfile(request, userCallback);
        }
    }

    //CALLBACKS

    /**
     * Callback which provide to listening the server image updating
     */
    private final ApiCallback<String> updateServerCallback = new ApiCallback<String>() {
        @Override
        public void success(String s) {
            if (view != null) {
                view.switchProgress(false);
                if (view.getCallback() != null) {
                    view.getCallback().onSavedSuccess("Avatar updated");
                }
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void failure(ApiError apiError) {
            Log.e(TAG, apiError.toString());
            if (view != null) {
                view.switchProgress(false);
                if (view.getCallback() != null) {
                    view.getCallback().onSavedError("Avatar not updated. Please try again.");
                }
            }
        }
    };

    /**
     * Callback which provide to the user updating listening
     */
    private final ApiCallback<User> userCallback = new ApiCallback<User>() {
        @Override
        public void success(User user) {
            if (view != null) {
                view.switchProgress(false);
                if (view.getCallback() != null) {
                    view.getCallback().onSavedSuccess("Profile updated successfully");
                }
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void failure(ApiError apiError) {
            Log.e(TAG, apiError.toString());
            if (view != null) {
                view.switchProgress(false);
                if (view.getCallback() != null) {
                    view.getCallback().onSavedSuccess("Profile didn't update");
                }
            }
        }
    };

//    /**
//     * Async task which provide to clear of the Glide cache
//     */
//    private final AsyncTask<Context, Void, Void> removeCacheAssyncTask = new AsyncTask<Context, Void, Void>() {
//        @Override
//        protected Void doInBackground(Context... params) {
//
//            return null;
//        }
//    };
}
