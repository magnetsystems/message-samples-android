package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;

import java.util.List;

/**
 * Created by aorehov on 27.05.16.
 */
public interface MMXChannelsContract {

    interface Presenter extends MMXPresenter {
        void doRefresh();

        void onScrollToItem(int localSize, int index);

        void doLeaveChannel(MMXChannelWrapper channel);

        void doOpenChannel(MMXChannelWrapper channel);

        void search(String term);

        void setRouterCallback(RouterCallback routerCallback);
    }

    interface View extends MMXInfoView {
        void onSet(List<MMXChannelWrapper> channels);

        void onPut(List<MMXChannelWrapper> channels);

        void onPut(MMXChannelWrapper channel);

        void onDelete(MMXChannelWrapper channel);

        void onLoading();

        void onLoadingFinished();
    }

    interface RouterCallback {
        void onLoading();

        void onLoadingFinished();

        void onOpenChannel(MMXChannelWrapper mmxChannelWrapper);
    }


}
