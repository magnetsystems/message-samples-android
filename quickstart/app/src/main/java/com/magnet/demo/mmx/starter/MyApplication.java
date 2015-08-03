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

import com.magnet.mmx.client.MMXClient;

import android.app.Application;

/**
 * Quick-start application extending the android Application class.  It registers
 * the <code>MyWakeupListener</code> to handle the GCM wake-up message sent by
 * MMX server.  Alternatively, the application may use a timer to wake itself
 * up; it is functionally equivalent to Android AlarmManager.  Typically a GCM
 * wake-up message is sent when there is a message waiting for the disconnected
 * client to retrieve, and the wake-up listener is responsible to establish the
 * connection.  Currently <code>MyWakeupListener</code> has this logic disabled
 * by a flag.
 * @see MyActivity
 * @see MyWakeupListener
 */
public class MyApplication extends Application {
  private final static boolean WAKEUP_BY_TIMER = false;
  
  public void onCreate() {
    super.onCreate();
    
    // register a wakeup listener for GCM and/or timer.
    MMXClient.registerWakeupListener(this, MyWakeupListener.class);
    
    if (WAKEUP_BY_TIMER) {
      // Alternatively, use a 60-second timer to wake up this application.
      MMXClient.setWakeupInterval(this, 60 * 1000);
    }
  }
}
