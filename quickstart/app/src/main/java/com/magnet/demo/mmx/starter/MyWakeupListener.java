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
import android.content.Intent;
import android.util.Log;

import com.magnet.mmx.client.MMXClient.MMXWakeupListener;
import com.magnet.mmx.client.api.MMX;

/**
 * This listener is registered during Application.onCreate() to handle GCM and
 * AlarmManager wakeups.  See {@link MyApplication#onCreate()}onCreate().
 * This is only necessary if the application should respond to MMX
 * wake-ups (including GCM wake-ups or timer-based events.  To enable the
 * reconnection, set {@link #ENABLE_RECONNECTION} to true.
 */
public class MyWakeupListener implements MMXWakeupListener {
  private static final String TAG = MyWakeupListener.class.getSimpleName();
  private static final boolean ENABLE_RECONNECTION = false;

  public void onWakeupReceived(final Context applicationContext, Intent intent) {
    if (ENABLE_RECONNECTION) {
      // TODO: Upon receiving a wakeup, the application may choose to connect.
      Log.d(TAG, "onWakeupReceived():  MagnetMessage session started.");
      MMX.login(MyActivity.QUICKSTART_USERNAME, MyActivity.QUICKSTART_PASSWORD,
              new MMX.OnFinishedListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          Log.d(TAG, "onWakeupReceived():  Login successful.");
        }

        @Override
        public void onFailure(MMX.FailureCode failureCode, Throwable e) {
          Log.e(TAG, "onWakeupReceived(): Login failed with code: " + failureCode, e);
        }
      });
    }
  }
}
