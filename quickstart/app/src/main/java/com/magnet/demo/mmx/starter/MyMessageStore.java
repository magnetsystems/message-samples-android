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

import com.magnet.mmx.client.common.MMXMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple implementation of an in-memory message store.
 */
public class MyMessageStore {
  private static ArrayList<Message> sMessageList = new ArrayList<Message>();

  /**
   * A data object to store the fields to display
   */
  public static class Message {
    private MMXMessage mMessage;
    private Date mTimestamp;
    private String mSentText;
    private boolean mIsIncoming;

    private Message(MMXMessage message, String sentText,
                    Date timestamp, boolean isIncoming) {
      mMessage = message;
      mSentText = sentText;
      mTimestamp = timestamp;
      mIsIncoming = isIncoming;
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
  }

  public static List<Message> getMessageList() {
    return Collections.unmodifiableList(sMessageList);
  }

  public static void addMessage(MMXMessage message, String sentText,
                                Date timestamp, boolean isIncoming) {
    synchronized (sMessageList) {
      sMessageList.add(new Message(message, sentText, timestamp, isIncoming));
    }
  }

}
