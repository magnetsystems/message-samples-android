package com.magnet.magnetchat.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.presenters.updated.ChatListContract;
import com.magnet.magnetchat.ui.dialogs.AttachmentDialogFragment;
import com.magnet.magnetchat.ui.dialogs.DefaultAttachmentDialogFragment;
import com.magnet.magnetchat.ui.views.chatlist.MMXChatView;
import com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aorehov on 04.05.16.
 */
public class ChatFragment extends MMXBaseFragment {
    private MMXChatView mmxChatView;
    private ChatListContract.ChannelNameListener listener;
    private AttachmentDialogFragment dialogFragment;

    private List<String> attachments;
    private GoogleApiClient googleApiClient;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    public void onStart() {
        super.onStart();
        mmxChatView.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        mmxChatView.onStop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mmxChatView.onResume();
    }

    @Override
    public void onPause() {
        dismissDialog();
        mmxChatView.onPause();
        super.onPause();
    }

    @Override
    protected void onCreateFragment(View containerView) {
        Bundle bundle = getArguments();
        MMXChannel channel = BundleHelper.readMMXChannelFromBundle(bundle);

        ArrayList<User> recipients = null;
        if (channel == null) {
            recipients = BundleHelper.readRecipients(bundle);
        }

        if (channel == null && recipients == null) {
            throw new IllegalArgumentException("MMXChannel or List of recipients cannot be null!");
        }

        attachments = Arrays.asList(
                getString(R.string.mmx_attachment_pic),
                getString(R.string.mmx_attachment_location),
                getString(R.string.mmx_attachment_poll)
        );

        FrameLayout uiContainer = findView(containerView, R.id.container);
        mmxChatView = ChatSDK.getViewFactory().createMMXChatView(getContext());
        uiContainer.addView(mmxChatView);
        mmxChatView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mmxChatView != null) {
            if (channel != null) {
                mmxChatView.setMMXChannel(channel);
            } else if (recipients != null) {
                mmxChatView.setRecipients(recipients);
            }
        }

        setChatNameListener(listener);
        mmxChatView.setListener(attachmentListener);

        googleApiClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API).build();
    }


    public void setChatNameListener(ChatListContract.ChannelNameListener listener) {
        if (mmxChatView == null) {
            this.listener = listener;
        } else {
            mmxChatView.setChannelNameListener(listener);
            this.listener = null;
        }
    }

    public PostMMXMessageContract.Presenter getMessageContract() {
        if (mmxChatView == null) return null;
        return mmxChatView.getPostPresenter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mmxChatView.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void dismissDialog() {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            dialogFragment = null;
        }
    }

    private void showAttachmentChooser() {
        dismissDialog();
        dialogFragment = ChatSDK.getViewFactory().createAttachmentDialogFragment(getContext());
        dialogFragment.setListener(attachmentTypeListener);
        dialogFragment.setAttachment(attachments);
        dialogFragment.show(getFragmentManager(), dialogFragment.getTag());
    }

    protected void onAttachmentTypeReceived(String attachment) {

        int indexOf = attachments.indexOf(attachment);
        switch (indexOf) {
            case 0:
                openChoosePicture();
                break;
            case 1:
                readLocation();
                break;
            case 2:
                openPollCreator();
                break;

        }
    }

    private void openPollCreator() {
        toast("show poll create view or activity here");
    }

    private void readLocation() {
        if (!Utils.isGooglePlayServiceInstalled()) {
            showMessage("It seems Google play services is not available, can't use location API");
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessage("Location permission is not enabled");
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            mmxChatView.getPostPresenter().sendLocationMessage(currentLocation);
        } else {
            showMessage("Can't get location");
        }
    }

    private void showMessage(String s) {
        toast(s);
    }

    private void openChoosePicture() {
        Intent intent = IntentHelper.photoCapture();
        getActivity().startActivityForResult(intent, Constants.MMX_RC_TAKE_PIC);
    }

    private final MMXPostMessageView.OnAttachmentSelectListener attachmentListener = new MMXPostMessageView.OnAttachmentSelectListener() {
        @Override
        public void onOpenAttachmentChooser() {
            showAttachmentChooser();
        }
    };

    private final AttachmentDialogFragment.ChooserDialogListener attachmentTypeListener = new AttachmentDialogFragment.ChooserDialogListener() {
        @Override
        public void onChooseAttachment(@NonNull String attachment) {
            onAttachmentTypeReceived(attachment);
        }
    };

}
