package com.magnet.magnetchat.presenters.impl;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.presenters.ChooseUserContract;
import com.magnet.magnetchat.persistence.AppScopePendingStateRepository;
import com.magnet.magnetchat.persistence.impl.PersistenceComponentImpl;
import com.magnet.magnetchat.ui.activities.ChatActivity;
import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import com.magnet.max.android.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlernatovich on 3/2/16.
 */
@Deprecated
public class ChooseUserPresenterImpl implements ChooseUserContract.Presenter {
    private final ChooseUserContract.View mView;
    private final AppScopePendingStateRepository appStateRepository;
    private Chat mConversation;
    private ChooseUserContract.ChooseMode mAddmingMode;
    private List<User> mDefaultQueryResults;
    private ChooseUserContract.UserQuery mCurrentQuery;
    private final ChooseUserContract.UserQuery mDefaultQuery;

    public ChooseUserPresenterImpl(ChooseUserContract.View view) {
        this(view, null);
    }

    public ChooseUserPresenterImpl(ChooseUserContract.View view, String channelName) {
        appStateRepository = new PersistenceComponentImpl(view.getActivity()).getApplicationPendingStateRepository();
        this.mView = view;
        if (null != channelName) {
            mConversation = ChatManager.getInstance().getConversationByName(channelName);
            mAddmingMode = ChooseUserContract.ChooseMode.MODE_ADD_USER;
        } else {
            mAddmingMode = ChooseUserContract.ChooseMode.MODE_NEW_CHAT;
        }

        mDefaultQuery = new ChooseUserContract.UserQuery(UserHelper.createNameQuery(""), ChooseUserContract.DEFAULT_USER_ORDER, true);

        mCurrentQuery = mDefaultQuery;
        mDefaultQueryResults = new ArrayList<>();
    }

    @Override
    public void onLoad(int offset, int limit) {
        if (mCurrentQuery.isDefault() && ((offset + limit) < mDefaultQueryResults.size())) {
            mView.showUsers(mDefaultQueryResults.subList(offset, limit), 0 != offset);
        } else {
            queryUser(mCurrentQuery, offset, limit);
        }
    }

    /**
     * Method which provide the searching of the user by query
     *
     * @param query current query
     */
    @Override
    public void onSearch(@NonNull String query, String order) {
        mCurrentQuery = new ChooseUserContract.UserQuery(query, order, false);
        queryUser(mCurrentQuery, 0, Constants.USER_PAGE_SIZE);
    }

    @Override
    public void onSearchReset() {
        mView.showUsers(mDefaultQueryResults, false);
    }

    @Override
    public void onItemSelect(int position, User item) {

    }

    @Override
    public void onItemLongClick(int position, User item) {

    }

    /**
     * Method which provide the user selection
     *
     * @param selectedUsers user list
     */
    @Override
    public void onUsersSelected(@NonNull List<User> selectedUsers) {
        if (selectedUsers.size() > 0) {
            switch (mAddmingMode) {
                case MODE_ADD_USER:
                    onAddUsersToChat(selectedUsers);
                    break;
                case MODE_NEW_CHAT:
                    onNewChat(selectedUsers);
                    break;
            }
        } else {
            Utils.showMessage("No contact was selected");
        }
    }

    /**
     * Method which provide to adding of the user to the chat
     *
     * @param selectedUsers selected users
     */
    @Override
    public void onAddUsersToChat(@NonNull List<User> selectedUsers) {
        mView.setProgressIndicator(true);
        ChannelHelper.addUserToConversation(mConversation, selectedUsers, addUserChannelListener);
    }

    /**
     * Method which provide the creating of the new chat
     *
     * @param selectedUsers selected users
     */
    @Override
    public void onNewChat(@NonNull List<User> selectedUsers) {
        Activity activity = mView.getActivity();
        if (activity != null) {
            activity.startActivity(ChatActivity.getIntentForNewChannel(activity, selectedUsers));
            onChannelUpdated(true);
        }
    }

    @Override
    public ChooseUserContract.UserQuery getDefaultQuery() {
        return mDefaultQuery;
    }

    @Override
    public BaseSortedAdapter.ItemComparator<User> getItemComparator() {
        return userItemComparator;
    }

    private void queryUser(final ChooseUserContract.UserQuery userQuery, final int offset, final int limit) {
        mView.setProgressIndicator(true);

        if (0 == offset) {
            mCurrentQuery.setCurrentOffset(0);
        }

        User.search(userQuery.getQuery(), limit, mCurrentQuery.getCurrentOffset(), userQuery.getOrder(), new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                if (null != users && !users.isEmpty()) {
                    mCurrentQuery.addCurrentOffset(users.size());

                    filterUsers(users);
                }

                if (userQuery.isDefault()) {
                    if (0 == offset) {
                        mDefaultQueryResults.clear();
                        mDefaultQueryResults.addAll(users);
                    } else {
                        mDefaultQueryResults.addAll(users);
                    }
                }

                mView.setProgressIndicator(false);
                Logger.debug("find users", "success");
                mView.showUsers(users, 0 != offset);
            }

            @Override
            public void failure(ApiError apiError) {
                mView.setProgressIndicator(false);
                Utils.showMessage("Can't find users");
                Logger.error("find users", apiError);
            }
        });
    }

    private void filterUsers(List<User> users) {
        users.remove(User.getCurrentUser());
        if (mConversation != null) {
            List<User> existingUsers = new ArrayList<>();
            for (User u : users) {
                if (mConversation.containSubscriber(u)) {
                    existingUsers.add(u);
                }
            }

            users.removeAll(existingUsers);
        }
    }

    /**
     * Listener which provide to listening of the action when users add to channel
     */
    private final ChannelHelper.OnAddUserListener addUserChannelListener = new ChannelHelper.OnAddUserListener() {
        @Override
        public void onSuccessAdded() {
            mView.setProgressIndicator(false);
            onChannelUpdated(true);
        }

        @Override
        public void onUserSetExists(String channelSetName) {

        }

        @Override
        public void onWasAlreadyAdded() {
            mView.setProgressIndicator(false);
            Utils.showMessage("Contact was already added");
            onChannelUpdated(false);
        }

        @Override
        public void onFailure(Throwable throwable) {
            mView.setProgressIndicator(false);
            Utils.showMessage("Can't add contact to channel");
        }
    };

    private void onChannelUpdated(boolean isUpdated) {
        appStateRepository.setNeedToUpdateChannel(isUpdated);
        mView.finishSelection();
    }

    private final BaseSortedAdapter.ItemComparator<User> userItemComparator = new BaseSortedAdapter.ItemComparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            if (StringUtil.isStringValueEqual(o1.getLastName(), o2.getLastName())) {
                return Utils.compareString(o1.getFirstName(), o2.getFirstName());
            } else {
                return Utils.compareString(o1.getLastName(), o2.getLastName());
            }
        }

        @Override
        public boolean areContentsTheSame(User o1, User o2) {
            return areItemsTheSame(o1, o2)
                    && o1.getDisplayName().equalsIgnoreCase(o2.getDisplayName());
        }

        @Override
        public boolean areItemsTheSame(User item1, User item2) {
            return item1 == item2 || item1.getUserIdentifier().equals(item2.getUserIdentifier());
        }
    };
}
