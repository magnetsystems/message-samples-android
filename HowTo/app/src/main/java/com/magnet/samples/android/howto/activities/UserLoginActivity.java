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

package com.magnet.samples.android.howto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.util.Logger;

public class UserLoginActivity extends BaseActivity {

    private enum InputError {NO_ERROR, INVALID_USERNAME, INVALID_PASSWORD};

    private TextView currentLoginText;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        loginButton = (Button) findViewById(R.id.userLoginBtn);
        loginButton.setOnClickListener(this);
        logoutButton = (Button) findViewById(R.id.userLogoutBtn);
        logoutButton.setOnClickListener(this);
        currentLoginText = (TextView) findViewById(R.id.userCurrentLogin);
        currentMode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLoginBtn:
                String username = getFieldText(R.id.userUsername);
                String password = getFieldText(R.id.userPassword);
                login(username, password);
                break;
            case R.id.userLogoutBtn:
                logout();
                break;
        }
    }

    private void currentMode() {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            findViewById(R.id.userUsername).setVisibility(View.VISIBLE);
            findViewById(R.id.userPassword).setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            currentLoginText.setText("");
            currentLoginText.setVisibility(View.GONE);
        } else {
            findViewById(R.id.userUsername).setVisibility(View.GONE);
            findViewById(R.id.userPassword).setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            String text = "You are logged in as " + currentUser.getUserName();
            currentLoginText.setText(text);
            currentLoginText.setVisibility(View.VISIBLE);
        }
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

    private void login(String username, String password) {
        InputError inputError = validateCredential(username, password);
        switch (inputError) {
            case INVALID_USERNAME:
                showMessage("Invalid username");
                break;
            case INVALID_PASSWORD:
                showMessage("Invalid password");
                break;
            case NO_ERROR:
                User.login(username, password, false, new ApiCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Logger.debug("login", "success");
                        Max.initModule(MMX.getModule(), new ApiCallback<Boolean>() {
                            @Override
                            public void success(Boolean aBoolean) {
                                Logger.debug("init module", "success");
                                MMX.start();
                                currentMode();
                            }

                            @Override
                            public void failure(ApiError apiError) {
                                showMessage("Can't init module : " + apiError.getMessage());
                                Logger.error("init module.", apiError, "error");
                            }
                        });
                    }

                    @Override
                    public void failure(ApiError apiError) {
                        showMessage("Can't login : " + apiError.getMessage());
                        Logger.error("login", apiError, "error");
                    }
                });
                break;
        }
    }

    private void logout() {
        User.logout(new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                Logger.debug("logout", "success");
                MMX.stop();
                Max.deInitModule(MMX.getModule(), new ApiCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Logger.debug("deInitModule", "success");
                        goToLoginScreen();
                        //currentMode();
                    }

                    @Override
                    public void failure(ApiError apiError) {
                        showMessage("Can't deInit module : " + apiError.getMessage());
                        Logger.error("deInit module", apiError, "error");
                        goToLoginScreen();
                    }

                    private void goToLoginScreen() {
                        Intent i = new Intent(UserLoginActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void failure(ApiError apiError) {
                showMessage("Can't logout : " + apiError.getMessage());
                Logger.error("logout", apiError, "error");
            }
        });
    }

}
