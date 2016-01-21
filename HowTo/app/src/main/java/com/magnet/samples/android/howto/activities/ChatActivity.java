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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.adapters.MessageListAdapter;
import com.magnet.samples.android.howto.fragments.NewMessageDialogFragment;
import com.magnet.samples.android.howto.helpers.MessageHelper;
import com.magnet.samples.android.howto.util.Logger;
import java.util.Date;
import java.util.List;

/**
 * For complete feature description, @see <a href="https://developer.magnet.com/docs/message/overview/user-to-user-chat/index.html">User-to-User Chat</a>
 * For complete API example, @see <a href="https://developer.magnet.com/docs/message/v2.3/android/creating-your-first-android-app/index.html">Creating your First App</a>
 */
public class ChatActivity extends BaseActivity implements
    NewMessageDialogFragment.NewMessageListener {

    private MMXChannel myChatChannel;
    private AlertDialog dialog;
    private ListView messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messagesList = (ListView) findViewById(R.id.chatMessagesList);
        findViewById(R.id.chatFetchAllBtn).setOnClickListener(this);

        final User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            final String name = currentUser.getUserName();
            MMXChannel.getPrivateChannel(name, new MMXChannel.OnFinishedListener<MMXChannel>() {
                @Override
                public void onSuccess(MMXChannel mmxChannel) {
                    Logger.debug("get channel", "success");
                    myChatChannel = mmxChannel;
                }

                @Override
                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    if (failureCode.equals(MMXChannel.FailureCode.CHANNEL_NOT_FOUND)) {
                        String summary = "Chat channel for myself";
                        // Create a new channel
                        MMXChannel.create(name, summary, false, MMXChannel.PublishPermission.SUBSCRIBER, new MMXChannel.OnFinishedListener<MMXChannel>() {
                            @Override
                            public void onSuccess(MMXChannel mmxChannel) {
                                Logger.debug("create channel", "success");
                                myChatChannel = mmxChannel;
                            }

                            @Override
                            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                                showMessage("Can't create channel : " + failureCode + " : " + throwable.getMessage());
                                Logger.error("create channel", throwable, "error : ", failureCode);
                            }
                        });
                    } else {
                        showMessage("Can't get channel : " + failureCode + " : " + throwable.getMessage());
                        Logger.error("get channel", throwable, "error : ", failureCode);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatFetchAllBtn:
                getLastMessages();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatMenuSend:
                MessageHelper.showSendMessageDialog(getSupportFragmentManager(), myChatChannel);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        MMX.unregisterListener(eventListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MMX.registerListener(eventListener);
    }

    private void updateList(List<MMXMessage> messages) {
        MessageListAdapter adapter = new MessageListAdapter(this, messages);
        messagesList.setAdapter(adapter);
    }

    private void getLastMessages() {
        Date now = new Date();
        Date anHourAgo = new Date(now.getTime() - (60 * 60 * 24 * 1000l));
        myChatChannel.getMessages(anHourAgo, now, 1000, 0, false, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
            @Override
            public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
                Logger.debug("get all messages", "success, messages count = ", mmxMessageListResult.totalCount);
                updateList(mmxMessageListResult.items);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                showMessage("Can't get all messages : " + failureCode + " : " + throwable.getMessage());
                Logger.error("get all messages", throwable, "error : ", failureCode);
            }
        });
    }

    private final MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(final MMXMessage mmxMessage) {
            Logger.debug("received message", "from " + mmxMessage.getSender().getUserName());
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            String title = "Message received";
            if (mmxMessage.getAttachments().size() > 0) {
                title += "\n(has attachment)";
            }
            builder.setTitle(title).setCancelable(false);
            builder.setMessage(mmxMessage.getContent().get("content"));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog = builder.show();
            return true;
        }
    };

    @Override public void decorateMessage(MMXMessage.Builder messageBuilder) {

    }

    @Override public void messageSent(MMXMessage message) {

    }

    @Override public void messageFailure(Throwable error) {

    }
}
