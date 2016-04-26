/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.mvp.presenters;

import android.app.Activity;
import android.util.Log;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.mvp.api.ChatDetailsContract;
import com.magnet.magnetchat.ui.activities.ChooseUserActivity;
import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatDetailsPresenterImpl implements ChatDetailsContract.Presenter {
    private static final String TAG = "ChatDetailsPresenter";

    private final ChatDetailsContract.View mView;
    private final MMXChannel mCurrentChannel;
    private WeakReference<Activity> mActivityRef;

    public ChatDetailsPresenterImpl(ChatDetailsContract.View view, MMXChannel channel, Activity activity) {
        this.mView = view;
        this.mCurrentChannel = channel;
        this.mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onLoadRecipients(boolean forceUpdate) {
        mView.setProgressIndicator(true);
        mCurrentChannel.getAllSubscribers(100, 0, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                onComplete();

                List<UserProfile> userProfiles = new ArrayList<>(userListResult.items.size());
                for (User u : userListResult.items) {
                    if (!u.getUserIdentifier().equals(User.getCurrentUserId())) {
                        userProfiles.add(u);
                    }
                }
                Collections.sort(userProfiles, UserHelper.getUserProfileComparator());
                mView.showRecipients(userProfiles);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                onComplete();
                Log.e(TAG, failureCode.toString(), throwable);
                Utils.showMessage("Failed to load recipients, please try later");
            }

            private void onComplete() {
                mView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void onAddRecipients() {
        if (null != mActivityRef.get()) {
            mActivityRef.get().startActivity(ChooseUserActivity.getIntentToAddUserToChannel(mActivityRef.get(),
                    mCurrentChannel.getName()));
            mView.finishDetails();
        }
    }

    @Override
    public boolean isChannelOwner() {
        if (mCurrentChannel == null) {
            return false;
        }
        return StringUtil.isStringValueEqual(mCurrentChannel.getOwnerId(), User.getCurrentUserId());
    }

    @Override
    public void changeMuteAction() {
        if(mCurrentChannel.isMuted()){
            unmute();
        }else {
            mute();
        }
    }

    private void mute() {
        mCurrentChannel.mute(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                mView.onMute(mCurrentChannel.isMuted());
            }

            @Override
            public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                mView.onMute(mCurrentChannel.isMuted());
                mView.onMessage(R.string.err_channel_mute);
            }
        });
    }

    private void unmute() {
        mCurrentChannel.unMute(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                mView.onMute(mCurrentChannel.isMuted());
            }

            @Override
            public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
                mView.onMute(mCurrentChannel.isMuted());
                mView.onMessage(R.string.err_channel_unmute);
            }
        });
    }

    @Override
    public BaseSortedAdapter.ItemComparator<UserProfile> getItemComparator() {
        return userProfileItemComparator;
    }

    private final BaseSortedAdapter.ItemComparator<UserProfile> userProfileItemComparator = new BaseSortedAdapter.ItemComparator<UserProfile>() {
        @Override
        public int compare(UserProfile o1, UserProfile o2) {
            if (StringUtil.isStringValueEqual(o1.getLastName(), o2.getLastName())) {
                return Utils.compareString(o1.getFirstName(), o2.getFirstName());
            } else {
                return Utils.compareString(o1.getLastName(), o2.getLastName());
            }
        }

        @Override
        public boolean areContentsTheSame(UserProfile o1, UserProfile o2) {
            return areItemsTheSame(o1, o2)
                    && o1.getDisplayName().equalsIgnoreCase(o2.getDisplayName());
        }

        @Override
        public boolean areItemsTheSame(UserProfile item1, UserProfile item2) {
            return item1.getUserIdentifier().equals(item2.getUserIdentifier());
        }
    };
}
