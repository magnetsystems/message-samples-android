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

/**
 * For complete feature list, @see <a href="https://developer.magnet.com/docs/message/overview/features/index.html">Features</a>
 */
public class FeaturesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        findViewById(R.id.featureChatBtn).setOnClickListener(this);
        findViewById(R.id.featurePublishBtn).setOnClickListener(this);
        findViewById(R.id.featureManagementBtn).setOnClickListener(this);
        findViewById(R.id.featurePushBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.featureChatBtn:
                startActivity(new Intent(this, ChatActivity.class));
                break;
            case R.id.featurePublishBtn:
                startActivity(new Intent(this, PubSubActivity.class));
                break;
            case R.id.featureManagementBtn:
                startActivity(new Intent(this, UserManagementActivity.class));
                break;
            case R.id.featurePushBtn:
                startActivity(new Intent(this, PushActivity.class));
                break;
            default:
                break;
        }
    }

}
