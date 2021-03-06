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

import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.max.android.Max;
import com.magnet.mmx.client.api.MMX;

import android.app.Application;
import android.content.Intent;

/**
 * Quick-start application extending the android Application class.  It registers
 * the a broadcast intent to handle the GCM wake-up message sent by MMX server.
 * Typically, a GCM wake-up message is sent when there is a message waiting for
 * the disconnected client to retrieve, and the wake-up broadcast receiver is responsible
 * to establish the connection.
 *
 * @see MyActivity
 */
public class MyApplication extends Application {

  public void onCreate() {
    super.onCreate();
    com.magnet.mmx.client.common.Log.setLoggable(null, com.magnet.mmx.client.common.Log.VERBOSE);
    //First thing to do is init the Max API.
    Max.init(this.getApplicationContext(),
            new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
    // Optionally register a wakeup broadcast intent.  This will be broadcast when a GCM message
    // for this MMX application.  If configure properly, the MMX server will send this GCM  to wakeup
    // the device when a message needs to be delivered.  It is up to the developer to define this intent
    // and implement/declare the BroadcastReceiver to handle this intent and thus to call MMX.login()
    // to retrieve pending messages.
    Intent intent = new Intent("QUICKSTART_WAKEUP");
    MMX.registerWakeupBroadcast(this, intent);
  }
}
