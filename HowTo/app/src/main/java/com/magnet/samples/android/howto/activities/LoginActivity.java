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
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UserRegistrationInfo;
import com.magnet.mmx.client.api.MMX;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.util.Logger;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loginRegisterBtn).setOnClickListener(this);
        findViewById(R.id.loginSingInBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = getFieldText(R.id.loginUsername);
        String password = getFieldText(R.id.loginPassword);
        if (username != null && password != null) {
            switch (v.getId()) {
                case R.id.loginRegisterBtn:
                    registerAndLogin(username, password);
                    break;
                case R.id.loginSingInBtn:
                    login(username, password);
                    break;
                default:
                    break;
            }
        }
    }

    private void login(String username, String password) {
        User.login(username, password, false, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                Logger.debug("login", "success");
                MMX.start();
                switchToFeatures();
            }

            @Override
            public void failure(ApiError apiError) {
                showMessage("Can't login : " + apiError.getMessage());
                Logger.error("login", apiError, "error");
            }
        });
    }

    private void registerAndLogin(final String username, final String password) {
        UserRegistrationInfo.Builder builder = new UserRegistrationInfo.Builder().userName(username).password(password);
        User.register(builder.build(), new ApiCallback<User>() {
            @Override
            public void success(User user) {
                Logger.debug("register", "success");
                login(username, password);
            }

            @Override
            public void failure(ApiError apiError) {
                if (apiError.getKind() == 409) {
                    login(username, password);
                    showMessage("User already exists, login now");
                } else {
                    showMessage("Can't register : " + apiError.getMessage());
                    Logger.error("register", apiError, "error");
                }
            }
        });
    }

    private void switchToFeatures() {
        startActivity(new Intent(this, FeaturesActivity.class));
    }

}
