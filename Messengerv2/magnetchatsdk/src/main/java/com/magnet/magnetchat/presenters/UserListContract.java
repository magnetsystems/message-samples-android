package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.model.Typed;
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

        void doClickOn(Typed typed);
    }

    interface View extends MMXInfoView {
        void onPut(Typed wrapper);

        void onDelete(Typed wrapper);

        void onSet(List<Typed> wrapper);

        void onLock();

        void onUnlock();

    }

}
