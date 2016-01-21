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

package com.magnet.samples.android.howto.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.adapters.MessageListAdapter;
import com.magnet.samples.android.howto.helpers.MessageHelper;
import com.magnet.samples.android.howto.util.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChannelMessagesFragment extends BaseFragment {

    //private OnChannelInteractionListener channelInteractionListener;
    private ListView messagesList;
    private MMXChannel mChannel;
    private List<MMXMessage> mMessages = new ArrayList<>();
    private MessageListAdapter mAdapter;

    public static ChannelMessagesFragment newInstance(MMXChannel channel) {
        ChannelMessagesFragment fragment = new ChannelMessagesFragment();
        fragment.setChannel(channel);
        return fragment;
    }

    public void setChannel(MMXChannel channel) {
        mChannel = channel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);
        messagesList = (ListView) view.findViewById(R.id.chatMessagesList);
        view.findViewById(R.id.chatFetchAllBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatFetchAllBtn:
                getLastMessages();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatMenuSend:
                MessageHelper.showSendMessageDialog(getFragmentManager(), mChannel);
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getLastMessages() {
        Date now = new Date();
        Date anHourAgo = new Date(now.getTime() - (60 * 60 * 24 * 1000l));
        mChannel.getMessages(anHourAgo, now, 1000, 0, false, messagesListener);
    }

    private void updateList(List<MMXMessage> messages) {
        if(null == mAdapter) {
            mAdapter = new MessageListAdapter(getActivity(), mMessages);
            messagesList.setAdapter(mAdapter);
        } else {
            mMessages.clear();
        }

        mMessages.addAll(messages);
        mAdapter.notifyDataSetChanged();

        if(null == messages || messages.isEmpty()) {
            showMessage("No message yet.");
        }
    }

    private final MMXChannel.OnFinishedListener<ListResult<MMXMessage>> messagesListener = new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
        @Override
        public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
            Logger.debug("get messages", "success, messages count = ", mmxMessageListResult.totalCount);
            updateList(mmxMessageListResult.items);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            showMessage("Can't get messages : " + failureCode + " : " + throwable.getMessage());
            Logger.error("get messages", throwable, "error : ", failureCode);
        }
    };
}
