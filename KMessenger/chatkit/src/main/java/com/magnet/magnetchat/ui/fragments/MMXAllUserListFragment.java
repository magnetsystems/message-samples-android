package com.magnet.magnetchat.ui.fragments;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.ui.views.users.MMXUserListView;

/**
 * Created by aorehov on 13.05.16.
 */
public class MMXAllUserListFragment extends MMXUserListFragment {

    @Override
    protected MMXUserListView createMMXUserListView() {
        return ChatSDK.getViewFactory().createMmxAllUserListView(getContext());
    }
}
