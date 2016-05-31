package com.magnet.magnetchat.beans;

import android.content.Context;
import android.content.Intent;

import com.magnet.mmx.client.api.MMXChannel;

/**
 * The factory returns common instance of objects
 * Created by aorehov on 20.05.16.
 */
public interface MMXBeanFactory {

    /**
     * The method returns date format pattern for messages
     * @see com.magnet.magnetchat.model.converters.impl.DefaultMMXMessageDateConverter
     * @see java.text.SimpleDateFormat
     *
     *
     * @return
     */
    String messageDateFormat();

    /**
     * @param mmxChannel instance of MMXChannel
     * @return instance of intent or null if channel null or corrupted
     */
    Intent getMagnetChatIntent(Context context, MMXChannel mmxChannel);
}
