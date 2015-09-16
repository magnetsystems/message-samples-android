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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.ParseException;
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

    public static final String KEY_MESSAGE_TEXT = "text";
    public static final String KEY_MESSAGE_IMAGE = "photo";
    public static final String KEY_MESSAGE_MAP = "location";
    public static final String KEY_MESSAGE_VIDEO = "video";
    final private int INTENT_REQUEST_GET_IMAGES = 14;
    final private int INTENT_SELECT_VIDEO = 13;

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
            String type = mmxMessage.getContent().get("type");
            switch (type) {
                case KEY_MESSAGE_TEXT:
                    updateList(KEY_MESSAGE_TEXT, mmxMessage.getContent().get("message"), true);
                    break;
                case KEY_MESSAGE_IMAGE:
                    updateList(KEY_MESSAGE_IMAGE, mmxMessage.getContent().get("url"), true);
                    break;
                case KEY_MESSAGE_MAP:
                    updateList(KEY_MESSAGE_MAP, mmxMessage.getContent().get("latitude") + "," + mmxMessage.getContent().get("longitude"), true);
                    break;
                case KEY_MESSAGE_VIDEO:
                    updateList(KEY_MESSAGE_VIDEO, mmxMessage.getContent().get("url"), true);
                    break;
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
//                selectVideo();
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

        HashMap<String, String> content = new HashMap<>();
        content.put("type", KEY_MESSAGE_TEXT);
        content.put("message", messageText);
        send(KEY_MESSAGE_TEXT, content);
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

                // TODO: upload image to server and store image url
                if (uris != null && uris.length > 0) {
                    for (Uri uri : uris) {
                        updateList(KEY_MESSAGE_IMAGE, uri.toString(), false);

                        HashMap<String, String> content = new HashMap<>();
                        content.put("type", KEY_MESSAGE_IMAGE);
                        content.put("url", uri.toString());
                        send(KEY_MESSAGE_IMAGE, content);
                    }
                }
            } else if (requestCode == INTENT_SELECT_VIDEO) {
                Uri selectedImageUri = intent.getData();
                String selectedPath = selectedImageUri.toString();
                sendVideo();
            }
        }
    }

//    private void sendVideo2(String videoPath) throws ParseException, IOException {
//
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost(YOUR_URL);
//
//        FileBody filebodyVideo = new FileBody(new File(videoPath));
//        StringBody title = new StringBody("Filename: " + videoPath);
//        StringBody description = new StringBody("This is a video of the agent");
//        StringBody code = new StringBody(realtorCodeStr);
//
//        MultipartEntity reqEntity = new MultipartEntity();
//        reqEntity.addPart("videoFile", filebodyVideo);
//        reqEntity.addPart("title", title);
//        reqEntity.addPart("description", description);
//        reqEntity.addPart("code", code);
//        httppost.setEntity(reqEntity);
//
//        // DEBUG
//        System.out.println( "executing request " + httppost.getRequestLine( ) );
//        HttpResponse response = httpclient.execute( httppost );
//        HttpEntity resEntity = response.getEntity( );
//
//        // DEBUG
//        System.out.println( response.getStatusLine( ) );
//        if (resEntity != null) {
//            System.out.println( EntityUtils.toString( resEntity ) );
//        } // end if
//
//        if (resEntity != null) {
//            resEntity.consumeContent( );
//        } // end if
//
//        httpclient.getConnectionManager( ).shutdown( );
//    }

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

        HashMap<String, String> content = new HashMap<>();
        content.put("type", KEY_MESSAGE_MAP);
        content.put("latitude", Double.toString(myLat));
        content.put("longitude", Double.toString(myLong));
        send(KEY_MESSAGE_MAP, content);
    }

    private void sendVideo() {
        String videoUrl = "http://jgfbucket.s3-us-west-1.amazonaws.com/magnet_test/small.mp4";
        updateList(KEY_MESSAGE_VIDEO, videoUrl, false);

        HashMap<String, String> content = new HashMap<>();
        content.put("type", KEY_MESSAGE_VIDEO);
        content.put("url", videoUrl);
        send(KEY_MESSAGE_VIDEO, content);
    }

    private void send(String type, HashMap<String, String> content) {
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
