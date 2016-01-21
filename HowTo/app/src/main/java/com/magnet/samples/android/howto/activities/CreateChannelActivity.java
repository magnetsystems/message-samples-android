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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.util.Logger;
import java.util.HashSet;
import java.util.StringTokenizer;

public class CreateChannelActivity extends BaseActivity {

    private Switch isPublicChannel;
    private Spinner permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);
        isPublicChannel = (Switch) findViewById(R.id.createChannelPublic);
        permission = (Spinner) findViewById(R.id.createChannelPermission);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createMenuSave:
                String name = getFieldText(R.id.createChannelName);
                if (name != null) {
                    String summary = getFieldText(R.id.createChannelSummary);
                    MMXChannel.PublishPermission publishPermission = MMXChannel.PublishPermission.OWNER_ONLY;
                    switch (permission.getSelectedItemPosition()) {
                        case 0:
                            publishPermission = MMXChannel.PublishPermission.OWNER_ONLY;
                            break;
                        case 1:
                            publishPermission = MMXChannel.PublishPermission.SUBSCRIBER;
                            break;
                        case 2:
                            publishPermission = MMXChannel.PublishPermission.ANYONE;
                            break;
                    }
                    boolean isPublic = isPublicChannel.isChecked();
                    String lineOfTags = getFieldText(R.id.createChannelTags);
                    HashSet<String> tags = getTagsFromString(lineOfTags);

                    createChannel(name, summary, isPublic, publishPermission, tags);
                } else {
                    showMessage("Input channel name");
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_channel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createChannel(String name, String summary, boolean isPublic, MMXChannel.PublishPermission permission, final HashSet<String> tags) {
        MMXChannel.create(name, summary, isPublic, permission, new MMXChannel.OnFinishedListener<MMXChannel>() {
            @Override
            public void onSuccess(MMXChannel mmxChannel) {
                Logger.debug("create channel", "success");
                showMessage("Channel was created");
                mmxChannel.setTags(tags, new MMXChannel.OnFinishedListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.debug("set tags", "success");
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        showMessage("Can't set tags : " + failureCode + " : " + throwable.getMessage());
                        Logger.error("set tags", throwable, "error : ", failureCode);
                    }
                });
                finish();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                showMessage("Can't create channel : " + failureCode + " : " + throwable.getMessage());
                Logger.error("create channel", throwable, "error : ", failureCode);
            }
        });
    }

    private HashSet<String> getTagsFromString(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        HashSet<String> hashSet = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            hashSet.add(tokenizer.nextToken().trim());
        }
        return hashSet;
    }

}
