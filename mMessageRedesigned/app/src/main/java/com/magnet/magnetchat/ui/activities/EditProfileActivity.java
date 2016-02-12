package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.ui.custom.FEditText;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UpdateProfileRequest;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Artli_000 on 11.02.2016.
 */
public class EditProfileActivity extends BaseActivity {
    private final static String TAG = EditProfileActivity.class.getSimpleName();

    private static final int RESULT_LOAD_IMAGE = 1;
    @InjectView(R.id.buttonClose)
    View buttonClose;
    @InjectView(R.id.buttonSaveChanges)
    View buttonSaveChanges;
    @InjectView(R.id.buttonChoosePicture)
    View buttonChoosePicture;

    @InjectView(R.id.textEmail)
    FTextView textEmail;
    @InjectView(R.id.editFirstName)
    FEditText editFirstName;
    @InjectView(R.id.editLastName)
    FEditText editLastName;

    @InjectView(R.id.viewProgress)
    View viewProgress;

    @InjectView(R.id.imageAvatar) CircleImageView imageViewAvatar;

    private User currentUser;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected int getBaseViewID() {
        return R.id.main_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = User.getCurrentUser();

        setOnClickListeners(buttonClose,
                buttonSaveChanges,
                buttonChoosePicture);

        onUserUpdate();

        Glide.with(this).load(User.getCurrentUser().getAvatarUrl()).placeholder(R.mipmap.ic_user).centerCrop().into(imageViewAvatar);
    }

    /**
     * Method which provide the updating UI with accordance to the currentUser information
     */
    private void onUserUpdate() {
        currentUser = User.getCurrentUser();
        if (currentUser != null) {
            textEmail.setText(currentUser.getEmail());
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
        }
    }

    /**
     * Method which provide the save changes with accordance to UI
     */
    private void onSaveChanges() {

        showProgress(true);

        UpdateProfileRequest request = new UpdateProfileRequest.Builder()
                .firstName(editFirstName.getStringValue())
                .lastName(editLastName.getStringValue())
                .build();

        User.updateProfile(request, new ApiCallback<User>() {
            @Override
            public void success(User user) {
                showProgress(false);
                showMessage("You\'ve updated your profile");

                onBackPressed();
            }

            @Override
            public void failure(ApiError apiError) {
                showProgress(false);
                AppLogger.error(this, apiError.toString());
            }
        });
    }

    /**
     * Method which provide the progress showing
     *
     * @param isNeedShowProgress
     */
    private void showProgress(boolean isNeedShowProgress) {
        if (isNeedShowProgress == true) {
            viewProgress.setVisibility(View.VISIBLE);
        } else {
            viewProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.buttonClose:
                onBackPressed();
                break;
            case R.id.buttonSaveChanges:
                onSaveChanges();
                break;
            case R.id.buttonChoosePicture:
                startActivityForResults(IntentHelper.pickImage());
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        showProgress(false);
        //startActivity(HomeActivity.class, true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            setImageBySource(imageViewAvatar, picturePath);

            User.getCurrentUser().setAvatar(((BitmapDrawable) imageViewAvatar.getDrawable()).getBitmap(), null,
                new ApiCallback<String>() {
                    @Override public void success(String s) {
                        Log.d(TAG, "Set user avatar successfuly");
                    }

                    @Override public void failure(ApiError apiError) {
                        Log.e(TAG, "Failed to set user avatar", apiError);
                    }
                });
        }
    }
}
