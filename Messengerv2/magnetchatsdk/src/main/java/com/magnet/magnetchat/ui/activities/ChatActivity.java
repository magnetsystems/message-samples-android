package com.magnet.magnetchat.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.EndlessLinearRecyclerViewScrollListener;
import com.magnet.magnetchat.callbacks.OnRecyclerViewItemClickListener;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.helpers.BitmapHelper;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.helpers.PermissionHelper;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.presenters.ChatContract;
import com.magnet.magnetchat.presenters.impl.ChatPresenterImpl;
import com.magnet.magnetchat.ui.adapters.MessagesAdapter;
import com.magnet.magnetchat.ui.views.poll.MMXEditPollView;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends BaseActivity implements ChatContract.View {

    public static final String TAG = ChatActivity.class.getSimpleName();

    public static final String TAG_CHANNEL_NAME = "channelName";
    public static final String TAG_CHANNEL_OWNER_ID = "channelOwnerId";
    public static final String TAG_CHANNEL_DETAIL = "channelDetail";
    public static final String TAG_CREATE_WITH_RECIPIENTS = "createWithRecipients";
    public static final String TAG_CREATE_NEW = "createNew";

    private static final String[] ATTACHMENT_VARIANTS = {"Take photo", "Choose from gallery", "Send location", /*"Send video",*/ "Create Poll", "Cancel"};

    public static final int INTENT_REQUEST_GET_IMAGES = 14;
    public static final int INTENT_SELECT_VIDEO = 13;

    public static final int REQUEST_LOCATION = 1111;
    public static final int REQUEST_VIDEO = 1112;
    public static final int REQUEST_IMAGE = 1113;

    private MessagesAdapter mAdapter;
    private RecyclerView messagesListView;
    private String channelName;
    private AlertDialog attachmentDialog;
    private GoogleApiClient googleApiClient;

    private ProgressBar chatMessageProgress;
    AppCompatEditText editMessage;
    TextView sendMessageButton;
    Toolbar toolbar;
    private MMXEditPollView uiPoll;
    private ViewGroup uiPollContainer;

    ChatContract.Presenter mPresenter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chat;
    }

    @Override
    protected int getBaseViewID() {
        return R.id.main_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //For keeping toolbar when user input message
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        editMessage = (AppCompatEditText) findViewById(R.id.chatMessageField);
        sendMessageButton = (TextView) findViewById(R.id.chatSendBtn);

        chatMessageProgress = (ProgressBar) findViewById(R.id.chatMessageProgress);
//        uiPoll = findView(R.id.mmx_poll);
        uiPoll = ChatSDK.getViewFactory().createPolView(this);
        uiPollContainer = findView(R.id.mmx_poll_container);
        uiPollContainer.addView(uiPoll);


        setOnClickListeners(sendMessageButton);
        findViewById(R.id.chatAddAttachment).setOnClickListener(this);

        messagesListView = (RecyclerView) findViewById(R.id.chatMessageList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        messagesListView.setLayoutManager(layoutManager);

        //TODO:Infinity scroll implementation (with crash for now)
        messagesListView.addOnScrollListener(new EndlessLinearRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "------------onLoadMore Message: " + page + "/" + totalItemsCount + "," + mPresenter.getCurrentConversation().getMessages().size() + "\n");
                mPresenter.onLoad(totalItemsCount, Constants.MESSAGE_PAGE_SIZE);
            }
        });

        channelName = getIntent().getStringExtra(TAG_CHANNEL_NAME);
        if (null != channelName) {
            Chat currentConversation = ChatManager.getInstance().getConversationByName(channelName);
            if (currentConversation != null) {
                mPresenter = new ChatPresenterImpl(this, currentConversation);
                mPresenter.onLoad(0, Constants.MESSAGE_PAGE_SIZE);
                MMXChannel mmxChannel = currentConversation.getChannel();
                uiPoll.setChannel(mmxChannel);
            } else {
                showMessage("Can load the conversation");
                finish();
                return;
            }
        } else {
            ArrayList<UserProfile> recipients = getIntent().getParcelableArrayListExtra(TAG_CREATE_WITH_RECIPIENTS);
            if (recipients != null) {
                mPresenter = new ChatPresenterImpl(this, recipients);
            } else {
                showMessage("Can load the conversation");
                finish();
                return;
            }
        }

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailedListener).addApi(LocationServices.API).build();
    }

    @Override
    public void onChannelCreated(MMXChannel channel) {
        uiPoll.setChannel(channel);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chatSendBtn) {
            String text = getSimpleText(editMessage);
            if (text != null && !text.isEmpty()) {
                sendMessageButton.setEnabled(false);
                mPresenter.onSendText(text);
            }
        } else if (v.getId() == R.id.chatAddAttachment) {
            showAttachmentDialog();
        }
    }

    @Override
    protected void onPause() {
        if (attachmentDialog != null && attachmentDialog.isShowing()) {
            attachmentDialog.dismiss();
        }

        mPresenter.onPause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onLoadRecipients(false);
        mPresenter.onLoad(false);

        mPresenter.onResume();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mmxchat_edit) {
            mPresenter.onChatDetails();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Show or hide the progress bar
     *
     * @param active
     */
    @Override
    public void setProgressIndicator(boolean active) {
        chatMessageProgress.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Method which provide to show the messages
     *
     * @param messages messages list
     */
    @Override
    public void showList(List<MMXMessage> messages, boolean toAppend) {
        if (null == mAdapter) {
            mAdapter = new MessagesAdapter(this, Message.fromMMXMessages(messages), mPresenter.getItemComparator());
            mAdapter.setmOnClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onClick(int position) {
                    mPresenter.onItemSelect(position, mAdapter.getItem(position));
                }

                @Override
                public void onLongClick(int position) {

                }
            });
            messagesListView.setAdapter(mAdapter);
        } else {
            if (toAppend) {
                if (!messages.isEmpty()) {
                    mAdapter.addItem(Message.fromMMXMessages(messages));
                }
            } else {
                mAdapter.swapData(Message.fromMMXMessages(messages));
            }
        }
    }

    /**
     * Method which provide to show the recipients
     *
     * @param recipients recipients list
     */
    @Override
    public void showRecipients(List<UserProfile> recipients) {

    }

    /**
     * Method whihc provide to show of the new message
     *
     * @param message new message
     */
    @Override
    public void showNewMessage(MMXMessage message) {
        if (mAdapter != null) {
            mAdapter.addItem(Message.createMessageFrom(message));
            //mAdapter.notifyItemChanged(mAdapter.getItemCount());
            messagesListView.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }

    /**
     * Method which provide to show of the image picker
     */
    @Override
    public void showImagePicker() {
        startActivityForResult(IntentHelper.photoCapture(), INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Method which provide to clearing of the input field
     */
    @Override
    public void clearInput() {
        editMessage.setText("");
    }

    /**
     * Method which provide the enabling of the send button
     *
     * @param enabled is need enable
     */
    @Override
    public void setSendEnabled(boolean enabled) {
        sendMessageButton.setEnabled(true);
    }

    /**
     * Method whihc provide to show of the location
     *
     * @param message message
     */
    @Override
    public void showLocation(Message message) {
        if (!Utils.isGooglePlayServiceInstalled()) {
            Utils.showMessage(this, "It seems Google play services is not available, can't use location API");
        } else {
            String uri = String.format(Locale.ENGLISH, "geo:%s?z=16&q=%s", message.getLatitudeLongitude(), message.getLatitudeLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                this.startActivity(intent);
            } catch (Throwable e) {
                Log.e(TAG, "Can find any app to show map", e);
                Utils.showMessage(this, "Can find any app to show map");
            }
        }
    }

    /**
     * Method which provide to show of the image message
     *
     * @param message image message
     */
    @Override
    public void showImage(Message message) {
        if (message.getAttachment() != null) {
            String newImagePath = message.getAttachment().getDownloadUrl();
            Log.d(TAG, "Viewing photo : " + newImagePath + "\n" + message.getAttachment());
            if (newImagePath != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newImagePath));
                intent.setDataAndType(Uri.parse(newImagePath), "image/*");
                try {
                    this.startActivity(intent);
                } catch (Throwable e) {
                    Log.e(TAG, "Can find any app to mView image", e);
                    Utils.showMessage(this, "Can find any app to mView image");
                }
            }
        }
    }

    /**
     * Method which provide the getting of the activity
     *
     * @return current activity
     */
    @Override
    @NonNull
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                Bundle extras = intent.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Uri uri = BitmapHelper.storeImage(imageBitmap, 100);

                if (uri == null) {
                    return;
                }
                Uri[] uris = {uri};
                mPresenter.onSendImages(uris);


//                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
//                if (parcelableUris == null) {
//                    return;
//                }
//                Uri[] uris = new Uri[parcelableUris.length];
//                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
//                mPresenter.onSendImages(uris);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermitted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                allPermitted = false;
                break;
            }
        }
        if (allPermitted) {
            switch (requestCode) {
                case REQUEST_IMAGE:
                    showImagePicker();
                    break;
                case REQUEST_LOCATION:
                    sendLocation();
                    break;
                //case REQUEST_VIDEO:
                //    selectVideo();
                //    break;
            }
        } else {
            showMessage("Can't do it without permission");
        }
    }

    private boolean needPermission(int requestCode, String... permissions) {
        return PermissionHelper.checkPermission(this, requestCode, permissions);
    }

    private void showAttachmentDialog() {
        if (attachmentDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(ATTACHMENT_VARIANTS, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if (!needPermission(REQUEST_IMAGE, PermissionHelper.CAMERA_PERMISSION, PermissionHelper.STORAGE_PERMISSION)) {
                                showImagePicker();
                            }
                            break;
                        case 2:
                            if (!needPermission(REQUEST_LOCATION, PermissionHelper.LOCATION_PERMISSION1, PermissionHelper.LOCATION_PERMISSION2)) {
                                sendLocation();
                            }
                            break;
                        case 3:
                            showPollCreateView();
                            break;
                        //case 2:
                        //    if (!needPermission(REQUEST_VIDEO, PermissionHelper.STORAGE_PERMISSION)) {
                        //        selectVideo();
                        //    }
                        //    break;
                        case 4:
                            break;
                    }
                    attachmentDialog.dismiss();
                }
            });
            builder.setCancelable(true);
            attachmentDialog = builder.create();
        }
        attachmentDialog.show();
    }

    private void showPollCreateView() {
        uiPollContainer.setVisibility(View.VISIBLE);
    }

    private void sendLocation() {
        if (!Utils.isGooglePlayServiceInstalled()) {
            showMessage("It seems Google play services is not available, can't use location API");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessage("Location permission is not enabled");
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            mPresenter.onSendLocation(currentLocation);
        } else {
            showMessage("Can't get location");
        }
    }

    public static Intent getIntentWithChannel(Context context, Chat conversation) {
        if (null != conversation && null != conversation.getChannel()) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(TAG_CHANNEL_NAME, conversation.getChannel().getName());
            return intent;
        } else {
            Log.e(TAG, "getIntentWithChannel return null because conversation or channel is null");
            return null;
        }
    }

    public static Intent getIntentForNewChannel(Context context, List<User> recipients) {
        Intent intent = new Intent(context, ChatActivity.class);
        ArrayList<User> arrayList = null;
        if (recipients instanceof ArrayList) {
            arrayList = (ArrayList<User>) recipients;
        } else {
            arrayList = new ArrayList<>(recipients);
        }
        intent.putParcelableArrayListExtra(TAG_CREATE_WITH_RECIPIENTS, arrayList);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (uiPollContainer.getVisibility() == View.VISIBLE) {
            uiPollContainer.setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }

    /**
     * Google api connection callback
     */
    private final GoogleApiClient.ConnectionCallbacks connectionCallback = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
        }

        @Override
        public void onConnectionSuspended(int i) {
        }
    };

    /**
     * Google API filed connection callback
     */
    private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    };
}
