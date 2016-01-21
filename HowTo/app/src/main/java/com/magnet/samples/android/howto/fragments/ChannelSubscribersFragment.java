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
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.adapters.UserListAdapter;
import com.magnet.samples.android.howto.util.Logger;
import java.util.List;

public class ChannelSubscribersFragment extends BaseFragment {

    private OnChannelSubscribersInteractionListener interactionListener;
    private ListView userList;

    public ChannelSubscribersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_subscribers, container, false);
        userList = (ListView) view.findViewById(R.id.channelSubscribersList);
        interactionListener.readSubscribers(resultOnFinishedListener);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_subscribers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subscribersMenuInvite:
                interactionListener.inviteUser();
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChannelSubscribersInteractionListener) {
            interactionListener = (OnChannelSubscribersInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onClick(View v) {

    }

    private void updateList(List<User> users) {
        if (isVisible()) {
            UserListAdapter adapter = new UserListAdapter(getActivity(), users);
            userList.setAdapter(adapter);
        }
    }

    private final MMXChannel.OnFinishedListener<ListResult<User>> resultOnFinishedListener = new MMXChannel.OnFinishedListener<ListResult<User>>() {
        @Override
        public void onSuccess(ListResult<User> userListResult) {
            Logger.debug("get subscribers", "success");
            updateList(userListResult.items);
        }

        @Override
        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            showMessage("Can't get subscribers : " + failureCode + " : " + throwable.getMessage());
            Logger.error("get subscribers", throwable, "error : ", failureCode);
        }
    };

    public interface OnChannelSubscribersInteractionListener {
        void readSubscribers(MMXChannel.OnFinishedListener<ListResult<User>> resultOnFinishedListener);
        void inviteUser();
    }

}
