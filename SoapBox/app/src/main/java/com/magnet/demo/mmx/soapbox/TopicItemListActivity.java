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

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class TopicItemListActivity extends Activity {
  private static final String TAG = TopicItemListActivity.class.getSimpleName();
  private static final String KEY_MESSAGE_TEXT = "messageText";

  private MyProfile mProfile;
  private MMXChannel mChannel;
  private List<MMXMessage> mTopicItems;
  private ListView mTopicItemsView;
  private TextView mTopicName;
  private EditText mPublishText;
  private AtomicBoolean mScrollToBottom = new AtomicBoolean(true);

  private MMX.EventListener mListener = new MMX.EventListener() {
    public boolean onMessageReceived(com.magnet.mmx.client.api.MMXMessage mmxMessage) {
      MMXChannel channel = mmxMessage.getChannel();
      if (channel != null && channel.getName().equals(mChannel.getName())) {
        updateTopicItems();
      }
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_topic_item_list);

    final String topicName = getIntent().getStringExtra(TopicListActivity.EXTRA_TOPIC_NAME);
    Log.d(TAG, "onCreate(): topicName=" + topicName);
    MMXChannel.findByName(topicName, 100, new MMX.OnFinishedListener<ListResult<MMXChannel>>() {
      @Override
      public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
        for (MMXChannel channel : mmxChannelListResult.items) {
          if (channel.getName().equalsIgnoreCase(topicName)) {
            mChannel = channel;
            updateTopicItems();
            break;
          }
        }
        if (mChannel == null) {
          Toast.makeText(TopicItemListActivity.this, "Unable to load channel: " +
                  topicName, Toast.LENGTH_LONG).show();
          TopicItemListActivity.this.finish();
          return;
        }
        MMX.registerListener(mListener);
      }

      @Override
      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(TopicItemListActivity.this, "Failed to load channel: " + topicName + ".  " +
                failureCode + ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        TopicItemListActivity.this.finish();
      }
    });
    mProfile = MyProfile.getInstance(TopicItemListActivity.this);
    mTopicItemsView = (ListView) findViewById(R.id.topic_items);
    mTopicName = (TextView) findViewById(R.id.topic_name);
    mPublishText = (EditText) findViewById(R.id.publishMessage);
    mTopicName.setText(topicName);
  }

  protected void onDestroy() {
    MMX.unregisterListener(mListener);
    super.onDestroy();
  }

  protected void onResume() {
    super.onResume();
    updateTopicItems();
  }

  private void updateTopicItems() {
    synchronized (this) {
      if (mChannel != null) {
        mChannel.getItems(null, null, 25, false,
                new MMX.OnFinishedListener<List<com.magnet.mmx.client.api.MMXMessage>>() {
                  public void onSuccess(List<com.magnet.mmx.client.api.MMXMessage> mmxMessages) {
                    //reverse the list
                    mTopicItems = new ArrayList<MMXMessage>();
                    for (int i = mmxMessages.size(); --i >= 0; ) {
                      mTopicItems.add(mmxMessages.get(i));
                    }
                    mScrollToBottom.set(true);
                    updateListView();
                  }

                  public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                    Toast.makeText(TopicItemListActivity.this, "Unable to retrieve items: "
                            + throwable.getMessage(), Toast.LENGTH_LONG).show();
                  }
                });
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
    HashMap<String, String> content = new HashMap<String, String>();
    content.put(KEY_MESSAGE_TEXT, mPublishText.getText().toString());
    mChannel.publish(content, new MMX.OnFinishedListener<String>() {
      @Override
      public void onSuccess(String s) {
        Toast.makeText(TopicItemListActivity.this, "Published successfully.",
                Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(TopicItemListActivity.this, "Unable to publish message: " +
                throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
    mPublishText.setText(null);
    mScrollToBottom.set(true);
    updateTopicItems();
  }

  public void doShowMenu(View view) {
    PopupMenu popup = new PopupMenu(this, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_topic_item_list, popup.getMenu());

    //decide which items to hide
    Menu menu = popup.getMenu();

    if (mChannel.isSubscribed()) {
      //remove subcribe
      menu.removeItem(R.id.action_subscribe);
    } else {
      //remove unsubscribe
      menu.removeItem(R.id.action_unsubscribe);
    }

    if (!mChannel.getOwnerUsername()
            .equalsIgnoreCase(MMX.getCurrentUser().getUsername())) {
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
    mChannel.subscribe(new MMX.OnFinishedListener<String>() {
      public void onSuccess(String s) {
        Toast.makeText(TopicItemListActivity.this, "Subscribed successfully", Toast.LENGTH_LONG).show();
      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(TopicItemListActivity.this, "Unable to subscribe: " +
                throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
  }

  public void doUnsubscribe() {
    mChannel.unsubscribe(new MMX.OnFinishedListener<Boolean>() {
      public void onSuccess(Boolean result) {
        if (result) {
          Toast.makeText(TopicItemListActivity.this, "Unsubscribed successfully", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(TopicItemListActivity.this, "Could not unsubscribe.", Toast.LENGTH_LONG).show();
        }
      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(TopicItemListActivity.this, "Exception caught: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
      }
    });
  }

  public void doDelete() {
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            mChannel.delete(new MMX.OnFinishedListener<Void>() {
              public void onSuccess(Void aVoid) {
                TopicItemListActivity.this.finish();
              }

              public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(TopicItemListActivity.this,
                        getString(R.string.error_unable_to_delete_topic) +
                                failureCode + ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();

              }
            });
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
      String authorStr = message.getSender().getUsername();
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
      datePosted.setText(mFormatter.format(message.getTimestamp()));
      TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
      messageText.setBackgroundResource(colorResId);
      messageText.setText(message.getContent().get(KEY_MESSAGE_TEXT));
      return convertView;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
      MMXMessage message = getItem(position);
      if (mProfile.getUsername().equals(message.getSender().getUsername())) {
        //me
        return TYPE_ME;
      } else {
        //them
        return TYPE_THEM;
      }
    }
  }
}
