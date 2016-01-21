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
import com.magnet.samples.android.howto.R;

/**
 * For complete feature description, @see <a href="https://developer.magnet.com/docs/message/overview/publish-subscribe/index.html">Publish-Subscribe (Pub/Sub)</a>
 * For complete API example,
 *  Public forum, @see <a href="https://developer.magnet.com/docs/message/v2.3/android/creating-a-public-forum-android/index.html">Creating a Public Forum</a>
 *  Private group, @see <a href="https://developer.magnet.com/docs/message/v2.3/android/creating-a-private-discussion-group-android/index.html">Creating a Private Discussion Group</a>
 */
public class PubSubActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        findViewById(R.id.publishCreateBtn).setOnClickListener(this);
        findViewById(R.id.publishFindBtn).setOnClickListener(this);
        findViewById(R.id.publishPrivateBtn).setOnClickListener(this);
        findViewById(R.id.publishPublicBtn).setOnClickListener(this);
        findViewById(R.id.publishSubscribedBtn).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishCreateBtn:
                startActivity(new Intent(this, CreateChannelActivity.class));
                break;
            case R.id.publishFindBtn:
                startActivity(new Intent(this, FindChannelActivity.class));
                break;
            case R.id.publishPrivateBtn:
                startActivity(makeIntentToChannelList(ChannelListActivity.CHANNELS_PRIVATE));
                break;
            case R.id.publishPublicBtn:
                startActivity(makeIntentToChannelList(ChannelListActivity.CHANNELS_PUBLIC));
                break;
            case R.id.publishSubscribedBtn:
                startActivity(makeIntentToChannelList(ChannelListActivity.CHANNELS_SUBSCRIPTIONS));
                break;
            default:
                break;
        }
    }

    private Intent makeIntentToChannelList(int mode) {
        Intent intent = new Intent(this, ChannelListActivity.class);
        intent.putExtra(ChannelListActivity.TAG_CHANNELS_MODE, mode);
        return intent;
    }

}
