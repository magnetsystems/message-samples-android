package com.magnet.messagingsample.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.MessageRecyclerViewAdapter;
import com.magnet.messagingsample.models.MessageImage;
import com.magnet.messagingsample.models.MessageMap;
import com.magnet.messagingsample.models.MessageText;
import com.magnet.messagingsample.models.MessageVideo;
import com.magnet.messagingsample.models.User;
import com.magnet.messagingsample.services.GPSTracker;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

public class ChatActivity extends AppCompatActivity {

    final String TAG = "ChatActivity";

    public static final String KEY_MESSAGE_TEXT = "messageContent";
    public static final String KEY_MESSAGE_IMAGE = "imageContent";
    public static final String KEY_MESSAGE_MAP = "mapContent";
    public static final String KEY_MESSAGE_VIDEO = "videoContent";
    final private int INTENT_REQUEST_GET_IMAGES = 14;

    GPSTracker gps;

    private User mUser;
    List<Object> messageList;

    private MessageRecyclerViewAdapter adapter;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSendText;
    private ImageButton btnSendPicture;
    private ImageButton btnSendLocation;
    private ImageButton btnSendVideo;

    private double myLat;
    private double myLong;

    private MMX.EventListener mEventListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            if (mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_TEXT) != null) {
                updateList(KEY_MESSAGE_TEXT, mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_TEXT).toString(), true);
            } else if (mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_IMAGE) != null) {
                updateList(KEY_MESSAGE_IMAGE, mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_IMAGE).toString(), true);
            } else if (mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_MAP) != null) {
                updateList(KEY_MESSAGE_MAP, mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_MAP).toString(), true);
            } else if (mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_VIDEO) != null) {
                updateList(KEY_MESSAGE_VIDEO, mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_VIDEO).toString(), true);
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(MMXUser mmXid, String s) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mUser = getIntent().getParcelableExtra("User");
        MMX.registerListener(mEventListener);

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

        gps = new GPSTracker(this);

        etMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
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
                sendVideo();
            }
        });

    }

    public void sendMessage() {
        String messageText = etMessage.getText().toString();
        if (messageText.isEmpty()) {
            return;
        }
        updateList(KEY_MESSAGE_TEXT, messageText, false);
        send(KEY_MESSAGE_TEXT, messageText);
        etMessage.setText(null);
    }

    private void selectImage() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
//                .setTabSelectionIndicatorColor(R.color.primary)
//                .setCameraButtonColor(R.color.accent)
                .setSelectionLimit(1)
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
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

                // TODO: upload image to server and store image url
                if (uris != null && uris.length > 0) {
                    for (Uri uri : uris) {
                        updateList(KEY_MESSAGE_IMAGE, uri.toString(), false);
                        send(KEY_MESSAGE_IMAGE, uri.toString());
                    }
                }
            }
        }
    }

    private void getLocation() {
        if (gps.canGetLocation() && gps.getLatitude() != 0.00 && gps.getLongitude() != 0.00) {
            myLat = gps.getLatitude();
            myLong = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }
    }

    private void sendLocation() {
        getLocation();
        if (Double.isNaN(myLat) || Double.isNaN(myLong)) {
            return;
        }
        String latlng = (Double.toString(myLat) + "," + Double.toString(myLong));
        updateList(KEY_MESSAGE_MAP, latlng, false);
        send(KEY_MESSAGE_MAP, latlng);
    }

    private void sendVideo() {
        String videoId = "6qVBB5bKCaM";
        updateList(KEY_MESSAGE_VIDEO, videoId, false);
        send(KEY_MESSAGE_VIDEO, videoId);
    }

    private void send(String type, String text) {
        HashMap<String, String> content = new HashMap<>();
        content.put(type, text);

        HashSet<MMXUser> recipients = new HashSet<>();
        recipients.add(new MMXUser.Builder().username(mUser.getUsername()).build());

        String messageID = new MMXMessage.Builder()
            .content(content)
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

    public void updateList(String type, String content, boolean orientation) {
        switch (type) {
            case ChatActivity.KEY_MESSAGE_TEXT:
                adapter.add(new MessageText(orientation, content));
                break;
            case ChatActivity.KEY_MESSAGE_IMAGE:
                adapter.add(new MessageImage(orientation, content));
                break;
            case ChatActivity.KEY_MESSAGE_MAP:
                adapter.add(new MessageMap(orientation, content));
                break;
            case ChatActivity.KEY_MESSAGE_VIDEO:
                adapter.add(new MessageVideo(orientation, content));
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
        MMX.logout(null);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
