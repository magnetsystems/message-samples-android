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

        void setChat(MMXChannelWrapper chat);

        void setChat(ChannelDetail channel);

        void setChat(List<UserProfile> users);

        void setChat(MMXChannel channel);

        void doRefresh();

        void onScrolledTo(int visibleItemIndex, int size);

        String getChannelName();

        void setPresenterChatReceiveListener(MMXChannelListener listener);

        void onCreatedPoll();
    }

    interface View extends MMXInfoView {

        void onSetMessage(List<MMXMessageWrapper> messages);

        void onPutMessage(List<MMXMessageWrapper> messages);

        void onPutMessage(MMXMessageWrapper message, boolean isNeedScroll);

        void onDelete(MMXMessageWrapper message);

        void onChannelName(String name);

        void onChannelCreationFailure();

        void onRefreshing();

        void onRefreshingFinished();

        void setChannelNameListener(ChannelNameListener channelNameListener);
    }

    interface MMXChannelListener {
        void onChannelReceived(MMXChannelWrapper mmxChannel);
    }

    interface ChannelNameListener {
        void onName(String name);
    }

}
