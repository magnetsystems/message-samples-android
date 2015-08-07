package com.magnet.demo.mmx.soapbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXTask;
import com.magnet.mmx.client.common.MMXErrorMessage;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXGlobalTopic;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.client.common.MMXPayload;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.TopicAction;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class TopicItemListActivity extends Activity {
  private static final String TAG = TopicItemListActivity.class.getSimpleName();
  private static final String KEY_USERNAME = "username";

  private MyProfile mProfile;
  private MMXClient mClient;
  private MMXTopic mTopic;
  private MMXTask<List<MMXMessage>> mTask;
  private List<MMXMessage> mTopicItems;
  private ListView mTopicItemsView;
  private TextView mTopicName;
  private EditText mPublishText;
  private AtomicBoolean mScrollToBottom = new AtomicBoolean(true);

  private MMXClient.MMXListener mListener = new MMXClient.MMXListener() {
    public void onConnectionEvent(MMXClient mmxClient, MMXClient.ConnectionEvent connectionEvent) {

    }

    public void onMessageReceived(MMXClient mmxClient, MMXMessage mmxMessage, String s) {

    }

    public void onSendFailed(MMXClient mmxClient, String s) {

    }

    public void onMessageDelivered(MMXClient mmxClient, MMXid mmXid, String s) {

    }

    public void onPubsubItemReceived(MMXClient mmxClient, MMXTopic mmxTopic, MMXMessage mmxMessage) {
      if (mmxTopic.getName().equals(mTopic.getName())) {
        //update the items if we are currently viewing the topic
        updateTopicItems();
      }
    }

    public void onErrorReceived(MMXClient mmxClient, MMXErrorMessage mmxErrorMessage) {

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_topic_item_list);

    String topicName = getIntent().getStringExtra(TopicListActivity.EXTRA_TOPIC_NAME);
    mTopic = new MMXGlobalTopic(topicName);
    Log.d(TAG, "onCreate(): topicName=" + topicName);
    MyMMXListener.getInstance(this).registerListener(mListener);
    mProfile = MyProfile.getInstance(this);
    mClient = MMXClient.getInstance(this, R.raw.soapbox);
    mTopicItemsView = (ListView) findViewById(R.id.topic_items);
    mTopicName = (TextView) findViewById(R.id.topic_name);
    mPublishText = (EditText) findViewById(R.id.publishMessage);
    mTopicName.setText(topicName);
  }

  protected void onDestroy() {
    MyMMXListener.getInstance(this).unregisterListener(mListener);
    super.onDestroy();
  }

  protected void onResume() {
    super.onResume();
    updateTopicItems();
  }

  private void updateTopicItems() {
    synchronized (this) {
      if (mTask == null) {
        Log.d(TAG, "onResume(): loading items for topic: " + mTopic.getName());
        mTask = new MMXTask<List<MMXMessage>>(mClient) {
          @Override
          public List<MMXMessage> doRun(MMXClient mmxClient) throws Throwable {
            return mClient.getPubSubManager().getItems(mTopic,
                    new TopicAction.FetchOptions().setMaxItems(25).setAscending(false));
          }

          @Override
          public void onException(Throwable exception) {
            Toast.makeText(TopicItemListActivity.this, "Unable to retrieve items: "
                    + exception.getMessage(), Toast.LENGTH_LONG).show();
            synchronized (TopicItemListActivity.this) {
              mTask = null;
            }
          }

          @Override
          public void onResult(List<MMXMessage> result) {
            //reverse the list
            mTopicItems = new ArrayList<MMXMessage>();
            for (int i=result.size(); --i>=0;) {
              mTopicItems.add(result.get(i));
            }
            mScrollToBottom.set(true);
            updateListView();
            synchronized (TopicItemListActivity.this) {
              mTask = null;
            }
          }
        };
        mTask.execute();
      } else {
        Log.d(TAG, "onResume():  Already loading items...");
      }
    }
  }

  private void updateListView() {
    runOnUiThread(new Runnable() {
      public void run() {
        if (mTopicItems != null) {
          TopicItemsAdapter adapter = new TopicItemsAdapter(TopicItemListActivity.this, mTopicItems, mProfile);
          mTopicItemsView.setAdapter(adapter);
          if (mScrollToBottom.compareAndSet(true, false)) {
            mTopicItemsView.setSelection(adapter.getCount() - 1);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mPublishText.getWindowToken(), 0);
          }
        }
      }
    });
  }

  public void doBack(View view) {
    onBackPressed();
  }

  public void doPublish(final View view) {
    try {
      MMXPayload payload = new MMXPayload(mPublishText.getText());
      payload.setMetaData(KEY_USERNAME, mProfile.getUsername());
      String messageId = mClient.getPubSubManager().publish(mTopic, payload);
      mPublishText.setText(null);
      mScrollToBottom.set(true);
      updateTopicItems();
    } catch (MMXException e) {
      Toast.makeText(this, "Unable to publish message: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  public void doShowMenu(View view) {
    PopupMenu popup = new PopupMenu(this, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_topic_item_list, popup.getMenu());

    //decide which items to hide
    Menu menu = popup.getMenu();

    TopicsManager tm = TopicsManager.getInstance(this);
    if (tm.isTopicSubscribed(mTopic)) {
      //remove subcribe
      menu.removeItem(R.id.action_subscribe);
    } else {
      //remove unsubscribe
      menu.removeItem(R.id.action_unsubscribe);
    }

    if (!tm.canDelete(mTopic, mProfile)) {
      menu.removeItem(R.id.action_delete);
    }

    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.action_subscribe:
            doSubscribe();
            break;
          case R.id.action_unsubscribe:
            doUnsubscribe();
            break;
          case R.id.action_delete:
            doDelete();
            break;
        }
        return true;
      }
    });

    popup.show();
  }

  public void doSubscribe() {
    MMXTask<String> subscribeTask = new MMXTask<String>(mClient) {
      @Override
      public String doRun(MMXClient mmxClient) throws Throwable {
        return mmxClient.getPubSubManager().subscribe(mTopic, false);
      }

      @Override
      public void onException(Throwable exception) {
        Toast.makeText(TopicItemListActivity.this, "Unable to subscribe: " + exception.getMessage(), Toast.LENGTH_LONG).show();
      }

      @Override
      public void onResult(String result) {
        Toast.makeText(TopicItemListActivity.this, "Subscribed successfully", Toast.LENGTH_LONG).show();
      }
    };
    subscribeTask.execute();
  }

  public void doUnsubscribe() {
    MMXTask<Boolean> unsubscribeTask = new MMXTask<Boolean>(mClient) {
      @Override
      public Boolean doRun(MMXClient mmxClient) throws Throwable {
        return mmxClient.getPubSubManager().unsubscribe(mTopic, null);
      }

      @Override
      public void onException(Throwable exception) {
        Toast.makeText(TopicItemListActivity.this, "Exception caught: " + exception.getMessage(), Toast.LENGTH_LONG).show();
      }

      @Override
      public void onResult(Boolean result) {
        if (result.booleanValue()) {
          Toast.makeText(TopicItemListActivity.this, "Unsubscribed successfully", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(TopicItemListActivity.this, "Could not unsubscribe.", Toast.LENGTH_LONG).show();
        }
      }
    };
    unsubscribeTask.execute();
  }

  public void doDelete() {
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MMXTask<MMXStatus> deleteTask = new MMXTask<MMXStatus>(mClient) {
              @Override
              public MMXStatus doRun(MMXClient mmxClient) throws Throwable {
                return mmxClient.getPubSubManager().deleteTopic(mTopic);
              }

              @Override
              public void onException(Throwable exception) {
                Toast.makeText(TopicItemListActivity.this,
                        getString(R.string.error_unable_to_delete_topic) + exception.getMessage(), Toast.LENGTH_LONG).show();
              }

              @Override
              public void onResult(MMXStatus result) {
                if (result.getCode() != MMXStatus.SUCCESS) {
                  Toast.makeText(TopicItemListActivity.this,
                          getString(R.string.error_unable_to_delete_topic) + result.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                  TopicItemListActivity.this.finish();
                }
              }
            };
            deleteTask.execute();

            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
        dialog.dismiss();
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle(R.string.dlg_delete_title)
            .setPositiveButton(R.string.dlg_delete_ok, clickListener)
            .setNegativeButton(R.string.dlg_delete_cancel, clickListener);
    builder.show();
  }

  private static class TopicItemsAdapter extends ArrayAdapter<MMXMessage> {
    private static final int[] COLOR_IDS = {R.color.chat_1, R.color.chat_2, R.color.chat_3, R.color.chat_4, R.color.chat_5, R.color.chat_6};
    private static final int TYPE_ME = 0;
    private static final int TYPE_THEM = 1;
    private MyProfile mProfile;
    private LayoutInflater mInflater;
    private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public TopicItemsAdapter(Context context, List<MMXMessage> messages, MyProfile profile) {
      super(context, 0, messages);
      mProfile = profile;
      mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      int type = getItemViewType(position);
      MMXMessage message = getItem(position);
      int colorResId = 0;
      String authorStr = message.getPayload().getMetaData(KEY_USERNAME, null);
      if (authorStr == null) {
        authorStr = getContext().getString(R.string.chat_unknown);
      }
      switch (type) {
        case TYPE_ME:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.topic_item_me, null);
          }
          colorResId = R.color.chat_me;
          break;
        case TYPE_THEM:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.topic_item_them, null);
          }
          //set author and color
          colorResId = COLOR_IDS[Math.abs(authorStr.hashCode() % COLOR_IDS.length)];

          TextView author = (TextView) convertView.findViewById(R.id.author);
          author.setText(authorStr + " - ");
          break;
      }
      TextView datePosted = (TextView) convertView.findViewById(R.id.datePosted);
      datePosted.setText(mFormatter.format(message.getPayload().getSentTime()));
      TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
      messageText.setBackgroundResource(colorResId);
      messageText.setText(message.getPayload().getDataAsText());
      return convertView;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
      MMXMessage message = getItem(position);
      if (mProfile.getUsername().equals(message.getFrom().getUserId())) {
        //me
        return TYPE_ME;
      } else {
        //them
        return TYPE_THEM;
      }
    }
  }
}
