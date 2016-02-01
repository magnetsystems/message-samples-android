package com.magnet.imessage.ui;

import android.os.Bundle;
import android.view.View;

import com.magnet.imessage.R;
import com.magnet.imessage.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UpdateProfileRequest;

public class ChangePasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setOnClickListeners(R.id.changePasswordSubmitBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePasswordSubmitBtn:
                String oldPassword = getFieldText(R.id.changePasswordOld);
                String newPassword = getFieldText(R.id.changePasswordNew);
                String newRepeat = getFieldText(R.id.changePasswordRepeat);
                if (checkStrings(oldPassword, newPassword, newRepeat) && newPassword.equals(newRepeat)) {
                    UpdateProfileRequest.Builder builder = new UpdateProfileRequest.Builder();
                    builder.password(newPassword);
                    User.updateProfile(builder.build(), new ApiCallback<User>() {
                        @Override
                        public void success(User user) {
                            Logger.debug("change password", "success");
                        }

                        @Override
                        public void failure(ApiError apiError) {
                            Logger.error("change password", "errr", apiError);

                        }
                    });
                }
                break;
        }
    }
}
