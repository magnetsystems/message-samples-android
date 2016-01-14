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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.magnet.samples.android.quickstart.R;

public class UserManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        findViewById(R.id.managementRegisterBtn).setOnClickListener(this);
        findViewById(R.id.managementLoginBtn).setOnClickListener(this);
        findViewById(R.id.managementSearchBtn).setOnClickListener(this);
        findViewById(R.id.managementRetrieveBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.managementRegisterBtn:
                startActivity(new Intent(this, UserRegisterActivity.class));
                break;
            case R.id.managementLoginBtn:
                startActivity(new Intent(this, UserLoginActivity.class));
                break;
            case R.id.managementSearchBtn:
                startActivity(new Intent(this, UserSearchActivity.class));
                break;
            case R.id.managementRetrieveBtn:
                startActivity(new Intent(this, UserRetrieveActivity.class));
                break;
            default:
                break;
        }
    }
}
