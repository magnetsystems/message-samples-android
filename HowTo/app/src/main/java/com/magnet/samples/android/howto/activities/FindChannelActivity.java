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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.adapters.ChannelListAdapter;
import com.magnet.samples.android.howto.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class FindChannelActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private Spinner modeChooser;
    private EditText searchText;
    private ListView searchResult;
    private ChannelListAdapter adapter;
    private List<MMXChannel> mChannels;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_channel);
        modeChooser = (Spinner) findViewById(R.id.findChannelMode);
        modeChooser.setOnItemSelectedListener(this);
        searchText = (EditText) findViewById(R.id.findChannelText);
        searchResult = (ListView) findViewById(R.id.findChannelResult);
        searchResult.setOnItemLongClickListener(this);
        searchResult.setOnItemClickListener(this);
        findViewById(R.id.findChannelBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.findChannelBtn:
                runSearch();
                break;
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
            case 1:
                searchText.setHint("Name");
                break;
            case 2:
            case 3:
                searchText.setHint("Prefix");
                break;
            case 4:
                searchText.setHint("Tag1, Tag2, Tag3");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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

    private void runSearch() {
        String query = searchText.getText().toString();
        if (!query.isEmpty()) {
            switch (modeChooser.getSelectedItemPosition()) {
                case 0:
                    getChannelByName(query, true);
                    break;
                case 1:
                    getChannelByName(query, false);
                    break;
                case 2:
                    searchStartedWith(query, true);
                    break;
                case 3:
                    searchStartedWith(query, false);
                    break;
                case 4:
                    searchByTags(query);
                    break;
            }
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

    private void deleteChannel(MMXChannel channel) {
        channel.delete(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                runSearch();
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

    private void searchByTags(String query) {
        HashSet<String> tags = getTagsFromString(query);
        MMXChannel.findByTags(tags, 100, 0, finishedListener);
    }

    private void getChannelByName(String name, boolean isPublic) {
        final String tag = "get " + (isPublic ? "public" : "private") + " channel by name ";
        MMXChannel.OnFinishedListener<MMXChannel> listener = new MMXChannel.OnFinishedListener<MMXChannel>() {
            @Override
            public void onSuccess(MMXChannel mmxChannel) {
                Logger.debug(tag, "success");
                updateList(Arrays.asList(mmxChannel));
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                showMessage("Can't " + tag + failureCode + " : " + throwable.getMessage());
                Logger.error(tag, throwable, "error : ", failureCode);
            }
        };

        if(isPublic) {
            MMXChannel.getPublicChannel(name, listener);
        } else {
            MMXChannel.getPrivateChannel(name, listener);
        }
    }

    private void searchStartedWith(String query, boolean isPublic) {
        if(isPublic) {
            MMXChannel.findPublicChannelsByName(query, 100, 0, finishedListener);
        } else {
            MMXChannel.findPrivateChannelsByName(query, 100, 0, finishedListener);
        }
    }

    private void updateList(List<MMXChannel> channels) {
        if(null == adapter) {
            mChannels = new ArrayList<>(channels);
            adapter = new ChannelListAdapter(this, mChannels);
            searchResult.setAdapter(adapter);
        } else {
            mChannels.clear();
            mChannels.addAll(channels);
            adapter.notifyDataSetChanged();

            if(null == channels || channels.isEmpty()) {
                showMessage("No channel matches");
            }
        }
    }

    private HashSet<String> getTagsFromString(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        HashSet<String> hashSet = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            hashSet.add(tokenizer.nextToken().trim());
        }
        return hashSet;
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXChannel>> finishedListener = new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {

        @Override
        public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
            Logger.debug("find channels", "success");
            updateList(mmxChannelListResult.items);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            showMessage("Can't find channels : " + failureCode + " : " + throwable.getMessage());
            Logger.error("find channels", throwable, "error : ", failureCode);
        }
    };

}
