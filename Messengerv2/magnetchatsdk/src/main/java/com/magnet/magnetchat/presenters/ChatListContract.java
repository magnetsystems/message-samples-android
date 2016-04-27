package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.model.Chat;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public interface ChatListContract {

    interface View extends IListView<Chat> {

        /**
         * Method which provide to show of the new chat
         */
        void createNewChat();

        void showConversationUpdate(Chat conversation, boolean isNew);

        /**
         * Method which provide the conversation details
         *
         * @param conversation current conversation
         */
        void showChatDetails(Chat conversation);

        void showLeaveConfirmation(Chat conversation);

        /**
         * Method which provide the dismissing of the leave dialog
         */
        void dismissLeaveDialog();

        /**
         * Show or hide the progress bar
         *
         * @param active
         */
        void setProgressIndicator(boolean active);
    }

    interface Presenter extends IListPresenter<Chat> {

        void onConversationUpdate(Chat conversation, boolean isNew);

        /**
         * Method which provide the action when activity or fragment call onResume
         * (WARNING: Should be inside the onCreate method)
         */
        void onResume();

        /**
         * Method which provide the action when activity or fragment call onPause
         * (WARNING: Should be inside the onPause method)
         */
        void onPause();
    }
}
