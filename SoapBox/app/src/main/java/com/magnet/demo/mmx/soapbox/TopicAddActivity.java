package com.magnet.demo.mmx.soapbox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXTask;
import com.magnet.mmx.client.common.MMXGlobalTopic;
import com.magnet.mmx.client.common.TopicExistsException;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.MMXTopicOptions;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class TopicAddActivity extends Activity {
  private static final String TAG = TopicAddActivity.class.getSimpleName();
  private MMXClient mClient = null;
  private EditText mTopicName = null;
  private ListView mTagList = null;
  private String[] mTagArray = null;
  private AtomicBoolean mSaving = new AtomicBoolean(false);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_topic_add);
    mClient = MMXClient.getInstance(this, R.raw.soapbox);
    mTagArray = getResources().getStringArray(R.array.topic_tags);
    mTopicName = (EditText) findViewById(R.id.topicName);
    mTagList = (ListView) findViewById(R.id.tagList);
    mTagList.setAdapter(
            new ArrayAdapter<String>(this, R.layout.simple_list_item_checked, mTagArray));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_topic_add, menu);
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

  public void doCancel(View view) {
    finish();
  }

  public void updateView() {
    runOnUiThread(new Runnable() {
      public void run() {
        boolean disableViews = mSaving.get();
        mTopicName.setEnabled(!disableViews);
        mTagList.setEnabled(!disableViews);
      }
    });
  }

  public void doSave(View view) {
    if (mSaving.compareAndSet(false, true)) {
      String topicName = mTopicName.getText().toString();
      if (topicName == null || topicName.isEmpty()) {
        mTopicName.setError(getString(R.string.error_topic_name_required));
        return;
      }
      final MMXGlobalTopic topicToCreate = new MMXGlobalTopic(topicName);
      MMXTask<MMXTopic> createTask = new MMXTask<MMXTopic>(mClient) {
        @Override
        public MMXTopic doRun(MMXClient mmxClient) throws Throwable {
          return mClient.getPubSubManager().createTopic(topicToCreate, null);
        }

        @Override
        public void onException(final Throwable exception) {
          TopicAddActivity.this.runOnUiThread(new Runnable() {
            public void run() {
              if (exception instanceof TopicExistsException) {
                mTopicName.setError(getString(R.string.error_topic_already_exists));
              } else {
                mTopicName.setError(exception.getMessage());
              }
              updateView();
            }
          });
          mSaving.set(false);
        }

        @Override
        public void onResult(final MMXTopic result) {
          //add tags
          SparseBooleanArray checkedPositions = mTagList.getCheckedItemPositions();
          final ArrayList<String> tags = new ArrayList<String>();
          for (int i=0; i<checkedPositions.size(); i++) {
            int position = checkedPositions.keyAt(i);
            boolean checked = checkedPositions.valueAt(i);
            if (checked) {
              Log.d(TAG, "createTopic(): adding tag: " + mTagArray[position]);
              tags.add(mTagArray[position]);
            }
          }
          if (tags.size() > 0) {
            MMXTask<MMXStatus> addTagsTask = new MMXTask<MMXStatus>(mClient) {
              @Override
              public MMXStatus doRun(MMXClient mmxClient) throws Throwable {
                return mClient.getPubSubManager().addTags(result, tags);
              }

              @Override
              public void onException(Throwable exception) {
                Toast.makeText(TopicAddActivity.this, "Topic '" + result.getName() +
                        "' created, but unable to add tags: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                mSaving.set(false);
                TopicAddActivity.this.finish();
              }

              @Override
              public void onResult(MMXStatus result) {
                if (result.getCode() != MMXStatus.SUCCESS) {
                  Toast.makeText(TopicAddActivity.this, "Topic '" + topicToCreate.getName() +
                          "' created, but unable to add tags: " + result.getMessage(), Toast.LENGTH_LONG).show();
                }
                mSaving.set(false);
                TopicAddActivity.this.finish();
              }
            };
            addTagsTask.execute();
            updateView();
          } else {
            mSaving.set(false);
            TopicAddActivity.this.finish();
          }
        }
      };
      createTask.execute();
      updateView();
    }
  }
}
