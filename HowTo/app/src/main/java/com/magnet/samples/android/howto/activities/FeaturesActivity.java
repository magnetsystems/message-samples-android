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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.magnet.max.android.config.MaxAndroidConfig;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.common.Log;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.helpers.Utils;

/**
 * For complete feature list, @see <a href="https://developer.magnet.com/docs/message/overview/features/index.html">Features</a>
 */
public class FeaturesActivity extends BaseActivity {
    private static final String TAG = FeaturesActivity.class.getSimpleName();

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
                if(isGCMConfigured()) {
                    startActivity(new Intent(this, PushActivity.class));
                }
                break;
            default:
                break;
        }
    }

    private boolean isGCMConfigured() {
        boolean isSenderIdValid = false;

        if(!isGooglePlayServiceInstalled()) {
            Utils.showWarning(this, "Google Play Service is not configured",  "Please install Google Play Service on your device or emulator");
        } else {
            MaxAndroidConfig config = new MaxAndroidPropertiesConfig(this, R.raw.magnetmax);
            String senderId = config.getAllConfigs().get("mmx-gcmSenderId");
            if (StringUtil.isNotEmpty(senderId)) {
                try {
                    long sendIdLong = Long.parseLong(senderId);
                    isSenderIdValid = true;
                } catch (NumberFormatException e) {
                }
                if (!isSenderIdValid) {
                    Utils.showWarning(this, "Push is not configured",  "Please follow the instruction in https://developer.magnet.com/docs/message/v2.3/android/set-up-gcm/index.html and set mmx-gcmSenderId in res/raw/magnetmax.properties");
                }
            }
        }

        return isSenderIdValid;
    }

    private boolean isGooglePlayServiceInstalled() {
        //try {
        //    Class.forName("com.google.android.gms.gcm.GoogleCloudMessaging");
        //    return true;
        //} catch (ClassNotFoundException e) {
        //
        //}
        //
        //return false;
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        Log.d(TAG, "----------------GooglePlayServicesUtil.isGooglePlayServicesAvailable : " + status);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        return false;
    }

}
