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

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXPushMessage;
import com.magnet.mmx.protocol.PushResult;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.helpers.Utils;
import com.magnet.samples.android.howto.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://developer.magnet.com/docs/message/v2.3/android/set-up-gcm/index.html">Setup GCM</a>
 * @see <a href="https://developer.magnet.com/docs/message/v2.3/android/creating-a-push-message/index.html">Creating a Push Message</a>
 */
public class PushActivity extends BaseActivity {

    private CheckBox soundOn;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        soundOn = (CheckBox) findViewById(R.id.pushSoundOn);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatMenuSend:
                String message = getFieldText(R.id.pushMessage);
                if (message != null) {
                    sendMessage(message, soundOn.isChecked());
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void sendMessage(String messageText, boolean needSound) {
        showMessage("Your message will be sent in 5 seconds. Please put your app in the background to see the notification in the tray");

        Map<String, Object> content = new HashMap<>();
        content.put("content", messageText);
        content.put("soundOn", needSound);
        final MMXPushMessage.Builder builder = new MMXPushMessage.Builder();
        builder.content(content).recipient(User.getCurrentUser()).type("text");
        mHandler.postDelayed(new Runnable() {
            @Override public void run() {
                builder.build().send(new MMXPushMessage.OnFinishedListener<PushResult>() {
                    @Override
                    public void onSuccess(PushResult pushResult) {
                        Logger.debug("send message", "success");
                     }

                    @Override
                    public void onFailure(MMXPushMessage.FailureCode failureCode, Throwable throwable) {
                        //showMessage("Can't send message : " + failureCode + " : " + throwable.getMessage());
                        Logger.error("send message", throwable, "error : ", failureCode);
                        Utils.showWarning(PushActivity.this, "Push failed",  "Please make sure you have followed the instruction in https://developer.magnet.com/docs/message/v2.3/android/set-up-gcm/index.html and set mmx-gcmSenderId in res/raw/magnetmax.properties");
                    }
                });
            }
        }, 5000);
    }

}
