package com.magnet.demo.mmx.soapbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

public class ChannelListActivity extends Activity {
  private static final String TAG = ChannelListActivity.class.getSimpleName();
  public static final String EXTRA_CHANNEL_NAME = "channelName";
  private static final long MILLIS_IN_ONE_DAY = 24 * 60 * 60 * 1000l;
  static final int REQUEST_LOGIN = 1;
  private ChannelsManager mChannelsManager = null;

  private Handler mSearchHandler = new Handler();
  private ListView mListView = null;
  private EditText mSearchFilter = null;
  private ChannelHeaderListAdapter mAdapter = null;

  private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      String channelName = (String) view.getTag();
      if (channelName != null) {
        Intent intent = new Intent(ChannelListActivity.this, ChannelItemListActivity.class);
        intent.putExtra(EXTRA_CHANNEL_NAME, channelName);
        startActivity(intent);
      }
    }
  };

  /**
   * Use this listener to be notified when we receive a message for a channel that we're subscribed to
   * so that we can update the counts.  NOTE:  onPubSubItemReceived() will only be called when messages
   * are published to subscribed channels.
   */
  private MMX.EventListener mListener = new MMX.EventListener() {
    public boolean onMessageReceived(MMXMessage mmxMessage) {
      updateChannelList();
      return false;
    }
  };

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_channel_list);
    mSearchFilter = (EditText) findViewById(R.id.search);
    mSearchFilter.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSearchHandler.removeCallbacks(null);
        mSearchHandler.postDelayed(new Runnable() {
          public void run() {
            updateChannelList();
          }
        }, 700);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    mChannelsManager = ChannelsManager.getInstance(this);
    mAdapter = new ChannelHeaderListAdapter(this,
            mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
            mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
    mListView = (ListView) findViewById(R.id.channels_list);
    mListView.setOnItemClickListener(mOnItemClickListener);
    MMX.registerListener(mListener);
  }

  protected void onDestroy() {
    MMX.unregisterListener(mListener);
    super.onDestroy();

  }

  protected void onResume() {
    super.onResume();
    if (MMX.getCurrentUser() == null) {
      Intent loginIntent = new Intent(this, LoginActivity.class);
      startActivityForResult(loginIntent, REQUEST_LOGIN);
    } else {
      //populate or update the view
      updateChannelList();
    }
  }

  private synchronized void updateChannelList() {
    MMXChannel.findByName(null, 100, new MMX.OnFinishedListener<ListResult<MMXChannel>>() {
      public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
        ChannelsManager.getInstance(ChannelListActivity.this).setChannels(mmxChannelListResult.items);
        updateView();
      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(ChannelListActivity.this, "Exception: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
        updateView();
      }
    });
  }

  private void updateView() {
    runOnUiThread(new Runnable() {
      public void run() {
        mAdapter = new ChannelHeaderListAdapter(ChannelListActivity.this,
                mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
                mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
        mListView.setAdapter(mAdapter);
      }
    });
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult() request=" + requestCode + ", result=" + resultCode);
    if (requestCode == REQUEST_LOGIN) {
      if (resultCode == RESULT_OK) {
        updateChannelList();
      } else {
        finish();
      }
    }
  }

  public void doLogout(View view) {
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      public void onClick(final DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MMX.logout(new MMX.OnFinishedListener<Void>() {
              public void onSuccess(Void aVoid) {
                ChannelListActivity.this.finish();
              }

              public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(ChannelListActivity.this, "Logout failed: " + failureCode +
                        ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
              }
            });
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            dialog.cancel();
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
            .setTitle(R.string.dlg_signout_title)
            .setMessage(R.string.dlg_signout_message)
            .setPositiveButton(R.string.dlg_signout_ok, clickListener)
            .setNegativeButton(R.string.dlg_signout_cancel, clickListener);
    builder.create().show();
  }

  public void doAddChannel(View view) {
    Intent intent = new Intent(this, AddChannelActivity.class);
    startActivity(intent);
  }

  private static class ChannelHeaderListAdapter extends BaseAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHANNEL = 1;
    private Context mContext;
    private List<MMXChannel> mSubscriptions;
    private List<MMXChannel> mOtherChannels;
    private LayoutInflater mLayoutInflater;
    private int mOtherChannelsHeaderPosition;

    public ChannelHeaderListAdapter(Context context, List<MMXChannel> subscriptions, List<MMXChannel> otherChannels) {
      super();
      mContext = context;
      mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
      mSubscriptions = subscriptions;
      mOtherChannels = otherChannels;
      mOtherChannelsHeaderPosition = mSubscriptions.size() + 1;
    }

    @Override
    public int getCount() {
      return mSubscriptions.size() + mOtherChannels.size() + 2; //two header rows
    }

    @Override
    public Object getItem(int position) {
      if (position == 0) {
        return mContext.getString(R.string.channel_list_header_subscriptions) + " (" + mSubscriptions.size() + ")";
      } else if (position == mOtherChannelsHeaderPosition) {
        return mContext.getString(R.string.channel_list_header_other_channels) + " (" + mOtherChannels.size() + ")";
      } else {
        if (position < mOtherChannelsHeaderPosition) {
          int subscriptionsIndex = position - 1;
          return mSubscriptions.get(subscriptionsIndex);
        } else {
          //look into other channels
          int otherChannelsIndex = position - mSubscriptions.size() - 2;
          return mOtherChannels.get(otherChannelsIndex);
        }
      }
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      int viewType = getItemViewType(position);
      switch (viewType) {
        case TYPE_HEADER:
          if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.channel_list_header, null);
          }
          String headerStr = (String) getItem(position);
          TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
          headerText.setText(headerStr);
          convertView.setTag(null);
          convertView.setEnabled(false);
          break;
        case TYPE_CHANNEL:
          if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.channel_list_item, null);
          }
          MMXChannel channel = (MMXChannel) getItem(position);
          populateChannelView(convertView, channel);
          break;
      }
      return convertView;
    }

    @Override
    public int getItemViewType(int position) {
      //H T T T T T H T T T T
      if (position == 0 || position == mOtherChannelsHeaderPosition) {
        return TYPE_HEADER;
      }
      return TYPE_CHANNEL;
    }

    @Override
    public int getViewTypeCount() {
      //header view and channel view
      return 2;
    }

    private void populateChannelView(View view, MMXChannel channel) {
      TextView channelNameView = (TextView) view.findViewById(R.id.channel_name);
      String channelName = channel.getName();
      channelNameView.setText(channelName);

      TextView countView = (TextView) view.findViewById(R.id.new_item_count);
      int count = channel.getNumberOfMessages();
      if (count > 0) {
        countView.setVisibility(View.VISIBLE);
        countView.setText(String.valueOf(count));
      } else {
        countView.setVisibility(View.INVISIBLE);
        countView.setText(null);
      }
      view.setTag(channelName);
    }
  }
}