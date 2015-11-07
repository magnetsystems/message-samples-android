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

import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.common.TopicExistsException;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;


public class AddChannelActivity extends Activity {
  private static final String TAG = AddChannelActivity.class.getSimpleName();
  private EditText mChannelName = null;
  private ListView mTagList = null;
  private String[] mTagArray = null;
  private AtomicBoolean mSaving = new AtomicBoolean(false);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_channel_add);
    mTagArray = getResources().getStringArray(R.array.channel_tags);
    mChannelName = (EditText) findViewById(R.id.channelName);
    mTagList = (ListView) findViewById(R.id.tagList);
    mTagList.setAdapter(
            new ArrayAdapter<String>(this, R.layout.simple_list_item_checked, mTagArray));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_channel_add, menu);
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
        mChannelName.setEnabled(!disableViews);
        mTagList.setEnabled(!disableViews);
      }
    });
  }

  public void doSave(View view) {
    if (mSaving.compareAndSet(false, true)) {
      final String channelName = mChannelName.getText().toString();
      if (channelName.isEmpty()) {
        mChannelName.setError(getString(R.string.error_channel_name_required));
        return;
      }
      MMXChannel.create(channelName, channelName, true, MMXChannel.PublishPermission.ANYONE,
              new MMXChannel.OnFinishedListener<MMXChannel>() {
        public void onSuccess(MMXChannel mmxChannel) {
          //add tags
          SparseBooleanArray checkedPositions = mTagList.getCheckedItemPositions();
          final HashSet<String> tags = new HashSet<String>();
          for (int i = 0; i < checkedPositions.size(); i++) {
            int position = checkedPositions.keyAt(i);
            boolean checked = checkedPositions.valueAt(i);
            if (checked) {
              Log.d(TAG, "create(): adding tag: " + mTagArray[position]);
              tags.add(mTagArray[position]);
            }
          }
          if (tags.size() > 0) {
            mmxChannel.setTags(tags, new MMXChannel.OnFinishedListener<Void>() {
              public void onSuccess(Void aVoid) {
                mSaving.set(false);
                AddChannelActivity.this.finish();

              }

              public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(AddChannelActivity.this, "Channel '" + channelName +
                        "' created, but unable to add tags: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
                mSaving.set(false);
                AddChannelActivity.this.finish();
              }
            });
            updateView();
          } else {
            mSaving.set(false);
            AddChannelActivity.this.finish();
          }
        }

        public void onFailure(MMXChannel.FailureCode failureCode, final Throwable throwable) {
          if (throwable instanceof TopicExistsException) {
            mChannelName.setError(getString(R.string.error_channel_already_exists));
          } else if (throwable.getCause() instanceof TopicExistsException) {
            mChannelName.setError(throwable.getMessage());
          }
          updateView();
          mSaving.set(false);
        }
      });
      updateView();
    }
  }
}
