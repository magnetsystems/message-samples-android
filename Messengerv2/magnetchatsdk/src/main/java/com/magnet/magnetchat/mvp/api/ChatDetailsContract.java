package com.magnet.magnetchat.mvp.api;

import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public interface ChatDetailsContract {

    interface View {
        /**
         * Show or hide the progress bar
         *
         * @param active
         */
        void setProgressIndicator(boolean active);

        void showRecipients(List<UserProfile> recipients);

        void finishDetails();

        void onMute(boolean isMuted);

        void onMessage(int stringRes);

        void onMessage(CharSequence message);
    }

    interface Presenter {

        /**
         * Method which provide to getting of the reading channels
         */
        void onLoadRecipients(boolean forceUpdate);

        void onAddRecipients();

        /**
         * Method which provide to check if this channel relative to user
         *
         * @return
         */
        boolean isChannelOwner();

        void changeMuteAction();

        BaseSortedAdapter.ItemComparator<UserProfile> getItemComparator();

        /**
         * the method requests mutable channel state. Result will be returned to onMute method of View
         *
         * @see ChatDetailsContract.View.onMute(boolean isMuted)
         */
        void requestMuteChannelState();
    }
}
