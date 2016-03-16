package com.magnet.magntetchatapp.ui.activities.section.edit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.api.EditProfileContract;
import com.magnet.magntetchatapp.mvp.views.AbstractEditProfileView;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;

import butterknife.InjectView;

public class EditProfileActivity extends BaseActivity {

    @InjectView(R.id.viewEditProfile)
    AbstractEditProfileView editProfileView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_register;
    }

    @Override
    protected void onCreateActivity() {
        editProfileView.onCreateActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editProfileView.setEditUserCallback(editUserCallback);
        editProfileView.onResumeActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editProfileView.onPauseActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editProfileView.onDestroyActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, Intent data) {
        editProfileView.onActivityResult(requestCode, data);
    }

    /**
     * Callback which provide the monitoring inside the AbstractEditProfileView
     */
    private final EditProfileContract.OnEditUserCallback editUserCallback = new EditProfileContract.OnEditUserCallback() {
        @Override
        public void onSavedSuccess(@Nullable String message) {
            showMessage(message);
        }

        @Override
        public void onSavedError(@NonNull String message) {
            showMessage(message);
        }
    };
}
