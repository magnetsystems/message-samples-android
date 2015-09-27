package com.magnet.wru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magnet.mmx.client.api.MMXChannel;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final long UPDATE_INTERVAL_MILLIS = 60 * 60 * 1000; //60 minutes
  private WRU mWru = null;
  private Button mCreateButton = null;
  private EditText mUsername = null;
  private EditText mPassphrase = null;
  private Button mJoinButton = null;
  private EditText mJoinTopicKey = null;
  private EditText mJoinTopicPassphrase = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mWru = WRU.getInstance(this);
    if (mWru.getJoinedTopicKey() != null) {
      //navigate to maps page
      Log.d(TAG, "onCreate(): found existing joined topic, forwarding to map page");
      startMapActivity(true);
      return;
    }
    mCreateButton = (Button) findViewById(R.id.btn_create);
    mPassphrase = (EditText) findViewById(R.id.passphrase);
    TextWatcher textWatcher = new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateViewState();
      }

      public void afterTextChanged(Editable s) {

      }
    };
    mPassphrase.addTextChangedListener(textWatcher);
    mUsername = (EditText) findViewById(R.id.username);
    mUsername.setText(mWru.getUsername());
    mJoinButton = (Button) findViewById(R.id.btn_join);
    mJoinTopicKey = (EditText) findViewById(R.id.join_topic_key);
    mJoinTopicKey.addTextChangedListener(textWatcher);
    mJoinTopicPassphrase = (EditText) findViewById(R.id.join_topic_passphrase);
    mJoinTopicPassphrase.addTextChangedListener(textWatcher);
  }

  public void doCreateTopic(View view) {
    final String passphrase = mPassphrase.getText().toString();
    mCreateButton.setEnabled(false);
    mWru.createTopic(passphrase, new MMXChannel.OnFinishedListener<String>() {
      public void onSuccess(String topicKey) {
        String username = mUsername.getText().toString();
        mWru.joinTopic(topicKey, passphrase, username, UPDATE_INTERVAL_MILLIS,
                new MMXChannel.OnFinishedListener<Void>() {
                  public void onSuccess(Void aVoid) {
                    startMapActivity(true);
                    runOnUiThread(new Runnable() {
                      public void run() {
                        mCreateButton.setEnabled(true);
                      }
                    });
                  }

                  public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Toast.makeText(MainActivity.this, "Unable to join topic.", Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {
                      public void run() {
                        mCreateButton.setEnabled(true);
                      }
                    });
                  }
        });
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Toast.makeText(MainActivity.this, "Unable to create topic.", Toast.LENGTH_LONG).show();
      }
    });
  }

  public void doJoinTopic(View view) {
    String topicKey = mJoinTopicKey.getText().toString();
    String topicPassphrase = mJoinTopicPassphrase.getText().toString();
    String username = mUsername.getText().toString();
    mJoinButton.setEnabled(false);
    mWru.joinTopic(topicKey, topicPassphrase, username, UPDATE_INTERVAL_MILLIS,
            new MMXChannel.OnFinishedListener<Void>() {
              public void onSuccess(Void aVoide) {
                startMapActivity(true);
                runOnUiThread(new Runnable() {
                  public void run() {
                    mJoinButton.setEnabled(true);
                  }
                });

              }

              public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(MainActivity.this,
                        "Unable to find the channel with the specified key and passphrase.",
                        Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                  public void run() {
                    mJoinButton.setEnabled(true);
                  }
                });
              }
    });
  }

  private void updateViewState() {
    if (mUsername.getText().toString().isEmpty()) {
      mJoinButton.setEnabled(false);
      mCreateButton.setEnabled(false);
    } else {
      mJoinButton.setEnabled(
              !mJoinTopicKey.getText().toString().isEmpty() &&
              !mJoinTopicPassphrase.getText().toString().isEmpty());
      mCreateButton.setEnabled(!mPassphrase.getText().toString().isEmpty());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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

  private void startMapActivity(boolean finish) {
    Intent intent = new Intent(this, MapActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    startActivity(intent);
    if (finish) {
      finish();
    }
  }
}
