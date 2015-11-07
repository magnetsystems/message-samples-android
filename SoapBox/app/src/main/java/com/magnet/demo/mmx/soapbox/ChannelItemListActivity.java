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


public class ChannelItemListActivity extends Activity {
  private static final String TAG = ChannelItemListActivity.class.getSimpleName();
  private static final String KEY_MESSAGE_TEXT = "content";

  private MyProfile mProfile;
  private MMXChannel mChannel;
  private List<MMXMessage> mChannelItems;
  private ListView mChannelItemsView;
  private TextView mChannelName;
  private EditText mPublishText;
  private AtomicBoolean mScrollToBottom = new AtomicBoolean(true);

  private MMX.EventListener mListener = new MMX.EventListener() {
    public boolean onMessageReceived(com.magnet.mmx.client.api.MMXMessage mmxMessage) {
      MMXChannel channel = mmxMessage.getChannel();
      if (channel != null && channel.getName().equals(mChannel.getName())) {
        updateChannelItems();
      }
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_channel_item_list);

    final String channelName = getIntent().getStringExtra(ChannelListActivity.EXTRA_CHANNEL_NAME);
    Log.d(TAG, "onCreate(): channelName=" + channelName);
    MMXChannel.findPublicChannelsByName(channelName, 100, 0,
            new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
      @Override
      public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
        for (MMXChannel channel : mmxChannelListResult.items) {
          if (channel.getName().equalsIgnoreCase(channelName)) {
            mChannel = channel;
            updateChannelItems();
            break;
          }
        }
        if (mChannel == null) {
          Toast.makeText(ChannelItemListActivity.this, "Unable to load channel: " +
                  channelName, Toast.LENGTH_LONG).show();
          ChannelItemListActivity.this.finish();
          return;
        }
        MMX.registerListener(mListener);
      }

      @Override
      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(ChannelItemListActivity.this, "Failed to load channel: " + channelName + ".  " +
                failureCode + ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        ChannelItemListActivity.this.finish();
      }
    });
    mProfile = MyProfile.getInstance(ChannelItemListActivity.this);
    mChannelItemsView = (ListView) findViewById(R.id.channel_items);
    mChannelName = (TextView) findViewById(R.id.channel_name);
    mPublishText = (EditText) findViewById(R.id.publishMessage);
    mChannelName.setText(channelName);
  }

  protected void onDestroy() {
    MMX.unregisterListener(mListener);
    super.onDestroy();
  }

  protected void onResume() {
    super.onResume();
    updateChannelItems();
  }

  private void updateChannelItems() {
    synchronized (this) {
      if (mChannel != null) {
        mChannel.getMessages(null, null, 25, 0, false,
                new MMXChannel.OnFinishedListener<ListResult<com.magnet.mmx.client.api.MMXMessage>>() {
                  public void onSuccess(ListResult<com.magnet.mmx.client.api.MMXMessage> mmxMessages) {
                    //reverse the list
                    mChannelItems = new ArrayList<MMXMessage>();
                    for (int i = mmxMessages.items.size(); --i >= 0; ) {
                      mChannelItems.add(mmxMessages.items.get(i));
                    }
                    mScrollToBottom.set(true);
                    updateListView();
                  }

                  public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Toast.makeText(ChannelItemListActivity.this, "Unable to retrieve items: "
                            + throwable.getMessage(), Toast.LENGTH_LONG).show();
                  }
                });
      }
    }
  }

  private void updateListView() {
    runOnUiThread(new Runnable() {
      public void run() {
        if (mChannelItems != null) {
          ChannelItemsAdapter adapter = new ChannelItemsAdapter(ChannelItemListActivity.this, mChannelItems, mProfile);
          mChannelItemsView.setAdapter(adapter);
          if (mScrollToBottom.compareAndSet(true, false)) {
            mChannelItemsView.setSelection(adapter.getCount() - 1);
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
    mChannel.publish(content, new MMXChannel.OnFinishedListener<String>() {
      @Override
      public void onSuccess(String s) {
        Toast.makeText(ChannelItemListActivity.this, "Published successfully.",
                Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(ChannelItemListActivity.this, "Unable to publish message: " +
                throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
    mPublishText.setText(null);
    mScrollToBottom.set(true);
    updateChannelItems();
  }

  public void doShowMenu(View view) {
    PopupMenu popup = new PopupMenu(this, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_channel_item_list, popup.getMenu());

    //decide which items to hide
    Menu menu = popup.getMenu();

    if (mChannel.isSubscribed()) {
      //remove subcribe
      menu.removeItem(R.id.action_subscribe);
    } else {
      //remove unsubscribe
      menu.removeItem(R.id.action_unsubscribe);
    }

    if (!mChannel.getOwnerId()
            .equalsIgnoreCase(MMX.getCurrentUser().getUserIdentifier())) {
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
    mChannel.subscribe(new MMXChannel.OnFinishedListener<String>() {
      public void onSuccess(String s) {
        Toast.makeText(ChannelItemListActivity.this, "Subscribed successfully", Toast.LENGTH_LONG).show();
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(ChannelItemListActivity.this, "Unable to subscribe: " +
                throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
  }

  public void doUnsubscribe() {
    mChannel.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
      public void onSuccess(Boolean result) {
        if (result) {
          Toast.makeText(ChannelItemListActivity.this, "Unsubscribed successfully", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(ChannelItemListActivity.this, "Could not unsubscribe.", Toast.LENGTH_LONG).show();
        }
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(ChannelItemListActivity.this, "Exception caught: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
      }
    });
  }

  public void doDelete() {
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            mChannel.delete(new MMXChannel.OnFinishedListener<Void>() {
              public void onSuccess(Void aVoid) {
                ChannelItemListActivity.this.finish();
              }

              public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(ChannelItemListActivity.this,
                        getString(R.string.error_unable_to_delete_channel) +
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

  private static class ChannelItemsAdapter extends ArrayAdapter<MMXMessage> {
    private static final int[] COLOR_IDS = {R.color.chat_1, R.color.chat_2, R.color.chat_3, R.color.chat_4, R.color.chat_5, R.color.chat_6};
    private static final int TYPE_ME = 0;
    private static final int TYPE_THEM = 1;
    private MyProfile mProfile;
    private LayoutInflater mInflater;
    private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public ChannelItemsAdapter(Context context, List<MMXMessage> messages, MyProfile profile) {
      super(context, 0, messages);
      mProfile = profile;
      mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      int type = getItemViewType(position);
      MMXMessage message = getItem(position);
      int colorResId = 0;
      String authorStr = message.getSender().getUserName();
      if (authorStr == null) {
        authorStr = getContext().getString(R.string.chat_unknown);
      }
      switch (type) {
        case TYPE_ME:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_item_me, null);
          }
          colorResId = R.color.chat_me;
          break;
        case TYPE_THEM:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_item_them, null);
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
      if (mProfile.getUsername().equalsIgnoreCase(message.getSender().getUserName())) {
        //me
        return TYPE_ME;
      } else {
        //them
        return TYPE_THEM;
      }
    }
  }
}
