package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 13.05.16.
 */
public interface MMXChannelSettingsContract {

    interface Presenter extends MMXPresenter {
        void doMute(boolean isMute);

        void delete();

        void setMMXChannel(MMXChannel mmxChannel);

        void setMMXChannelName(String name);

        void setMMXChannelLoadingListener(MMXAction<MMXChannel> action);
    }

    interface View extends MMXInfoView {

        void onMuteState(boolean isMute);

        void onLoading();

        void onLoadingComleated();

        void onChannelDeleted();

    }

}
