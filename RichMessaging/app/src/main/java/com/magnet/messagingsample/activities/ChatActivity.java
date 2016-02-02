package com.magnet.messagingsample.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.MessageRecyclerViewAdapter;
import com.magnet.messagingsample.helpers.FileHelper;
import com.magnet.messagingsample.models.MessageImage;
import com.magnet.messagingsample.models.MessageMap;
import com.magnet.messagingsample.models.MessageText;
import com.magnet.messagingsample.models.MessageVideo;
import com.magnet.messagingsample.services.GPSTracker;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

public class ChatActivity extends AppCompatActivity {

    final String TAG = "ChatActivity";

    public static final String KEY_MESSAGE_TEXT = "text";
    public static final String MIME_IMAGE = "image/*";
    public static final String KEY_MESSAGE_MAP = "location";
    public static final String MIME_VIDEO = "video/*";
    final private int INTENT_REQUEST_GET_IMAGES = 14;
    final private int INTENT_SELECT_VIDEO = 13;

    GPSTracker mGPS;

    private User mUser;
    private String mUserName;
    List<Object> messageList;

    private MessageRecyclerViewAdapter adapter;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSendText;
    private ImageButton btnSendPicture;
    private ImageButton btnSendLocation;
    private ImageButton btnSendVideo;

    private MMX.EventListener mEventListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            String type = mmxMessage.getContent().get("type");
            String username = mmxMessage.getSender().getUserName();
            if (mmxMessage.getAttachments().size() > 0) {
                type = mmxMessage.getAttachments().get(0).getMimeType();
            }
            switch (type) {
                case KEY_MESSAGE_TEXT:
                    updateList(username, type, mmxMessage.getContent().get("message"), null, true);
                    break;
                case MIME_IMAGE:
                    if (mmxMessage.getAttachments().size() > 0) {
                        updateList(username, type, "", mmxMessage.getAttachments().get(0), true);
                    }
                    break;
                case KEY_MESSAGE_MAP:
                    updateList(username, type, mmxMessage.getContent().get("latitude") + "," + mmxMessage.getContent().get("longitude"), null, true);
                    break;
                case MIME_VIDEO:
                    if (mmxMessage.getAttachments().size() > 0) {
                        updateList(username, type, "", mmxMessage.getAttachments().get(0), true);
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User mmXid, String s) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (MMX.getCurrentUser() == null) {
            MMX.unregisterListener(mEventListener);
            MMX.logout(null);
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mUserName = getIntent().getStringExtra("User");
        User.getUsersByUserNames(Arrays.asList(mUserName), new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> usersList) {
                for (User user : usersList) {
                    if (user.getUserName().equals(mUserName)) {
                        mUser = user;
                    }
                }
            }

            @Override
            public void failure(ApiError apiError) {
                Toast.makeText(ChatActivity.this, "Unable to find user.", Toast.LENGTH_LONG).show();
            }
        });

        MMX.registerListener(mEventListener);
        mGPS = new GPSTracker(this);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle("Chatting With: " + mUserName);
        }

        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSendText = (ImageButton) findViewById(R.id.btnSendText);
        btnSendPicture = (ImageButton) findViewById(R.id.btnSendPicture);
        btnSendLocation = (ImageButton) findViewById(R.id.btnSendLocation);
        btnSendVideo = (ImageButton) findViewById(R.id.btnSendVideo);

        messageList = new ArrayList<>();
        adapter = new MessageRecyclerViewAdapter(this, messageList);

        rvMessages.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        rvMessages.setLayoutManager(layoutManager);

        etMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        btnSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        btnSendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation();
            }
        });

        btnSendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideo();
            }
        });
    }

    public void sendMessage() {
        String username = User.getCurrentUser().getUserName();
        String messageText = etMessage.getText().toString();
        if (messageText.isEmpty()) {
            return;
        }
        updateList(username, KEY_MESSAGE_TEXT, messageText, null, false);

        HashMap<String, String> content = new HashMap<>();
        content.put("type", KEY_MESSAGE_TEXT);
        content.put("message", messageText);
        send(content, null);
        etMessage.setText(null);
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
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), INTENT_SELECT_VIDEO);
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

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null && uris.length > 0) {
                    for (Uri uri : uris) {
                        sendMedia(MIME_IMAGE, uri.toString());
                    }
                }
            } else if (requestCode == INTENT_SELECT_VIDEO) {
                Uri videoUri = intent.getData();
                String videoPath = FileHelper.getPath(this, videoUri);
                sendMedia(MIME_VIDEO, videoPath);
            }
        }
    }

    private void sendMedia(final String mimeType, String filePath) {
        File f = new File(filePath);
        Attachment attachment = new Attachment(f, mimeType, f.getName(), mimeType);
        attachment.upload(new Attachment.UploadListener() {
            @Override
            public void onStart(Attachment attachment) {
            }

            @Override
            public void onComplete(Attachment attachment) {
                String username = User.getCurrentUser().getUserName();
                updateList(username, mimeType, "", attachment, false);
                send(null, attachment);
            }

            @Override
            public void onError(Attachment attachment, Throwable throwable) {
                Log.e(TAG, "send(): exception during upload", throwable);
            }
        });
    }

    private void sendLocation() {
        if (mGPS.canGetLocation() && mGPS.getLatitude() != 0.00 && mGPS.getLongitude() != 0.00) {
            double myLat = mGPS.getLatitude();
            double myLong = mGPS.getLongitude();
            String latlng = (Double.toString(myLat) + "," + Double.toString(myLong));
            String username = User.getCurrentUser().getUserName();

            updateList(username, KEY_MESSAGE_MAP, latlng, null, false);

            HashMap<String, String> content = new HashMap<>();
            content.put("type", KEY_MESSAGE_MAP);
            content.put("latitude", Double.toString(myLat));
            content.put("longitude", Double.toString(myLong));
            send(content, null);
        }else{
            mGPS.showSettingsAlert(this);
        }
    }

    private void send(HashMap<String, String> content, Attachment attachment) {
        HashSet<User> recipients = new HashSet<>();
        recipients.add(mUser);
        MMXMessage.Builder messageBuilder = new MMXMessage.Builder();
        if (content != null) {
            messageBuilder.content(content);
        }
        if (attachment != null) {
            messageBuilder.attachments(attachment);
        }
        String messageID = messageBuilder
            .recipients(recipients)
                .build()
                .send(new MMXMessage.OnFinishedListener<String>() {
                    public void onSuccess(String s) {
//                        Toast.makeText(ChatActivity.this, "Message sent.", Toast.LENGTH_LONG).show();
                    }

                    public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                        Log.e(TAG, "send() failure: " + failureCode, e);
                        Toast.makeText(ChatActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    public void updateList(String username, String type, String content, Attachment attachment, boolean orientation) {
        switch (type) {
            case KEY_MESSAGE_TEXT:
                adapter.add(new MessageText(orientation, content, username));
                break;
            case MIME_IMAGE:
                adapter.add(new MessageImage(orientation, attachment, username));
                break;
            case KEY_MESSAGE_MAP:
                adapter.add(new MessageMap(orientation, content, username));
                break;
            case MIME_VIDEO:
                adapter.add(new MessageVideo(orientation, attachment, username));
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.getAdapter().notifyDataSetChanged();
                rvMessages.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    /**
     * On destroying of this activity, unregister this activity as a listener
     * so it won't process any incoming messages.
     */
    @Override
    public void onDestroy() {
        MMX.unregisterListener(mEventListener);
        mGPS.stopUsingGPS();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
