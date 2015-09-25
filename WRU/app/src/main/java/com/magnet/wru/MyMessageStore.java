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
package com.magnet.wru;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import com.magnet.mmx.util.Base64;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple implementation of an in-memory message store.
 */
public class MyMessageStore {
  private static final String TAG = MyMessageStore.class.getSimpleName();
  private static final ArrayList<Message> sMessageList = new ArrayList<>();
  private static final ArrayList<OnChangeListener> sListeners = new ArrayList<>();
  static boolean sSuppressNotification = false;

  public interface OnChangeListener {
    void onChange();
  }

  /**
   * A data object to store the fields to display
   */
  public static class Message {
    private String mId;
    private Map<String, String> mContent;
    private Date mTimestamp;
    private boolean mIsIncoming;

    private Message(String id, Map<String, String> content,
                    Date timestamp, boolean isIncoming) {
      mId = id;
      mContent = content;
      mTimestamp = timestamp;
      mIsIncoming = isIncoming;
    }

    public String getId() {
      return mId;
    }

    public Map<String,String> getContent() {
      return mContent;
    }

    public Date getTimestamp() {
      return mTimestamp;
    }

    public boolean isIncoming() {
      return mIsIncoming;
    }
  }

  public static List<Message> getMessageList() {
    return Collections.unmodifiableList(sMessageList);
  }

  public static void addMessage(String id, Map<String, String> content,
                                Date timestamp, boolean isIncoming) {
    synchronized (sMessageList) {
      sMessageList.add(new Message(id, content, timestamp, isIncoming));
      notifyListeners();
      if (!sSuppressNotification) {
        doNotify();
      }
    }
  }

  public static void clear() {
    synchronized (sMessageList) {
      sMessageList.clear();
      notifyListeners();
    }
  }

  public static void registerListener(OnChangeListener listener) {
    synchronized (sListeners) {
      boolean alreadyAdded = false;
      for (OnChangeListener existing : sListeners) {
        if (alreadyAdded = (existing == listener)) {
          break;
        }
      }
      if (!alreadyAdded) {
        sListeners.add(listener);
      }
    }
  }

  public static void unregisterListener(OnChangeListener listener) {
    synchronized (sListeners) {
      for (int i=sListeners.size(); --i>=0;) {
        if (sListeners.get(i) == listener) {
          sListeners.remove(i);
          break;
        }
      }
    }
  }

  private static void notifyListeners() {
    synchronized (sListeners) {
      for (OnChangeListener listener : sListeners) {
        try {
          listener.onChange();
        } catch (Throwable t) {
          Log.e(TAG, "notifyListener(): caught exception for listener: " + listener, t);
        }
      }
    }
  }

  private static final HashMap<String, Bitmap> sBitmapCache = new HashMap<>();

  /**
   * If the drawable is already cached for the specified id.  This returns true
   * is it's ready to go (for example, if the main thread is deciding whether or not to call
   * getDrawable() on the main thread or on non-main threads.
   * @param id the id
   * @return true if the drawable is ready
   */
  public static boolean isBitmapCached(String id) {
    synchronized (sBitmapCache) {
      return sBitmapCache.containsKey(id) && sBitmapCache.get(id) != null;
    }
  }

  /**
   * This should be called on a separate thread as it may block.  If it is already cached, it will
   * return immediately.  If not, it will process the drawable or wait if the id is already pending
   * load (this will hopefully reduce the number of image decodes).
   * @param id the id
   * @param base64EncodedImage the base64 encoded image
   * @return the drawable
   */
  public static Bitmap getBitmap(String id, String base64EncodedImage) {
    Bitmap bitmap;
    synchronized (sBitmapCache) {
      bitmap = sBitmapCache.get(id);
      if (bitmap != null) {
        return bitmap;
      } else {
        if (!sBitmapCache.containsKey(id)) {
          //first time caching
          sBitmapCache.put(id, null);
        } else {
          //already pending, wait for the pending to finish
          while ((bitmap = sBitmapCache.get(id)) == null) {
            try {
              sBitmapCache.wait();
            } catch (InterruptedException e) {
              Log.w(TAG, "getDrawable(): interrupted exception", e);
            }
          }
          return bitmap;
        }
      }
    }
    try {
      File tempAttachmentFile = File.createTempFile(id + "-attach-" + System.currentTimeMillis(), null);
      Base64.decodeToFile(base64EncodedImage, tempAttachmentFile.getAbsolutePath());
      bitmap = BitmapFactory.decodeFile(tempAttachmentFile.getAbsolutePath());
    } catch (IOException ex) {
      Log.e(TAG, "getDrawable(): unable to get drawable for id: " + id, ex);
    }
    synchronized (sBitmapCache) {
      sBitmapCache.put(id, bitmap);
      sBitmapCache.notify();
    }
    return bitmap;
  }

  private static void doNotify() {
    Intent intent = new Intent(WRUApplication.sContext, MapActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(WRUApplication.sContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationManager noteMgr = (NotificationManager)
            WRUApplication.sContext.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification note = new Notification.Builder(WRUApplication.sContext).setAutoCancel(true)
            .setSmallIcon(android.R.drawable.stat_notify_chat).setWhen(System.currentTimeMillis())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setContentTitle("WRU").setContentText("message received").build();
    noteMgr.notify(0, note);
  }
}
