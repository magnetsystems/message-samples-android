/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.callbacks;

import com.magnet.magnetchat.model.Chat;
import com.magnet.mmx.client.api.MMXMessage;

public interface NewMessageProcessListener {
  void onProcessSuccess(Chat conversation, MMXMessage message, boolean isNewChat);
  void onProcessFailure(Throwable throwable);
}
