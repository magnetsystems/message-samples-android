package com.magnet.magnetchat.presenters.updated;


import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public interface ChatContract {

    interface Presenter extends MMXPresenter {

        void setChat(MMXChannelWrapper chat);

        void setChat(MMXChannel channel);

        void setChat(List<UserProfile> users);

        void doRefresh();

        void onScrolledTo(int visibleItemIndex, int size);
    }

    interface View extends MMXInfoView {

        void onSetMessage(List<MMXMessageWrapper> messages);

        void onPutMessage(List<MMXMessageWrapper> messages);

        void onPutMessage(MMXMessageWrapper message);

        void onDelete(MMXMessageWrapper message);

        void onChannelName(String name);

        void onChannelCreationFailure();
    }

}
