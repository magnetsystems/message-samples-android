package com.magnet.wru;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.protocol.GeoLoc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class WRU {
  private static final String TAG = WRU.class.getSimpleName();
  private static final String SHARED_PREF_NAME = WRU.class.getName();
  private static final String SHARED_PREF_KEY_MMX_USERNAME = "mmxUsername";
  private static final String SHARED_PREF_KEY_MMX_PASSWORD = "mmxPassword";
  private static final String SHARED_PREF_KEY_JOINED_TOPIC_KEY = "joinedTopicKey";
  private static final String SHARED_PREF_KEY_JOINED_TOPIC_PASSPHRASE = "joinedTopicPassphrase";
  private static final String SHARED_PREF_KEY_JOINED_TOPIC_AS_USERNAME = "joinedTopicUsername";
  private static final String SHARED_PREF_KEY_UPDATE_INTERVAL = "joinedTopicUpdateInterval";
  private static final String SUFFIX_CHAT = "_chat";
  private static WRU sInstance = null;

  private Context mContext;
  private SQLiteDatabase mDb = null;
  private SharedPreferences mSharedPrefs;
  private Random mRandom;
  private MessageDigest mDigester;
  private MMXChannel mJoinedTopic;
  private MMXChannel mJoinedTopicChat;
  private String mJoinedTopicKey;
  private String mJoinedTopicPassphrase;
  private long mJoinedTopicUpdateInterval;
  private String mUsername;
  private AtomicBoolean mLoginSuccess = new AtomicBoolean(false);
  private AtomicBoolean mLoggingIn = new AtomicBoolean(false);

  private GoogleApiClient mGoogleApiClient = null;
  private final AtomicBoolean mGoogleApiInitialized = new AtomicBoolean(false);
  private GoogleApiClient.OnConnectionFailedListener mGoogleConnectionFailedListener =
          new GoogleApiClient.OnConnectionFailedListener() {
    public void onConnectionFailed(ConnectionResult connectionResult) {
      synchronized (mGoogleApiInitialized) {
        mGoogleApiInitialized.set(false);
        mGoogleApiInitialized.notify();
      }
    }
  };
  private GoogleApiClient.ConnectionCallbacks mGoogleConnectionCallbacks =
          new GoogleApiClient.ConnectionCallbacks() {
    public void onConnected(Bundle bundle) {
      synchronized (mGoogleApiInitialized) {
        mGoogleApiInitialized.set(true);
        mGoogleApiInitialized.notify();
      }
    }

    public void onConnectionSuspended(int i) {
      synchronized (mGoogleApiInitialized) {
        mGoogleApiInitialized.set(false);
        mGoogleApiInitialized.notify();
      }
    }
  };

  private final ConcurrentHashMap<String, LocationTime> mLocations = new ConcurrentHashMap<>();

  private MMX.EventListener mListener = new MMX.EventListener() {
    public boolean onMessageReceived(MMXMessage mmxMessage) {
      Map<String, String> content = mmxMessage.getContent();
      String messageType = content.get(KEY_MESSAGE_TYPE);
      boolean handled = false;
      if (mmxMessage.getChannel() == null) {
        if (MessageType.LOCATION_REQUEST.name().equals(messageType)) {
          EventLog.getInstance(mContext).add(EventLog.Type.INFO, "WRU: Location request received.");
          LocationUpdaterService.updateLocation(mContext, mUsername, mJoinedTopic);
          handled = true;
        }
      } else {
        if (mJoinedTopicChat.getName().equalsIgnoreCase(mmxMessage.getChannel().getName())) {
          //this is a chat message
          if (!mmxMessage.getSender().equals(MMX.getCurrentUser())) {
            MyMessageStore.addMessage(mmxMessage.getId(),
                    mmxMessage.getContent(), mmxMessage.getTimestamp(), true);
          }
          handled = true;
        } else if (mJoinedTopic.getName().equalsIgnoreCase(mmxMessage.getChannel().getName())) {
          if (MessageType.LOCATION_UPDATE.name().equals(messageType)) {
            handlePayload(mmxMessage.getSender(), content);
            handled = true;
          }
        }
      }
      if (!handled) {
        Log.e(TAG, "onMessageReceived(): Unhandled message.  channel=" + mmxMessage.getChannel() + ", type=" + messageType);
      }
      return false;
    }
  };

  private WRU(Context context) {
    mContext = context.getApplicationContext();
    mDb = new WRUHelper().getWritableDatabase();
    MMX.registerListener(mListener);
    mSharedPrefs = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    mJoinedTopicKey = mSharedPrefs.getString(SHARED_PREF_KEY_JOINED_TOPIC_KEY, null);
    mJoinedTopicPassphrase = mSharedPrefs.getString(SHARED_PREF_KEY_JOINED_TOPIC_PASSPHRASE, null);
    mUsername = mSharedPrefs.getString(SHARED_PREF_KEY_JOINED_TOPIC_AS_USERNAME, null);
    mJoinedTopicUpdateInterval = mSharedPrefs.getLong(SHARED_PREF_KEY_UPDATE_INTERVAL, -1l);
    if (mJoinedTopicKey != null) {
      loadLastLocations(encodeTopic(mJoinedTopicKey, mJoinedTopicPassphrase));
    }

    if (playServicesConnected(mContext)) {
      mGoogleApiClient = new GoogleApiClient.Builder(mContext)
              .addOnConnectionFailedListener(mGoogleConnectionFailedListener)
              .addConnectionCallbacks(mGoogleConnectionCallbacks)
              .addApi(LocationServices.API)
              .build();
      mGoogleApiClient.connect();
    }
    registerMMXUser();
  }

  public static synchronized WRU getInstance(final Context context) {
    if (sInstance == null) {
      sInstance = new WRU(context);
    }
    sInstance.login();
    return sInstance;
  }

  public synchronized String getUsername() {
    return mUsername;
  }

  public synchronized MMXChannel getJoinedTopic() {
    return mJoinedTopic;
  }

  public synchronized MMXChannel getJoinedTopicChat() {
    return mJoinedTopicChat;
  }

  public synchronized String getJoinedTopicKey() {
    return mJoinedTopicKey;
  }

  public synchronized String getJoinedTopicPassphrase() {
    return mJoinedTopicPassphrase;
  }

  public String generateTopicKey() {
    return Long.toString(System.currentTimeMillis(), 36);
  }

  public void createTopic(String passphrase, final MMXChannel.OnFinishedListener<String> listener) {
    final String topicKey = generateTopicKey();
    final String topicName = encodeTopic(topicKey, passphrase);
    Log.d(TAG, "createTopic(): creating topic " + topicName);
    MMXChannel.create(topicName, topicName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
      public void onSuccess(MMXChannel mmxChannel) {
        Log.d(TAG, "createTopic(): success");
        //at this point it should already be subscribed
        final String chatTopicName = topicName + SUFFIX_CHAT;
        MMXChannel.create(chatTopicName, chatTopicName, true,
                new MMXChannel.OnFinishedListener<MMXChannel>() {
                  public void onSuccess(MMXChannel mmxChannel) {
                    Log.d(TAG, "createTopic(): success creating chat topic");
                    //at this point it should already be subscribed
                    listener.onSuccess(topicKey);
                  }

                  @Override
                  public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                    Log.e(TAG, "createTopic(): failure creating chat topic: " + failureCode, throwable);
                    listener.onFailure(failureCode, throwable);
                  }
                });
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Log.e(TAG, "createTopic(): failure: " + failureCode, throwable);
        listener.onFailure(failureCode, throwable);
      }
    });
  }

  public synchronized void joinTopic(final String topicKey, final String passphrase,
                                     final String username, final long updateInterval,
                                     final MMXChannel.OnFinishedListener<Void> listener) {
    Log.d(TAG, "joinTopic(): topicKey: " + topicKey + ", passphrase: " + passphrase);
    //clean up old subscriptions
    MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
      public void onSuccess(List<MMXChannel> mmxChannels) {
        for (MMXChannel sub : mmxChannels) {
          sub.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
            public void onSuccess(Boolean aBoolean) {
              Log.d(TAG, "joinTopic(): unsubscribe success: " + aBoolean);
            }

            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
              Log.e(TAG, "joinTopic(): unsubscribe failure: " + failureCode, throwable);
            }
          });
        }
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {

      }
    });

    final String topicName = encodeTopic(topicKey, passphrase);
    MMXChannel.getPublicChannel(topicName, new MMXChannel.OnFinishedListener<MMXChannel>() {
      public void onSuccess(MMXChannel mmxChannel) {
        mJoinedTopic = mmxChannel;
        mJoinedTopicKey = topicKey;
        mJoinedTopicPassphrase = passphrase;
        mUsername = username;
        mSharedPrefs.edit()
                .putString(SHARED_PREF_KEY_JOINED_TOPIC_KEY, topicKey)
                .putString(SHARED_PREF_KEY_JOINED_TOPIC_PASSPHRASE, passphrase)
                .putString(SHARED_PREF_KEY_JOINED_TOPIC_AS_USERNAME, username)
                .putLong(SHARED_PREF_KEY_UPDATE_INTERVAL, updateInterval)
                .apply();
        mJoinedTopic.subscribe(new MMXChannel.OnFinishedListener<String>() {
          public void onSuccess(String s) {
            Log.d(TAG, "joinTopic(): successfully subscribed to group");
          }

          public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            Log.e(TAG, "joinTopic(): unable to subscribe to group: " + failureCode, throwable);
          }
        });

        LocationUpdaterService.startLocationUpdates(mContext, mUsername, mJoinedTopic, updateInterval);
        final String chatTopicName = topicName + SUFFIX_CHAT;
        MMXChannel.getPublicChannel(chatTopicName, new MMXChannel.OnFinishedListener<MMXChannel>() {
          public void onSuccess(MMXChannel mmxChannel) {
            mJoinedTopicChat = mmxChannel;
            loadMessages(mJoinedTopicChat);
            mJoinedTopicChat.subscribe(new MMXChannel.OnFinishedListener<String>() {
              public void onSuccess(String s) {
                Log.d(TAG, "joinTopic(): successfully subscribed to group chat");
              }

              public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "joinTopic(): unable to subscribe to group chat: " + failureCode, throwable);
              }
            });
          }

          public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            //for backwards compatibility
            MMXChannel.create(chatTopicName, chatTopicName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
              public void onSuccess(MMXChannel mmxChannel) {
                mJoinedTopicChat = mmxChannel;
              }

              public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "Unable to create chat topic: " + failureCode, throwable);
              }
            });
          }
        });
        listener.onSuccess(null);
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        listener.onFailure(failureCode, throwable);
      }
    });
  }

  public synchronized void leaveTopic() {
    if (mJoinedTopic != null) {
      mSharedPrefs.edit()
              .remove(SHARED_PREF_KEY_JOINED_TOPIC_KEY)
              .remove(SHARED_PREF_KEY_JOINED_TOPIC_PASSPHRASE)
              .apply();
      LocationUpdaterService.stopLocationUpdates(mContext, mUsername, mJoinedTopic);
      clearLastLocations(mJoinedTopic);
      MyMessageStore.clear();
      final MMXChannel tempTopic = mJoinedTopic;
      mJoinedTopic = null;
      mJoinedTopicChat = null;
      mJoinedTopicKey = null;
      mJoinedTopicPassphrase = null;
      mLocations.clear();
      tempTopic.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
        public void onSuccess(Boolean aBoolean) {
          Log.d(TAG, "leaveTopic(): success");
        }

        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
          Log.d(TAG, "leaveTopic(): failure: " + failureCode, throwable);
        }
      });
    }
  }

  private synchronized String encodeTopic(String topicKey, String passphrase) {
    byte[] digestedPassphrase = digest(passphrase + topicKey);
    String encodedDigestedPassphrase = bytesToHex(digestedPassphrase);
    return topicKey + '_' + encodedDigestedPassphrase;
  }

  private byte[] digest(String text) {
    if (mDigester == null) {
      try {
        mDigester = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "encodeTopic(): caught exception", e);
      }
    }
    byte[] topicBytes = text.getBytes();
    mDigester.update(topicBytes, 0, topicBytes.length);
    byte[] digest = mDigester.digest();
    mDigester.reset();
    return digest;
  }

  private enum MessageType {LOCATION_REQUEST, LOCATION_UPDATE}
  private static final String KEY_MESSAGE_TYPE = "type";
  private static final String KEY_USERNAME = "username";
  private static final String KEY_LOCATION = "location";
  private static final String KEY_TIMESTAMP = "timestamp";

  public static class LocationTime {
    public final String username;
    public final GeoLoc location;
    public final long locationTimestamp;
    public final long timestamp;

    private LocationTime(String username, GeoLoc location, long locationTimestamp, long timestamp) {
      this.username = username;
      this.location = location;
      this.locationTimestamp = locationTimestamp;
      this.timestamp = timestamp;
    }
  }

  public boolean requestLocationUpdates() {
    if (MMX.getCurrentUser() != null) {
      final HashMap<String, String> content = new HashMap<>();
      content.put(KEY_MESSAGE_TYPE, MessageType.LOCATION_REQUEST.name());
      Log.d(TAG, "requestLocationUpdates(): requesting location updates from subscribers");
      mJoinedTopic.getAllSubscribers(0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXUser>>() {
        public void onSuccess(ListResult<MMXUser> mmxUserListResult) {
          HashSet<MMXUser> recipients = new HashSet<>(mmxUserListResult.items);
          if (recipients.size() > 0) {
            MMXMessage message = new MMXMessage.Builder()
                    .recipients(recipients)
                    .content(content)
                    .build();
            message.send(new MMXMessage.OnFinishedListener<String>() {
              public void onSuccess(String s) {
                Log.d(TAG, "requestLocationUpdates(): success");
              }

              public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "requestLocationUpdates(): failed: " + failureCode, throwable);
              }
            });
          } else {
            Log.d(TAG, "requestLocationUpdates(): no subscribers...");
          }
        }

        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
          Log.e(TAG, "requestLocationUpdates(): failed: " + failureCode, throwable);
        }
      });
      return true;
    }
    return false;
  }

  public static Map<String, String> buildPayload(String username, GeoLoc geo, Date locationDate) {
    HashMap<String, String> content = new HashMap<>();
    content.put(KEY_MESSAGE_TYPE, MessageType.LOCATION_UPDATE.name());
    content.put(KEY_USERNAME, username);
    content.put(KEY_LOCATION, geo.toJson());
    content.put(KEY_TIMESTAMP, String.valueOf(locationDate.getTime()));
    return content;
  }

  private void handlePayload(MMXUser from, Map<String, String> content) {
    String username = content.get(KEY_USERNAME);
    String locationStr = content.get(KEY_LOCATION);
    String locationTimeStr = content.get(KEY_TIMESTAMP);

    if (locationStr == null || username == null || locationTimeStr == null) {
      Log.w(TAG, "handlePayload(): unable to parse payload.  ignoring");
      return;
    }
    GeoLoc geo = GeoLoc.fromJson(locationStr);
    long locationTime = Long.valueOf(locationTimeStr);
    EventLog.getInstance(mContext).add(EventLog.Type.INFO, "WRU: Location update received from: " + username);

    //put this in the map
    synchronized (mLocations) {
      long timestamp = System.currentTimeMillis();
      LocationTime newLocationTime = new LocationTime(username, geo, locationTime, timestamp);
      mLocations.put(username, newLocationTime);
      saveLastLocation(mJoinedTopic, newLocationTime);
      notifyLocationListeners(from, newLocationTime);
    }
  }

  public ConcurrentHashMap<String, LocationTime> getLocationTimes() {
    return mLocations;
  }

  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  private final ArrayList<OnLocationReceivedListener> mLocationListeners = new ArrayList<>();

  public interface OnLocationReceivedListener {
    void onLocationReceived(MMXUser user, LocationTime locationTime);
  }

  public void registerOnLocationReceivedListener(OnLocationReceivedListener listener) {
    synchronized (mLocationListeners) {
      for (OnLocationReceivedListener existingListener : mLocationListeners) {
        if (existingListener == listener) {
          return;
        }
      }
      mLocationListeners.add(listener);
    }
  }

  public void unregisterOnLocationReceivedListener(OnLocationReceivedListener listener) {
    synchronized (mLocationListeners) {
      mLocationListeners.remove(listener);
    }
  }

  private void notifyLocationListeners(MMXUser user, LocationTime locationTime) {
    synchronized (mLocationListeners) {
      for (OnLocationReceivedListener listener : mLocationListeners) {
        try {
          listener.onLocationReceived(user, locationTime);
        } catch (Exception ex) {
          Log.e(TAG, "notifyLocationListeners(): caught exception, but continuing.", ex);
        }
      }
    }
  }

  // ==================
  // LOGIN HELPER STUFF
  // ==================
  private final AtomicBoolean mRegistered = new AtomicBoolean(false);
  private synchronized void registerMMXUser() {
    String username = getMMXUsername();
    if (username == null) {
      //register a new user
      final String newUsername = generateString();
      final String newPassword = generateString();
      MMXUser user = new MMXUser.Builder().username(newUsername).build();
      user.register(newPassword.getBytes(), new MMXUser.OnFinishedListener<Void>() {
        public void onSuccess(Void aVoid) {
          Log.d(TAG, "register() success: " + newUsername);
          mRegistered.set(true);
          mSharedPrefs.edit().putString(SHARED_PREF_KEY_MMX_USERNAME, newUsername).apply();
          mSharedPrefs.edit().putString(SHARED_PREF_KEY_MMX_PASSWORD, newPassword).apply();
          synchronized (mRegistered) {
            mRegistered.notify();
          }
        }

        public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
          Log.e(TAG, "registration() failure: " + newUsername);
          Toast.makeText(mContext, "Unable to register user", Toast.LENGTH_LONG).show();
          synchronized (mRegistered) {
            mRegistered.notify();
          }
        }
      });
    } else {
      mRegistered.set(true);
      synchronized (mRegistered) {
        mRegistered.notify();
      }
    }
  }

  private synchronized void login() {
    if (mLoggingIn.get() || mLoginSuccess.get()) {
      //already logging in or logged in
      return;
    }
    mLoggingIn.set(true);
    if (!mRegistered.get()) {
      synchronized (mRegistered) {
        try {
          mRegistered.wait(10000);
        } catch (InterruptedException e) {
          Log.e(TAG, "login(): caught exception", e);
        }
      }
    }

    if (!mRegistered.get()) {
      Toast.makeText(mContext, "Unable to connect to server.", Toast.LENGTH_LONG).show();
      return;
    }

    MMX.login(getMMXUsername(), getMMXPassword().getBytes(), new MMX.OnFinishedListener<Void>() {
      public void onSuccess(Void aVoid) {
        mLoggingIn.set(false);
        mLoginSuccess.set(true);
        MMX.start();
        if (mJoinedTopicKey != null && mJoinedTopicPassphrase != null) {
          final String topicName = encodeTopic(mJoinedTopicKey, mJoinedTopicPassphrase);
          MMXChannel.getPublicChannel(topicName, new MMXChannel.OnFinishedListener<MMXChannel>() {
            public void onSuccess(MMXChannel mmxChannel) {
              mJoinedTopic = mmxChannel;
              final String chatTopicName = topicName + SUFFIX_CHAT;
              MMXChannel.getPublicChannel(chatTopicName, new MMXChannel.OnFinishedListener<MMXChannel>() {
                public void onSuccess(MMXChannel mmxChannel) {
                  mJoinedTopicChat = mmxChannel;
                  loadMessages(mJoinedTopicChat);
                }

                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                  Log.d(TAG, "Unable to retrieve existing chat topic: " + failureCode, throwable);
                  //support older app which didn't have chat
                  MMXChannel.create(chatTopicName, chatTopicName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
                    public void onSuccess(MMXChannel mmxChannel) {
                      mJoinedTopicChat = mmxChannel;
                    }

                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                      Log.e(TAG, "Unable to create chat topic: " + failureCode, throwable);
                    }
                  });
                }
              });
              requestLocationUpdates();
              LocationUpdaterService.startLocationUpdates(mContext, mUsername, mJoinedTopic, mJoinedTopicUpdateInterval);
            }

            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
              Log.e(TAG, "Unable to retrieve existing topic: " + failureCode, throwable);
            }
          });
        }
      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
        Log.e(TAG, "login(): failed: " + failureCode, throwable);
        mLoggingIn.set(false);
        mLoginSuccess.set(false);
        Toast.makeText(mContext, "Unable to connect to server.", Toast.LENGTH_LONG).show();
      }
    });
  }

  public synchronized String getMMXUsername() {
    return mSharedPrefs.getString(SHARED_PREF_KEY_MMX_USERNAME, null);
  }

  public synchronized String getMMXPassword() {
    return mSharedPrefs.getString(SHARED_PREF_KEY_MMX_PASSWORD, null);
  }

  private synchronized String generateString() {
    if (mRandom == null) {
      mRandom = new Random();
    }
    return Long.toString(mRandom.nextLong(), 36) + '_' + Long.toString(System.currentTimeMillis(), 36);
  }

  // remember recent locations
  private class WRUHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "WRU";
    private static final int DB_VERSION = 1;
    private static final String TABLE_HISTORY = "history";
    private static final String COL_USERNAME = "username";
    private static final String COL_TOPIC_NAME = "topic";
    private static final String COL_LOCATION_TIME = "locationTime";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_LOCATION = "location";
    private static final String CREATE_HISTORY_TABLE =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COL_USERNAME + " TEXT, " +
                    COL_TOPIC_NAME + " TEXT, " +
                    COL_TIMESTAMP + " NUM, " +
                    COL_LOCATION_TIME + " NUM, " +
                    COL_LOCATION + " TEXT)";



    private WRUHelper() {
      super(mContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
  }

  private void saveLastLocation(MMXChannel topic, LocationTime locationTime) {
    Log.d(TAG, "saveLastLocation(): " + locationTime.username);
    ContentValues values = new ContentValues();
    values.put(WRUHelper.COL_LOCATION, locationTime.location.toJson());
    values.put(WRUHelper.COL_LOCATION_TIME, locationTime.locationTimestamp);
    values.put(WRUHelper.COL_TIMESTAMP, System.currentTimeMillis());
    String where = WRUHelper.COL_USERNAME + "=? AND " + WRUHelper.COL_TOPIC_NAME + "=?";
    String[] whereArgs = {locationTime.username, topic.getName().toLowerCase()};
    int rows = mDb.update(WRUHelper.TABLE_HISTORY, values, where, whereArgs);
    Log.d(TAG, "saveLastLocation(): rows updated=" + rows);
    if (rows == 0) {
      //do an insert instead
      values.put(WRUHelper.COL_USERNAME, locationTime.username);
      values.put(WRUHelper.COL_TOPIC_NAME, topic.getName());
      Log.d(TAG, "saveLastLocation(): inserting new record for " + locationTime.username);
      mDb.insert(WRUHelper.TABLE_HISTORY, "", values);
    }
  }

  private void loadLastLocations(String topicName) {
    Log.d(TAG, "loadLastLocations(): for topic: " + topicName);
    String where = WRUHelper.COL_TOPIC_NAME + "=?";
    String[] whereArgs = {topicName.toLowerCase()};
    Cursor cursor = null;
    try {
      cursor = mDb.query(WRUHelper.TABLE_HISTORY, null, where, whereArgs, null, null, null);
      int usernameIdx = cursor.getColumnIndex(WRUHelper.COL_USERNAME);
      int locationIdx = cursor.getColumnIndex(WRUHelper.COL_LOCATION);
      int locationTimeIdx = cursor.getColumnIndex(WRUHelper.COL_LOCATION_TIME);
      int timestampIdx = cursor.getColumnIndex(WRUHelper.COL_TIMESTAMP);
      synchronized (mLocations) {
        while (cursor.moveToNext()) {
          String username = cursor.getString(usernameIdx);
          GeoLoc geo = GeoLoc.fromJson(cursor.getString(locationIdx));
          long locationTimestamp = cursor.getLong(locationTimeIdx);
          long timestamp = cursor.getLong(timestampIdx);
          mLocations.put(username, new LocationTime(username, geo, locationTimestamp, timestamp));
        }
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  private void loadMessages(MMXChannel topic) {
    Log.d(TAG, "loadMessages(): for topic: " + topic.getName());
    MyMessageStore.clear();
    topic.getMessages(null, null, 0, 20, true, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
      public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
        Log.d(TAG, "loadMessages(): seeding existing messages");
        MMXUser currentUser = MMX.getCurrentUser();
        MyMessageStore.sSuppressNotification = true;
        for (MMXMessage message : mmxMessageListResult.items) {
          MyMessageStore.addMessage(message.getId(), message.getContent(), message.getTimestamp(),
                  !message.getSender().equals(currentUser));
        }
        MyMessageStore.sSuppressNotification = false;
      }

      public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
        Log.w(TAG, "loadMessages() failed: " + failureCode, throwable);
      }
    });
  }

  private void clearLastLocations(MMXChannel topic) {
    String where = WRUHelper.COL_TOPIC_NAME + "=?";
    String[] whereArgs = {topic.getName()};
    int count = mDb.delete(WRUHelper.TABLE_HISTORY, where, whereArgs);
    Log.d(TAG, "clearLastLocations: deleted " + count + " rows for topic: " + topic.getName());
  }

  public GoogleApiClient waitForGoogleApi() {
    synchronized (mGoogleApiInitialized) {
      if (mGoogleApiInitialized.get()) {
        return mGoogleApiClient;
      } else {
        try {
          mGoogleApiInitialized.wait();
        } catch (InterruptedException e) {
        }
        if (mGoogleApiInitialized.get()) {
          return mGoogleApiClient;
        } else {
          return null;
        }
      }
    }
  }

  private static boolean playServicesConnected(Context context) {
    // Check that Google Play services is available
    int resultCode =
            GooglePlayServicesUtil.
                    isGooglePlayServicesAvailable(context);
    // If Google Play services is available
    if (ConnectionResult.SUCCESS == resultCode) {
      Log.d(TAG, "playServicesConnected():  Google Play services is available.");
      // Continue
      return true;
      // Google Play services was not available for some reason.
      // resultCode holds the error code.
    } else {
      // log an error
      Log.e(TAG, "playServicesConnected(): Google Play services is NOT AVAILABLE.");
      return false;
    }
  }
}

