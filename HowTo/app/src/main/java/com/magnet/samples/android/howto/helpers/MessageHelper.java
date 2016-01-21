/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
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
 *
 */
package com.magnet.samples.android.howto.helpers;

import android.support.v4.app.FragmentManager;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.fragments.NewMessageDialogFragment;

public class MessageHelper {
  public static void showSendMessageDialog(FragmentManager fm) {
    showSendMessageDialog(fm, null);
  }

  public static void showSendMessageDialog(FragmentManager fm, MMXChannel toChannel) {
    String title = null;
    if(null != toChannel) {
      title = "Send Message to Channel " + toChannel.getName();
    } else {
      title = "Send Message";
    }
    NewMessageDialogFragment dialogFragment = NewMessageDialogFragment.newInstance(title);
    if(null != toChannel) {
      dialogFragment.setChannel(toChannel);
    }

    dialogFragment.show(fm, title);
  }
}
