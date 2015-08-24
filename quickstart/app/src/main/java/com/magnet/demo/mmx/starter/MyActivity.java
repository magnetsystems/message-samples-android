/*   Copyright (c) 2015 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.magnet.demo.mmx.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.api.MMX;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the primary activity for this application.  It establishes a
 * connection to the MMX server, registers a global listener
 * <code>MyMMXListener</code> to receive incoming messages from this connection,
 * and shows received messages in a List view. The global listener
 * <code>MyMMXListener</code> extending from AbstractMMXListener automatically
 * dispatches the incoming messages to this activity registered as a local
 * listener.  It also saves the messages to an in-memory
 * <code>MyMessageStore</code> and posts a notification to the Status Bar in
 * case this activity is not available to show the incoming message.  Furthermore,
 * user can also send a message to self or one of the two bots from this
 * activity.
 */
public class MyActivity extends Activity {
  static final String QUICKSTART_USERNAME = "QuickstartUser1";
  static final byte[] QUICKSTART_PASSWORD = "QuickstartUser1".getBytes();
  static final String KEY_MESSAGE_TEXT = "textContent";

  private static final String TAG = MyActivity.class.getSimpleName();
  private static final String[] TO_LIST = {QUICKSTART_USERNAME, "amazing_bot", "echo_bot"};
  private TextView mStatus = null;
  private ImageButton mSendButton = null;
  private EditText mSendText = null;
  private ListView mMessageListView = null;
  private MessageListAdapter mMessageListAdapter = null;
  private String mToUsername = QUICKSTART_USERNAME;
  private AtomicBoolean mLoginSuccess = new AtomicBoolean(false);

  private MMX.EventListener mEventListener =
          new MMX.EventListener() {
            public boolean onMessageReceived(MMXMessage mmxMessage) {
              updateViewState();
              return false;
            }

            @Override
            public boolean onMessageAcknowledgementReceived(MMXUser mmXid, String s) {
              return false;
            }
          };

  /**
   * On creating this activity, register this activity as a local listener to
   * the global listener, establish a connection to the MMX server and register
   * the global listener to the connection.
   * @see #onDestroy()
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Register this activity as a listener to receive and show incoming
    // messages.  See #onDestroy for the unregister call.
    MMX.registerListener(mEventListener);
    MMXUser quickstartUser = new MMXUser.Builder()
            .username(QUICKSTART_USERNAME)
            .displayName(QUICKSTART_USERNAME)
            .build();
    quickstartUser.register(QUICKSTART_PASSWORD, new MMXUser.OnFinishedListener<Void>() {
      public void onSuccess(Void aVoid) {
        Log.d(TAG, "register user succeeded");
        loginHelper();
      }

      public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
        Log.d(TAG, "register user failed because: " + failureCode);
        loginHelper();
      }
    });
    setContentView(R.layout.activity_my_activity);

    //Setup the views
    mStatus = (TextView) findViewById(R.id.status_field);
    mSendText = (EditText) findViewById(R.id.message_text);
    mSendButton = (ImageButton) findViewById(R.id.btn_send);
    mMessageListView = (ListView) findViewById(R.id.message_list_view);
    mMessageListAdapter = new MessageListAdapter(this, MyMessageStore.getMessageList(), QUICKSTART_USERNAME);
    mMessageListView.setAdapter(mMessageListAdapter);
    mSendText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                || actionId == EditorInfo.IME_ACTION_DONE) {
          doSendMessage(v);
        }
        return false;
      }
    });
    updateViewState();
  }

  private void loginHelper() {
    MMX.login(QUICKSTART_USERNAME, QUICKSTART_PASSWORD, new MMX.OnFinishedListener<Void>() {
      public void onSuccess(Void aVoid) {
        mLoginSuccess.set(true);
        MMX.enableIncomingMessages(true);
        updateViewState();
      }

      public void onFailure(MMX.FailureCode failureCode, Throwable e) {
        mLoginSuccess.set(false);
        updateViewState();
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

  /**
   * On resuming of this activity, just update the view state.
   */
  @Override
  public void onResume() {
    updateViewState();
    super.onResume();
  }

  /**
   * This can be called from anywhere to make sure that the view is updated.
   */
  private void updateViewState() {
    runOnUiThread(new Runnable() {
      public void run() {
        if (mLoginSuccess.get()) {
          String username = MMX.getCurrentUser().getUsername();
          String status = getString(R.string.status_connected) +
                  (username != null ? " as " + username : " " + getString(R.string.user_anonymously));
          mStatus.setText(status);
          mSendButton.setEnabled(true);
        } else {
          mStatus.setText(R.string.status_disconnected);
          mSendButton.setEnabled(false);
        }

        List<MyMessageStore.Message> messageList = MyMessageStore.getMessageList();
        mMessageListAdapter.setMessageList(messageList);
        mMessageListView.smoothScrollToPosition(mMessageListAdapter.getCount());
      }
    });
  }

  private static class MessageListAdapter extends BaseAdapter {
    private static final int[] COLOR_IDS = {R.color.chat_1, R.color.chat_2, R.color.chat_3, R.color.chat_4, R.color.chat_5, R.color.chat_6};
    private static final int TYPE_ME = 0;
    private static final int TYPE_THEM = 1;
    private String mUsername;
    private List<MyMessageStore.Message> mMessageList = null;
    private LayoutInflater mInflater;
    private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public MessageListAdapter(Context context, List<MyMessageStore.Message> messageList, String username) {
      mUsername = username;
      mMessageList = messageList;
      mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      int type = getItemViewType(position);
      int colorResId = 0;
      String datePostedStr = null;
      String messageStr = null;
      MyMessageStore.Message message = (MyMessageStore.Message)getItem(position);
      switch (type) {
        case TYPE_ME:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item_me, null);
          }
          colorResId = R.color.chat_me;
          datePostedStr = mFormatter.format(message.getTimestamp());
          messageStr = message.getSentText();
          break;
        case TYPE_THEM:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item_them, null);
          }
          //set author and color
          MMXMessage msg = message.getMessage();
          String authorStr = msg.getSender().getUsername();
          for (int i=TO_LIST.length; --i >= 0;) {
            if (TO_LIST[i].equalsIgnoreCase(authorStr)) {
              colorResId = COLOR_IDS[i];
              break;
            }
          }
          if (colorResId == 0) {
            colorResId = COLOR_IDS[Math.abs(authorStr.hashCode() % COLOR_IDS.length)];
          }
          TextView author = (TextView) convertView.findViewById(R.id.author);
          author.setText(authorStr + " - ");
          datePostedStr = mFormatter.format(msg.getTimestamp());
          Object textObj = msg.getContent().get(KEY_MESSAGE_TEXT);
          messageStr = textObj != null ? textObj.toString() : "<no text>";
          break;
      }
      TextView datePosted = (TextView) convertView.findViewById(R.id.datePosted);
      datePosted.setText(datePostedStr);
      TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
      messageText.setBackgroundResource(colorResId);
      messageText.setText(messageStr);
      return convertView;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
      MyMessageStore.Message message = (MyMessageStore.Message)getItem(position);
      if (!message.isIncoming()) {
        //me
        return TYPE_ME;
      } else {
        //them
        return TYPE_THEM;
      }
    }

    private void setMessageList(List<MyMessageStore.Message> messageList) {
      mMessageList = messageList;
      notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
      return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }
  }

  public void doSendMessage(View view) {
    String messageText = mSendText.getText().toString();
    if (messageText.isEmpty()) {
      //don't send an empty message
      return;
    }
    HashMap<String, String> content = new HashMap<String, String>();
    content.put(KEY_MESSAGE_TEXT, messageText);

    HashSet<MMXUser> recipients = new HashSet<MMXUser>();
    recipients.add(new MMXUser.Builder().username(mToUsername).build());

    String messageID = new MMXMessage.Builder()
            .content(content)
            .recipients(recipients)
            .build()
            .send(new MMXMessage.OnFinishedListener<String>() {
              public void onSuccess(String s) {
                Toast.makeText(MyActivity.this, "Message sent.", Toast.LENGTH_LONG).show();
                updateViewState();
              }

              public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                Log.e(TAG, "doSendMessage() failure: " + failureCode, e);
                Toast.makeText(MyActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
              }
            });
    MyMessageStore.addMessage(null, messageText, new Date(), false);
    mSendText.setText(null);
  }

  public void showToDialog(View view) {
    AlertDialog toDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
            .setTitle(R.string.title_send_to)
            .setAdapter(new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1, TO_LIST),
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        mToUsername = TO_LIST[which];
                      }
                    })
            .create();
    toDialog.show();
  }
}
