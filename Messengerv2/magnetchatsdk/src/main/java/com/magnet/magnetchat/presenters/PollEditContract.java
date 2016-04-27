package com.magnet.magnetchat.presenters;

import android.os.Bundle;

import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public interface PollEditContract {

    interface Presenter {

        /**
         * use this method for poll creation or update
         */
        void doSaveAction();

        /**
         * view instance
         *
         * @param view
         */
        void setView(View view);

        void setMMXChannel(MMXChannel mmxChannel);

        /**
         * view create callback
         *
         * @param bundle
         * @param savedInstances saved data
         */
        void onCreate(Bundle bundle, Bundle savedInstances);

        /**
         * onStart callback
         */
        void onStart();

        /**
         * onStop callback
         */
        void onStop();
    }

    interface View {
        void onAnswersList(List<String> answers);

        List<String> getAnswersList();

        String getQuestion();

        boolean isMultipleChoice();

        void onLockScreen();

        void onUnlockScreen();

        void onMessage(CharSequence message);

        void onMessage(int resId);

        void onPollSaved(MMXMessage mmxMessage);
    }

}
