package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.converters.MMXPollOptionStringConverter;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 17.05.16.
 */
public class DefaultMMXPollOptionStringConverter extends MMXPollOptionStringConverter {
    @Override
    public MMXPollOption convert(String s) {
        return new MMXPollOption(s);
    }
}
