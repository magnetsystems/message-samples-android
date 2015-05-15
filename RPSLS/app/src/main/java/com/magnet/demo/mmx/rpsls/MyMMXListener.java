package com.magnet.demo.mmx.rpsls;

import android.content.Context;
import android.util.Log;

import com.magnet.mmx.client.AbstractMMXListener;
import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.protocol.MMXTopic;

/**
 * This is the singleton MMXListener that should be passed to
 * MMXClient.connect().  The reason for this is that the application developer needs to implement
 * these methods to handle situations where messages arrive even when there is no UI shown to the developer.
 *
 * In this implementation, messages are dropped if no additional listeners are registered.  In an ideal
 * situation, the application will decide how to handle messages when there are no listeners by overriding
 * the onMessageReceived and onPubsubItemReceived methods.
 */
public class MyMMXListener extends AbstractMMXListener {
  private static final String TAG = MyMMXListener.class.getSimpleName();
  private static MyMMXListener sInstance = null;
  private Context mApplicationContext = null;
  private boolean mExiting = false;

  private MyMMXListener(Context context) {
    mApplicationContext = context.getApplicationContext();
  }

  public synchronized static MyMMXListener getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new MyMMXListener(context);
    }
    return sInstance;
  }

  public void setExiting(boolean exiting) {
    mExiting = exiting;
  }

  @Override
  public void onConnectionEvent(MMXClient mmxClient, MMXClient.ConnectionEvent connectionEvent) {
    Log.d(TAG, "onConnectionEvent(): " + connectionEvent);
    switch (connectionEvent) {
      case CONNECTED:
        Log.d(TAG, "onConnectionEvent(): CONNECTED.  Setting up game messaging and publishing availability");
        RPSLS.Util.setupGameMessaging(mApplicationContext, mmxClient);
        RPSLS.Util.publishAvailability(mApplicationContext, mmxClient, true);
        break;
//      case CONNECTION_FAILED:
//      case DISCONNECTED:
//        if (!mExiting) {
//          MyProfile myProfile = MyProfile.getInstance(mApplicationContext);
//          mmxClient.connectWithCredentials(myProfile.getUsername(), myProfile.getPassword(), this, null);
//        }
//        break;
    }
    super.onConnectionEvent(mmxClient, connectionEvent);
  }

  public void handleMessageReceived(MMXClient mmxClient, MMXMessage mmxMessage, String receiptId) {
    Log.d(TAG, "handleMessageReceived(): start. ");
  }

  public void handleMessageDelivered(MMXClient mmxClient, MMXid recipient, String messageId) {
    Log.d(TAG, "handleMessageDelivered(): messageId=" + messageId);
  }

  public void handlePubsubItemReceived(MMXClient mmxClient, MMXTopic mmxTopic, MMXMessage mmxMessage) {
    Log.d(TAG, "handlePubsubItemReceived(): topic=" + mmxTopic);
  }
}
