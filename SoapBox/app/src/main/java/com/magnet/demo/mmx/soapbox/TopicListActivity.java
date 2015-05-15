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

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXTask;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXSubscription;
import com.magnet.mmx.client.common.MMXTopicInfo;
import com.magnet.mmx.client.common.MMXTopicSearchResult;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.SearchAction;
import com.magnet.mmx.protocol.TopicAction;
import com.magnet.mmx.protocol.TopicSummary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopicListActivity extends Activity {
  private static final String TAG = TopicListActivity.class.getSimpleName();
  public static final String EXTRA_TOPIC_NAME = "topicName";
  private static final long MILLIS_IN_ONE_DAY = 24 * 60 * 60 * 1000l;
  static final int REQUEST_LOGIN = 1;
  private MMXClient mClient = null;
  private TopicsManager mTopicsManager = null;

  private Handler mSearchHandler = new Handler();
  private ListView mListView = null;
  private EditText mSearchFilter = null;
  private TopicHeaderListAdapter mAdapter = null;
  private MMXTask<List<MMXSubscription>> mSubscriptionsTask = null;
  private MMXTask<MMXTopicSearchResult> mTopicsTask = null;

  private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      String topicName = (String) view.getTag();
      if (topicName != null) {
        Intent intent = new Intent(TopicListActivity.this, TopicItemListActivity.class);
        intent.putExtra(EXTRA_TOPIC_NAME, topicName);
        startActivity(intent);
      }
    }
  };

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_topic_list);
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
            updateTopicList();
          }
        }, 700);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    mTopicsManager = TopicsManager.getInstance(this);
    mAdapter = new TopicHeaderListAdapter(this,
            mTopicsManager.getSubscribedTopics(mSearchFilter.getText().toString()),
            mTopicsManager.getOtherTopics(mSearchFilter.getText().toString()));
    mListView = (ListView) findViewById(R.id.topics_list);
    mListView.setOnItemClickListener(mOnItemClickListener);
    mClient = MMXClient.getInstance(this, R.raw.soapbox);
  }

  protected void onResume() {
    super.onResume();
    if (!mClient.isConnected()) {
      Intent loginIntent = new Intent(this, LoginActivity.class);
      startActivityForResult(loginIntent, REQUEST_LOGIN);
    } else {
      //populate or update the view
      updateTopicList();
    }
  }

  private synchronized void updateTopicList() {
    if (mSubscriptionsTask == null) {
      mSubscriptionsTask = new MMXTask<List<MMXSubscription>>(mClient) {
        @Override
        public List<MMXSubscription> doRun(MMXClient mmxClient) throws Throwable {
          return mmxClient.getPubSubManager().listAllSubscriptions();
        }

        @Override
        public void onException(Throwable exception) {
          Log.e(TAG, "subscriptionsTask() caught exception", exception);
          Toast.makeText(TopicListActivity.this, "Exception: " + exception.getMessage(), Toast.LENGTH_LONG).show();
          synchronized (TopicListActivity.this) {
            mSubscriptionsTask = null;
          }
        }

        @Override
        public void onResult(List<MMXSubscription> result) {
          mTopicsManager.setSubscriptions(result);
          updateView();
          synchronized (TopicListActivity.this) {
            mSubscriptionsTask = null;
          }
        }
      };
      mSubscriptionsTask.execute();
    }

    if (mTopicsTask == null) {
      mTopicsTask = new MMXTask<MMXTopicSearchResult>(mClient) {
        @Override
        public MMXTopicSearchResult doRun(MMXClient mmxClient) throws Throwable {
          return mmxClient.getPubSubManager().searchBy(SearchAction.Operator.OR,
                  new TopicAction.TopicSearch(), null);
        }

        @Override
        public void onException(Throwable exception) {
          Log.e(TAG, "subscriptionsTask() caught exception", exception);
          Toast.makeText(TopicListActivity.this, "Exception: " + exception.getMessage(), Toast.LENGTH_LONG).show();
          synchronized (TopicListActivity.this) {
            mTopicsTask = null;
          }
        }

        @Override
        public void onResult(MMXTopicSearchResult result) {
          mTopicsManager.setTopics(result.getResults());
          ArrayList<MMXTopic> topicsList = new ArrayList<MMXTopic>();
          for (MMXTopicInfo info : result.getResults()) {
            MMXTopic topic = info.getTopic();
            if (topic.getName() != null && !topic.getName().isEmpty()) {
              topicsList.add(info.getTopic());
            }
          }
          try {
            List<TopicSummary> summaries = mClient.getPubSubManager().getTopicSummary(topicsList,
                    new Date(System.currentTimeMillis() - MILLIS_IN_ONE_DAY), null);
            mTopicsManager.setTopicSummaries(summaries);
          } catch (MMXException e) {
            Log.e(TAG, "Unable to retrieve topic summaries", e);
          }
          updateView();
          synchronized (TopicListActivity.this) {
            mTopicsTask = null;
          }
        }
      };
      mTopicsTask.execute();
    }
  }

  private void updateView() {
    runOnUiThread(new Runnable() {
      public void run() {
        mAdapter = new TopicHeaderListAdapter(TopicListActivity.this,
                mTopicsManager.getSubscribedTopics(mSearchFilter.getText().toString()),
                mTopicsManager.getOtherTopics(mSearchFilter.getText().toString()));
        mListView.setAdapter(mAdapter);
      }
    });
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult() request=" + requestCode + ", result=" + resultCode);
    if (requestCode == REQUEST_LOGIN) {
      if (resultCode == RESULT_OK) {
        updateTopicList();
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
            MMXTask<Void> logoutTask = new MMXTask<Void>(mClient) {
              @Override
              public Void doRun(MMXClient mmxClient) throws Throwable {
                mmxClient.disconnect();
                return null;
              }

              @Override
              public void onResult(Void result) {
                TopicListActivity.this.finish();
              }
            };
            logoutTask.execute();
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

  public void doAddTopic(View view) {
    Intent intent = new Intent(this, TopicAddActivity.class);
    startActivity(intent);
  }

  private static class TopicHeaderListAdapter extends BaseAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TOPIC = 1;
    private Context mContext;
    private List<MMXTopic> mSubscriptions;
    private List<MMXTopic> mTopics;
    private LayoutInflater mLayoutInflater;
    private int mOtherTopicsHeaderPosition;
    private TopicsManager mTopicsManager;

    public TopicHeaderListAdapter(Context context, List<MMXTopic> subscriptions, List<MMXTopic> topics) {
      super();
      mContext = context;
      mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
      mTopicsManager = TopicsManager.getInstance(mContext);
      mSubscriptions = subscriptions;
      mTopics = topics;
      mOtherTopicsHeaderPosition = mSubscriptions.size() + 1;
    }

    @Override
    public int getCount() {
      return mSubscriptions.size() + mTopics.size() + 2; //two header rows
    }

    @Override
    public Object getItem(int position) {
      if (position == 0) {
        return mContext.getString(R.string.topic_list_header_subscriptions) + " (" + mSubscriptions.size() + ")";
      } else if (position == mOtherTopicsHeaderPosition) {
        return mContext.getString(R.string.topic_list_header_other_topics) + " (" + mTopics.size() + ")";
      } else {
        if (position < mOtherTopicsHeaderPosition) {
          int subscriptionsIndex = position - 1;
          return mSubscriptions.get(subscriptionsIndex);
        } else {
          //look into other topics
          int otherTopicsIndex = position - mSubscriptions.size() - 2;
          return mTopics.get(otherTopicsIndex);
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
            convertView = mLayoutInflater.inflate(R.layout.topic_list_header, null);
          }
          String headerStr = (String) getItem(position);
          TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
          headerText.setText(headerStr);
          convertView.setTag(null);
          convertView.setEnabled(false);
          break;
        case TYPE_TOPIC:
          if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.topic_list_item, null);
          }
          MMXTopic topic = (MMXTopic) getItem(position);
          populateTopicView(convertView, topic);
          break;
      }
      return convertView;
    }

    @Override
    public int getItemViewType(int position) {
      //H T T T T T H T T T T
      if (position == 0 || position == mOtherTopicsHeaderPosition) {
        return TYPE_HEADER;
      }
      return TYPE_TOPIC;
    }

    @Override
    public int getViewTypeCount() {
      //header view and topic view
      return 2;
    }

    private void populateTopicView(View view, MMXTopic topic) {
      TextView topicNameView = (TextView) view.findViewById(R.id.topic_name);
      String topicName = topic.getName();
      topicNameView.setText(topicName);

      TextView countView = (TextView) view.findViewById(R.id.new_item_count);
      TopicSummary summary = mTopicsManager.getTopicSummary(topic);
      int count = summary == null ? 0 : summary.getCount();
      if (count > 0) {
        countView.setVisibility(View.VISIBLE);
        countView.setText(String.valueOf(count));
      } else {
        countView.setVisibility(View.INVISIBLE);
        countView.setText(null);
      }
      view.setTag(topicName);
    }
  }
}