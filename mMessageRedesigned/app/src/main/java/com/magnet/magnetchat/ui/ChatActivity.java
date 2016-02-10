package com.magnet.magnetchat.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.FileHelper;
import com.magnet.magnetchat.helpers.PermissionHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.adapters.MessagesAdapter;
import com.magnet.magnetchat.ui.custom.FEditText;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

import butterknife.InjectView;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

public class ChatActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = ChatActivity.class.getSimpleName();

    public static final String TAG_CHANNEL_NAME = "channelName";
    public static final String TAG_CREATE_WITH_USER_ID = "createWithUserId";
    public static final String TAG_CREATE_NEW = "createNew";

    private static final String[] ATTACHMENT_VARIANTS = {"Send photo", "Send location", "Send video", "Cancel"};

    public static final int INTENT_REQUEST_GET_IMAGES = 14;
    public static final int INTENT_SELECT_VIDEO = 13;

    public static final int REQUEST_LOCATION = 1111;
    public static final int REQUEST_VIDEO = 1112;
    public static final int REQUEST_IMAGE = 1113;

    private Conversation currentConversation;
    private MessagesAdapter adapter;
    private RecyclerView messagesListView;
    private String channelName;
    private AlertDialog attachmentDialog;
    private GoogleApiClient googleApiClient;

    private ProgressBar chatMessageProgress;

    @InjectView(R.id.chatMessageField)
    FEditText editMessage;
    @InjectView(R.id.chatSuppliers)
    FTextView textChatSupliers;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatMessageProgress = (ProgressBar) findViewById(R.id.chatMessageProgress);

        findViewById(R.id.chatSendBtn).setOnClickListener(this);
        findViewById(R.id.chatAddAttachment).setOnClickListener(this);

        messagesListView = (RecyclerView) findViewById(R.id.chatMessageList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        messagesListView.setLayoutManager(layoutManager);
        if (getIntent().getBooleanExtra(TAG_CREATE_NEW, false)) {
            String[] userIds = getIntent().getStringArrayExtra(TAG_CREATE_WITH_USER_ID);
            if (userIds != null) {
                ChannelHelper.getInstance().createChannelForUsers(userIds, createListener);
            }
        } else {
            channelName = getIntent().getStringExtra(TAG_CHANNEL_NAME);
            if (channelName != null) {
                currentConversation = ChannelCacheManager.getInstance().getConversationByName(channelName);
                if (currentConversation == null) {
                    showMessage("Can load the conversation");
                    finish();
                    return;
                }
                updateConversationUserList();
            }
        }

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatSendBtn:
                String text = editMessage.getStringValue();
                if (text != null && !text.isEmpty()) {
                    sendText(text);
                }
                break;
            case R.id.chatAddAttachment:
                showAttachmentDialog();
                break;
        }
    }

    @Override
    protected void onPause() {
        MMX.unregisterListener(eventListener);
        if (attachmentDialog != null && attachmentDialog.isShowing()) {
            attachmentDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentConversation != null) {
            prepareConversation(currentConversation);
        }
        MMX.registerListener(eventListener);
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
        switch (item.getItemId()) {
            case R.id.menuChatOpenDetails:
                if (currentConversation != null) {
                    String name = currentConversation.getChannel().getName();
                    startActivity(DetailsActivity.createIntentForChannel(name));
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                if (parcelableUris == null) {
                    return;
                }
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris.length > 0) {
                    for (Uri uri : uris) {
                        chatMessageProgress.setVisibility(View.VISIBLE);
                        String filePath = uri.toString();
                        currentConversation.sendPhoto(filePath, FileHelper.getMimeType(this, uri, filePath, Message.FILE_TYPE_PHOTO), sendMessageListener);
                    }
                }
            } else if (requestCode == INTENT_SELECT_VIDEO) {
                Uri videoUri = intent.getData();
                String videoPath = FileHelper.getPath(this, videoUri);
                Logger.debug(TAG, "selected video from Uri : " + videoUri + " file path : " + videoPath);
                if (StringUtil.isNotEmpty(videoPath)) {
                    chatMessageProgress.setVisibility(View.VISIBLE);
                    currentConversation.sendVideo(videoPath, FileHelper.getMimeType(this, videoUri, videoPath, Message.FILE_TYPE_VIDEO), sendMessageListener);
                } else {
                    Logger.error(TAG, "Can't read video from Uri : " + videoUri);
                    showMessage("Can't read the video file");
                }
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
                    selectImage();
                    break;
                case REQUEST_LOCATION:
                    sendLocation();
                    break;
                case REQUEST_VIDEO:
                    selectVideo();
                    break;
            }
        } else {
            showMessage("Can't do it without permission");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                                selectImage();
                            }
                            break;
                        case 1:
                            if (!needPermission(REQUEST_LOCATION, PermissionHelper.LOCATION_PERMISSION1, PermissionHelper.LOCATION_PERMISSION2)) {
                                sendLocation();
                            }
                            break;
                        case 2:
                            if (!needPermission(REQUEST_VIDEO, PermissionHelper.STORAGE_PERMISSION)) {
                                selectVideo();
                            }
                            break;
                        case 3:
                            break;
                    }
                    attachmentDialog.dismiss();
                }
            });
            builder.setCancelable(false);
            attachmentDialog = builder.create();
        }
        attachmentDialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)
                .setSelectionLimit(1)
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    private void selectVideo() {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, INTENT_SELECT_VIDEO);
        } else {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a Video "), INTENT_SELECT_VIDEO);
        }

    }

    private void sendText(String text) {
        currentConversation.sendTextMessage(text, sendMessageListener);
    }

    private void sendLocation() {
        if (!Utils.isGooglePlayServiceInstalled(this)) {
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
            currentConversation.sendLocation(currentLocation, sendMessageListener);
        } else {
            showMessage("Can't get location");
        }
    }

    private void setMessagesList(List<Message> messages) {
        adapter = new MessagesAdapter(this, messages);
        messagesListView.setAdapter(adapter);
    }

    private void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            messagesListView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    private void prepareConversation(Conversation conversation) {
        String channelName = conversation.getChannel().getName();
        if (channelName == null) {
            finish();
            return;
        }
        if (ChannelCacheManager.getInstance().getConversation(channelName) == null) {
            ChannelCacheManager.getInstance().addConversation(channelName, conversation);
        }
        currentConversation = conversation;
        updateUsers();
        conversation.setHasUnreadMessage(false);
        ChannelCacheManager.getInstance().setConversationListUpdated();
        setMessagesList(conversation.getMessages());
    }

    private void updateUsers() {
        List<UserProfile> suppliersList = currentConversation.getSuppliersList();
        if (suppliersList.size() == 1) {
            setTitle(UserHelper.getDisplayNames(suppliersList));
            findViewById(R.id.chatSuppliers).setVisibility(View.GONE);
        } else {
            setTitle("Group");
            findViewById(R.id.chatSuppliers).setVisibility(View.VISIBLE);
            String suppliers = UserHelper.getDisplayNames(suppliersList);
            textChatSupliers.setText(String.format("To: %s", suppliers));
        }
    }

    private Conversation.OnSendMessageListener sendMessageListener = new Conversation.OnSendMessageListener() {
        @Override
        public void onSuccessSend(Message message) {
            chatMessageProgress.setVisibility(View.GONE);
            ChannelCacheManager.getInstance().getMessagesToApproveDeliver().put(message.getMessageId(), message);
            if (message.getType() != null && message.getType().equals(Message.TYPE_TEXT)) {
                editMessage.clear();
            }
            updateList();
        }

        @Override
        public void onFailure(Throwable throwable) {
            chatMessageProgress.setVisibility(View.GONE);
            Logger.error(TAG, "send message error", throwable);
            showMessage("Can't send message");
        }
    };

    private ChannelHelper.OnCreateChannelListener createListener = new ChannelHelper.OnCreateChannelListener() {
        @Override
        public void onSuccessCreated(MMXChannel channel) {
            channelName = channel.getName();
            ChannelHelper.getInstance().readChannelInfo(channel, readChannelInfoListener);
        }

        @Override
        public void onChannelExists(MMXChannel channel) {
            currentConversation = ChannelCacheManager.getInstance().getConversationByName(channel.getName());
            if (currentConversation == null) {
                ChannelHelper.getInstance().readChannelInfo(channel, readChannelInfoListener);
            } else {
                prepareConversation(currentConversation);
            }
        }

        @Override
        public void onFailureCreated(Throwable throwable) {
            showMessage("Can't create conversation");
            finish();
        }
    };

    private ChannelHelper.OnReadChannelInfoListener readChannelInfoListener = new ChannelHelper.OnReadChannelInfoListener() {
        @Override
        public void onSuccessFinish(Conversation lastConversation) {
            if (lastConversation == null) {
                String message = "Can't load conversation";
                Logger.debug(TAG, message);
                showMessage(message);
                finish();
            } else {
                prepareConversation(lastConversation);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            showMessage("Can't read conversation information");
            finish();
        }
    };

    private MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug(TAG, "Received message in : " + mmxMessage);
            if (adapter != null && mmxMessage.getChannel() != null && StringUtil.isStringValueEqual(channelName, mmxMessage.getChannel().getName())) {
                currentConversation.addMessage(Message.createMessageFrom(mmxMessage));
                updateList();
                currentConversation.setHasUnreadMessage(false);

                return true;
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            if (adapter != null) {
                //updateList();
            }
            return true;
        }
    };

    public static Intent getIntentWithChannel(Conversation conversation) {
        String name = conversation.getChannel().getName();
        Intent intent = new Intent(CurrentApplication.getInstance(), ChatActivity.class);
        intent.putExtra(TAG_CHANNEL_NAME, name);
        return intent;
    }

    public static Intent getIntentForNewChannel(String[] userId) {
        Intent intent = new Intent(CurrentApplication.getInstance(), ChatActivity.class);
        intent.putExtra(TAG_CREATE_NEW, true);
        intent.putExtra(TAG_CREATE_WITH_USER_ID, userId);
        return intent;
    }

    private void updateConversationUserList() {
        final MMXChannel channel = currentConversation.getChannel();
        if (channel == null) {
            return;
        }
        channel.getAllSubscribers(100, 0, new MMXChannel.OnFinishedListener<ListResult<User>>() {
            @Override
            public void onSuccess(ListResult<User> userListResult) {
                boolean subscribersUpdated = userListResult.items.size() != currentConversation.getSuppliersList().size();
                if (!subscribersUpdated) {
                    for (User u : userListResult.items) {
                        if (null == currentConversation.getSupplier(u.getUserIdentifier())) {
                            subscribersUpdated = true;
                            break;
                        }
                    }
                }

                if (subscribersUpdated) {
                    Logger.debug("channel subscribers", "success. channel " + channel.getName() + " : " + userListResult.items);
                    for (User user : userListResult.items) {
                        if (!user.getUserIdentifier().equals(User.getCurrentUserId())) {
                            currentConversation.addSupplier(user);
                        }
                    }

                    updateUsers();
                }

                Log.d(TAG, "subscribersUpdated = " + subscribersUpdated);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error(TAG, "channel subscribers", throwable);
            }
        });
    }
}
