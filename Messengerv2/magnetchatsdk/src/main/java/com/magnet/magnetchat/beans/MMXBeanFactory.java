package com.magnet.magnetchat.beans;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.magnet.mmx.client.api.MMXChannel;

/**
 * The factory returns common instance of objects
 * Created by aorehov on 20.05.16.
 */
public interface MMXBeanFactory {

    /**
     * The method returns date format pattern for messages
     *
     * @return
     * @see com.magnet.magnetchat.model.converters.impl.DefaultMMXMessageDateConverter
     * @see java.text.SimpleDateFormat
     */
    String messageDateFormat();

    /**
     * @param mmxChannel instance of MMXChannel
     * @return instance of intent or null if channel null or corrupted
     */
    Intent getMagnetChatIntent(Context context, MMXChannel mmxChannel);

    /**
     * The method return channel details intent
     *
     * @return intent of chat details activity or null
     */
    @Nullable
    Intent createChatDetailsIntent(Context context, MMXChannel mmxChannel);
}
