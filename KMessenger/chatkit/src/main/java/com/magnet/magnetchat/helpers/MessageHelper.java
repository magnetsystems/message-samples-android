/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.helpers;

import com.magnet.magnetchat.model.Message;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMXMessage;

public class MessageHelper {

  public static String getMessageType(MMXMessage mmxMessage) {
    return mmxMessage.getContent().get(Message.TAG_TYPE);
  }

  public static String getMessageSummary(MMXMessage message) {
    String msgType = message.getContent().get(Message.TAG_TYPE);
    if (msgType == null) {
      msgType = Message.TYPE_TEXT;
    }
    switch (msgType) {
      case Message.TYPE_MAP:
        return "User's location";
      case Message.TYPE_VIDEO:
        return "User's video";
      case Message.TYPE_PHOTO:
        return "User's photo";
      case Message.TYPE_TEXT:
        String text = message.getContent().get(Message.TAG_TEXT);
        if (StringUtil.isNotEmpty(text)) {
          text.replace(System.getProperty("line.separator"), " ");
        }
        if (text.length() > 23) {
          text = text.substring(0, 20) + "...";
        }
        return text;
    }

    return "";
  }
}
