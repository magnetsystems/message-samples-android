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

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.common.MMXErrorMessage;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXPayload;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.protocol.MMXTopic;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

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
public class MyActivity extends Activity implements MMXClient.MMXListener {
  static final String QUICKSTART_USERNAME = "QuickstartUser1";
  static final byte[] QUICKSTART_PASSWORD = "QuickstartUser1".getBytes();

  private static final String TAG = MyActivity.class.getSimpleName();
  private static final String[] TO_LIST = {QUICKSTART_USERNAME, "amazing_bot", "echo_bot"};
  private MMXClient mClient = null;
  private TextView mStatus = null;
  private ImageButton mSendButton = null;
  private EditText mSendText = null;
  private ListView mMessageListView = null;
  private MessageListAdapter mMessageListAdapter = null;
  private String mToUsername = QUICKSTART_USERNAME;

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
    MyMMXListener globalListener = MyMMXListener.getInstance(this);
    globalListener.registerListener(this);
    setContentView(R.layout.activity_my_activity);

    //Setup the views
    mClient = MMXClient.getInstance(this, R.raw.quickstart);
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
    doConnect();
    updateViewState();
  }

  /**
   * On destroying of this activity, unregister this activity as a listener
   * so it won't process any incoming messages.
   */
  @Override
  public void onDestroy() {
    MyMMXListener.getInstance(this).unregisterListener(this);
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
        if (mClient.isConnected()) {
          String username = mClient.getConnectionInfo().username;
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

  /**
   * This callback is invoked if the connection state is changed.  Show the
   * connection state and prompt the user for reconnection if the client is
   * disconnected from the MMX server.
   */
  public void onConnectionEvent(MMXClient mmxClient, MMXClient.ConnectionEvent connectionEvent) {
    if (connectionEvent == MMXClient.ConnectionEvent.DISCONNECTED) {
      AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this)
              .setPositiveButton(R.string.reconnect, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  doConnect();
                }
              })
              .setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                  MyActivity.this.finish();
                }
              })
              .setMessage(R.string.event_disconnected);
      builder.show();
    }
    updateViewState();
  }

  /**
   * This callback is invoked only if an incoming message is received.  Update
   * the view.
   */
  public void onMessageReceived(MMXClient mmxClient, MMXMessage mmxMessage, String deliveryReceiptId) {
    updateViewState();
  }

  /**
   * This callback is invoked only if the message cannot be sent.  It is ignored
   * by this application for now.
   */
  public void onSendFailed(MMXClient mmxClient, String messageId) {
  }

  /**
   * This callback is invoked only if the message sender requests for a delivery
   * receipt and the message recipient returns the receipt.  It is not applicable
   * to this application.
   */
  public void onMessageDelivered(MMXClient mmxClient, MMXid recipient, String messageId) {
  }

  /**
   * This callback is invoked only if a published item is received.  It is not
   * applicable to this application.
   */
  public void onPubsubItemReceived(MMXClient mmxClient, MMXTopic mmxTopic, MMXMessage mmxMessage) {
  }

  /**
   * This callback is invoked only if an error message is received.  It is
   * ignored by this application for now.
   */
  public void onErrorReceived(MMXClient mmxClient, MMXErrorMessage error) {
  }

  /**
   * Connect to the MMX server using the pre-defined username/password.
   */
  private void doConnect() {
    if (!mClient.isConnected()) {
      mClient.connectWithCredentials(QUICKSTART_USERNAME, QUICKSTART_PASSWORD,
              MyMMXListener.getInstance(this), new MMXClient.ConnectionOptions().setAutoCreate(true));
    }
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
          String authorStr = msg.getFrom().getUserId();
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
          datePostedStr = mFormatter.format(msg.getPayload().getSentTime());
          messageStr = msg.getPayload().getDataAsText().toString();
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
    MMXPayload payload = new MMXPayload(messageText);
    String result;
    try {
      String messageID = mClient.getMessageManager().sendPayload(new MMXid(mToUsername), payload, null);
      MyMessageStore.addMessage(null, messageText, new Date(), false);
      mSendText.setText(null);
      result = "Message sent.";
    } catch (MMXException e) {
      Log.e(TAG, "doSendMessage() exception caught", e);
      result = "Exception: " + e.getMessage();
    }
    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    updateViewState();
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
