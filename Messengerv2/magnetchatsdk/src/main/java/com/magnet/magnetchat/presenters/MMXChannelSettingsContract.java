package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * channel details abstraction
 * Created by aorehov on 13.05.16.
 */
public interface MMXChannelSettingsContract {

    interface Presenter extends MMXPresenter {

        /**
         * this method mute or unmute channel
         *
         * @param isMute
         */
        void doMute(boolean isMute);

        /**
         * you can delete/or leave from channel using this method
         */
        void delete();

        /**
         * init presenter with MMXChannel instance
         *
         * @param mmxChannel
         */
        void setMMXChannel(MMXChannel mmxChannel);

        /**
         * init presenter using channel name
         *
         * @param name
         */
        void setMMXChannelName(String name);

        /**
         * channel loading callback
         * instance of channel will be returned into this callback if you have initialized channel using setMMXChannelName(String name) method
         *
         * @param action
         */
        void setMMXChannelLoadingListener(MMXAction<MMXChannel> action);
    }

    interface View extends MMXInfoView {

        /**
         * you should set mute state into ui
         *
         * @param isMute
         */
        void onMuteState(boolean isMute);

        /**
         * called if users are loading
         */
        void onLoading();

        /**
         * called if users have been loaded
         */
        void onLoadingCompleted();

        /**
         * called if channel was removed
         */
        void onChannelDeleted();

    }

}
