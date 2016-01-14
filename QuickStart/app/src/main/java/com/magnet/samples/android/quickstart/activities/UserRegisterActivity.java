/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.quickstart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UserRegistrationInfo;
import com.magnet.samples.android.quickstart.R;
import com.magnet.samples.android.quickstart.util.Logger;

public class UserRegisterActivity extends BaseActivity {

    private enum InputError {NO_ERROR, INVALID_USERNAME, INVALID_PASSWORD};

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        findViewById(R.id.registerOkBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerOkBtn:
                String username = getFieldText(R.id.registerUsername);
                String password = getFieldText(R.id.registerPassword);
                register(username, password);
                break;
        }
    }

    @Override
    protected void onPause() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPause();
    }

    private InputError validateCredential(String username, String password) {
        if (username == null || username.isEmpty()) {
            return InputError.INVALID_USERNAME;
        }
        if (password == null || password.isEmpty()) {
            return InputError.INVALID_PASSWORD;
        }
        return InputError.NO_ERROR;
    }

    private void register(final String username, final String password) {
        InputError inputError = validateCredential(username, password);
        switch (inputError) {
            case INVALID_USERNAME:
                showMessage("Invalid username");
                break;
            case INVALID_PASSWORD:
                showMessage("Invalid password");
                break;
            case NO_ERROR:
                UserRegistrationInfo.Builder builder = new UserRegistrationInfo.Builder();
                builder.userName(username).password(password);
                User.register(builder.build(), new ApiCallback<User>() {
                    @Override
                    public void success(User user) {
                        Logger.debug("register", "success");
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserRegisterActivity.this);
                        dialogBuilder.setTitle("User created successfully").setCancelable(false);
                        dialogBuilder.setMessage(String.format("Credential: (%s)/(%s)", username, password));
                        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog = dialogBuilder.show();
                    }

                    @Override
                    public void failure(ApiError apiError) {
                        if (apiError.getKind() == 409) {
                            showMessage("User already exists");
                        } else {
                            showMessage("Can't register : " + apiError.getMessage());
                            Logger.error("register", apiError, "error");
                        }
                    }
                });
                break;
        }
    }

}
