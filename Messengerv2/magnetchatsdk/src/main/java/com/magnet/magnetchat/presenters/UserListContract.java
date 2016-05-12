package com.magnet.magnetchat.presenters;

import android.os.Bundle;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.presenters.core.MMXInfoView;
import com.magnet.magnetchat.presenters.core.MMXPresenter;

import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
public interface UserListContract {

    interface Presenter extends MMXPresenter {
        void doRefresh();

        void onCurrentPosition(int localSize, int index);

        void doClickOn(MMXUserWrapper typed);

        void onInit(Bundle bundle);

//        void setMMXChannel(MMXChannel mmxChannel);
//
//        void setMMXChannelDetails(ChannelDetail details);
//
//        void setMMXChannelWrapper(MMXChannelWrapper mmxChannelWrapper);

    }

    interface View extends MMXInfoView {
        void onPut(MMXUserWrapper wrapper);

        void onDelete(MMXUserWrapper wrapper);

        void onSet(List<MMXUserWrapper> wrapper);

        void onLoading();

        void onLoadingComplete();

        void onCantLoadChannel();
    }

}
