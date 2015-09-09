/*   Copyright (c) 2015 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.magnet.demo.mmx.starter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple implementation of an in-memory message store.
 */
public class MyMessageStore {
  private static final String TAG = MyMessageStore.class.getSimpleName();
  private static final String AWS_S3_BUCKETNAME = "foo"; //amazon s3 bucket name
  private static final String AWS_IDENTITY_POOL_ID = "bar"; //looks like this: "us-east-1:a1b2c3d4-a1b2-a1b2-a1b2-a1b2c3d4e5f6";
  private static final String PREFIX = "MyAppDump/";
  private static final String META_FILE_MIME_TYPE = "MimeType";
  private static final String META_FILE_URL = "FileAttachment";
  private static final ArrayList<Message> sMessageList = new ArrayList<Message>();
  private static TransferUtility sTransferUtility = null;
  private static final ArrayList<OnChangeListener> sListeners = new ArrayList<OnChangeListener>();

  /**
   * A data object to store the fields to display
   */
  public static class Message {
    private MMXMessage mMessage;
    private Date mTimestamp;
    private String mSentText;
    private boolean mIsIncoming;
    private File mFile;
    private String mFileType;

    private Message(MMXMessage message, String sentText,
                    Date timestamp, boolean isIncoming, File file, String fileType) {
      mMessage = message;
      mSentText = sentText;
      mTimestamp = timestamp;
      mIsIncoming = isIncoming;
      mFile = file;
      mFileType = fileType;
    }

    public MMXMessage getMessage() {
      return mMessage;
    }

    public String getSentText() {
      return mSentText;
    }

    public Date getTimestamp() {
      return mTimestamp;
    }

    public boolean isIncoming() {
      return mIsIncoming;
    }

    public File getFile() { return mFile; }

    public void setFile(File file) { mFile = file; }

    public String getFileType() {
      return mFileType;
    }

    public void setFileType(String fileType) {
      mFileType = fileType;
    }
  }

  public static List<Message> getMessageList() {
    ArrayList<Message> result = new ArrayList<Message>();
    synchronized (sMessageList) {
      for (Message msg:sMessageList) {
        result.add(msg);
      }
    }
    return result;
  }

  /**
   * Initializes the AWS S3 credentials provider and transfer manager
   *
   * @param context the android context
   */
  public static void init(Context context) {
    // Initialize the Amazon Cognito credentials provider
    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            context.getApplicationContext(), // Context
            AWS_IDENTITY_POOL_ID, // Identity Pool ID
            Regions.US_EAST_1 // Region
    );
    AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
    sTransferUtility = new TransferUtility(s3Client, context);
  }

  /**
   * Generates a key for use with the external storage provider.  This could also just
   * be a unique path/filename.
   *
   * @param file the file to be uploaded
   * @return the generated unique key
   */
  private static String generateKey(File file) {
    String suffix = "";
    String path = file.getPath();
    int idx = path.lastIndexOf('.');
    if (idx >= 0) {
      suffix = path.substring(idx);
    }
    return PREFIX + UUID.randomUUID().toString() + suffix;
  }

  /**
   * Uploads the file to the external stroage provider
   * @param file the file to be uploaded
   * @return the key associated with the file
   */
  private static void uploadFile(String key, File file, TransferListener listener) {
    final TransferObserver uploadObserver = sTransferUtility.upload(AWS_S3_BUCKETNAME, key, file);
    uploadObserver.setTransferListener(listener);
  }

  /**
   * Called upon receiving a message.  If there is a file url, the file will be downloaded
   * locally.  This could be done lazily or possibly to external storage.  Care should
   * be taken to prevent downloading large files that may fill up the local storage.
   *
   * @param context the android context
   * @param message the received message
   */
  public static void handleMessage(Context context, final MMXMessage message) {
    Map<String, String> content = message.getContent();
    File file = null;
    String fileType = null;
    String urlStr = content.get(META_FILE_URL);
    if (urlStr != null) {
      fileType = content.get(META_FILE_MIME_TYPE);
      int suffixIdx = urlStr.lastIndexOf('.');
      String suffix = null;
      if (suffixIdx >= 0) {
        suffix = urlStr.substring(suffixIdx);
      }
      HttpURLConnection conn = null;
      try {
        file = File.createTempFile("msg_file-", suffix);
        URL url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
          fos.write(buffer, 0, len);
        }
      } catch (IOException e) {
        Log.e(TAG, "addMessage(): caught exception", e);
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }
    addMessage(message, null, new Date(), true, file, fileType);
  }

  /**
   * Send message wrapper method that uploads the specified file before sending the message.
   *
   * @param username the target username
   * @param text the text in the message
   * @param file the file to be uploaded
   * @param fileMimeType the mime type of the file
   */
  public static void sendMessage(final Context context, String username, final String text,
                                 final File file, final String fileMimeType,
                                 final MMXMessage.OnFinishedListener<String> listener) {
    final HashMap<String, String> content = new HashMap<String,String>();
    content.put(MyActivity.KEY_MESSAGE_TEXT, text);
    final HashSet<MMXUser> recipients = new HashSet<MMXUser>();
    recipients.add(new MMXUser.Builder().username(username).build());
    final MMXMessage message = new MMXMessage.Builder()
            .recipients(recipients)
            .content(content)
            .build();
    if (file != null) {
      Log.d(TAG, "doSendMessage(): file exists=" + file.exists());
      final String key = generateKey(file);
      uploadFile(key, file, new TransferListener() {
        public void onStateChanged(int id, TransferState state) {
          switch (state) {
            case COMPLETED:
              content.put(META_FILE_MIME_TYPE, fileMimeType);
              content.put(META_FILE_URL, buildUrl(key));
              message.send(listener);
              addMessage(null, text, new Date(), false, file, fileMimeType);
              break;
            case CANCELED:
            case FAILED:
              Toast.makeText(context, "Unable to send message.", Toast.LENGTH_LONG).show();
              break;
          }
        }

        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        public void onError(int id, Exception ex) {
          Log.e(TAG, "sendMessage(): exception during upload", ex);
        }
      });
    } else {
      String messageID = message.send(listener);
      addMessage(null, text, new Date(), false, null, fileMimeType);
    }
  }

  /**
   * Builds a URL for the file to be retrieved from the external provider.  In this example for Amazon S3,
   * it is based off of a bucket name and a key
   *
   * @param key the key identifying the file
   * @return the url with which this file can be retrieved
   */
  private static String buildUrl(String key) {
    //construct the publicly accessible url for the file
    //in this case, we have our bucket name and the key
    return "https://" + AWS_S3_BUCKETNAME + ".s3-us-west-1.amazonaws.com/" + key;
  }

  /**
   * Adds a message to the local store for display to the user.
   * @param message the message
   * @param sentText the text that was sent (if this is a send)
   * @param timestamp the time that the message was sent or received
   * @param isIncoming true if this is a send, false otherwise
   * @param file the file associated with this message
   * @param fileType the mime type of the file
   */
  private static void addMessage(MMXMessage message, String sentText,
                                Date timestamp, boolean isIncoming, File file, String fileType) {
    synchronized (sMessageList) {
      Message msg = new Message(message, sentText, timestamp, isIncoming, file, fileType);
      sMessageList.add(msg);
    }
    notifyChange();
  }

  private static void notifyChange() {
    synchronized (sListeners) {
      for (OnChangeListener listener : sListeners) {
        try {
          listener.onChange();
        } catch (Exception ex) {
          Log.w(TAG, "notifyChange(): caught exception", ex);
        }
      }
    }
  }

  public static void registerOnChangeListener(OnChangeListener listener) {
    synchronized (sListeners) {
      for (OnChangeListener existingListener : sListeners) {
        if (existingListener == listener) {
          return;
        }
      }
      sListeners.add(listener);
    }
  }

  public static void unregisterOnChangeListener(OnChangeListener listener) {
    synchronized (sListeners) {
      for (int i=sListeners.size(); --i >=0;) {
        if (sListeners.get(i) == listener) {
          sListeners.remove(i);
          return;
        }
      }
    }
  }

  public interface OnChangeListener {
    void onChange();
  }
}
