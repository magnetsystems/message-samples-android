package com.magnet.magnetchat.ui.fragments;

import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.ui.views.poll.MMXEditPollView;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * You should send MMXChannel to the fragment
 * Pack MMXChannel using BundleHelper into Bundle and send as arguments
 *
 * @see BundleHelper
 * @see android.os.Bundle
 * <p/>
 * Created by aorehov on 18.05.16.
 */
public class MMXEditPollFragment extends MMXBaseFragment {

    private MMXEditPollView uiPollView;
    private MMXEditPollView.OnPollCreatedListener onPollCreateListener;

    @Override
    protected void onCreateFragment(View containerView) {
        MMXChannel channel = BundleHelper.readMMXChannelFromBundle(getArguments());

        FrameLayout uiContainer = findView(containerView, R.id.container);
        uiPollView = ChatSDK.getViewFactory().createPolView(getContext());
        uiContainer.addView(uiPollView);

        uiPollView.setOnPollCreateListener(onPollCreateListener);
        uiPollView.setChannel(channel);
    }

    public void setOnPollCreateListener(MMXEditPollView.OnPollCreatedListener onPollCreateListener) {
        if (uiPollView == null) {
            this.onPollCreateListener = onPollCreateListener;
        } else {
            uiPollView.setOnPollCreateListener(onPollCreateListener);
            this.onPollCreateListener = null;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_container;
    }

    public void doCreatePoll() {
        if (uiPollView != null)
            uiPollView.doCreatePoll();
    }
}
