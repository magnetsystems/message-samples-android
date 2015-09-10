package com.magnet.messagingsample.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.CommentArrayAdapter;
import com.magnet.messagingsample.models.Comment;
import com.magnet.messagingsample.models.MessageStore;
import com.magnet.messagingsample.models.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatActivity extends AppCompatActivity {

    final String TAG = "ChatActivity";

    public static final String KEY_MESSAGE_TEXT = "messageContent";

    private User mUser;

    private CommentArrayAdapter adapter;
    private AtomicBoolean mLoginSuccess = new AtomicBoolean(false);

    private ListView lvComments;
    private static Random random;
    private EditText etMessage;
    private ImageButton btnSend;

    private MMX.EventListener mEventListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            updateViewState(mmxMessage.getContent().get(ChatActivity.KEY_MESSAGE_TEXT).toString(), false);
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

        lvComments = (ListView) findViewById(R.id.lvComments);
        etMessage = (EditText) findViewById(R.id.etMessage);

        adapter = new CommentArrayAdapter(getApplicationContext(), R.layout.activity_chat);

        lvComments.setAdapter(adapter);

        etMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    doSendMessage();
                    return true;
                }
                return false;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSendMessage();
            }
        });

        addItems();
    }

    public void doSendMessage() {
        String messageText = etMessage.getText().toString();
        if (messageText.isEmpty()) {
            //don't send an empty message
            return;
        }
        HashMap<String, String> content = new HashMap<String, String>();
        content.put(KEY_MESSAGE_TEXT, messageText);

        HashSet<MMXUser> recipients = new HashSet<MMXUser>();
        recipients.add(new MMXUser.Builder().username(mUser.getUsername()).build());

        updateViewState(messageText, true);

        String messageID = new MMXMessage.Builder()
            .content(content)
            .recipients(recipients)
            .build()
            .send(new MMXMessage.OnFinishedListener<String>() {
                public void onSuccess(String s) {
                    Toast.makeText(ChatActivity.this, "Message sent.", Toast.LENGTH_LONG).show();
                }

                public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                    Log.e(TAG, "doSendMessage() failure: " + failureCode, e);
                    Toast.makeText(ChatActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        etMessage.setText(null);
    }

    private void updateViewState(String comment, boolean isOwner) {
        adapter.add(new Comment(isOwner, comment));
    }

    private void addItems() {
        random = new Random();
        adapter.add(new Comment(true, "Hello bubbles!"));

        for (int i = 0; i < 4; i++) {
            boolean left = getRandomInteger(0, 1) == 0 ? true : false;
            int word = getRandomInteger(1, 10);
            int start = getRandomInteger(1, 40);
            String words = "hello world";

            adapter.add(new Comment(left, words));
        }
    }

    private static int getRandomInteger(int aStart, int aEnd) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        long range = (long) aEnd - (long) aStart + 1;
        long fraction = (long) (range * random.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
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
