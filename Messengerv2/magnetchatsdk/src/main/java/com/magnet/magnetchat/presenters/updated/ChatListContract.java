package com.magnet.magnetchat.presenters.updated;


import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public interface ChatListContract {

    interface Presenter extends MMXPresenter {

        /**
         * init presenter with MMXChatWrapper
         *
         * @param chat
         */
        void setChat(MMXChannelWrapper chat);

        /**
         * init presenter with channel details
         *
         * @param channel
         */
        void setChat(ChannelDetail channel);

        /**
         * init presenter with user's amount
         *
         * @param users
         */
        void setChat(List<UserProfile> users);

        /**
         * init presenter with MMXChannel
         *
         * @param channel
         */
        void setChat(MMXChannel channel);

        /**
         * refresh current channel
         */
        void doRefresh();

        /**
         * scroll callback
         *
         * @param visibleItemIndex
         * @param size
         */
        void onScrolledTo(int visibleItemIndex, int size);

        /**
         * The method returns display channel name
         *
         * @return
         */
        String getChannelName();

        /**
         * Channel receive callback
         * <p>
         * Register if you need to receive MMXChannelWrapper instance if you have initialized channel using next methods:
         * void setChat(List<UserProfile> users);
         * void setChat(MMXChannel channel);
         *
         * @param listener
         */
        void setPresenterChatReceiveListener(MMXChannelListener listener);

        /**
         * call this method if you have created poll
         */
        void onCreatedPoll();
    }

    interface View extends MMXInfoView {

        /**
         * in this method you should replace all messages
         *
         * @param messages
         */
        void onSetMessage(List<MMXMessageWrapper> messages);

        /**
         * in this method you should add all messages to the set of exists messages
         *
         * @param messages
         */
        void onPutMessage(List<MMXMessageWrapper> messages);

        /**
         * in this method you should add one message to the set of exists messages
         *
         * @param message
         * @param isNeedScroll
         */
        void onPutMessage(MMXMessageWrapper message, boolean isNeedScroll);

        /**
         * in this method you should delete message from ui list
         *
         * @param message
         */
        void onDelete(MMXMessageWrapper message);

        /**
         * channel name callback
         *
         * @param name
         */
        void onChannelName(String name);

        /**
         * the method calls if channel wan't created
         */
        void onChannelCreationFailure();

        /**
         * the method calls if chanel is in downloads state
         */
        void onRefreshing();

        /**
         * the method calls if chat has been downloaded
         */
        void onRefreshingFinished();

        /**
         * in this method you set ChannelNameListener to presenter
         *
         * @param channelNameListener
         */
        void setChannelNameListener(ChannelNameListener channelNameListener);
    }

    /**
     * channel receive callback
     */
    interface MMXChannelListener {
        void onChannelReceived(MMXChannelWrapper mmxChannel);
    }

    /**
     * channel name callback
     */
    interface ChannelNameListener {
        void onName(String name);
    }

}
