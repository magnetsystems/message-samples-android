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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.adapters.ChannelListAdapter;
import com.magnet.samples.android.howto.util.Logger;
import java.util.List;

public class ChannelListActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final String TAG_CHANNELS_MODE = "channels_mode";
    public static final int CHANNELS_SUBSCRIPTIONS = 0;
    public static final int CHANNELS_PRIVATE = 1;
    public static final int CHANNELS_PUBLIC = 2;

    private ListView channelList;
    private ChannelListAdapter adapter;
    private int mode;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        channelList = (ListView) findViewById(R.id.channelList);
        channelList.setOnItemLongClickListener(this);
        channelList.setOnItemClickListener(this);
        mode = getIntent().getIntExtra(TAG_CHANNELS_MODE, CHANNELS_SUBSCRIPTIONS);
        readChannelsByMode();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            MMXChannel selectedChannel = adapter.getItem(position);
            if (User.getCurrentUserId().equals(selectedChannel.getOwnerId())) {
                showDialogForDeleteChannel(selectedChannel);
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ChannelActivity.TAG_SELECTED_CHANNEL, adapter.getItem(position).getName());
            intent.putExtra(ChannelActivity.TAG_IS_PUBLIC, adapter.getItem(position).isPublic());
            startActivity(intent);
        }
    }

    private void showDialogForDeleteChannel(final MMXChannel channel) {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure that you want to delete channel?");
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog = builder.create();
        }
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteChannel(channel);
            }
        });
        alertDialog.show();
    }

    private void updateList(List<MMXChannel> channels) {
        adapter = new ChannelListAdapter(this, channels);
        channelList.setAdapter(adapter);
    }

    private void readSubscriptions() {
        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
            @Override
            public void onSuccess(List<MMXChannel> channels) {
                findViewById(R.id.channelListProgress).setVisibility(View.GONE);
                Logger.debug("get channels", "success");
                updateList(channels);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                findViewById(R.id.channelListProgress).setVisibility(View.GONE);
                showMessage("Can't get channels : " + failureCode + " : " + throwable.getMessage());
                Logger.error("get channels", throwable, "error : ", failureCode);
            }
        });
    }

    private void readChannelsByMode() {
        switch (mode) {
            case CHANNELS_SUBSCRIPTIONS:
                setTitle("Subscribed channels");
                readSubscriptions();
                break;
            case CHANNELS_PRIVATE:
                setTitle("Private channels");
                readPrivateChannels();
                break;
            case CHANNELS_PUBLIC:
                setTitle("Public channels");
                readPublicChannels();
                break;
        }
    }

    private void readPrivateChannels() {
        MMXChannel.getAllPrivateChannels(100, 0, finishedListener);
    }

    private void readPublicChannels() {
        MMXChannel.getAllPublicChannels(100, 0, finishedListener);
    }

    private void deleteChannel(MMXChannel channel) {
        channel.delete(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                readChannelsByMode();
                showMessage("Channel was deleted");
                Logger.debug("delete channel", "success");
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                showMessage("Can't delete channel : " + failureCode + " : " + throwable.getMessage());
                Logger.error("delete channel", throwable, "error : ", failureCode);
            }
        });
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXChannel>> finishedListener = new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
        @Override
        public void onSuccess(ListResult<MMXChannel> channels) {
            findViewById(R.id.channelListProgress).setVisibility(View.GONE);
            Logger.debug("get channels", "success");
            updateList(channels.items);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            findViewById(R.id.channelListProgress).setVisibility(View.GONE);
            showMessage("Can't get channels : " + failureCode + " : " + throwable.getMessage());
            Logger.error("get channels", throwable, "error : ", failureCode);
        }
    };
}
