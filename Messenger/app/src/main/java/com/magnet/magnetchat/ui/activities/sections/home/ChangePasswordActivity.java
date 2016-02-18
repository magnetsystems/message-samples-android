package com.magnet.magnetchat.ui.activities.sections.home;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.SharedPreferenceManager;
import com.magnet.magnetchat.ui.custom.FEditText;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UpdateProfileRequest;

import butterknife.InjectView;

public class ChangePasswordActivity extends BaseActivity {

    @InjectView(R.id.changePasswordOld)
    AppCompatEditText editOldPassword;
    @InjectView(R.id.changePasswordNew)
    AppCompatEditText editNewPassword;
    @InjectView(R.id.changePasswordRepeat)
    AppCompatEditText editNewPasswordRepeat;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListeners(R.id.changePasswordSubmitBtn);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.changePasswordSubmitBtn:
                setUpdateMode(true);
                String oldPassword = editOldPassword.getText().toString();
                final String newPassword = editNewPassword.getText().toString();
                String newRepeat = editNewPasswordRepeat.getText().toString();
                final String[] credence = SharedPreferenceManager.getInstance().readCredence();
                if (checkStrings(oldPassword, newPassword, newRepeat)) {
                    if (!oldPassword.equals(credence[1])) {
                        showDialog("Wrong password", "You input wrong password. Please, try again");
                        return;
                    }
                    if (!newPassword.equals(newRepeat)) {
                        showDialog("Wrong password", "Passwords do not match. Please, try again");
                        return;
                    }
                    UpdateProfileRequest.Builder builder = new UpdateProfileRequest.Builder();
                    builder.password(newPassword);
                    User.updateProfile(builder.build(), new ApiCallback<User>() {
                        @Override
                        public void success(User user) {
                            setUpdateMode(false);
                            SharedPreferenceManager.getInstance().saveCredence(credence[0], newPassword);
                            showMessage("Password was successfully changed");
                            Logger.debug("change password", "success");
                            finish();
                        }

                        @Override
                        public void failure(ApiError apiError) {
                            setUpdateMode(false);
                            showMessage("Can't change password");
                            Logger.error("change password", apiError);

                        }
                    });
                } else {
                    showDialog(null, "Please, enter all fields");
                }
                break;
        }
    }

    private void showDialog(String title, String message) {
        setUpdateMode(false);
        showInfoDialog(title, message);
    }

    private void setUpdateMode(boolean update) {
        if (update) {
            findViewById(R.id.changePasswordSubmitBtn).setVisibility(View.GONE);
            findViewById(R.id.changePasswordProgress).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.changePasswordSubmitBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.changePasswordProgress).setVisibility(View.GONE);
        }
    }

}
